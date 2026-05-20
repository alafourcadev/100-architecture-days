package com.architecturedays.day017.despues;

/**
 * Aguila — FlyingBird concreto. Cumple el contrato de vuelo.
 */
public class Eagle extends FlyingBird {

    public Eagle() {
        super("Eagle");
    }

    @Override
    public void fly() {
        System.out.println("[" + getName() + "] Soaring at 120 km/h");
    }
}
