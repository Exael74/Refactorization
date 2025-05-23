package poobkemon.presentacion;

import javax.swing.*;
import java.awt.*;

/**
 * Versión simplificada del panel para GIFs usando el renderizador personalizado
 */
public class AnimatedGifPanel extends JPanel {

    private String pokemonName;
    private CustomGifRenderer renderer;
    private boolean isLarge;

    /**
     * Constructor para un panel de GIF animado
     */
    public AnimatedGifPanel(String pokemonName, int width, int height, boolean isLarge) {
        this.pokemonName = pokemonName;
        this.isLarge = isLarge;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
        setOpaque(true);

        // Usar el nuevo renderizador personalizado
        renderer = new CustomGifRenderer(pokemonName, isLarge);
        add(renderer, BorderLayout.CENTER);

        // Si no es panel grande, mostrar el nombre debajo
        if (!isLarge) {
            JLabel nameLabel = new JLabel(pokemonName);
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setOpaque(true);
            nameLabel.setBackground(Color.BLACK);
            add(nameLabel, BorderLayout.SOUTH);
        }
    }

    /**
     * Marca el panel como seleccionado
     */
    public void setSelected(boolean selected) {
        if (selected) {
            setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        } else {
            setBorder(BorderFactory.createLineBorder(Color.WHITE));
        }
    }

    /**
     * Libera recursos
     */
    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
            renderer = null;
        }
        removeAll();
    }

    /**
     * Detiene la animación
     */
    public void stopAnimation() {
        if (renderer != null) {
            renderer.stopAnimation();
        }
    }
}