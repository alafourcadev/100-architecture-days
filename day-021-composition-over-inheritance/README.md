# Dia 021: La jerarquía de 4 niveles imposible de tocar.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

El equipo de pagos tenía una jerarquía que funcionaba.
`BasePaymentProcessor` manejaba el logging. `CardPaymentProcessor` sumaba el fraude y la comisión del 1.5%.
`InternationalCardProcessor` convertía divisas. `VisaInternationalProcessor` enrutaba a la red Visa.
`MastercardInternationalProcessor` enrutaba a Mastercard. Cuatro niveles, dos hojas. Prolijo.

Entonces llegó el requerimiento: **"Visa Infinite no paga comisión de tarjeta".**

¿Las opciones?

1. Agregar un flag `boolean waiveFee` en `CardPaymentProcessor` y poner un `if` adentro. La clase de nivel 2 se convierte en un objeto de configuración con comportamiento ambiguo.
2. Crear `VisaInfiniteInternationalProcessor extends VisaInternationalProcessor`. Nivel 5. La jerarquía crece hacia abajo.
3. Sobreescribir `doProcess()` en la hoja y copiar el fraud check a mano. El mismo código en dos lugares.

Ninguna es correcta. Y todavía falta AmEx, falta Visa doméstico, falta la transferencia bancaria que no necesita fraud check. Cada combinación es un nodo nuevo del árbol.

---

## Por qué duele

La herencia modela una cosa: **variación de tipo**. "Un Pingüino ES UN Pájaro."

Pero "un procesador Visa Infinite con conversión de divisas pero sin comisión de tarjeta" no es un tipo.
Es una **combinación de comportamientos**. Y la herencia no fue diseñada para modelar combinaciones.

Lo que quedó atrapado en la jerarquía:

- `processedCount` heredado en cada hoja, aunque solo el nodo raíz lo usa.
- El fraud check es parte de `CardPaymentProcessor` — si necesitás un procesador sin fraude tenés que subclasificar ahí arriba y romper la jerarquía hacia abajo.
- La tasa de conversión vive en `InternationalCardProcessor.getConversionRate()` — no podés reusar solo eso sin arrastrar toda la maquinaria de tarjetas.
- Cada nueva red de tarjetas (AmEx, Discover, UnionPay) necesita un nodo en el nivel 4. Siempre.

El árbol crece. El acoplamiento entre niveles hace que entender la hoja requiera leer 3 archivos más.

---

## La trampa

El arreglo obvio es agregar un nivel más de jerarquía o meter flags en los nodos intermedios.
Parece poco trabajo. Creás `VisaInfiniteInternationalProcessor`, copiás el constructor, sobreescribís `chargeCard()`.

Funciona. El sprint cierra.

Dos meses después: AmEx también quiere sin comisión. Y Mastercard Infinite. Y el equipo de transferencias bancarias quiere reusar el fraud check sin la lógica de tarjetas. En ese punto la jerarquía tiene ramas contradictorias, flags en los nodos base, y nadie se anima a tocar nada porque no saben qué rompen.

La trampa es que la herencia se siente ordenada hasta que necesitás una combinación que el árbol no modeló. En ese punto el árbol es una deuda técnica con forma de carpeta.

---

## La Decisión y su Porqué

La solución es separar los comportamientos en colaboradores independientes y componerlos:

```
ANTES:  BasePaymentProcessor → CardPaymentProcessor → InternationalCardProcessor → VisaInternationalProcessor
DESPUES: PaymentProcessor recibe FeeStrategy + CurrencyConverter + FraudGuard + CardNetworkGateway
```

Cada comportamiento es una clase independiente:

- `FeeStrategy` — interfaz funcional. `FeeStrategy.none()` o `FeeStrategy.percentage(0.015)`. Sin herencia.
- `CurrencyConverter` — hace conversión de divisas. No sabe nada de tarjetas ni de fraude.
- `FraudGuard` — valida montos. Si un procesador no lo necesita, no lo recibe. Punto.
- `CardNetworkGateway` — enruta a Visa, Mastercard, AmEx. Parametrizado por nombre de red, no por subclase.
- `PaymentProcessor` — el único procesador. Recibe los colaboradores que necesite y los ejecuta en orden.

```java
// Visa Infinite con sin comisión de tarjeta — cero clases nuevas
public static PaymentProcessor visaInfiniteInternational(AuditLogger logger) {
    return new PaymentProcessor(
            CardNetworkGateway.visa(),
            FeeStrategy.percentage(CURRENCY_FEE_PERCENT), // no card fee
            new CurrencyConverter(),
            new FraudGuard(FRAUD_THRESHOLD),
            logger);
}

// AmEx internacional — tampoco necesita una clase nueva
public static PaymentProcessor amexInternational(AuditLogger logger) {
    return new PaymentProcessor(
            CardNetworkGateway.amex(),
            FeeStrategy.percentage(CARD_FEE_PERCENT + CURRENCY_FEE_PERCENT),
            new CurrencyConverter(),
            new FraudGuard(FRAUD_THRESHOLD),
            logger);
}
```

El trade-off que resolvés: cambiar una combinación de comportamientos sin tocar código existente.
Lo que sacrificás: el orden visual de una jerarquía. La carpeta ya no dice "todos los procesadores de tarjetas están bajo `CardPaymentProcessor`" — decís eso en la factory, que es donde corresponde.

---

## Antes (jerarquía de 4 niveles)

```java
// Para agregar Visa Infinite sin comisión, ¿qué hacés?
// Opción a: flag en el nivel 2 — el if te espera en CardPaymentProcessor
// Opción b: Level 5 — extends VisaInternationalProcessor
// Opción c: copy-paste del fraud check en la hoja
public class VisaInternationalProcessor extends InternationalCardProcessor {
    // Hereda: processedCount, log(), validateAmount(), runFraudCheck(),
    //         CARD_PROCESSING_FEE_PERCENT, convertCurrency(), getConversionRate()
    // Implementa: una sola operación de 3 líneas
    @Override
    protected PaymentResult doChargeInternational(double amountUsd, String accountId) {
        String transactionId = "VISA-INTL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, transactionId, amountUsd, "Visa international payment approved");
    }
}
```

## Despues (composición)

```java
// La misma lógica, sin jerarquía
public class PaymentProcessor {
    public PaymentProcessor(
            CardNetworkGateway gateway,
            FeeStrategy feeStrategy,
            CurrencyConverter currencyConverter,  // null = sin conversión
            FraudGuard fraudGuard,                // null = sin fraud check
            AuditLogger auditLogger) { ... }

    public PaymentResult process(PaymentRequest request) {
        if (fraudGuard != null) fraudGuard.check(...);
        double amount = feeStrategy.apply(request.getAmount());
        if (currencyConverter != null) amount = currencyConverter.toUsd(amount, ...);
        return gateway.charge(amount, ...);
    }
}
```

---

## Estructura

```
src/main/java/dev/alafourca/day021/
  antes/
    BasePaymentProcessor.java          → Nivel 1: logging + validación
    CardPaymentProcessor.java          → Nivel 2: fraud check + comisión 1.5%
    InternationalCardProcessor.java    → Nivel 3: conversión de divisas
    VisaInternationalProcessor.java    → Nivel 4: enrutamiento Visa
    MastercardInternationalProcessor.java → Nivel 4 (duplicado): enrutamiento Mastercard
    PaymentResult.java
  despues/
    PaymentProcessor.java              → Único procesador, composable
    PaymentProcessorFactory.java       → Composition root: ensambla combinaciones
    FeeStrategy.java                   → Estrategia de comisiones (interfaz funcional)
    CurrencyConverter.java             → Conversión de divisas, standalone
    FraudGuard.java                    → Validación de fraude, standalone
    CardNetworkGateway.java            → Enrutamiento a red de tarjeta, parametrizado
    AuditLogger.java                   → Logging de auditoría, standalone
    PaymentRequest.java, PaymentResult.java

src/test/java/dev/alafourca/day021/
  InheritanceHierarchyTest.java  → documenta qué funciona y qué queda atrapado en el diseño "antes"
  CompositionTest.java           → verifica que cada combinación funciona sin clases nuevas
```

---

## Métricas

| Métrica | Antes | Después |
|---------|-------|---------|
| Clases para Visa + Mastercard internacionales | 5 (toda la jerarquía) | 1 `PaymentProcessor` + 1 factory |
| Clases necesarias para agregar AmEx | 1 nueva subclase Level-4 | 0 — factory method |
| Clases necesarias para Visa sin comisión | 1 Level-5 o copiar código | 0 — factory method |
| Clases necesarias para procesador sin fraud check | Romper la jerarquía desde Level-2 | 0 — pasar `null` como `fraudGuard` |
| Para testear `CurrencyConverter` aislado | Imposible sin instanciar la jerarquía | `new CurrencyConverter()` |

---

## Concepto Clave

**Composición sobre herencia:** preferí componer comportamientos combinando objetos
en lugar de construir jerarquías de tipos que fusionan comportamiento con estructura.

La herencia es una herramienta para **variación de tipo**: cuando B es un tipo especial de A
y tiene que ser sustituible por A en todos los contextos. Eso es todo.

Cuando usás herencia para **reusar comportamiento**, estás creando acoplamiento estructural
entre cosas que no tienen relación de tipo. El resultado es una jerarquía donde cada nivel
hereda más de lo que necesita, y agregar una combinación nueva requiere un nodo nuevo en el árbol.

La señal de alarma es clara: cuando ves una jerarquía de 3+ niveles y te cuesta agregar
una variante que "casi es igual pero con una diferencia pequeña", la herencia está modelando
combinaciones que le corresponden a la composición.

La solución tampoco es agregar interfaces encima de la herencia para "hacer que compile".
Es separar cada comportamiento en su propia clase independiente y componerlos donde se necesite.
