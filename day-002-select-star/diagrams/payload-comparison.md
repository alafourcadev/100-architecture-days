# Comparacion de Payload - Dia 002

## SELECT * vs Proyeccion

```mermaid
flowchart LR
    subgraph BEFORE["SELECT *"]
        A[Request] --> B[Query: SELECT *]
        B --> C[User Entity]
        C --> D["id, email, name,<br/>photoBase64 (500KB),<br/>bio"]
        D --> E["Response: ~25MB<br/>(50 users x 500KB)"]
    end

    subgraph AFTER["Proyeccion"]
        F[Request] --> G["Query: SELECT id,<br/>email, name"]
        G --> H[UserSummaryDTO]
        H --> I["id, email, name"]
        I --> J["Response: ~5KB<br/>(50 users x 100 bytes)"]
    end
```

## Comparacion de tamanio

```mermaid
pie title Tamanio del Payload (50 usuarios)
    "SELECT * (25MB)" : 25000
    "Proyeccion (5KB)" : 5
```

## Metricas

| Metrica | SELECT * | Proyeccion | Mejora |
|---------|----------|------------|--------|
| Payload | 25 MB | 5 KB | 99.98% |
| Tiempo BD | ~500ms | ~50ms | 90% |
| Memoria | Alta | Baja | - |
