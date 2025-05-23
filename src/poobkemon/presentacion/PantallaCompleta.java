package poobkemon.presentacion;

import java.awt.*;
import javax.swing.*;

/**
 * Clase utilitaria para gestionar la funcionalidad de pantalla completa
 * @author Exael74
 */
public class PantallaCompleta {

    private GraphicsDevice dispositivo;
    private boolean esPantallaCompleta = false;
    private JFrame ventana;
    private Rectangle configuracionAnterior;

    /**
     * Constructor
     * @param ventana JFrame a manejar
     */
    public PantallaCompleta(JFrame ventana) {
        this.ventana = ventana;
        GraphicsEnvironment entorno = GraphicsEnvironment.getLocalGraphicsEnvironment();
        dispositivo = entorno.getDefaultScreenDevice();
    }

    /**
     * Alterna entre pantalla completa y modo ventana
     */
    public void alternarPantallaCompleta() {
        if (esPantallaCompleta) {
            salirPantallaCompleta();
        } else {
            activarPantallaCompleta();
        }
    }

    /**
     * Activa el modo de pantalla completa
     */
    public void activarPantallaCompleta() {
        if (!esPantallaCompleta) {
            configuracionAnterior = ventana.getBounds();
            ventana.dispose(); // Eliminar la ventana actual
            ventana.setUndecorated(true); // Quitar decoraciones

            if (dispositivo.isFullScreenSupported()) {
                dispositivo.setFullScreenWindow(ventana);
                esPantallaCompleta = true;
            } else {
                // Alternativa si no se soporta pantalla completa exclusiva
                ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
                ventana.setVisible(true);
                esPantallaCompleta = true;
            }
        }
    }

    /**
     * Sale del modo de pantalla completa
     */
    public void salirPantallaCompleta() {
        if (esPantallaCompleta) {
            if (dispositivo.isFullScreenSupported()) {
                dispositivo.setFullScreenWindow(null);
            }

            ventana.dispose();
            ventana.setUndecorated(false);
            ventana.setBounds(configuracionAnterior);
            ventana.setVisible(true);
            esPantallaCompleta = false;
        }
    }

    /**
     * Verifica si está en modo pantalla completa
     */
    public boolean esPantallaCompleta() {
        return esPantallaCompleta;
    }
}