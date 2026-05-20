package com.architecturedays.day017.antes;

/**
 * Abstraccion base de un pajaro.
 *
 * El contrato implicito que cualquier cliente asume al recibir un Bird:
 * puede llamar a fly() y el pajaro volara.
 *
 * PROBLEMA: ese contrato no lo pueden cumplir todos los subtipos.
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
     * Hace volar al pajaro.
     * El cliente asume que este metodo siempre funciona para cualquier Bird.
     */
    public abstract void fly();
}
