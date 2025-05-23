package poobkemon.presentacion;

import poobkemon.dominio.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.*;

/**
 * Panel que implementa la Poobkedex con estilo retro
 */
public class PoobkedexPanel extends JPanel {

    private InterfazPrincipal parentFrame;
    private PokemonCatalogo catalogo;
    public JPanel gridPanel; // Cambiado a público para acceso desde el optimizador
    public JPanel rightPanel; // Cambiado a público para acceso desde el optimizador
    public JPanel infoPanel; // Cambiado a público para acceso desde el optimizador
    private JButton btnAtras;
    private JButton btnPrev, btnNext;
    private JTabbedPane tabbedPane;
    private JTextArea infoTextArea;

    private List<String> pokemonNames;
    private int currentPage = 0;
    private static final int POKEMONS_PER_PAGE = 6;
    private static final int GRID_ROWS = 3;
    private static final int GRID_COLS = 2;

    /**
     * Constructor que inicializa el panel de Poobkedex
     * @param parent Referencia a la ventana principal
     */
    public PoobkedexPanel(InterfazPrincipal parent) {
        this.parentFrame = parent;

        // Configuración necesaria para mejorar la compatibilidad con GIFs
        System.setProperty("sun.java2d.noddraw", "true");

        // Inicializar componentes
        catalogo = new PokemonCatalogo();

        // Configurar layout principal
        setLayout(new BorderLayout());

        // Imprimir información del entorno
        System.out.println("Directorio actual: " + System.getProperty("user.dir"));
        File resourceDir = new File("Recursos/PokemonsPokedex");
        System.out.println("Directorio de recursos existe: " + resourceDir.exists());

        // Crear el panel principal con fondo
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Cargar nombres de Pokémon
        String[] nameArray = {
                "Absol", "Articuno", "Banette", "Blaziken", "Charizard", "Crobat",
                "Dragonite", "Dugtrio", "Flygon", "Gardevoir", "Gengar", "Glalie",
                "Golem", "Gyarados", "Hariyama", "Heracross", "Machamp", "Metagross",
                "Mewtwo", "Nidoking", "Pidgeot", "Pikachu", "Raikou", "Rayquaza",
                "Salamence", "Sceptile", "Scyther", "Slaking", "Snorlax", "Steelix",
                "Swampert", "Tyranitar", "Umbreon", "Venusaur"
        };
        Arrays.sort(nameArray);
        pokemonNames = new ArrayList<>(Arrays.asList(nameArray));
        System.out.println("Pokémon encontrados: " + pokemonNames.size());

        // Crear el encabezado con título y botón atrás
        createHeader(mainPanel);

        // Crear los paneles principales
        JPanel contentPanel = new JPanel(new BorderLayout(10, 5));
        contentPanel.setOpaque(false);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Panel izquierdo (grid de Pokémon)
        JPanel leftPanel = createLeftPanel();
        contentPanel.add(leftPanel, BorderLayout.WEST);

        // Panel derecho (vista del Pokémon seleccionado)
        rightPanel = createRightPanel();
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        // Panel inferior (pestañas de información)
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Actualizar la vista inicial
        updatePokemonGrid();

        // Aplicar optimizaciones de rendimiento
        PerformanceOptimizer.optimizePoobkedexPanel(this);

        // Seleccionar el primer Pokémon al iniciar
        if (!pokemonNames.isEmpty()) {
            SwingUtilities.invokeLater(() -> selectPokemon(pokemonNames.get(0)));
        }
    }

    /**
     * Libera los recursos de todos los paneles de GIF
     */
    public void dispose() {
        // Liberar paneles en la cuadrícula
        for (Component comp : gridPanel.getComponents()) {
            if (comp instanceof JPanel) {
                // Buscar JLabels con iconos y limpiarlos
                for (Component innerComp : ((JPanel)comp).getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel label = (JLabel)innerComp;
                        if (label.getIcon() != null) {
                            label.setIcon(null);
                        }
                    } else if (innerComp instanceof CustomGifRenderer) {
                        ((CustomGifRenderer) innerComp).dispose();
                    }
                }
            }
        }

        // Liberar panel derecho
        for (Component comp : rightPanel.getComponents()) {
            if (comp instanceof PokemonViewer) {
                ((PokemonViewer) comp).dispose();
            }
        }

        // Recolector de basura explícito para liberar memoria
        System.gc();
    }

    /**
     * Crea el panel principal con fondo
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Fondo con degradado para un look retro
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 64, 128),
                        0, getHeight(), new Color(0, 100, 160));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    /**
     * Crea el encabezado con título y botón atrás
     */
    private void createHeader(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Botón Atrás - ahora vuelve al menú principal
        btnAtras = new JButton("ATRAS");
        btnAtras.setFont(new Font("Arial", Font.BOLD, 16));
        btnAtras.setForeground(Color.WHITE);
        btnAtras.setBackground(new Color(255, 50, 50));
        btnAtras.setPreferredSize(new Dimension(100, 40));
        btnAtras.setFocusPainted(false);
        btnAtras.addActionListener(e -> {
            dispose(); // Liberar recursos antes de volver
            parentFrame.volverDesdePoobkedex();
        });

        // Título Poobkedex
        JLabel titleLabel = new JLabel("POOBKEDEX");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Agregar componentes al encabezado
        headerPanel.add(btnAtras, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Crea el panel izquierdo con la cuadrícula de Pokémon
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 400));

        // Panel para la cuadrícula de Pokémon
        gridPanel = new JPanel(new GridLayout(GRID_ROWS, GRID_COLS, 5, 5));
        gridPanel.setBackground(new Color(10, 10, 40));
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Scroll para la cuadrícula
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Panel de navegación (botones prev/next)
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(new Color(10, 10, 40));

        btnPrev = new JButton("<");
        btnPrev.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updatePokemonGrid();
            }
        });

        btnNext = new JButton(">");
        btnNext.addActionListener(e -> {
            if ((currentPage + 1) * POKEMONS_PER_PAGE < pokemonNames.size()) {
                currentPage++;
                updatePokemonGrid();
            }
        });

        JPanel pageIndicator = new JPanel();
        pageIndicator.setBackground(new Color(10, 10, 40));

        navPanel.add(btnPrev, BorderLayout.WEST);
        navPanel.add(pageIndicator, BorderLayout.CENTER);
        navPanel.add(btnNext, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(navPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea el panel derecho para mostrar el Pokémon seleccionado
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawStarburstBackground(g, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.setPreferredSize(new Dimension(600, 600));

        return panel;
    }

    /**
     * Crea el panel inferior con las pestañas de información
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(getWidth(), 200));

        // Pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Monospaced", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(50, 50, 80));
        tabbedPane.setForeground(Color.WHITE);

        // Área de texto para información
        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoTextArea.setForeground(Color.WHITE);
        infoTextArea.setBackground(new Color(20, 20, 60));
        infoTextArea.setMargin(new Insets(10, 10, 10, 10));
        infoTextArea.setText("SELECCIONA UN POKEMON PARA VER SU DESCRIPCION");

        JScrollPane infoScroll = new JScrollPane(infoTextArea);

        // Crear los paneles para cada pestaña
        JPanel descripcionPanel = new JPanel(new BorderLayout());
        descripcionPanel.add(infoScroll, BorderLayout.CENTER);

        JPanel estadisticasPanel = new JPanel(new BorderLayout());
        estadisticasPanel.add(new JScrollPane(createStatsPanel()), BorderLayout.CENTER);

        JPanel ataquesPanel = new JPanel(new BorderLayout());
        ataquesPanel.add(new JScrollPane(createAttacksPanel()), BorderLayout.CENTER);

        // Agregar las pestañas
        tabbedPane.addTab("DESCRIPCION", descripcionPanel);
        tabbedPane.addTab("ESTADISTICAS", estadisticasPanel);
        tabbedPane.addTab("ATAQUES", ataquesPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel para estadísticas
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 20, 60));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("SELECCIONA UN POKEMON PARA VER SUS ESTADISTICAS");
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        return panel;
    }

    /**
     * Crea el panel para ataques
     */
    private JPanel createAttacksPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 20, 60));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("SELECCIONA UN POKEMON PARA VER SUS ATAQUES");
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        return panel;
    }

    /**
     * Actualiza la cuadrícula de Pokémon con la página actual
     */
    private void updatePokemonGrid() {
        // Limpiar panel existente
        gridPanel.removeAll();

        int startIndex = currentPage * POKEMONS_PER_PAGE;
        int endIndex = Math.min(startIndex + POKEMONS_PER_PAGE, pokemonNames.size());

        for (int i = startIndex; i < endIndex; i++) {
            String pokemonName = pokemonNames.get(i);
            JPanel pokemonCard = createPokemonCard(pokemonName);
            gridPanel.add(pokemonCard);
        }

        // Rellenar con paneles vacíos si es necesario
        for (int i = endIndex - startIndex; i < POKEMONS_PER_PAGE; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(10, 10, 40));
            gridPanel.add(emptyPanel);
        }

        // Actualizar botones de navegación
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled((currentPage + 1) * POKEMONS_PER_PAGE < pokemonNames.size());

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Crea una celda para un Pokémon en la cuadrícula
     */
    private JPanel createPokemonCard(final String pokemonName) {
        // Panel contenedor principal
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.BLACK);
        container.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        container.setOpaque(true);

        // Usar el nuevo renderizador personalizado
        CustomGifRenderer renderer = new CustomGifRenderer(pokemonName, false);

        // Agregar el renderer al centro del panel
        container.add(renderer, BorderLayout.CENTER);

        // Añadir el nombre en la parte inferior
        JLabel nameLabel = new JLabel(pokemonName);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setOpaque(true);
        nameLabel.setBackground(Color.BLACK);
        container.add(nameLabel, BorderLayout.SOUTH);

        // Añadir evento de clic
        container.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectPokemon(pokemonName);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                container.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                container.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            }
        });

        return container;
    }

    /**
     * Selecciona un Pokémon y actualiza la vista de detalles
     */
    private void selectPokemon(final String pokemonName) {
        System.out.println("Seleccionando Pokémon: " + pokemonName);

        try {
            // Limpiar el panel derecho
            for (Component comp : rightPanel.getComponents()) {
                if (comp instanceof PokemonViewer) {
                    ((PokemonViewer) comp).dispose();
                }
            }
            rightPanel.removeAll();

            // Crear un visor de Pokémon para este Pokémon
            PokemonViewer viewer = new PokemonViewer(pokemonName);

            // Añadir al panel derecho
            rightPanel.add(viewer, BorderLayout.CENTER);

            // Forzar actualización
            rightPanel.revalidate();
            rightPanel.repaint();

            // Obtener datos del Pokémon
            Pokemon pokemon = catalogo.getPokemon(pokemonName);
            if (pokemon == null) return;

            // Actualizar información en las pestañas
            updateDescripcionTab(pokemon);
            updateEstadisticasTab(pokemon);
            updateAtaquesTab(pokemon);

        } catch (Exception e) {
            System.err.println("Error al seleccionar Pokémon: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la pestaña de descripción
     */
    private void updateDescripcionTab(Pokemon pokemon) {
        StringBuilder sb = new StringBuilder();
        sb.append("NOMBRE: ").append(pokemon.getNombre().toUpperCase()).append("\n\n");
        sb.append("TIPO: ").append(pokemon.getTipo().toUpperCase()).append("\n\n");
        sb.append("DESCRIPCIÓN:\n").append(PokemonCatalogo.getDescripcion(pokemon.getNombre()));

        infoTextArea.setText(sb.toString());
        infoTextArea.setCaretPosition(0);
    }

    /**
     * Actualiza la pestaña de estadísticas
     */
    private void updateEstadisticasTab(Pokemon pokemon) {
        JPanel statsPanel = createStatsPanel();
        statsPanel.removeAll();

        // Título
        JLabel titleLabel = new JLabel("ESTADÍSTICAS DE " + pokemon.getNombre().toUpperCase());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(titleLabel);
        statsPanel.add(Box.createVerticalStrut(20));

        // Estadísticas
        addStatBar(statsPanel, "NIVEL", pokemon.getNivel(), 100);
        addStatBar(statsPanel, "HP", pokemon.getHp(), 255);
        addStatBar(statsPanel, "ATAQUE", pokemon.getAtaque(), 255);
        addStatBar(statsPanel, "DEFENSA", pokemon.getDefensa(), 255);
        addStatBar(statsPanel, "VELOCIDAD", pokemon.getVelocidad(), 255);

        // Actualizar panel de forma segura
        Component comp = tabbedPane.getComponentAt(1);
        if (comp instanceof JPanel) {
            JPanel panel = (JPanel)comp;
            Component scrollPaneComp = panel.getComponent(0);
            if (scrollPaneComp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)scrollPaneComp;
                scrollPane.setViewportView(statsPanel);
                scrollPane.revalidate();
                scrollPane.repaint();
            }
        }
    }

    /**
     * Actualiza la pestaña de ataques
     */
    private void updateAtaquesTab(Pokemon pokemon) {
        JPanel attacksPanel = createAttacksPanel();
        attacksPanel.removeAll();

        // Título
        JLabel titleLabel = new JLabel("ATAQUES DE " + pokemon.getNombre().toUpperCase());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        attacksPanel.add(titleLabel);
        attacksPanel.add(Box.createVerticalStrut(20));

        // Lista de ataques
        for (String ataque : pokemon.getMovimientos()) {
            JLabel ataqueLabel = new JLabel("• " + ataque.toUpperCase());
            ataqueLabel.setForeground(Color.WHITE);
            ataqueLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
            ataqueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            attacksPanel.add(ataqueLabel);
            attacksPanel.add(Box.createVerticalStrut(10));
        }

        // Actualizar panel de forma segura
        Component comp = tabbedPane.getComponentAt(2);
        if (comp instanceof JPanel) {
            JPanel panel = (JPanel)comp;
            Component scrollPaneComp = panel.getComponent(0);
            if (scrollPaneComp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)scrollPaneComp;
                scrollPane.setViewportView(attacksPanel);
                scrollPane.revalidate();
                scrollPane.repaint();
            }
        }
    }

    /**
     * Añade una barra de estadísticas al panel
     */
    private void addStatBar(JPanel panel, String statName, int value, int maxValue) {
        JPanel statPanel = new JPanel(new BorderLayout(10, 0));
        statPanel.setOpaque(false);
        statPanel.setMaximumSize(new Dimension(500, 30));

        JLabel nameLabel = new JLabel(statName + ":");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setPreferredSize(new Dimension(100, 25));

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setPreferredSize(new Dimension(50, 25));

        JProgressBar progressBar = new JProgressBar(0, maxValue);
        progressBar.setValue(value);
        progressBar.setStringPainted(false);

        // Color según el valor
        if (value < maxValue / 3) {
            progressBar.setForeground(new Color(255, 50, 50)); // Rojo
        } else if (value < 2 * maxValue / 3) {
            progressBar.setForeground(new Color(255, 255, 50)); // Amarillo
        } else {
            progressBar.setForeground(new Color(50, 255, 50)); // Verde
        }

        statPanel.add(nameLabel, BorderLayout.WEST);
        statPanel.add(progressBar, BorderLayout.CENTER);
        statPanel.add(valueLabel, BorderLayout.EAST);

        panel.add(statPanel);
        panel.add(Box.createVerticalStrut(10));
    }

    /**
     * Dibuja un fondo de starburst para la vista de Pokémon
     */
    private void drawStarburstBackground(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;

        // Centro del starburst
        int centerX = width / 2;
        int centerY = height / 2;

        // Gradiente radial con colores brillantes
        float[] dist = {0.0f, 0.3f, 1.0f};
        Color[] colors = {
                new Color(0, 255, 255), // Cyan brillante en el centro
                new Color(0, 180, 255), // Azul intermedio
                new Color(0, 40, 150)   // Azul oscuro en el exterior
        };

        RadialGradientPaint gradient = new RadialGradientPaint(
                centerX, centerY, Math.max(width, height) / 2,
                dist, colors
        );

        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Dibujar los rayos con mayor brillo
        g2d.setColor(new Color(255, 255, 255, 100));

        int numRays = 24;
        for (int i = 0; i < numRays; i++) {
            double angle = Math.toRadians(i * (360.0 / numRays));
            int rayLength = Math.max(width, height);

            int x2 = centerX + (int)(Math.cos(angle) * rayLength);
            int y2 = centerY + (int)(Math.sin(angle) * rayLength);

            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(centerX, centerY, x2, y2);
        }
    }
}