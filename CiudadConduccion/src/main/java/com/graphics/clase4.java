package com.graphics;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class clase4 extends clase3 {
    private boolean mostrarMapa = true;
    private int entregas = 0;
    private float tiempo = 0;
    private static final float[][] DESTINOS = {
        {centro(MAPA.length - 1), centro(0)},
        {centro(0), centro(0)},
        {centro(0), centro(MAPA.length - 1)}
    };

    @Override
    protected void tecla(int key) {
        super.tecla(key);
        if (key == GLFW_KEY_M) {
            mostrarMapa = !mostrarMapa;
        }
    }

    @Override
    protected void reiniciar() {
        super.reiniciar();
        entregas = 0;
        tiempo = 0;
    }

    @Override
    protected void actualizar(float deltaTime) {
        super.actualizar(deltaTime);
        if (entregas >= DESTINOS.length) {
            return;
        }
        tiempo += deltaTime;
        float distanciaX = autoX - DESTINOS[entregas][0];
        float distanciaZ = autoZ - DESTINOS[entregas][1];
        float distanciaCuadrada = distanciaX * distanciaX + distanciaZ * distanciaZ;
        boolean estaCerca = distanciaCuadrada < 3 * 3;
        boolean estaFrenando = Math.abs(velocidad) < 1;
        if (estaCerca && estaFrenando) {
            entregas++;
        }
    }

    @Override
    protected String estadoExtra() {
        String mensaje = super.estadoExtra();
        mensaje += " | M: mapa | ";
        if (entregas == DESTINOS.length) {
            mensaje += "GANASTE en " + (int) tiempo + " s! R: jugar otra vez";
        } else {
            mensaje += "Entregas " + entregas + "/" + DESTINOS.length;
            mensaje += " | Frena en la marca dorada | " + (int) tiempo + " s";
        }
        return mensaje;
    }

    @Override
    protected void escena() {
        super.escena();
        if (!vistaMapa) {
            decorarCiudad();
        }
        if (entregas < DESTINOS.length) {
            dibujarDestino();
        }
    }

    private void dibujarDestino() {
        float x = DESTINOS[entregas][0];
        float z = DESTINOS[entregas][1];
        entero("uEmision", 1);
        caja(x, 0.06f, z, 5, 0.08f, 5, 1, 0.72f, 0.12f);
        if (!vistaMapa) {
            float alturaBaliza = 3.5f + (float) Math.sin(tiempo * 2) * 0.3f;
            cajaGirada(x, alturaBaliza, z, 0.8f, 0.8f, 0.8f, 1, 0.8f, 0.15f, tiempo);
        }
        entero("uEmision", 0);
    }

    private void decorarCiudad() {
        for (int fila = 0; fila < MAPA.length; fila++) {
            for (int columna = 0; columna < MAPA[fila].length; columna++) {
                float x = centro(columna);
                float z = centro(fila);
                int tipo = MAPA[fila][columna];
                if (tipo == 2) {
                    dibujarParque(x, z);
                }
                if (tipo == 1) {
                    float altura = 5 + (fila * 3 + columna * 7) % 9;
                    dibujarVentanas(x, z, altura);
                }
                if (tipo != 0) {
                    dibujarPasoPeatonal(x, z);
                    dibujarSemaforo(x + 4, z - 4);
                }
            }
        }
    }

    private void dibujarParque(float x, float z) {
        float[] posiciones = {-2.5f, 2.5f};
        for (float desplazamientoX : posiciones) {
            for (float desplazamientoZ : posiciones) {
                float arbolX = x + desplazamientoX;
                float arbolZ = z + desplazamientoZ;
                caja(arbolX, 1.2f, arbolZ, 0.35f, 2, 0.35f, 0.38f, 0.22f, 0.12f);
                caja(arbolX, 2.7f, arbolZ, 2, 2.3f, 2, 0.12f, 0.42f, 0.23f);
            }
        }
        caja(x, 0.65f, z, 3, 0.25f, 0.8f, 0.55f, 0.30f, 0.13f);
        caja(x, 0.4f, z, 2, 0.6f, 0.35f, 0.22f, 0.24f, 0.24f);
        caja(x, 1, z + 0.35f, 3, 0.7f, 0.15f, 0.55f, 0.30f, 0.13f);
    }

    private void dibujarVentanas(float x, float z, float altura) {
        if (noche) {
            entero("uEmision", 1);
        }
        for (float y = 1.7f; y < altura; y += 2) {
            for (float desplazamiento = -2; desplazamiento <= 2; desplazamiento += 2) {
                caja(x + desplazamiento, y, z - 3.51f, 0.8f, 0.9f, 0.04f, 0.95f, 0.75f, 0.38f);
                caja(x + desplazamiento, y, z + 3.51f, 0.8f, 0.9f, 0.04f, 0.95f, 0.75f, 0.38f);
                caja(x - 3.51f, y, z + desplazamiento, 0.04f, 0.9f, 0.8f, 0.95f, 0.75f, 0.38f);
                caja(x + 3.51f, y, z + desplazamiento, 0.04f, 0.9f, 0.8f, 0.95f, 0.75f, 0.38f);
            }
        }
        entero("uEmision", 0);
    }

    private void dibujarPasoPeatonal(float x, float z) {
        for (int desplazamiento = -4; desplazamiento <= 4; desplazamiento++) {
            caja(x, 0.045f, z - 10 + desplazamiento, 2, 0.04f, 0.45f, 0.85f, 0.87f, 0.83f);
        }
    }

    private void dibujarSemaforo(float x, float z) {
        caja(x, 1.7f, z, 0.18f, 2.8f, 0.18f, 0.18f, 0.20f, 0.22f);
        caja(x, 3.1f, z, 0.55f, 1.2f, 0.45f, 0.08f, 0.10f, 0.12f);
        int fase = (int) (tiempo % 12);
        for (int indice = 0; indice < 3; indice++) {
            boolean encendida = false;
            if (indice == 0) {
                encendida = fase < 5;
            } else if (indice == 1) {
                encendida = fase >= 10;
            } else {
                encendida = fase >= 5 && fase < 10;
            }
            float brillo = 0.15f;
            entero("uEmision", 0);
            if (encendida) {
                brillo = 1;
                entero("uEmision", 1);
            }
            float rojo = 0;
            float verde = 0;
            if (indice < 2) {
                rojo = brillo;
            }
            if (indice > 0) {
                verde = brillo;
            }
            float altura = 3.45f - indice * 0.35f;
            caja(x, altura, z - 0.24f, 0.28f, 0.25f, 0.06f, rojo, verde, 0.02f);
        }
        entero("uEmision", 0);
    }

    @Override
    protected void dibujarFrame() {
        super.dibujarFrame();
        if (!mostrarMapa) {
            return;
        }
        int dimensionMenor = Math.min(ancho, alto);
        int lado = Math.min(260, dimensionMenor / 3);
        int margen = Math.min(18, dimensionMenor / 20);
        int x = ancho - lado - margen;
        int y = alto - lado - margen;
        glEnable(GL_SCISSOR_TEST);
        glScissor(x - 3, y - 3, lado + 6, lado + 6);
        glClearColor(0.8f, 0.87f, 0.94f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glScissor(x, y, lado, lado);
        glClearColor(0.06f, 0.10f, 0.15f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(x, y, lado, lado);
        entero("uMapa", 1);
        vistaMapa = true;
        try {
            escena();
            dibujarIndicadorAuto();
        } finally {
            vistaMapa = false;
            entero("uMapa", 0);
            glDisable(GL_SCISSOR_TEST);
            glViewport(0, 0, ancho, alto);
        }
        if (glGetError() != GL_NO_ERROR) {
            throw new IllegalStateException("Error OpenGL en minimapa");
        }
    }

    private void dibujarIndicadorAuto() {
        cajaGirada(autoX, 25, autoZ, 2.2f, 0.1f, 3.2f, 0.1f, 1, 1, angulo);
        float frenteX = -(float) Math.sin(angulo);
        float frenteZ = -(float) Math.cos(angulo);
        float puntaX = autoX + frenteX * 2;
        float puntaZ = autoZ + frenteZ * 2;
        caja(puntaX, 26, puntaZ, 0.9f, 0.1f, 0.9f, 1, 1, 1);
    }

    public static void main(String[] args) {
        clase4 aplicacion = new clase4();
        aplicacion.run();
    }
}
