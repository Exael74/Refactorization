package poobkemon.presentacion;

import java.awt.*;
import javax.swing.*;

/**
 * Clase para optimizar el rendimiento de toda la aplicación
 */
public class PerformanceOptimizer {

    public static final int DEFAULT_GIF_FRAME_RATE = 60; // milisegundos (≈16.6 FPS)
    private static boolean initialized = false;

    /**
     * Inicializa las optimizaciones globales. Llamar al inicio de la aplicación.
     */
    public static void initialize() {
        if (initialized) return;

        // Configurar propiedades del sistema para mejor rendimiento gráfico
        System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("sun.java2d.d3d", "True");
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.pmoffscreen", "false");

        // Ajustar el comportamiento de anti-aliasing para mejor rendimiento
        System.setProperty("swing.aatext", "false");

        // Optimizar JComponent buffering
        RepaintManager.currentManager(null).setDoubleBufferingEnabled(true);

        // Aumentar la prioridad del hilo de la interfaz de usuario
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        // Nota: Ya no intentamos desactivar VSync ya que no es compatible con todos los sistemas

        initialized = true;
    }

    /**
     * Optimiza un componente específico para mostrar animaciones
     */
    public static void optimizeForAnimation(JComponent component) {
        component.setDoubleBuffered(true);

        // Si es un viewport de JScrollPane, eliminar el comportamiento de scroll suave
        if (component instanceof JViewport) {
            ((JViewport) component).setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        }
    }

    /**
     * Adapta la velocidad de animación según el hardware
     */
    public static int getOptimalFrameRate() {
        // Evaluar rendimiento del sistema y ajustar
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        long memory = runtime.maxMemory() / (1024 * 1024); // En MB

        // Algoritmo simple que ajusta el frame rate según el hardware
        if (processors >= 4 && memory >= 2048) {
            // Hardware potente: máxima fluidez
            return 33; // ~30 FPS
        } else if (processors >= 2 && memory >= 1024) {
            // Hardware medio
            return 50; // ~20 FPS
        } else {
            // Hardware limitado
            return 66; // ~15 FPS
        }
    }

    /**
     * Optimiza un panel de Poobkedex completo
     */
    public static void optimizePoobkedexPanel(PoobkedexPanel panel) {
        // Aplicar configuraciones generales
        JComponent[] components = {panel, panel.gridPanel, panel.infoPanel, panel.rightPanel};

        for (JComponent comp : components) {
            if (comp != null) {
                optimizeForAnimation(comp);
            }
        }

        // Ajustar frecuencia de animación de todos los renderizadores
        setOptimalFrameRateForAllRenderers(panel);
    }

    /**
     * Configura la tasa de frames óptima para todos los renderizadores
     */
    public static void setOptimalFrameRateForAllRenderers(Container container) {
        int optimal = getOptimalFrameRate();

        for (Component comp : container.getComponents()) {
            if (comp instanceof CustomGifRenderer) {
                ((CustomGifRenderer) comp).setFrameDelay(optimal);
            }

            if (comp instanceof Container) {
                setOptimalFrameRateForAllRenderers((Container) comp);
            }
        }
    }
}