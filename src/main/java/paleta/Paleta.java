package paleta;

import java.awt.Color;
import java.awt.Graphics;

public class Paleta extends ElementoBasico {

    public Paleta(int posicionX, int posicionY, double velocidadX, double velocidadY, int ancho, int largo,
            Color color) {
        super(posicionX, posicionY, velocidadX, velocidadY, ancho, largo, color);
    }

    public void dibujarse(Graphics graphics) {
        graphics.setColor(getColor());
        graphics.fillRect(getPosicionX(), getPosicionY(), getAncho(), getLargo());
    }

}
