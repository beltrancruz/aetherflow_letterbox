package com.kiwisaki;

import net.fabricmc.api.ClientModInitializer;


public class LetterboxModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Toda la lógica de las barras se ha movido al Mixin.
        // Si no tienes otra inicialización de cliente aquí, esta clase puede estar casi vacía.
    }
}