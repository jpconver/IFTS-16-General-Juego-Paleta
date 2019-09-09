package paleta;

import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class ArrancarJuego {

    public static void main(String[] args) {

        // Propiedades del Juego
        int anchoVentana = 800;
        int largoVentana = 600;
        int tiempoDeEsperaEntreActualizaciones = 5;
        int enemigosPorLinea = 10;
        int filasDeEnemigos = 6;

        // Activar aceleracion de graficos en 2 dimensiones
        System.setProperty("sun.java2d.opengl", "true");

        // Crear un objeto de tipo JFrame que es la ventana donde va estar el juego
        JFrame ventana = new JFrame("Mi Juego");

        // Cerrar la aplicacion cuando el usuario hace click en la 'X'
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Abrir la ventana en el centro de la pantalla
        ventana.setLocationRelativeTo(null);

        // Mostrar la ventana
        ventana.setVisible(true);

        // Crear un "JComponent" llamado Juego y agregarlo a la ventana
        Juego juego = new Juego(anchoVentana, largoVentana, tiempoDeEsperaEntreActualizaciones);

        // Agregar a la ventana el JComponent (Juego hereda de JComponent)
        ventana.add(juego);

        // Enviar los eventos recibidos de movimientos del teclado al juego (esto es
        // porque la clase Juego implementa: MouseMotionListener)
        ventana.addKeyListener(juego);

        // Achicar la ventana lo maximo posible para que entren los componentes
        ventana.pack();

        // Agregar enemigos al juego
        agregarEnemigos(juego, enemigosPorLinea, filasDeEnemigos);

        // Crear un thread y pasarle como parametro al juego que implementa la interfaz
        // "Runnable"
        Thread thread = new Thread(juego);

        // Arrancar el juego
        thread.start();

    }

    private static void agregarEnemigos(Juego juego, int enemigosPorLinea, int filasDeEnemigos) {
        for (int x = 1; x < enemigosPorLinea; x++) {
            for (int y = 1; y < filasDeEnemigos; y++) {
                Color color = new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
                if (x % 2 == 0) {
                    juego.agregarEnemigo(new EnemigoRedondo(50 + x * 60, 60 + y * 30, 0.5, 0, 20, 20, color));
                } else {
                    juego.agregarEnemigo(new EnemigoCuadrado(50 + x * 60, 60 + y * 30, 0.5, 0, 20, 20, color));
                }

            }
        }
    }

}
