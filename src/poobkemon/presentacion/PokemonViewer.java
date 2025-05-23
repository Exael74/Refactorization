package poobkemon.presentacion;

import javax.swing.*;
import java.awt.*;

/**
 * Componente especializado para mostrar Pokémon
 */
public class PokemonViewer extends JPanel {

    private String pokemonName;
    private CustomGifRenderer renderer;

    /**
     * Constructor
     */
    public PokemonViewer(String pokemonName) {
        this.pokemonName = pokemonName;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setOpaque(true);

        // Usar el nuevo renderizador personalizado
        renderer = new CustomGifRenderer(pokemonName, true);

        // Centrar el renderizador
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setBackground(Color.BLACK);
        centeringPanel.add(renderer);

        add(centeringPanel, BorderLayout.CENTER);
    }

    /**
     * Detiene la animación
     */
    public void stopAnimation() {
        if (renderer != null) {
            renderer.stopAnimation();
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
    }
}