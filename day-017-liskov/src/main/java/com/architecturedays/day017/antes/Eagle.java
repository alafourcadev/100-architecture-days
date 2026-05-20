package com.architecturedays.day017.antes;

/**
 * Aguila — tambien cumple el contrato de Bird.
 */
public class Eagle extends Bird {

    public Eagle() {
        super("Eagle");
    }

    @Override
    public void fly() {
        System.out.println("[" + getName() + "] Soaring at 120 km/h");
    }
}
