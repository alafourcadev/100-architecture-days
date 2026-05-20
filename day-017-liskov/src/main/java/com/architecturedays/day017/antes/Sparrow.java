package com.architecturedays.day017.antes;

/**
 * Gorrion — cumple el contrato de Bird sin problema.
 */
public class Sparrow extends Bird {

    public Sparrow() {
        super("Sparrow");
    }

    @Override
    public void fly() {
        System.out.println("[" + getName() + "] Flying at 45 km/h");
    }
}
