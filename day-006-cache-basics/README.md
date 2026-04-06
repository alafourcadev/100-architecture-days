# Día 006: Mi caché no funciona (y es mi culpa)

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Pusiste `@Cacheable` en todo. La app sigue lenta. Y peor: ahora los usuarios ven precios viejos.

---

## La Solución

```
ANTES:  @Cacheable en TODO → datos stale, bugs silenciosos
DESPUÉS: @Cacheable SOLO en datos que cambian poco → datos frescos donde importa
```

**Concepto:** Caching Strategy & Cache Invalidation
**Regla:** Cacheá lo que se lee MUCHO y cambia POCO

---

## Las 4 preguntas antes de cachear

1. **¿Cada cuánto cambia?** → Si cambia cada minuto, NO cachees
2. **¿Cada cuánto se lee?** → Si se lee 1 vez al día, NO cachees
3. **¿Qué pasa si está viejo?** → Si el usuario ve un precio viejo y compra, tenés un problema legal
4. **¿Cómo lo invalido?** → Si no tenés respuesta, NO cachees

---

## Implementación

### ANTES — Cacheo todo sin pensar

```java
@Cacheable("products")
public List<Product> getAllProducts() { ... }

@Cacheable("productPrice")
public BigDecimal getProductPrice(Long id) { ... }

// Actualizo precio en BD pero el caché sigue con el viejo
public void updatePrice(Long id, BigDecimal newPrice) {
    product.setPrice(newPrice);
    repository.save(product);
    // ❌ El caché NO se enteró del cambio
}
```

### DESPUÉS — Cacheo solo lo que tiene sentido

```java
// ✅ Sin caché — precios cambian constantemente
public List<Product> getAllProducts() { ... }
public BigDecimal getProductPrice(Long id) { ... }

// ✅ Con caché — categorías cambian 1 vez al mes
@Cacheable("categories")
public List<Category> getAllCategories() { ... }
```

---

## Qué cachear vs qué NO

| Dato | Cambia | Se lee | ¿Cachear? |
|------|--------|--------|-----------|
| Categorías | 1x/mes | 1000x/día | ✅ SÍ |
| Configuraciones | 1x/semana | 500x/día | ✅ SÍ |
| Precios | cada minuto | 100x/día | ❌ NO |
| Stock | cada compra | 50x/día | ❌ NO |
| Sesión usuario | cada request | 1x | ❌ NO |

---

## Ejecutar

```bash
# Versión BEFORE (caché que miente)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# 1. Consultá productos: curl http://localhost:8080/api/products
# 2. Cambiá el precio: curl -X PUT "http://localhost:8080/api/products/1/price?price=999"
# 3. Consultá de nuevo: curl http://localhost:8080/api/products
# → El precio sigue siendo el viejo. El caché te mintió.

# Versión AFTER (caché inteligente)
mvn spring-boot:run -Dspring-boot.run.profiles=after
# → Mismo flujo, pero ahora el precio se actualiza correctamente
```

---

## Concepto Clave

El caché no es un switch que prendés y todo se acelera. Es una **decisión de arquitectura**. Cachear lo incorrecto es peor que no cachear nada — porque te da la ilusión de que todo funciona mientras los datos son basura.

Esto aplica a cualquier caché: Redis, Memcached, CDN, browser cache. La pregunta siempre es la misma: **¿qué pasa si este dato está viejo?**
