package com.graphics;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class clase1 {
    protected long ventana;
    protected int programa;
    protected int vao;
    protected int vbo;
    protected int ancho = 1100;
    protected int alto = 760;
    protected float orbita = 0.6f;
    protected boolean vistaMapa = false;
    protected static final float CELDA = 10;
    private final Map<String, Integer> uniforms = new HashMap<>();

    protected static final int[][] MAPA = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 2, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 2, 0, 1, 0, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 2, 0, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 1, 0, 2, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    protected static final float LIMITE = MAPA.length * CELDA / 2;

    public void run() {
        GLFWErrorCallback errores = GLFWErrorCallback.createPrint(System.err);
        errores.set();
        try {
            iniciar();
            loop();
        } finally {
            limpiar();
            glfwSetErrorCallback(null);
            errores.free();
        }
    }

    private void iniciar() {
        if (!glfwInit()) {
            throw new IllegalStateException("No se pudo iniciar GLFW");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        ventana = glfwCreateWindow(ancho, alto, "Clase 1 | Ciudad | Flechas: orbitar | ESC: salir", 0, 0);
        if (ventana == 0) {
            throw new IllegalStateException("No se pudo crear la ventana OpenGL");
        }
        glfwMakeContextCurrent(ventana);
        glfwSwapInterval(1);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glfwSetKeyCallback(ventana, (ventanaEvento, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                tecla(key);
            }
        });
        crearPrograma();
        crearCubo();
    }

    private void loop() {
        double tiempoAnterior = glfwGetTime();
        int cuadros = 0;
        int maxCuadros = Integer.getInteger("demo.frames", 0);
        int[] anchoReal = new int[1];
        int[] altoReal = new int[1];

        while (!glfwWindowShouldClose(ventana)) {
            glfwPollEvents();
            double tiempoActual = glfwGetTime();
            float deltaTime = (float) Math.min(tiempoActual - tiempoAnterior, 0.05);
            tiempoAnterior = tiempoActual;
            actualizar(deltaTime);
            glfwGetFramebufferSize(ventana, anchoReal, altoReal);
            ancho = anchoReal[0];
            alto = altoReal[0];
            if (ancho > 0 && alto > 0) {
                dibujarFrame();
            }
            glfwSwapBuffers(ventana);
            cuadros++;
            if (maxCuadros > 0 && cuadros >= maxCuadros) {
                glfwSetWindowShouldClose(ventana, true);
            }
        }
    }

    private void limpiar() {
        if (programa != 0) {
            glDeleteProgram(programa);
        }
        if (vbo != 0) {
            glDeleteBuffers(vbo);
        }
        if (vao != 0) {
            glDeleteVertexArrays(vao);
        }
        if (ventana != 0) {
            Callbacks.glfwFreeCallbacks(ventana);
            glfwDestroyWindow(ventana);
        }
        glfwTerminate();
    }

    protected void tecla(int key) {
        if (key == GLFW_KEY_ESCAPE) {
            glfwSetWindowShouldClose(ventana, true);
        }
    }

    protected boolean pulsada(int key) {
        return glfwGetKey(ventana, key) == GLFW_PRESS;
    }

    protected void actualizar(float deltaTime) {
        if (pulsada(GLFW_KEY_LEFT)) {
            orbita -= deltaTime;
        }
        if (pulsada(GLFW_KEY_RIGHT)) {
            orbita += deltaTime;
        }
    }

    protected void configurarCamara() {
        float radio = LIMITE * 1.86f;
        float alturaCamara = LIMITE * 1.57f;
        float camaraX = (float) Math.sin(orbita) * radio;
        float camaraZ = (float) Math.cos(orbita) * radio;
        vector("uOjo", camaraX, alturaCamara, camaraZ);
        vector("uObjetivo", 0, 0, 0);
        decimal("uAspecto", (float) ancho / alto);
    }

    protected void prepararLuces() {
    }

    protected static float centro(int indice) {
        return -LIMITE + CELDA * (indice + 0.5f);
    }

    protected void dibujarFrame() {
        glViewport(0, 0, ancho, alto);
        glClearColor(0.12f, 0.20f, 0.30f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram(programa);
        glBindVertexArray(vao);
        entero("uMapa", 0);
        decimal("uLimiteMapa", LIMITE + 2);
        configurarCamara();
        prepararLuces();
        escena();
        if (glGetError() != GL_NO_ERROR) {
            throw new IllegalStateException("Error OpenGL al dibujar");
        }
    }

    protected void escena() {
        float ladoSuelo = LIMITE * 2;
        caja(0, -0.25f, 0, ladoSuelo, 0.5f, ladoSuelo, 0.16f, 0.19f, 0.23f);
        for (int fila = 0; fila < MAPA.length; fila++) {
            for (int columna = 0; columna < MAPA[fila].length; columna++) {
                float x = centro(columna);
                float z = centro(fila);
                int tipo = MAPA[fila][columna];
                if (tipo == 0) {
                    dibujarMarcasCalle(fila, columna, x, z);
                } else {
                    caja(x, 0.15f, z, 10, 0.3f, 10, 0.60f, 0.64f, 0.66f);
                    if (tipo == 1) {
                        float altura = 5 + (fila * 3 + columna * 7) % 9;
                        float rojo = 0.28f + columna * 0.045f;
                        float azul = 0.48f + fila * 0.025f;
                        caja(x, altura / 2 + 0.3f, z, 7, altura, 7, rojo, 0.40f, azul);
                        caja(x, altura + 0.45f, z, 7.3f, 0.3f, 7.3f, 0.20f, 0.26f, 0.32f);
                    } else {
                        caja(x, 0.32f, z, 9, 0.1f, 9, 0.20f, 0.45f, 0.28f);
                    }
                }
            }
        }
    }

    private void dibujarMarcasCalle(int fila, int columna, float x, float z) {
        if (fila % 2 == 0 && columna % 2 == 1) {
            for (int desplazamiento = -3; desplazamiento <= 3; desplazamiento += 3) {
                caja(x + desplazamiento, 0.025f, z, 1.6f, 0.03f, 0.13f, 1, 0.84f, 0.35f);
            }
        }
        if (columna % 2 == 0 && fila % 2 == 1) {
            for (int desplazamiento = -3; desplazamiento <= 3; desplazamiento += 3) {
                caja(x, 0.025f, z + desplazamiento, 0.13f, 0.03f, 1.6f, 1, 0.84f, 0.35f);
            }
        }
    }

    protected void caja(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, 0);
    }

    protected void cajaGirada(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float angulo) {
        vector("uPos", x, y, z);
        vector("uEscala", sx, sy, sz);
        vector("uColor", r, g, b);
        decimal("uGiro", angulo);
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    protected int uniform(String nombre) {
        if (!uniforms.containsKey(nombre)) {
            int ubicacion = glGetUniformLocation(programa, nombre);
            uniforms.put(nombre, ubicacion);
        }
        return uniforms.get(nombre);
    }

    protected void vector(String nombre, float x, float y, float z) {
        glUniform3f(uniform(nombre), x, y, z);
    }

    protected void decimal(String nombre, float valor) {
        glUniform1f(uniform(nombre), valor);
    }

    protected void entero(String nombre, int valor) {
        glUniform1i(uniform(nombre), valor);
    }

    private String vertexShader() {
        return """
            #version 330 core
            layout (location = 0) in vec3 aPos;
            layout (location = 1) in vec3 aNormal;
            uniform vec3 uPos;
            uniform vec3 uEscala;
            uniform vec3 uOjo;
            uniform vec3 uObjetivo;
            uniform float uGiro;
            uniform float uAspecto;
            uniform int uMapa;
            uniform float uLimiteMapa;
            out vec3 vMundo;
            out vec3 vNormal;

            void main() {
                float coseno = cos(uGiro);
                float seno = sin(uGiro);
                mat3 giro = mat3(
                    coseno, 0.0, -seno,
                    0.0, 1.0, 0.0,
                    seno, 0.0, coseno
                );
                vMundo = giro * (aPos * uEscala) + uPos;
                vNormal = normalize(giro * (aNormal / uEscala));

                if (uMapa == 1) {
                    float pantallaX = vMundo.x / uLimiteMapa;
                    float pantallaY = -vMundo.z / uLimiteMapa;
                    float profundidad = -vMundo.y / 100.0;
                    gl_Position = vec4(pantallaX, pantallaY, profundidad, 1.0);
                } else {
                    vec3 frente = normalize(uObjetivo - uOjo);
                    vec3 derecha = normalize(cross(frente, vec3(0.0, 1.0, 0.0)));
                    vec3 arriba = cross(derecha, frente);
                    vec3 diferencia = vMundo - uOjo;
                    float vistaX = dot(diferencia, derecha);
                    float vistaY = dot(diferencia, arriba);
                    float vistaZ = -dot(diferencia, frente);
                    float factor = 1.0 / tan(radians(55.0) * 0.5);
                    float cerca = 0.1;
                    float lejos = 250.0;
                    float clipX = vistaX * factor / uAspecto;
                    float clipY = vistaY * factor;
                    float clipZ = (lejos + cerca) / (cerca - lejos) * vistaZ;
                    clipZ += 2.0 * lejos * cerca / (cerca - lejos);
                    gl_Position = vec4(clipX, clipY, clipZ, -vistaZ);
                }
            }
            """;
    }

    protected String fragmentShader() {
        return """
            #version 330 core
            uniform vec3 uColor;
            out vec4 color;
            void main() {
                color = vec4(uColor, 1.0);
            }
            """;
    }

    private int compilar(int tipo, String fuente) {
        int shader = glCreateShader(tipo);
        glShaderSource(shader, fuente);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            throw new IllegalStateException(error);
        }
        return shader;
    }

    private void crearPrograma() {
        int shaderVertices = compilar(GL_VERTEX_SHADER, vertexShader());
        int shaderFragmentos = 0;
        try {
            shaderFragmentos = compilar(GL_FRAGMENT_SHADER, fragmentShader());
            programa = glCreateProgram();
            glAttachShader(programa, shaderVertices);
            glAttachShader(programa, shaderFragmentos);
            glLinkProgram(programa);
            if (glGetProgrami(programa, GL_LINK_STATUS) == GL_FALSE) {
                throw new IllegalStateException(glGetProgramInfoLog(programa));
            }
        } finally {
            glDeleteShader(shaderVertices);
            if (shaderFragmentos != 0) {
                glDeleteShader(shaderFragmentos);
            }
        }
    }

    private void crearCubo() {
        float[] vertices = {
            -0.5f, -0.5f, 0.5f, 0, 0, 1,
            0.5f, -0.5f, 0.5f, 0, 0, 1,
            0.5f, 0.5f, 0.5f, 0, 0, 1,
            0.5f, 0.5f, 0.5f, 0, 0, 1,
            -0.5f, 0.5f, 0.5f, 0, 0, 1,
            -0.5f, -0.5f, 0.5f, 0, 0, 1,

            -0.5f, -0.5f, -0.5f, 0, 0, -1,
            -0.5f, 0.5f, -0.5f, 0, 0, -1,
            0.5f, 0.5f, -0.5f, 0, 0, -1,
            0.5f, 0.5f, -0.5f, 0, 0, -1,
            0.5f, -0.5f, -0.5f, 0, 0, -1,
            -0.5f, -0.5f, -0.5f, 0, 0, -1,

            -0.5f, 0.5f, 0.5f, -1, 0, 0,
            -0.5f, 0.5f, -0.5f, -1, 0, 0,
            -0.5f, -0.5f, -0.5f, -1, 0, 0,
            -0.5f, -0.5f, -0.5f, -1, 0, 0,
            -0.5f, -0.5f, 0.5f, -1, 0, 0,
            -0.5f, 0.5f, 0.5f, -1, 0, 0,

            0.5f, 0.5f, 0.5f, 1, 0, 0,
            0.5f, -0.5f, 0.5f, 1, 0, 0,
            0.5f, -0.5f, -0.5f, 1, 0, 0,
            0.5f, -0.5f, -0.5f, 1, 0, 0,
            0.5f, 0.5f, -0.5f, 1, 0, 0,
            0.5f, 0.5f, 0.5f, 1, 0, 0,

            -0.5f, 0.5f, -0.5f, 0, 1, 0,
            -0.5f, 0.5f, 0.5f, 0, 1, 0,
            0.5f, 0.5f, 0.5f, 0, 1, 0,
            0.5f, 0.5f, 0.5f, 0, 1, 0,
            0.5f, 0.5f, -0.5f, 0, 1, 0,
            -0.5f, 0.5f, -0.5f, 0, 1, 0,

            -0.5f, -0.5f, -0.5f, 0, -1, 0,
            0.5f, -0.5f, -0.5f, 0, -1, 0,
            0.5f, -0.5f, 0.5f, 0, -1, 0,
            0.5f, -0.5f, 0.5f, 0, -1, 0,
            -0.5f, -0.5f, 0.5f, 0, -1, 0,
            -0.5f, -0.5f, -0.5f, 0, -1, 0
        };
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        int bytesPorVertice = 6 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, bytesPorVertice, 0L);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, bytesPorVertice, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public static void main(String[] args) {
        clase1 aplicacion = new clase1();
        aplicacion.run();
    }
}
