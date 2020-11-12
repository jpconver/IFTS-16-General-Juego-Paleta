package paleta;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

// Clase para manejar cargar y tocar sonidos
public class Sonidos {

    private Map<String, byte[]> sonidos;

    public Sonidos() {
        this.sonidos = new HashMap<String, byte[]>();
    }

    // agrega un sonido al mapa de sonidos
    public void agregarSonido(String nombre, String archivo) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(Sonidos.class.getClassLoader().getResource(archivo).toURI()));
            sonidos.put(nombre, fileContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // toca un sonido del mapa de sonidos
    public void tocarSonido(String sonido) {
        try {
            byte[] sonidoEnBytes = sonidos.get(sonido);
            InputStream myInputStream = new ByteArrayInputStream(sonidoEnBytes);
            AudioInputStream ais = AudioSystem.getAudioInputStream(myInputStream);
            DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(ais);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}