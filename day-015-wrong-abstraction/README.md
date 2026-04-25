# Dia 015: La abstraccion equivocada cuesta mas que la duplicacion.

> **#100ArchitectureDays** | Spring Boot 4 + Java 21

---

## El Problema

Tres flujos parecidos (crear User, Company, Partner) se "DRY-aron" en
un unico `EntityCreationService.createEntity(...)` con generics, flags
booleanos, switch por `entityType` y un `Map<String, Object>` para "lo demas".

Cada cambio en cualquiera de los flujos hace crecer el monstruo.

---

## La Solucion

```
ANTES:  1 metodo generico con switch interno y 7 parametros
DESPUES: 2 servicios independientes, cada uno con su request especifico
```

- `UserService.createUser(CreateUserRequest)`
- `CompanyService.createCompany(CreateCompanyRequest)`

Codigo "duplicado"? Si: ambos llaman `repository.save(...)` y mandan email.
Pero ya no son la misma cosa. Cuando uno cambie, el otro no se entera.

---

## La Regla del Tres

No abstraigas en la segunda repeticion. Espera la tercera. Y mira si
las tres cambian *por la misma razon*. Si no, no es duplicacion.
Es coincidencia.

> "Duplication is far cheaper than the wrong abstraction."
> — Sandi Metz

---

## Concepto Clave

DRY no es sobre lineas de codigo. Es sobre razones para cambiar.
Dos lineas iguales que cambian por motivos distintos no son duplicacion.
Son dos cosas que se parecen hoy.
