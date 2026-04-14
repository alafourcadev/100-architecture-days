# Dia 012: 5.000 personas hicieron click al mismo tiempo. Tu servidor pidio perdon.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Tu tienda sale a internet. 5.000 personas esperando. Todos le dan click a "Comprar" al mismo tiempo.
Tu servidor tiene 20 threads. Se saturan. Todo se cae. Incluido el login.

---

## La Solucion

```
ANTES:  Sin limites → el que llega primero se lleva todo, los demas sufren
DESPUES: Token Bucket → cada cliente tiene un limite, el sistema se mantiene estable
```

**Concepto:** Rate Limiting & Token Bucket Algorithm
**Regla:** Una API sin rate limiting es una ruta sin limite de velocidad.

---

## Ejecutar

```bash
# BEFORE — sin proteccion (todo pasa)
mvn spring-boot:run -Dspring-boot.run.profiles=before

# Simular rafaga de 50 requests
for i in {1..50}; do curl -s -o /dev/null -w "%{http_code}\n" \
  -X POST "http://localhost:8080/api/tienda/comprar/1?cliente=user$i"; done
# → 50x "200" — todas pasan, el servidor se arrastra

# AFTER — con rate limiting (Token Bucket)
mvn spring-boot:run -Dspring-boot.run.profiles=after

# Misma rafaga
for i in {1..50}; do curl -s -o /dev/null -w "%{http_code}\n" \
  -X POST "http://localhost:8080/api/tienda/comprar/1?cliente=test"; done
# → 10x "200" + 40x "429 Too Many Requests" — controlado

# Ver estadisticas
curl http://localhost:8080/api/tienda/estadisticas
```

---

## Concepto Clave

Rate limiting no es hostilidad hacia tus clientes.
Es proteger la experiencia de TODOS tus clientes.
Incluyendo los que no estan haciendo nada mal.
