package paleta;

import java.awt.Color;
import java.awt.Graphics;

// Implementacion de la pelota
public class Pelota extends ElementoBasico {

    public Pelota(int posicionX, int posicionY, int velocidadX, int velocidadY, int ancho, int largo, Color color) {
        super(posicionX, posicionY, velocidadX, velocidadY, ancho, largo, color);
    }

    public void dibujarse(Graphics graphics) {
        graphics.setColor(getColor());
        graphics.fillOval(getPosicionX(), getPosicionY(), getAncho(), getLargo());
    }

}
