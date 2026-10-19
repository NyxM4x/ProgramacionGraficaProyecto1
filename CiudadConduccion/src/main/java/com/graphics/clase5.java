package com.graphics;

public class clase5 extends clase4 {
    protected static final float DISTANCIA_ENGANCHE = 1.55f;
    protected static final float LARGO_LANZA = 2.2f;
    protected static final float RADIO_CHATA = 1.0f;
    protected static final float GIRO_MAXIMO = 1.1f;
    protected float chataX = INICIO_X;
    protected float chataZ = INICIO_Z + DISTANCIA_ENGANCHE + LARGO_LANZA;
    protected float chataAngulo = 0;

    @Override
    protected void reiniciar() {
        super.reiniciar();
        chataX = INICIO_X;
        chataZ = INICIO_Z + DISTANCIA_ENGANCHE + LARGO_LANZA;
        chataAngulo = 0;
    }

    @Override
    protected void actualizar(float deltaTime) {
        float autoAnteriorX = autoX;
        float autoAnteriorZ = autoZ;
        float anguloAnterior = angulo;
        super.actualizar(deltaTime);

        if (!moverChata()) {
            autoX = autoAnteriorX;
            autoZ = autoAnteriorZ;
            angulo = anguloAnterior;
            velocidad = 0;
        }
    }

    protected boolean moverChata() {
        float engancheX = autoX + (float) Math.sin(angulo) * DISTANCIA_ENGANCHE;
        float engancheZ = autoZ + (float) Math.cos(angulo) * DISTANCIA_ENGANCHE;
        float nuevoAngulo = (float) Math.atan2(chataX - engancheX, chataZ - engancheZ);
        float diferencia = nuevoAngulo - angulo;
        diferencia = (float) Math.atan2(Math.sin(diferencia), Math.cos(diferencia));
        diferencia = Math.max(-GIRO_MAXIMO, Math.min(GIRO_MAXIMO, diferencia));
        nuevoAngulo = angulo + diferencia;
        float nuevaX = engancheX + (float) Math.sin(nuevoAngulo) * LARGO_LANZA;
        float nuevaZ = engancheZ + (float) Math.cos(nuevoAngulo) * LARGO_LANZA;

        if (!puedeCircular(nuevaX, nuevaZ, RADIO_CHATA)) {
            return false;
        }

        chataX = nuevaX;
        chataZ = nuevaZ;
        chataAngulo = nuevoAngulo;
        return true;
    }

    @Override
    protected void escena() {
        super.escena();
        dibujarChata();
    }

    @Override
    protected void dibujarAuto() {
        super.dibujarAuto();
        pieza(0, 0.47f, 1.45f, 0.12f, 0.08f, 0.3f, 0.08f, 0.08f, 0.09f);
    }

    protected void dibujarChata() {
        float madera = 0.55f;
        piezaChata(0, 0.5f, 0, 1.56f, 0.1f, 1.96f, 0.08f, 0.08f, 0.09f);
        piezaChata(0, 0.58f, 0, 1.5f, 0.06f, 1.9f, 0.40f, 0.23f, 0.10f);
        float[] alturasTablas = {0.72f, 0.98f};

        for (float y : alturasTablas) {
            piezaChata(-0.72f, y, 0, 0.06f, 0.22f, 1.8f, madera, 0.32f, 0.14f);
            piezaChata(0.72f, y, 0, 0.06f, 0.22f, 1.8f, madera, 0.32f, 0.14f);
            piezaChata(0, y, -0.92f, 1.44f, 0.22f, 0.06f, madera, 0.32f, 0.14f);
            piezaChata(0, y, 0.92f, 1.44f, 0.22f, 0.06f, madera, 0.32f, 0.14f);
        }

        float[] ladosPostes = {-0.75f, 0.75f};
        float[] posicionesPostes = {-0.95f, 0, 0.95f};

        for (float x : ladosPostes) {
            for (float z : posicionesPostes) {
                piezaChata(x, 0.85f, z, 0.09f, 0.58f, 0.09f, 0.08f, 0.08f, 0.09f);
            }
        }

        float[] ladosRuedas = {-0.88f, 0.88f};

        for (float x : ladosRuedas) {
            piezaChata(x, 0.27f, 0, 0.18f, 0.54f, 0.54f, 0.055f, 0.065f, 0.08f);
            piezaChata(x, 0.6f, 0, 0.24f, 0.05f, 0.7f, 0.08f, 0.08f, 0.09f);
        }

        piezaChata(0, 0.27f, 0, 1.6f, 0.06f, 0.06f, 0.08f, 0.08f, 0.09f);
        float largoBarra = LARGO_LANZA - 0.98f;
        piezaChata(0, 0.47f, -0.98f - largoBarra / 2, 0.1f, 0.1f, largoBarra, 0.08f, 0.08f, 0.09f);
        piezaChata(0, 0.5f, -LARGO_LANZA + 0.05f, 0.2f, 0.14f, 0.24f, 0.6f, 0.6f, 0.62f);
    }

    protected void piezaChata(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        float coseno = (float) Math.cos(chataAngulo);
        float seno = (float) Math.sin(chataAngulo);
        float mundoX = chataX + coseno * x + seno * z;
        float mundoZ = chataZ - seno * x + coseno * z;
        cajaGirada(mundoX, y, mundoZ, sx, sy, sz, r, g, b, chataAngulo);
    }

    public static void main(String[] args) {
        clase5 aplicacion = new clase5();
        aplicacion.run();
    }
}
