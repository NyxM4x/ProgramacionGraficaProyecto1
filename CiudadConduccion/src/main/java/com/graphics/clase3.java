package com.graphics;

import static org.lwjgl.glfw.GLFW.*;

public class clase3 extends clase2 {
    protected boolean noche = true;
    protected boolean faros = true;
    protected static final float[][] LUCES = {
        {-44, 4.5f, -44},
        {-4, 4.5f, -44},
        {36, 4.5f, -44},
        {-44, 4.5f, -4},
        {-4, 4.5f, -4},
        {36, 4.5f, -4},
        {-44, 4.5f, 36},
        {-4, 4.5f, 36},
        {36, 4.5f, 36}
    };

    @Override
    protected void tecla(int key) {
        super.tecla(key);
        if (key == GLFW_KEY_N) {
            noche = !noche;
        }
        if (key == GLFW_KEY_F) {
            faros = !faros;
        }
    }

    @Override
    protected String estadoExtra() {
        String ambiente = "dia";
        if (noche) {
            ambiente = "noche";
        }
        String estadoFaros = "OFF";
        if (faros) {
            estadoFaros = "ON";
        }
        return " | N: " + ambiente + " | F: faros " + estadoFaros;
    }

    @Override
    protected void prepararLuces() {
        entero("uNoche", 0);
        if (noche) {
            entero("uNoche", 1);
        }
        entero("uFaros", 0);
        if (faros) {
            entero("uFaros", 1);
        }
        entero("uEmision", 0);

        for (int indice = 0; indice < LUCES.length; indice++) {
            float x = LUCES[indice][0];
            float y = LUCES[indice][1];
            float z = LUCES[indice][2];
            vector("uLuces[" + indice + "]", x, y, z);
        }

        float frenteX = -(float) Math.sin(angulo);
        float frenteZ = -(float) Math.cos(angulo);
        vector("uAuto", autoX, 0.7f, autoZ);
        vector("uFrente", frenteX, 0, frenteZ);
    }

    @Override
    protected void escena() {
        super.escena();
        for (float[] luz : LUCES) {
            float x = luz[0];
            float y = luz[1];
            float z = luz[2];
            caja(x, 2.2f, z, 0.18f, 4.4f, 0.18f, 0.20f, 0.24f, 0.28f);
            if (noche) {
                entero("uEmision", 1);
            }
            caja(x, y, z, 0.7f, 0.35f, 0.7f, 1, 0.83f, 0.42f);
            entero("uEmision", 0);
        }
    }

    @Override
    protected String fragmentShader() {
        return """
            #version 330 core
            in vec3 vMundo;
            in vec3 vNormal;
            uniform vec3 uColor;
            uniform vec3 uLuces[9];
            uniform vec3 uAuto;
            uniform vec3 uFrente;
            uniform int uNoche;
            uniform int uFaros;
            uniform int uEmision;
            uniform int uMapa;
            out vec4 color;

            void main() {
                if (uEmision == 1 || uMapa == 1) {
                    color = vec4(uColor, 1.0);
                    return;
                }

                vec3 normal = normalize(vNormal);
                vec3 luz = vec3(0.48);
                float intensidadSol = 0.65;
                if (uNoche == 1) {
                    luz = vec3(0.12, 0.16, 0.24);
                    intensidadSol = 0.10;
                }
                vec3 direccionSol = normalize(vec3(0.4, 1.0, 0.3));
                float incidenciaSol = max(dot(normal, direccionSol), 0.0);
                luz += vec3(intensidadSol) * incidenciaSol;

                if (uNoche == 1) {
                    for (int indice = 0; indice < 9; indice++) {
                        vec3 haciaLuz = uLuces[indice] - vMundo;
                        float distancia = length(haciaLuz);
                        float difusa = max(dot(normal, normalize(haciaLuz)), 0.0);
                        float atenuacion = 1.0 + 0.12 * distancia + 0.045 * distancia * distancia;
                        vec3 colorFarola = vec3(1.0, 0.73, 0.34);
                        luz += colorFarola * difusa * 3.0 / atenuacion;
                    }
                }

                if (uFaros == 1) {
                    for (int indice = 0; indice < 2; indice++) {
                        vec3 lateral = vec3(-uFrente.z, 0.0, uFrente.x);
                        float separacion = -0.55;
                        if (indice == 1) {
                            separacion = 0.55;
                        }
                        vec3 origen = uAuto + uFrente * 1.36 + lateral * separacion;
                        vec3 haciaSuperficie = vMundo - origen;
                        float distancia = length(haciaSuperficie);
                        vec3 eje = normalize(uFrente + vec3(0.0, -0.10, 0.0));
                        float alineacion = dot(normalize(haciaSuperficie), eje);
                        float cono = smoothstep(0.85, 0.97, alineacion);
                        float difusa = max(dot(normal, -normalize(haciaSuperficie)), 0.0);
                        float atenuacion = 1.0 + 0.04 * distancia * distancia;
                        vec3 colorFaro = vec3(1.0, 0.94, 0.72);
                        luz += colorFaro * cono * difusa * 8.0 / atenuacion;
                    }
                }

                color = vec4(uColor * luz, 1.0);
            }
            """;
    }

    public static void main(String[] args) {
        clase3 aplicacion = new clase3();
        aplicacion.run();
    }
}
