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

public class Juego extends JComponent implements KeyListener, Runnable {

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
        this.juegoCorriendo = true;
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
        while (juegoCorriendo) {
            actualizarJuego();
            dibujarJuego();
            try {
                Thread.sleep(tiempoDeEsperaEntreActualizaciones);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == 39) {
            paleta.setVelocidadX(1);
        }
        if (arg0.getKeyCode() == 37) {
            paleta.setVelocidadX(-1);
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        if (arg0.getKeyCode() == 39 || arg0.getKeyCode() == 37) {
            paleta.setVelocidadX(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void paintComponent(Graphics g) {
        limpiarPantalla(g);
        if (!pararJuego) {
            paleta.dibujarse(g);
            puntaje.dibujarse(g);
            vidas.dibujarse(g);
            pelota.dibujarse(g);
            dibujarEnemigos(g);
        } else {
            dibujarFinJuego(g);
            juegoCorriendo = false;
        }
    }

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

    private void mostrarMensaje(Graphics g, String mensaje) {
        this.limpiarPantalla(g);
        g.setColor(Color.blue);
        g.setFont(new Font("Arial", 8, 30));
        g.drawString(mensaje, 10, 40);
    }

    private void dibujarFinJuego(Graphics g) {
        mostrarMensaje(g, "Fin del juego, puntaje: " + String.valueOf(puntaje.getPuntaje()));
    }

    private void moverEnemigos() {
        for (Enemigo enemigo : enemigos) {
            enemigo.moverse();
        }
    }

    private void dibujarEnemigos(Graphics g) {
        for (Enemigo enemigo : enemigos) {
            enemigo.dibujarse(g);
        }
    }

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

    private void verificarReboteEntreEnemigos() {
        for (Enemigo enemigo1 : enemigos) {
            for (Enemigo enemigo2 : enemigos) {
                if (enemigo1 != enemigo2 && enemigo1.hayColision(enemigo2)) {
                    enemigo1.rebotarEnEjeX();
                }
            }
        }
    }

    private void verificarReboteEntrePelotaYPaleta() {
        if (paleta.hayColision(pelota)) {
            pelota.rebotarEnEjeY();
            sonidos.tocarSonido("toc");
        }
    }

    private void verificarReboteEnemigosContraParedesLaterales() {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getPosicionX() <= 0 || enemigo.getPosicionX() + enemigo.getAncho() >= anchoJuego) {
                enemigo.rebotarEnEjeX();
            }
        }
    }

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

    private void verificarFinDeJuego() {
        if (enemigos.size() == 0) {
            pararJuego = true;
        }
        if (vidas.getVidas() == 0) {
            pararJuego = true;
        }
    }

    private void verificarSiPelotaTocaElPiso() {
        if (pelota.getPosicionY() + pelota.getLargo() >= largoJuego) {
            vidas.perderVida();
            pelota = createPelota();
            sonidos.tocarSonido("muerte");
            mostrarMensaje(this.getGraphics(), "Perdiste una vida!, espera 5 segundos");
            esperar(5);
        }
    }

    private void verificarRebotePelotaContraParedLateral() {
        if (pelota.getPosicionX() <= 0 || pelota.getPosicionX() + pelota.getAncho() >= anchoJuego) {
            pelota.rebotarEnEjeX();
        }
    }

    private void verificarRebotePelotaContraLaParedSuperior() {
        if (pelota.getPosicionY() <= 0) {
            pelota.rebotarEnEjeY();
        }
    }

    private void limpiarPantalla(Graphics graphics) {
        graphics.setColor(Color.cyan);
        graphics.fillRect(0, 0, anchoJuego, largoJuego);
    }

    private void esperar(int segundos) {
        try {
            Thread.sleep(2000);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

}
