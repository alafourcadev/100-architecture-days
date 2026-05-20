package com.architecturedays.day017.despues;

/**
 * Gorrion — FlyingBird concreto. Cumple el contrato de vuelo.
 */
public class Sparrow extends FlyingBird {

    public Sparrow() {
        super("Sparrow");
    }

    @Override
    public void fly() {
        System.out.println("[" + getName() + "] Flying at 45 km/h");
    }
}
