package com.graphics;

import junit.framework.TestCase;

public class ConduccionTest extends TestCase {
    public void testCallesYDestinosTransitables() {
        clase2 juego = new clase2();
        for (float z = -50; z <= 50; z += 0.25f) {
            assertTrue(juego.puedeCircular(-50, z));
        }
        for (float x = -50; x <= 50; x += 0.25f) {
            assertTrue(juego.puedeCircular(x, -50));
        }
        assertTrue(juego.puedeCircular(-10, -10));
        assertTrue(juego.puedeCircular(30, 30));
        assertFalse(juego.puedeCircular(0, 0));
    }

    public void testObstaculosYLimites() {
        clase2 juego = new clase2();
        assertFalse(juego.puedeCircular(-20, -20));
        assertFalse(juego.puedeCircular(0, -40));
        assertFalse(juego.puedeCircular(40, 40));
        assertFalse(juego.puedeCircular(-25.5f, -20));
        assertFalse(juego.puedeCircular(54, 0));
        assertFalse(juego.puedeCircular(0, -56));
        assertTrue(juego.puedeCircular(-27, -20));
        assertTrue(juego.puedeCircular(-26.2f, -20));
    }

    public void testReinicio() {
        clase4 juego = new clase4();
        juego.autoX = 12;
        juego.autoZ = 4;
        juego.velocidad = 10;
        juego.angulo = 2;
        juego.giroCamara = 1;
        juego.reiniciar();
        assertEquals(0f, juego.giroCamara, 0f);
        assertEquals(-50f, juego.autoX, 0f);
        assertEquals(50f, juego.autoZ, 0f);
        assertEquals(0f, juego.velocidad, 0f);
        assertEquals(0f, juego.angulo, 0f);
    }

    public void testChataEmpiezaDetrasDelAuto() {
        clase5 juego = new clase5();
        juego.autoZ = 10;
        juego.chataZ = 0;
        juego.reiniciar();
        float distancia = clase5.DISTANCIA_ENGANCHE + clase5.LARGO_LANZA;
        assertEquals(juego.autoX, juego.chataX, 0.001f);
        assertEquals(juego.autoZ + distancia, juego.chataZ, 0.001f);
        assertTrue(juego.puedeCircular(juego.chataX, juego.chataZ, clase5.RADIO_CHATA));
    }

    public void testChataSigueAlEnganche() {
        clase5 juego = new clase5();
        juego.autoZ -= 6;
        juego.angulo = 0.4f;
        assertTrue(juego.moverChata());
        float engancheX = juego.autoX + (float) Math.sin(juego.angulo) * clase5.DISTANCIA_ENGANCHE;
        float engancheZ = juego.autoZ + (float) Math.cos(juego.angulo) * clase5.DISTANCIA_ENGANCHE;
        float distanciaX = juego.chataX - engancheX;
        float distanciaZ = juego.chataZ - engancheZ;
        float distancia = (float) Math.sqrt(distanciaX * distanciaX + distanciaZ * distanciaZ);
        assertEquals(clase5.LARGO_LANZA, distancia, 0.001f);
    }

    public void testChataNoSePlegaContraElAuto() {
        clase5 juego = new clase5();
        juego.autoZ = 20;
        juego.chataZ = 20 + clase5.DISTANCIA_ENGANCHE + clase5.LARGO_LANZA;
        juego.angulo = 2.5f;
        assertTrue(juego.moverChata());
        float diferencia = juego.chataAngulo - juego.angulo;
        diferencia = (float) Math.atan2(Math.sin(diferencia), Math.cos(diferencia));
        assertTrue(Math.abs(diferencia) <= clase5.GIRO_MAXIMO + 0.001f);
    }

    public void testChataChocaYNoSeMueve() {
        clase5 juego = new clase5();
        float chataAnteriorZ = juego.chataZ;
        juego.autoZ += 0.5f;
        assertFalse(juego.moverChata());
        assertEquals(chataAnteriorZ, juego.chataZ, 0f);
    }

    public void testMotorSeAveriaAlConducirSieteSegundos() {
        clase6 juego = new clase6();
        for (int cuadro = 0; cuadro < 100; cuadro++) {
            juego.velocidad = 10;
            juego.actualizarDesgaste(0.05f);
        }
        assertFalse(juego.motorAveriado);
        for (int cuadro = 0; cuadro < 50; cuadro++) {
            juego.velocidad = 10;
            juego.actualizarDesgaste(0.05f);
        }
        assertTrue(juego.motorAveriado);
        assertTrue(juego.velocidad <= clase6.VELOCIDAD_AVERIADA);
    }

    public void testDetenidoNoSeDesgasta() {
        clase6 juego = new clase6();
        juego.velocidad = 0;
        juego.actualizarDesgaste(10);
        assertEquals(0f, juego.desgaste, 0f);
    }

    public void testTallerReparaElMotor() {
        clase6 juego = new clase6();
        assertTrue(juego.puedeCircular(clase6.TALLER_X, clase6.TALLER_Z));
        juego.desgaste = 1;
        juego.motorAveriado = true;
        juego.autoX = clase6.TALLER_X + 1;
        juego.autoZ = clase6.TALLER_Z;
        juego.velocidad = 0.5f;
        juego.actualizarDesgaste(0.05f);
        assertFalse(juego.motorAveriado);
        assertTrue(juego.motorReparado);
        assertEquals(0f, juego.desgaste, 0f);
    }

    public void testDesgasteOcurreUnaSolaVez() {
        clase6 juego = new clase6();
        juego.motorReparado = true;
        for (int cuadro = 0; cuadro < 300; cuadro++) {
            juego.velocidad = 10;
            juego.actualizarDesgaste(0.05f);
        }
        assertFalse(juego.motorAveriado);
        assertEquals(0f, juego.desgaste, 0f);
    }

    public void testReinicioRestauraElMotor() {
        clase6 juego = new clase6();
        juego.desgaste = 1;
        juego.motorAveriado = true;
        juego.motorReparado = true;
        juego.reiniciar();
        assertFalse(juego.motorAveriado);
        assertFalse(juego.motorReparado);
        assertEquals(0f, juego.desgaste, 0f);
    }
}
