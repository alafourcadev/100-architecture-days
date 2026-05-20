package com.architecturedays.day017.despues;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio cliente que opera sobre FlyingBird, no sobre Bird generico.
 *
 * El cliente pide exactamente lo que necesita. El compilador garantiza
 * que nadie puede pasar un Penguin aqui — ni por accidente.
 * No hay instanceof, no hay try/catch, no hay sorpresas en runtime.
 *
 * LSP se cumple: cualquier FlyingBird en la lista sustituye a cualquier otro
 * sin cambiar el comportamiento observable del servicio.
 */
@Service
public class BirdFlightService {

    /**
     * Hace volar a todos los pajaros del santuario.
     * Garantia: nunca lanza UnsupportedOperationException.
     * El tipo lo dice todo.
     */
    public void flyAll(List<FlyingBird> birds) {
        for (FlyingBird bird : birds) {
            bird.fly();
        }
    }
}
