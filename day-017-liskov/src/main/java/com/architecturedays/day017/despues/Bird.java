package com.architecturedays.day017.despues;

/**
 * Abstraccion base de un pajaro.
 *
 * Contiene SOLO lo que TODO pajaro puede hacer sin excepcion.
 * fly() ya no esta aqui — no todos los pajaros vuelan.
 */
public abstract class Bird {

    private final String name;

    protected Bird(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Todo pajaro puede ser descrito. Eso SI es un contrato que cualquier
     * subtipo puede cumplir sin lanzar excepciones ni cambiar el comportamiento esperado.
     */
    public String describe() {
        return "Bird: " + name;
    }
}
