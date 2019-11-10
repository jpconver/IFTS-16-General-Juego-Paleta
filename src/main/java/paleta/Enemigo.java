package paleta;

import java.awt.Color;
import java.awt.Graphics;

// Clase abstracta para enemigo, implementa ElementoBasico
public abstract class Enemigo extends ElementoBasico {

    public Enemigo(int posicionX, int posicionY, double velocidadX, double velocidadY, int ancho, int largo, Color color) {
        super(posicionX, posicionY, velocidadX, velocidadY, ancho, largo, color);
    }

    // Todos los enemigos deben implementar el metodo destruirse
    public abstract void destruirse(Graphics graphics);

}
