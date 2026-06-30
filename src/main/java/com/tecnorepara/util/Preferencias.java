package com.tecnorepara.util;

import java.util.prefs.Preferences;

public class Preferencias {

    private static final Preferences prefs =
            Preferences.userRoot().node("TecnoRepara");

    public static void guardarTemaOscuro(boolean oscuro) {
        prefs.putBoolean("temaOscuro", oscuro);
    }

    public static boolean temaOscuro() {
        return prefs.getBoolean("temaOscuro", false);
    }
}