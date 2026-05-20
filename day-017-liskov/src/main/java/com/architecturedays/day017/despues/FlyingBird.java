package com.architecturedays.day017.despues;

/**
 * Subtipo de Bird que modela pajaros con capacidad de vuelo.
 *
 * Solo los subtipos de esta clase prometen poder volar.
 * El cliente que necesita pajaros voladores pide FlyingBird, no Bird.
 * LSP se cumple: cualquier FlyingBird sustituye a otro FlyingBird sin sorpresas.
 */
public abstract class FlyingBird extends Bird {

    protected FlyingBird(String name) {
        super(name);
    }

    /**
     * Todo FlyingBird puede volar — el contrato es sustituible por definicion.
     * Ningun subtipo de FlyingBird puede romper esto sin dejar de ser un FlyingBird.
     */
    public abstract void fly();
}
