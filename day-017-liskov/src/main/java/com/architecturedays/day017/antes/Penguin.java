package com.architecturedays.day017.antes;

/**
 * Pinguino — es un pajaro biologicamente, pero no puede volar.
 *
 * VIOLACION DE LSP: sobreescribe fly() lanzando una excepcion en lugar
 * de cumplir el contrato del padre. Cualquier cliente que recibe un Bird
 * y llama a fly() explota en runtime si ese Bird es un Penguin.
 *
 * La herencia aqui miente: "es un pajaro" es verdad biologica,
 * pero NO es verdad en el modelo de comportamiento del sistema.
 */
public class Penguin extends Bird {

    public Penguin() {
        super("Penguin");
    }

    @Override
    public void fly() {
        // Rompe el contrato: el cliente esperaba volar, no una excepcion.
        // Ningun test del cliente puede protegerse de esto sin instanceof — que es otra violacion.
        throw new UnsupportedOperationException(
                "Penguins cannot fly. Substituting Bird with Penguin breaks the client.");
    }
}
