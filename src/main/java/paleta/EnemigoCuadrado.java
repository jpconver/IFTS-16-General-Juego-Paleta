package paleta;

import java.awt.Color;
import java.awt.Graphics;

// Implementacion de un Enemigo donde el enemigo se dibuja como un cuadrado
public class EnemigoCuadrado extends Enemigo {

    public EnemigoCuadrado(int posicionX, int posicionY, double velocidadX, double velocidadY, int ancho, int largo, Color color) {
        super(posicionX, posicionY, velocidadX, velocidadY, ancho, largo, color);
    }

    public void dibujarse(Graphics graphics) {
        graphics.setColor(getColor());
        graphics.fillRect(getPosicionX(), getPosicionY(), getAncho(), getLargo());
    }

    public void destruirse(Graphics graphics) {
        graphics.setColor(Color.red);
        graphics.fillRect(getPosicionX(), getPosicionY(), getAncho(), getLargo());
    }

}
