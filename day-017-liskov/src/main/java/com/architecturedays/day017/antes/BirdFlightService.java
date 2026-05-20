package com.architecturedays.day017.antes;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio cliente que opera sobre una lista de Bird.
 *
 * El cliente no sabe (ni deberia saber) que subtipo concreto recibe.
 * Asume que cualquier Bird puede volar — y tiene razon de asumirlo:
 * el contrato de Bird lo dice.
 *
 * Cuando la lista incluye un Penguin, explota en runtime con
 * UnsupportedOperationException. El cliente es correcto; la jerarquia es la que miente.
 */
@Service
public class BirdFlightService {

    /**
     * Hace volar a todos los pajaros del santuario.
     *
     * @throws UnsupportedOperationException si algun Bird en la lista no puede volar
     *                                        (violacion de LSP — el cliente no puede saberlo)
     */
    public void flyAll(List<Bird> birds) {
        for (Bird bird : birds) {
            // El cliente llama al contrato publicado por Bird.
            // Si el subtipo no lo cumple, el runtime explota aqui.
            bird.fly();
        }
    }
}
