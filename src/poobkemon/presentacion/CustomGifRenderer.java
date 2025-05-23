package poobkemon.presentacion;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Renderizador personalizado para GIFs que evita el problema de estelas
 */
public class CustomGifRenderer extends JPanel {

    private List<BufferedImage> frames = new ArrayList<>();
    private int currentFrame = 0;
    private Timer animationTimer;
    private String pokemonName;
    private int delay = 60; // Reducido de 100ms a 60ms para más fluidez
    private boolean isLarge = false;
    private BufferedImage scaledCurrentFrame;
    private boolean loadingError = false;

    /**
     * Constructor del renderizador
     */
    public CustomGifRenderer(String pokemonName, boolean isLarge) {
        this.pokemonName = pokemonName;
        this.isLarge = isLarge;

        // Configurar el panel
        setBackground(Color.BLACK);
        setOpaque(true);
        setDoubleBuffered(true);

        // Cargar el gif - hacerlo en un hilo separado para no bloquear la UI
        SwingUtilities.invokeLater(() -> {
            loadGif();

            // Iniciar la animación si se cargaron frames
            if (!frames.isEmpty()) {
                animationTimer = new Timer(delay, e -> {
                    currentFrame = (currentFrame + 1) % frames.size();
                    updateScaledFrame();
                    repaint();
                });
                animationTimer.start();
            }
        });
    }

    /**
     * Carga y descompone el GIF en frames individuales
     */
    private void loadGif() {
        try {
            // Ruta al GIF
            String gifPath = "C:\\Users\\stive\\Desktop\\Poobkemon\\Recursos\\PokemonsPokedex\\" +
                    pokemonName + "_pokedex.gif";

            File file = new File(gifPath);
            if (!file.exists()) {
                // Intentar con nombre en minúscula como respaldo
                file = new File("C:\\Users\\stive\\Desktop\\Poobkemon\\Recursos\\PokemonsPokedex\\" +
                        pokemonName.toLowerCase() + "_pokedex.gif");

                if (!file.exists()) {
                    System.err.println("No se encontró el archivo de GIF para: " + pokemonName);
                    loadingError = true;
                    return;
                }
            }

            // Código especial para Blaziken debido a sus peculiaridades
            if (pokemonName.equalsIgnoreCase("Blaziken")) {
                System.out.println("Aplicando manejo especial para Blaziken");
                loadBlaziken(file);
                return;
            }

            // Usar ImageIO para leer el GIF frame por frame
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream stream = ImageIO.createImageInputStream(file);
            reader.setInput(stream);

            // Leer todos los frames
            int numFrames = reader.getNumImages(true);
            System.out.println("Cargando " + pokemonName + " - Frames totales: " + numFrames);

            for (int i = 0; i < numFrames; i++) {
                try {
                    BufferedImage frame = reader.read(i);

                    // Crear una nueva imagen con fondo negro (sin transparencia)
                    BufferedImage solidFrame = new BufferedImage(
                            frame.getWidth(),
                            frame.getHeight(),
                            BufferedImage.TYPE_INT_RGB);

                    Graphics2D g2d = solidFrame.createGraphics();
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, frame.getWidth(), frame.getHeight());
                    g2d.drawImage(frame, 0, 0, null);
                    g2d.dispose();

                    frames.add(solidFrame);
                } catch (Exception e) {
                    System.err.println("Error procesando frame " + i + " de " + pokemonName + ": " + e.getMessage());
                }
            }

            reader.dispose();
            stream.close();

            System.out.println("GIF cargado: " + pokemonName + " - Frames efectivos: " + frames.size());

            // Preparar el primer frame escalado
            if (!frames.isEmpty()) {
                updateScaledFrame();
            } else {
                loadingError = true;
            }

        } catch (IOException e) {
            System.err.println("Error al cargar GIF de " + pokemonName + ": " + e.getMessage());
            e.printStackTrace();
            loadingError = true;
        }
    }

    /**
     * Manejo especial para Blaziken
     */
    private void loadBlaziken(File file) {
        try {
            // Cargar la imagen completa primero
            BufferedImage fullImage = ImageIO.read(file);
            if (fullImage == null) {
                throw new IOException("No se pudo leer la imagen de Blaziken");
            }

            // Crear un único frame con fondo completamente negro
            BufferedImage solidFrame = new BufferedImage(
                    fullImage.getWidth(),
                    fullImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = solidFrame.createGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, fullImage.getWidth(), fullImage.getHeight());
            g2d.drawImage(fullImage, 0, 0, null);
            g2d.dispose();

            // Agregar este frame único a nuestra colección
            frames.add(solidFrame);

            // Como respaldo, también intentemos agregar frames individuales si es un GIF
            try {
                ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
                ImageInputStream stream = ImageIO.createImageInputStream(file);
                reader.setInput(stream);

                int numFrames = reader.getNumImages(true);
                if (numFrames > 1) {
                    frames.clear(); // Limpiar el frame inicial si vamos a cargar múltiples frames

                    for (int i = 0; i < numFrames; i++) {
                        BufferedImage frame = reader.read(i);

                        BufferedImage newSolidFrame = new BufferedImage(
                                frame.getWidth(),
                                frame.getHeight(),
                                BufferedImage.TYPE_INT_RGB);

                        Graphics2D g = newSolidFrame.createGraphics();
                        g.setColor(Color.BLACK);
                        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
                        g.drawImage(frame, 0, 0, null);
                        g.dispose();

                        frames.add(newSolidFrame);
                    }
                }

                reader.dispose();
                stream.close();

            } catch (Exception e) {
                System.err.println("Error al intentar cargar Blaziken como GIF animado: " + e.getMessage());
                // No es un error crítico - ya tenemos al menos un frame
            }

            updateScaledFrame();

        } catch (IOException e) {
            System.err.println("Error en manejo especial para Blaziken: " + e.getMessage());
            e.printStackTrace();
            loadingError = true;
        }
    }

    /**
     * Actualiza el frame escalado actual con técnicas optimizadas
     */
    private void updateScaledFrame() {
        if (frames.isEmpty()) return;

        BufferedImage currentOriginal = frames.get(currentFrame);

        // Determinar el factor de escala
        double scale = isLarge ? 3.0 : 1.0;

        // Crear una nueva imagen escalada
        int newWidth = (int)(currentOriginal.getWidth() * scale);
        int newHeight = (int)(currentOriginal.getHeight() * scale);

        // Reutilizar el buffer si es posible para mejorar rendimiento
        if (scaledCurrentFrame == null ||
                scaledCurrentFrame.getWidth() != newWidth ||
                scaledCurrentFrame.getHeight() != newHeight) {

            // Crear un nuevo buffer solo si es necesario
            scaledCurrentFrame = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        }

        Graphics2D g2d = scaledCurrentFrame.createGraphics();

        // Configurar para mejor calidad de escalado
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                isLarge ? RenderingHints.VALUE_INTERPOLATION_BILINEAR :
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Limpiar completamente el buffer
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, newWidth, newHeight);

        // Dibujar la imagen escalada
        g2d.drawImage(currentOriginal, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Usar Graphics2D para mejor rendimiento
        Graphics2D g2d = (Graphics2D)g;

        // Limpiar todo el área con negro
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Optimización de renderizado
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);

        // Dibujar el frame actual si existe
        if (scaledCurrentFrame != null) {
            // Centrar la imagen
            int x = (getWidth() - scaledCurrentFrame.getWidth()) / 2;
            int y = (getHeight() - scaledCurrentFrame.getHeight()) / 2;

            g2d.drawImage(scaledCurrentFrame, x, y, null);
        } else if (loadingError || frames.isEmpty()) {
            // Si hay error o no hay frames, mostrar mensaje
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();

            String message;
            if (loadingError) {
                message = "Error: No se pudo cargar " + pokemonName;
            } else {
                message = "Cargando " + pokemonName + "...";
            }

            int textWidth = fm.stringWidth(message);
            g2d.drawString(message, (getWidth() - textWidth) / 2, getHeight() / 2);
        }
    }

    /**
     * Ajusta el retraso entre frames para optimizar la animación
     */
    public void setFrameDelay(int millis) {
        this.delay = millis;
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.setDelay(millis);
        }
    }

    /**
     * Detiene la animación
     */
    public void stopAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }

    /**
     * Libera recursos
     */
    public void dispose() {
        stopAnimation();
        frames.clear();
        scaledCurrentFrame = null;
    }

    @Override
    public Dimension getPreferredSize() {
        if (scaledCurrentFrame != null) {
            return new Dimension(scaledCurrentFrame.getWidth(), scaledCurrentFrame.getHeight());
        }
        return new Dimension(100, 100); // Tamaño por defecto
    }
}