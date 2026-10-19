package com.graphics;

public class clase6 extends clase5 {
    protected static final float TIEMPO_DESGASTE = 7;
    protected static final float VELOCIDAD_AVERIADA = 5;
    protected static final float TALLER_X = centro(4);
    protected static final float TALLER_Z = centro(6);
    protected float desgaste = 0;
    protected boolean motorAveriado = false;
    protected boolean motorReparado = false;
    private float reloj = 0;

    @Override
    protected void reiniciar() {
        super.reiniciar();
        desgaste = 0;
        motorAveriado = false;
        motorReparado = false;
    }

    @Override
    protected void actualizar(float deltaTime) {
        super.actualizar(deltaTime);
        actualizarDesgaste(deltaTime);
    }

    protected void actualizarDesgaste(float deltaTime) {
        reloj += deltaTime;

        if (Math.abs(velocidad) > 0.5f && !motorReparado) {
            desgaste = Math.min(1, desgaste + deltaTime / TIEMPO_DESGASTE);
        }

        if (desgaste >= 1) {
            motorAveriado = true;
        }

        if (motorAveriado) {
            velocidad = Math.max(-VELOCIDAD_AVERIADA / 2, Math.min(VELOCIDAD_AVERIADA, velocidad));
        }

        if (motorAveriado && estaEnTaller() && Math.abs(velocidad) < 1) {
            desgaste = 0;
            motorAveriado = false;
            motorReparado = true;
        }
    }

    protected boolean estaEnTaller() {
        float distanciaX = autoX - TALLER_X;
        float distanciaZ = autoZ - TALLER_Z;
        return distanciaX * distanciaX + distanciaZ * distanciaZ < 3 * 3;
    }

    @Override
    protected String estadoExtra() {
        String mensaje = super.estadoExtra();

        if (motorAveriado) {
            mensaje += " | PROBLEMA EN EL MOTOR: frena en el TALLER";
        } else if (motorReparado) {
            mensaje += " | Motor reparado";
        } else {
            mensaje += " | Desgaste " + Math.round(desgaste * 100) + "%";
        }

        return mensaje;
    }

    @Override
    protected void escena() {
        super.escena();
        dibujarTaller();

        if (motorAveriado && !vistaMapa) {
            boolean parpadeo = (int) (reloj * 3) % 2 == 0;

            if (parpadeo) {
                dibujarTexto("MOTOR", autoX, 3.2f, autoZ, 1, 0.15f, 0.1f);
            }
        }
    }

    private void dibujarTaller() {
        entero("uEmision", 1);
        caja(TALLER_X, 0.06f, TALLER_Z, 5, 0.08f, 5, 1, 0.72f, 0.12f);
        entero("uEmision", 0);

        if (!vistaMapa) {
            float alturaLetrero = 4 + (float) Math.sin(reloj * 2) * 0.3f;
            dibujarTexto("TALLER", TALLER_X, alturaLetrero, TALLER_Z, 1, 0.8f, 0.15f);
        }
    }

    protected void dibujarTexto(String texto, float x, float y, float z, float r, float g, float b) {
        float pixel = 0.25f;
        float giro = anguloHaciaCamara(x, z);
        float coseno = (float) Math.cos(giro);
        float seno = (float) Math.sin(giro);
        int columnas = texto.length() * 4 - 1;
        float inicio = -(columnas - 1) * pixel / 2;
        float anchoPanel = columnas * pixel + 0.5f;
        float altoPanel = 5 * pixel + 0.5f;
        float panelX = x - seno * 0.12f;
        float panelZ = z - coseno * 0.12f;
        cajaGirada(panelX, y + 2 * pixel, panelZ, anchoPanel, altoPanel, 0.08f, 0.08f, 0.10f, 0.14f, giro);
        entero("uEmision", 1);

        for (int letra = 0; letra < texto.length(); letra++) {
            String[] filas = filasLetra(texto.charAt(letra));

            for (int fila = 0; fila < 5; fila++) {
                for (int columna = 0; columna < 3; columna++) {
                    if (filas[fila].charAt(columna) == '0') {
                        continue;
                    }

                    float localX = inicio + (letra * 4 + columna) * pixel;
                    float mundoX = x + coseno * localX;
                    float mundoZ = z - seno * localX;
                    float alturaPixel = y + (4 - fila) * pixel;
                    cajaGirada(mundoX, alturaPixel, mundoZ, pixel, pixel, 0.1f, r, g, b, giro);
                }
            }
        }

        entero("uEmision", 0);
    }

    private float anguloHaciaCamara(float x, float z) {
        float anguloCamara = angulo + giroCamara;
        float camaraX = autoX + (float) Math.sin(anguloCamara) * 12;
        float camaraZ = autoZ + (float) Math.cos(anguloCamara) * 12;

        if (camaraAerea) {
            camaraX = (float) Math.sin(orbita) * LIMITE * 1.86f;
            camaraZ = (float) Math.cos(orbita) * LIMITE * 1.86f;
        }

        return (float) Math.atan2(camaraX - x, camaraZ - z);
    }

    private String[] filasLetra(char letra) {
        switch (letra) {
            case 'T':
                return new String[] {"111", "010", "010", "010", "010"};
            case 'A':
                return new String[] {"010", "101", "111", "101", "101"};
            case 'L':
                return new String[] {"100", "100", "100", "100", "111"};
            case 'E':
                return new String[] {"111", "100", "110", "100", "111"};
            case 'R':
                return new String[] {"110", "101", "110", "101", "101"};
            case 'M':
                return new String[] {"101", "111", "111", "101", "101"};
            case 'O':
                return new String[] {"111", "101", "101", "101", "111"};
            default:
                return new String[] {"000", "000", "000", "000", "000"};
        }
    }

    public static void main(String[] args) {
        clase6 aplicacion = new clase6();
        aplicacion.run();
    }
}
