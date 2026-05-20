# Dia 017: El subtipo que explota en runtime.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

`BirdFlightService` recibe una `List<Bird>` y llama a `fly()` sobre cada uno.
El contrato de `Bird` lo promete. El cliente lo asume con razon.

Cuando alguien agrega `Penguin extends Bird` al sistema — biologicamente correcto,
arquitectonicamente catastrofico — `Penguin.fly()` lanza `UnsupportedOperationException`.
El cliente explota en runtime. El cliente no hizo nada mal: la jerarquia le mintio.

Eso es una violacion de Liskov: un subtipo que no puede sustituir al padre
sin cambiar el comportamiento observable del sistema.

---

## La Solucion

```
ANTES:  Bird con fly() abstracto — Penguin hereda y explota al cumplir el contrato
DESPUES: Bird sin fly() + FlyingBird con fly() — el compilador hace imposible el error
```

- `Bird`: solo lo que TODOS los pajaros pueden hacer (describe, getName).
- `FlyingBird extends Bird`: subtipo que agrega la capacidad de volar. Solo los
  pajaros que realmente vuelan la implementan.
- `Penguin extends Bird`: existe, nada, se describe. `fly()` no existe en su tipo.
  El compilador rechaza pasar un `Penguin` a `BirdFlightService`. El error desaparece
  en tiempo de compilacion, no en produccion a las 3am.

---

## Antes (viola LSP)

```java
// Bird.java — promete fly() para todos los subtipos
public abstract class Bird {
    public abstract void fly();
}

// Penguin.java — hereda Bird pero no puede cumplir el contrato
public class Penguin extends Bird {
    @Override
    public void fly() {
        // La herencia biologica no es herencia de comportamiento
        throw new UnsupportedOperationException(
            "Penguins cannot fly. Substituting Bird with Penguin breaks the client.");
    }
}

// BirdFlightService.java — cliente correcto que explota por culpa de la jerarquia
public void flyAll(List<Bird> birds) {
    for (Bird bird : birds) {
        bird.fly(); // <- UnsupportedOperationException si bird es Penguin
    }
}
```

## Despues (cumple LSP)

```java
// Bird.java — solo lo que TODO pajaro puede hacer
public abstract class Bird {
    public String describe() { return "Bird: " + name; }
    // sin fly() — no todos los pajaros vuelan
}

// FlyingBird.java — subtipo que SÍ promete volar
public abstract class FlyingBird extends Bird {
    public abstract void fly(); // contrato cumplible por todos sus subtipos
}

// Penguin.java — Bird, pero no FlyingBird. La verdad esta en el tipo.
public class Penguin extends Bird {
    public void swim() { ... } // lo que realmente puede hacer
    // fly() no existe — el compilador es la red de seguridad
}

// BirdFlightService.java — pide exactamente lo que necesita
public void flyAll(List<FlyingBird> birds) {
    for (FlyingBird bird : birds) {
        bird.fly(); // garantia: nunca lanza UnsupportedOperationException
    }
}
```

---

## Estructura

```
src/main/java/com/architecturedays/day017/
  antes/    → Bird abstracto con fly(), Penguin que explota, BirdFlightService sobre List<Bird>
  despues/  → Bird sin fly(), FlyingBird, Penguin como Bird puro, BirdFlightService sobre List<FlyingBird>

src/test/java/com/architecturedays/day017/
  LiskovViolationTest.java  → documenta que Penguin explota en runtime en la jerarquia "antes"
  LiskovComplianceTest.java → verifica que cualquier FlyingBird sustituye sin sorpresas en "despues"
```

---

## Concepto Clave

**Liskov Substitution Principle:** si S es subtipo de T, cualquier objeto de tipo T
puede ser reemplazado por un objeto de tipo S sin alterar el comportamiento correcto del programa.

En criollo: si tu cliente tiene un `Bird` y lo reemplazas por un `Penguin`,
el programa tiene que seguir funcionando igual. Si no, la herencia miente.

La violacion clasica es usar herencia para modelar relaciones "es-un" biologicas
o taxonomicas cuando lo que el sistema necesita es una relacion de comportamiento.
Un pinguino ES un pajaro en la naturaleza. En el modelo de vuelo, no lo es.

La solucion no es agregar un `instanceof` o un `try/catch`. Es redisenar la jerarquia
para que el sistema de tipos refleje la realidad del dominio, no la de un libro de biologia.
Cuando el tipo dice `FlyingBird`, el compilador garantiza que nadie va a meter un pinguino.
