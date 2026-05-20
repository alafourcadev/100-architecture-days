package com.architecturedays.day017.despues;

/**
 * Pinguino — Bird concreto, pero NO FlyingBird.
 *
 * El sistema de tipos ahora refleja la realidad del dominio:
 * un Penguin es un Bird (tiene nombre, puede describirse), pero no puede volar,
 * y el compilador lo hace explicito. No hay excepcion en runtime posible.
 *
 * El cliente que necesita pajaros voladores pide List<FlyingBird>.
 * El cliente que solo necesita describir pajaros pide List<Bird>.
 * Cada uno obtiene exactamente las garantias que necesita.
 */
public class Penguin extends Bird {

    public Penguin() {
        super("Penguin");
    }

    public void swim() {
        System.out.println("[" + getName() + "] Swimming at 25 km/h");
    }
}
