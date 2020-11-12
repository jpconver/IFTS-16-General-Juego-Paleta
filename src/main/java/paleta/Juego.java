package paleta;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

// Implemento KeyListener para poder leer en los metods keyPressed y keyReleased los codigos de tecla que apreto el usuario
// Implemento Runnable para crear un Threads que ejecute en paralelo con mi programa
public class Juego extends JPanel implements KeyListener, Runnable {

    private static final long serialVersionUID = 1L;
    private int anchoJuego;
    private int largoJuego;
    private int tiempoDeEsperaEntreActualizaciones;
    private ElementoBasico pelota;
    private ElementoBasico paleta;
    private Puntaje puntaje;
    private Vidas vidas;
    private List<Enemigo> enemigos;
    private boolean pararJuego;
    private boolean juegoCorriendo;
    private Sonidos sonidos;

    public Juego(int anchoJuego, int largoJuego, int tiempoDeEsperaEntreActualizaciones) {
        this.anchoJuego = anchoJuego;
        this.largoJuego = largoJuego;
        this.pelota = createPelota();
        this.paleta = new Paleta(30, largoJuego - 20, 0, 0, 80, 20, Color.black);
        this.puntaje = new Puntaje(10, 20, new Font("Arial", 8, 20), Color.blue);
        this.enemigos = new ArrayList<Enemigo>();
        this.vidas = new Vidas(10, 45, new Font("Arial", 8, 20), Color.blue, 3);
        this.juegoCorriendo = false;
        this.pararJuego = false;
        this.tiempoDeEsperaEntreActualizaciones = tiempoDeEsperaEntreActualizaciones;
        cargarSonidos();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(anchoJuego, largoJuego);
    }

    /*
     * Actualizar la actualizacion y el dibujado del juego de esta forma no es
     * recomendable dado que tendra distintas velocidades en distinto hardware Se
     * hizo asi por simplicidad para facilitar el aprendizaje Lo recomendado es
     * separar la parte de dibujado de la de actualizacion y usar interpolation
     */
    @Override
    public void run() {
        juegoCorriendo = true;
        while (juegoCorriendo) {
            actualizarJuego();
            dibujarJuego();
            esperar(tiempoDeEsperaEntreActualizaciones);
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        // si mantengo apretada la tecla de la derecha se asigna velocidad 1 a la paleta
        if (arg0.getKeyCode() == 39) {
            paleta.setVelocidadX(1);
        }

        // si mantengo apretada la tecla de la izquierda se asigna velocidad -1 a la paleta
        if (arg0.getKeyCode() == 37) {
            paleta.setVelocidadX(-1);
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // si suelto la tecla 39 o la 37 se asigna velocidad 0 a la paleta
        if (arg0.getKeyCode() == 39 || arg0.getKeyCode() == 37) {
            paleta.setVelocidadX(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    // Este metodo se llama cuando se hace un this.repaint() automaticamente
    // Aca se dibujan a todos los elementos, para ello cada elemento implementa el metodo dibujarse
    protected void paintComponent(Graphics g) {
        if (juegoCorriendo) {
            limpiarPantalla(g);
            // si el juego no esta parado entonces dibujar todos los elementos y los enemigos
            if (!pararJuego) {
                paleta.dibujarse(g);
                puntaje.dibujarse(g);
                vidas.dibujarse(g);
                pelota.dibujarse(g);
                dibujarEnemigos(g);
            } else {
                // si el juego esta parado entonces dibujar el fin del juego y cambiar el atributo juegoCorriendo a false
                dibujarFinJuego(g);
                juegoCorriendo = false;
            }
        }
    }

    // En este metodo se actualiza el estado de todos los elementos del juego
    private void actualizarJuego() {
        verificarEstadoAmbiente();
        pelota.moverse();
        paleta.moverse();
        moverEnemigos();
    }

    private void dibujarJuego() {
        this.repaint();
    }

    public void agregarEnemigo(Enemigo enemigo) {
        this.enemigos.add(enemigo);
    }

    private ElementoBasico createPelota() {
        return new Pelota(anchoJuego / 2, largoJuego - 50, 1, -1, 15, 15, Color.blue);
    }

    // En ese metodo se cargan los sonidos que estan es src/main/resources
    private void cargarSonidos() {
        try {
            sonidos = new Sonidos();
            sonidos.agregarSonido("toc", "sonidos/toc.wav");
            sonidos.agregarSonido("tic", "sonidos/tic.wav");
            sonidos.agregarSonido("muerte", "sonidos/muerte.wav");
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    // Este metodo se usa para mostrar un mensaje
    private void mostrarMensaje(Graphics g, String mensaje) {
        this.limpiarPantalla(g);
        g.setColor(Color.blue);
        g.setFont(new Font("Arial", 8, 30));
        g.drawString(mensaje, 10, 40);
    }

    // dibujar el fin del juego
    private void dibujarFinJuego(Graphics g) {
        mostrarMensaje(g, "Fin del juego, puntaje: " + String.valueOf(puntaje.getPuntaje()));
    }

    // se hace una iteracion de todos los enemigos cargados en la lista de enemigos y se le dice a cada uno que ejecute el metodo moverse(). 
    // moverse() actualiza la posicionX y posicionY del elemento en base a la direccion que tenia para X e Y
    private void moverEnemigos() {
        for (Enemigo enemigo : enemigos) {
            enemigo.moverse();
        }
    }

    // se hace una iteracion en la lista de enemigos y se ejecuta el metodo dibujarse()
    private void dibujarEnemigos(Graphics g) {
        for (Enemigo enemigo : enemigos) {
            enemigo.dibujarse(g);
        }
    }

    // En este metodo verifico las colisiones, los rebotes de la pelota contra las paredes, la colision entre enemigos y el fin de juego
    private void verificarEstadoAmbiente() {
        verificarReboteEntrePelotaYPaleta();
        verificarSiPelotaTocaElPiso();
        verificarRebotePelotaContraParedLateral();
        verificarRebotePelotaContraLaParedSuperior();
        verificarReboteEnemigosContraParedesLaterales();
        verificarReboteEntreEnemigos();
        verificarColisionEntreEnemigoYPelota();
        verificarFinDeJuego();
    }

    // se iteran todos los enemigos y se verifica para cada enemigo si hay colision con cada enemigo. Si hay colision se ejecuta el metodo rebotarEnEjeX() del enemigo
    // esto hace que el enemigo cambie de direccion en el eje x
    private void verificarReboteEntreEnemigos() {
        for (Enemigo enemigo1 : enemigos) {
            for (Enemigo enemigo2 : enemigos) {
                if (enemigo1 != enemigo2 && enemigo1.hayColision(enemigo2)) {
                    enemigo1.rebotarEnEjeX();
                }
            }
        }
    }

    // se verifica si hay colision entre la paleta y la pelota. Si hay colision se cambia la direccion de la pelota en el eje Y
    private void verificarReboteEntrePelotaYPaleta() {
        if (paleta.hayColision(pelota)) {
            pelota.rebotarEnEjeY();
            sonidos.tocarSonido("toc");
        }
    }

    // se verifica si hay colision de cada enemigo contra las paredes laterales, si hay colision se cambia la direccion del enemigo en el eje X
    private void verificarReboteEnemigosContraParedesLaterales() {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getPosicionX() <= 0 || enemigo.getPosicionX() + enemigo.getAncho() >= anchoJuego) {
                enemigo.rebotarEnEjeX();
            }
        }
    }

    // se verifica si la pelota colisiona con cada uno de los enemigos. Si hay colision se hace rebotar la pelota en el ejeY, se suma un punto y se toca el sonido toc
    private void verificarColisionEntreEnemigoYPelota() {
        Iterator<Enemigo> iterador = enemigos.iterator();
        while (iterador.hasNext()) {
            Enemigo enemigo = iterador.next();
            if (enemigo.hayColision(pelota)) {
                iterador.remove();
                pelota.rebotarEnEjeY();
                puntaje.sumarPunto();
                sonidos.tocarSonido("tic");
            }
        }
    }

    // se verifica si la cantidad de enemigos es 0 o si la cantidad de vidas es 0 para parar el juego
    private void verificarFinDeJuego() {
        if (enemigos.size() == 0) {
            pararJuego = true;
        }
        if (vidas.getVidas() == 0) {
            pararJuego = true;
        }
    }

    // se verifica si la pelota toca el piso, si es hace se pierde una vida, se crea una nueva pelota, se toca el sonido muerte y se muesra
    // el mensaje perdiste una vida y se esperan 5 segundos
    private void verificarSiPelotaTocaElPiso() {
        if (pelota.getPosicionY() + pelota.getLargo() >= largoJuego) {
            vidas.perderVida();
            pelota = createPelota();
            sonidos.tocarSonido("muerte");
            mostrarMensaje(this.getGraphics(), "Perdiste una vida!, espera 5 segundos");
            esperar(5000);
        }
    }

    // se verifica si la pelota colisiona contra la pared lateral, si es asi, se hace rebotar la pelota en el eje X
    private void verificarRebotePelotaContraParedLateral() {
        if (pelota.getPosicionX() <= 0 || pelota.getPosicionX() + pelota.getAncho() >= anchoJuego) {
            pelota.rebotarEnEjeX();
        }
    }

    // se verifica si la pelota colisiona contra la pared superior, si es asi se hace rebotar la pelota en el eje Y
    private void verificarRebotePelotaContraLaParedSuperior() {
        if (pelota.getPosicionY() <= 0) {
            pelota.rebotarEnEjeY();
        }
    }

    // metodo para limpiar la pantalla
    private void limpiarPantalla(Graphics graphics) {
        graphics.setColor(Color.cyan);
        graphics.fillRect(0, 0, anchoJuego, largoJuego);
    }

    // metodo para esperar una cantidad de milisegundos
    private void esperar(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

}
