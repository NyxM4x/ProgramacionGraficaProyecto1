package com.graphics;

import static org.lwjgl.glfw.GLFW.*;

public class clase2 extends clase1 {
    protected static final float INICIO_X = centro(0);
    protected static final float INICIO_Z = centro(MAPA.length - 1);
    protected float autoX = INICIO_X;
    protected float autoZ = INICIO_Z;
    protected float angulo = 0;
    protected float velocidad = 0;
    protected boolean camaraAerea = false;
    protected float giroCamara = 0;
    protected static final float RADIO_AUTO = 1.1f;

    @Override
    protected void tecla(int key) {
        super.tecla(key);

        if (key == GLFW_KEY_C) {
            camaraAerea = !camaraAerea;
        }

        if (key == GLFW_KEY_R) {
            reiniciar();
        }
    }

    protected void reiniciar() {
        autoX = INICIO_X;
        autoZ = INICIO_Z;
        angulo = 0;
        velocidad = 0;
        giroCamara = 0;
    }

    @Override
    protected void actualizar(float deltaTime) {
        float giroFlechas = 0;

        if (pulsada(GLFW_KEY_LEFT)) {
            giroFlechas -= 1;
        }

        if (pulsada(GLFW_KEY_RIGHT)) {
            giroFlechas += 1;
        }

        if (camaraAerea) {
            orbita += giroFlechas * deltaTime;
        } else {
            giroCamara += giroFlechas * 1.5f * deltaTime;
        }

        float acelerador = 0;

        if (pulsada(GLFW_KEY_W)) {
            acelerador += 1;
        }

        if (pulsada(GLFW_KEY_S)) {
            acelerador -= 1;
        }

        velocidad += acelerador * 9 * deltaTime;
        float resistencia = 0.7f;

        if (pulsada(GLFW_KEY_SPACE)) {
            resistencia = 7;
        }

        float factorFrenado = (float) Math.exp(-resistencia * deltaTime);
        velocidad *= factorFrenado;
        velocidad = Math.max(-6, Math.min(16, velocidad));
        float direccion = 0;

        if (pulsada(GLFW_KEY_A)) {
            direccion += 1;
        }

        if (pulsada(GLFW_KEY_D)) {
            direccion -= 1;
        }

        angulo += direccion * velocidad * 0.11f * deltaTime;
        float frenteX = -(float) Math.sin(angulo);
        float frenteZ = -(float) Math.cos(angulo);
        float siguienteX = autoX + frenteX * velocidad * deltaTime;
        float siguienteZ = autoZ + frenteZ * velocidad * deltaTime;

        if (puedeCircular(siguienteX, siguienteZ)) {
            autoX = siguienteX;
            autoZ = siguienteZ;
        } else {
            if (puedeCircular(siguienteX, autoZ)) {
                autoX = siguienteX;
            } else if (puedeCircular(autoX, siguienteZ)) {
                autoZ = siguienteZ;
            }
            velocidad *= (float) Math.exp(-7 * deltaTime);
        }

        actualizarTitulo();
    }

    private void actualizarTitulo() {
        int kilometrosPorHora = Math.round(Math.abs(velocidad) * 3.6f);
        String titulo = getClass().getSimpleName();
        titulo += " | WASD: conducir | Espacio: freno | Flechas: girar camara";
        titulo += " | C: camara | R: reiniciar | ";
        titulo += kilometrosPorHora + " km/h";
        titulo += estadoExtra();
        glfwSetWindowTitle(ventana, titulo);
    }

    protected String estadoExtra() {
        return "";
    }

    protected boolean puedeCircular(float x, float z) {
        return puedeCircular(x, z, RADIO_AUTO);
    }

    protected boolean puedeCircular(float x, float z, float radio) {
        float limitePermitido = LIMITE - radio;

        if (Math.abs(x) > limitePermitido || Math.abs(z) > limitePermitido) {
            return false;
        }

        for (int fila = 0; fila < MAPA.length; fila++) {
            for (int columna = 0; columna < MAPA[fila].length; columna++) {
                if (MAPA[fila][columna] == 0) {
                    continue;
                }

                float centroX = centro(columna);
                float centroZ = centro(fila);
                float cercaX = Math.max(centroX - 5, Math.min(x, centroX + 5));
                float cercaZ = Math.max(centroZ - 5, Math.min(z, centroZ + 5));
                float distanciaX = x - cercaX;
                float distanciaZ = z - cercaZ;
                float distanciaCuadrada = distanciaX * distanciaX + distanciaZ * distanciaZ;

                if (distanciaCuadrada < radio * radio) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void configurarCamara() {
        if (camaraAerea) {
            super.configurarCamara();
            return;
        }

        float anguloCamara = angulo + giroCamara;
        float camaraX = autoX + (float) Math.sin(anguloCamara) * 12;
        float camaraZ = autoZ + (float) Math.cos(anguloCamara) * 12;
        vector("uOjo", camaraX, 9, camaraZ);
        vector("uObjetivo", autoX, 0.8f, autoZ);
        decimal("uAspecto", (float) ancho / alto);
    }

    @Override
    protected void escena() {
        super.escena();
        dibujarAuto();
    }

    protected void dibujarAuto() {
        pieza(0, 0.65f, 0, 1.65f, 0.55f, 2.6f, 0.95f, 0.24f, 0.12f);
        pieza(0, 1.12f, 0.12f, 1.3f, 0.55f, 1.25f, 0.22f, 0.65f, 0.78f);
        float[] ladosRuedas = {-0.88f, 0.88f};
        float[] ejesRuedas = {-0.82f, 0.82f};

        for (float x : ladosRuedas) {
            for (float z : ejesRuedas) {
                pieza(x, 0.38f, z, 0.24f, 0.58f, 0.6f, 0.055f, 0.065f, 0.08f);
            }
        }

        float[] ladosFaros = {-0.55f, 0.55f};
        for (float x : ladosFaros) {
            pieza(x, 0.68f, -1.32f, 0.38f, 0.2f, 0.07f, 1, 0.95f, 0.65f);
            pieza(x, 0.68f, 1.32f, 0.35f, 0.17f, 0.07f, 0.85f, 0.05f, 0.05f);
        }
    }

    protected void pieza(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        float coseno = (float) Math.cos(angulo);
        float seno = (float) Math.sin(angulo);
        float mundoX = autoX + coseno * x + seno * z;
        float mundoZ = autoZ - seno * x + coseno * z;
        cajaGirada(mundoX, y, mundoZ, sx, sy, sz, r, g, b, angulo);
    }

    public static void main(String[] args) {
        clase2 aplicacion = new clase2();
        aplicacion.run();
    }
}
