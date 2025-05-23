package poobkemon.presentacion;

import poobkemon.dominio.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Panel para la selección de ataques para cada Pokémon antes de la batalla
 */
public class SeleccionAtaquesPanel extends JPanel {

    private InterfazPrincipal parentFrame;
    private String jugador1Nombre, jugador2Nombre;
    private Color jugador1Color, jugador2Color;
    private List<Pokemon> equipoJugador1;
    private List<Pokemon> equipoJugador2;

    // Panel principal
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Paneles de selección para cada Pokémon
    private List<JPanel> panelesPokemon;

    // Panel actual
    private int pokemonActualIndex = 0;
    private int jugadorActual = 1;

    // Componentes de interfaz
    private JLabel tituloLabel;
    private JLabel pokemonNombreLabel;
    private JPanel pokemonPreviewPanel;
    private CustomGifRenderer pokemonRenderer;

    // Paneles de ataques
    private JPanel ataqueFisicoPanel;
    private JPanel ataqueEspecialPanel;
    private JPanel ataqueEstadoPanel;
    private JScrollPane ataqueFisicoScroll;
    private JScrollPane ataqueEspecialScroll;
    private JScrollPane ataqueEstadoScroll;

    // Lista de ataques seleccionados
    private List<Ataque> ataquesSeleccionados;
    private JPanel ataquesSeleccionadosPanel;
    private List<JButton> botonesAtaquesSeleccionados;

    // Mapa de todos los ataques disponibles por tipo
    private Map<String, List<Ataque>> ataquesDisponiblesPorTipo;

    // Botones de navegación
    private JButton siguienteButton;
    private JButton anteriorButton;
    private JButton finalizarButton;

    // Constantes
    private static final int MAX_ATAQUES_POR_POKEMON = 4;
    private static final Color COLOR_FISICO = new Color(192, 48, 40);
    private static final Color COLOR_ESPECIAL = new Color(48, 80, 200);
    private static final Color COLOR_ESTADO = new Color(104, 144, 72);
    private static final String RUTA_ATAQUES = "C:\\Users\\stive\\Desktop\\Poobkemon\\Recursos\\Ataques\\Ataques.txt";

    /**
     * Constructor del panel de selección de ataques
     */
    public SeleccionAtaquesPanel(InterfazPrincipal parent, String j1Nombre, String j2Nombre,
                                 Color j1Color, Color j2Color, List<Pokemon> equipo1, List<Pokemon> equipo2) {
        this.parentFrame = parent;
        this.jugador1Nombre = j1Nombre;
        this.jugador2Nombre = j2Nombre;
        this.jugador1Color = j1Color;
        this.jugador2Color = j2Color;
        this.equipoJugador1 = new ArrayList<>(equipo1);
        this.equipoJugador2 = new ArrayList<>(equipo2);

        // Configurar el panel principal
        setLayout(new BorderLayout());
        setBackground(new Color(0, 32, 96)); // Azul oscuro
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Inicializar listas
        panelesPokemon = new ArrayList<>();
        ataquesSeleccionados = new ArrayList<>();
        botonesAtaquesSeleccionados = new ArrayList<>();

        // Cargar los ataques desde el archivo
        cargarAtaquesDesdeArchivo();

        // Inicializar componentes
        initComponents();

        // Mostrar el primer Pokémon
        mostrarPokemonActual();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void initComponents() {
        // Panel de título
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Panel principal con CardLayout para cambiar entre Pokémon
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.setOpaque(false);

        // Crear paneles para cada Pokémon
        crearPanelesParaPokemon();

        add(mainPanel, BorderLayout.CENTER);

        // Panel inferior con botones de navegación
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel del título
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        tituloLabel = new JLabel("SELECCIÓN DE ATAQUES - " + jugador1Nombre);
        tituloLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(tituloLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea un panel para cada Pokémon de ambos jugadores
     */
    private void crearPanelesParaPokemon() {
        // Crear paneles para los Pokémon del jugador 1
        for (Pokemon pokemon : equipoJugador1) {
            JPanel pokemonPanel = createPokemonAtaquesPanel(pokemon, 1);
            mainPanel.add(pokemonPanel, "J1_" + pokemon.getNombre());
            panelesPokemon.add(pokemonPanel);
        }

        // Crear paneles para los Pokémon del jugador 2
        for (Pokemon pokemon : equipoJugador2) {
            JPanel pokemonPanel = createPokemonAtaquesPanel(pokemon, 2);
            mainPanel.add(pokemonPanel, "J2_" + pokemon.getNombre());
            panelesPokemon.add(pokemonPanel);
        }
    }

    /**
     * Crea un panel para seleccionar ataques de un Pokémon específico
     */
    private JPanel createPokemonAtaquesPanel(Pokemon pokemon, int jugador) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Cabecera con información del Pokémon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Nombre del Pokémon
        pokemonNombreLabel = new JLabel(pokemon.getNombre().toUpperCase());
        pokemonNombreLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        pokemonNombreLabel.setForeground(Color.WHITE);
        pokemonNombreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Información adicional del Pokémon
        JLabel infoLabel = new JLabel("Tipo: " + pokemon.getTipo() + " | Nivel: " + pokemon.getNivel() + " | HP: " + pokemon.getHp());
        infoLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel para la previsualización del Pokémon
        pokemonPreviewPanel = new JPanel(new BorderLayout());
        pokemonPreviewPanel.setBackground(Color.BLACK);
        pokemonPreviewPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        pokemonPreviewPanel.setPreferredSize(new Dimension(150, 150));

        // Renderer para mostrar el GIF
        pokemonRenderer = new CustomGifRenderer(pokemon.getNombre(), true);
        pokemonPreviewPanel.add(pokemonRenderer, BorderLayout.CENTER);

        // Añadir componentes al panel de cabecera
        JPanel pokemonInfoPanel = new JPanel();
        pokemonInfoPanel.setLayout(new BoxLayout(pokemonInfoPanel, BoxLayout.Y_AXIS));
        pokemonInfoPanel.setOpaque(false);
        pokemonInfoPanel.add(pokemonNombreLabel);
        pokemonInfoPanel.add(Box.createVerticalStrut(5));
        pokemonInfoPanel.add(infoLabel);

        headerPanel.add(pokemonInfoPanel, BorderLayout.NORTH);
        headerPanel.add(pokemonPreviewPanel, BorderLayout.CENTER);

        // Panel principal con GridBagLayout
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Panel para los ataques físicos
        JPanel ataqueFisicoContainer = createAtaqueTypePanel("ATAQUES FÍSICOS", COLOR_FISICO);
        ataqueFisicoPanel = new JPanel();
        ataqueFisicoPanel.setLayout(new BoxLayout(ataqueFisicoPanel, BoxLayout.Y_AXIS));
        ataqueFisicoPanel.setOpaque(false);
        populateAtaquePanel(ataqueFisicoPanel, "Físico", pokemon);
        ataqueFisicoScroll = new JScrollPane(ataqueFisicoPanel);
        ataqueFisicoScroll.setBorder(null);
        ataqueFisicoScroll.setOpaque(false);
        ataqueFisicoScroll.getViewport().setOpaque(false);
        ataqueFisicoContainer.add(ataqueFisicoScroll, BorderLayout.CENTER);

        // Panel para los ataques especiales
        JPanel ataqueEspecialContainer = createAtaqueTypePanel("ATAQUES ESPECIALES", COLOR_ESPECIAL);
        ataqueEspecialPanel = new JPanel();
        ataqueEspecialPanel.setLayout(new BoxLayout(ataqueEspecialPanel, BoxLayout.Y_AXIS));
        ataqueEspecialPanel.setOpaque(false);
        populateAtaquePanel(ataqueEspecialPanel, "Especial", pokemon);
        ataqueEspecialScroll = new JScrollPane(ataqueEspecialPanel);
        ataqueEspecialScroll.setBorder(null);
        ataqueEspecialScroll.setOpaque(false);
        ataqueEspecialScroll.getViewport().setOpaque(false);
        ataqueEspecialContainer.add(ataqueEspecialScroll, BorderLayout.CENTER);

        // Panel para los ataques de estado
        JPanel ataqueEstadoContainer = createAtaqueTypePanel("ATAQUES DE ESTADO", COLOR_ESTADO);
        ataqueEstadoPanel = new JPanel();
        ataqueEstadoPanel.setLayout(new BoxLayout(ataqueEstadoPanel, BoxLayout.Y_AXIS));
        ataqueEstadoPanel.setOpaque(false);
        populateAtaquePanel(ataqueEstadoPanel, "Estado", pokemon);
        ataqueEstadoScroll = new JScrollPane(ataqueEstadoPanel);
        ataqueEstadoScroll.setBorder(null);
        ataqueEstadoScroll.setOpaque(false);
        ataqueEstadoScroll.getViewport().setOpaque(false);
        ataqueEstadoContainer.add(ataqueEstadoScroll, BorderLayout.CENTER);

        // Panel para los ataques seleccionados
        JPanel ataquesSeleccionadosContainer = createAtaqueTypePanel("ATAQUES SELECCIONADOS",
                jugador == 1 ? jugador1Color : jugador2Color);
        ataquesSeleccionadosPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        ataquesSeleccionadosPanel.setOpaque(false);

        // Crear slots para ataques seleccionados
        botonesAtaquesSeleccionados = new ArrayList<>();
        for (int i = 0; i < MAX_ATAQUES_POR_POKEMON; i++) {
            JButton slotButton = createEmptyAtaqueSlot();
            botonesAtaquesSeleccionados.add(slotButton);
            ataquesSeleccionadosPanel.add(slotButton);
        }

        ataquesSeleccionadosContainer.add(ataquesSeleccionadosPanel, BorderLayout.CENTER);

        // Añadir todos los paneles al panel principal con GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainContentPanel.add(ataqueFisicoContainer, gbc);

        gbc.gridx = 1;
        mainContentPanel.add(ataqueEspecialContainer, gbc);

        gbc.gridx = 2;
        mainContentPanel.add(ataqueEstadoContainer, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weighty = 0.3;
        mainContentPanel.add(ataquesSeleccionadosContainer, gbc);

        // Panel final completo
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(mainContentPanel, BorderLayout.CENTER);

        // Guardar datos del Pokémon en el panel para referencia posterior
        panel.putClientProperty("pokemon", pokemon);
        panel.putClientProperty("jugador", jugador);
        panel.putClientProperty("ataques_seleccionados", new ArrayList<Ataque>());

        return panel;
    }

    /**
     * Crea un panel para un tipo de ataque específico
     */
    private JPanel createAtaqueTypePanel(String titulo, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        // Ajustar color para hacerlo más oscuro
        Color colorOscuro = new Color(
                Math.max(color.getRed() / 2, 0),
                Math.max(color.getGreen() / 2, 0),
                Math.max(color.getBlue() / 2, 0)
        );
        panel.setBackground(colorOscuro);

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    /**
     * Crea un botón para un ataque vacío
     */
    private JButton createEmptyAtaqueSlot() {
        JButton button = new JButton("(Vacío)");
        button.setFont(new Font("Monospaced", Font.PLAIN, 12));
        button.setForeground(Color.GRAY);
        button.setBackground(new Color(30, 30, 30));
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        button.setFocusPainted(false);
        button.setEnabled(false);

        return button;
    }

    /**
     * Crea el panel inferior con botones de navegación
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panel.setOpaque(false);

        anteriorButton = new JButton("ANTERIOR");
        estilizarBoton(anteriorButton, new Color(80, 80, 200));
        anteriorButton.addActionListener(e -> mostrarPokemonAnterior());
        anteriorButton.setEnabled(false);  // Inicialmente deshabilitado

        siguienteButton = new JButton("SIGUIENTE");
        estilizarBoton(siguienteButton, new Color(80, 80, 200));
        siguienteButton.addActionListener(e -> mostrarPokemonSiguiente());

        finalizarButton = new JButton("FINALIZAR");
        estilizarBoton(finalizarButton, new Color(200, 30, 30));
        finalizarButton.addActionListener(e -> finalizarSeleccion());
        finalizarButton.setEnabled(false);  // Inicialmente deshabilitado

        panel.add(anteriorButton);
        panel.add(siguienteButton);
        panel.add(finalizarButton);

        return panel;
    }

    /**
     * Carga los ataques desde el archivo de texto
     */
    private void cargarAtaquesDesdeArchivo() {
        ataquesDisponiblesPorTipo = new HashMap<>();
        ataquesDisponiblesPorTipo.put("Físico", new ArrayList<>());
        ataquesDisponiblesPorTipo.put("Especial", new ArrayList<>());
        ataquesDisponiblesPorTipo.put("Estado", new ArrayList<>());

        try {
            File file = new File(RUTA_ATAQUES);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                // Formato: Nombre,Tipo,Categoría,Poder,Precisión,PP,Efecto
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String nombre = parts[0].trim();
                    String tipo = parts[1].trim();
                    String categoria = parts[2].trim();
                    int poder = Integer.parseInt(parts[3].trim());
                    int precision = Integer.parseInt(parts[4].trim());
                    int pp = Integer.parseInt(parts[5].trim());
                    String efecto = parts.length > 6 ? parts[6].trim() : "";

                    Ataque ataque = new Ataque(nombre, tipo, categoria, poder, precision, pp, efecto);
                    ataquesDisponiblesPorTipo.get(categoria).add(ataque);
                }
            }

            scanner.close();

            // Ordenar ataques por nombre
            for (String categoria : ataquesDisponiblesPorTipo.keySet()) {
                Collections.sort(ataquesDisponiblesPorTipo.get(categoria),
                        (a1, a2) -> a1.getNombre().compareTo(a2.getNombre()));
            }

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo encontrar el archivo de ataques en: " + RUTA_ATAQUES,
                    "Error al cargar ataques",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al leer el archivo de ataques: " + e.getMessage(),
                    "Error al cargar ataques",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    /**
     * Popula un panel con los ataques de una categoría específica
     */
    private void populateAtaquePanel(JPanel panel, String categoria, Pokemon pokemon) {
        panel.removeAll();

        List<Ataque> ataques = ataquesDisponiblesPorTipo.get(categoria);

        // Filtrar ataques por tipo del Pokémon para mostrarlos primero
        List<Ataque> ataquesFiltrados = new ArrayList<>();
        String[] tiposPokemon = pokemon.getTipo().split("/");

        // Primero añadir ataques del mismo tipo que el Pokémon
        for (Ataque ataque : ataques) {
            for (String tipoPokemon : tiposPokemon) {
                if (ataque.getTipo().equalsIgnoreCase(tipoPokemon.trim())) {
                    ataquesFiltrados.add(ataque);
                    break;
                }
            }
        }

        // Luego añadir el resto de ataques
        for (Ataque ataque : ataques) {
            boolean yaIncluido = false;
            for (Ataque ataqueIncluido : ataquesFiltrados) {
                if (ataque.getNombre().equals(ataqueIncluido.getNombre())) {
                    yaIncluido = true;
                    break;
                }
            }

            if (!yaIncluido) {
                ataquesFiltrados.add(ataque);
            }
        }

        // Crear botón para cada ataque
        for (Ataque ataque : ataquesFiltrados) {
            JButton ataqueButton = createAtaqueButton(ataque, categoria);
            panel.add(ataqueButton);
            panel.add(Box.createVerticalStrut(5));
        }

        panel.revalidate();
        panel.repaint();
    }

    /**
     * Crea un botón para un ataque específico
     */
    private JButton createAtaqueButton(final Ataque ataque, String categoria) {
        // Determinar el color según la categoría
        Color colorAtaque;
        if ("Físico".equals(categoria)) {
            colorAtaque = COLOR_FISICO;
        } else if ("Especial".equals(categoria)) {
            colorAtaque = COLOR_ESPECIAL;
        } else {
            colorAtaque = COLOR_ESTADO;
        }

        JButton button = new JButton() {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };

        // Configurar el botón con la información del ataque
        button.setLayout(new BorderLayout());

        JLabel nombreLabel = new JLabel(ataque.getNombre());
        nombreLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        nombreLabel.setForeground(Color.WHITE);
        nombreLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        JLabel infoLabel = new JLabel(String.format("Tipo: %s | Poder: %d | Precisión: %d | PP: %d",
                ataque.getTipo(), ataque.getPoder(), ataque.getPrecision(), ataque.getPp()));
        infoLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(nombreLabel, BorderLayout.NORTH);
        textPanel.add(infoLabel, BorderLayout.SOUTH);

        button.add(textPanel, BorderLayout.CENTER);

        // Configuración visual del botón
        button.setBackground(colorAtaque);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        button.setFocusPainted(false);

        // Acción del botón
        button.addActionListener(e -> seleccionarAtaque(ataque));

        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(colorAtaque.brighter());
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // Mostrar descripción del ataque como tooltip
                if (ataque.getEfecto() != null && !ataque.getEfecto().isEmpty()) {
                    button.setToolTipText(ataque.getEfecto());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(colorAtaque);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return button;
    }

    /**
     * Selecciona un ataque para el Pokémon actual
     */
    @SuppressWarnings("unchecked")
    private void seleccionarAtaque(Ataque ataque) {
        JPanel pokemonPanel = panelesPokemon.get(pokemonActualIndex);
        List<Ataque> ataquesDelPokemon = (List<Ataque>) pokemonPanel.getClientProperty("ataques_seleccionados");

        // Verificar si ya está seleccionado
        for (Ataque a : ataquesDelPokemon) {
            if (a.getNombre().equals(ataque.getNombre())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Este ataque ya está seleccionado.",
                        "Ataque duplicado",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
        }

        // Verificar si ya hay 4 ataques seleccionados
        if (ataquesDelPokemon.size() >= MAX_ATAQUES_POR_POKEMON) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ya has seleccionado el máximo de " + MAX_ATAQUES_POR_POKEMON + " ataques.",
                    "Límite alcanzado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Agregar el ataque y actualizar interfaz
        ataquesDelPokemon.add(ataque);

        // Actualizar botón correspondiente
        JButton slotButton = botonesAtaquesSeleccionados.get(ataquesDelPokemon.size() - 1);

        // Determinar el color según la categoría del ataque
        Color colorAtaque;
        if ("Físico".equals(ataque.getCategoria())) {
            colorAtaque = COLOR_FISICO;
        } else if ("Especial".equals(ataque.getCategoria())) {
            colorAtaque = COLOR_ESPECIAL;
        } else {
            colorAtaque = COLOR_ESTADO;
        }

        slotButton.setText(ataque.getNombre());
        slotButton.setForeground(Color.WHITE);
        slotButton.setBackground(colorAtaque);
        slotButton.setEnabled(true);
        slotButton.setToolTipText(String.format(
                "Tipo: %s | Poder: %d | Precisión: %d | PP: %d | %s",
                ataque.getTipo(), ataque.getPoder(), ataque.getPrecision(), ataque.getPp(),
                ataque.getEfecto() != null ? ataque.getEfecto() : ""
        ));

        // Permitir quitar un ataque al hacer clic en él
        slotButton.removeActionListener(slotButton.getActionListeners()[0]);
        slotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int respuesta = JOptionPane.showConfirmDialog(
                        SeleccionAtaquesPanel.this,
                        "¿Quieres quitar este ataque?",
                        "Quitar ataque",
                        JOptionPane.YES_NO_OPTION
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    quitarAtaque(ataque, ataquesDelPokemon.indexOf(ataque));
                }
            }
        });

        // Verificar si todos los Pokémon tienen al menos 1 ataque para habilitar Finalizar
        actualizarEstadoBotones();
    }

    /**
     * Quita un ataque seleccionado
     */
    @SuppressWarnings("unchecked")
    private void quitarAtaque(Ataque ataque, int indice) {
        JPanel pokemonPanel = panelesPokemon.get(pokemonActualIndex);
        List<Ataque> ataquesDelPokemon = (List<Ataque>) pokemonPanel.getClientProperty("ataques_seleccionados");

        // Quitar el ataque
        ataquesDelPokemon.remove(ataque);

        // Reorganizar botones
        for (int i = 0; i < MAX_ATAQUES_POR_POKEMON; i++) {
            JButton slotButton = botonesAtaquesSeleccionados.get(i);

            if (i < ataquesDelPokemon.size()) {
                Ataque ataqueActual = ataquesDelPokemon.get(i);

                // Determinar el color según la categoría del ataque
                Color colorAtaque;
                if ("Físico".equals(ataqueActual.getCategoria())) {
                    colorAtaque = COLOR_FISICO;
                } else if ("Especial".equals(ataqueActual.getCategoria())) {
                    colorAtaque = COLOR_ESPECIAL;
                } else {
                    colorAtaque = COLOR_ESTADO;
                }

                slotButton.setText(ataqueActual.getNombre());
                slotButton.setForeground(Color.WHITE);
                slotButton.setBackground(colorAtaque);
                slotButton.setEnabled(true);

                // Actualizar actionListener para el nuevo ataque en esta posición
                for (ActionListener al : slotButton.getActionListeners()) {
                    slotButton.removeActionListener(al);
                }

                final int pos = i;
                slotButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int respuesta = JOptionPane.showConfirmDialog(
                                SeleccionAtaquesPanel.this,
                                "¿Quieres quitar este ataque?",
                                "Quitar ataque",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (respuesta == JOptionPane.YES_OPTION) {
                            quitarAtaque(ataqueActual, pos);
                        }
                    }
                });

            } else {
                // Resetear slots vacíos
                slotButton.setText("(Vacío)");
                slotButton.setForeground(Color.GRAY);
                slotButton.setBackground(new Color(30, 30, 30));
                slotButton.setEnabled(false);
                slotButton.setToolTipText(null);

                // Quitar actionListeners
                for (ActionListener al : slotButton.getActionListeners()) {
                    slotButton.removeActionListener(al);
                }
            }
        }

        actualizarEstadoBotones();
    }

    /**
     * Muestra el Pokémon actual
     */
    private void mostrarPokemonActual() {
        // Mostrar el panel correspondiente usando CardLayout
        cardLayout.show(mainPanel, getPokemonCardName(pokemonActualIndex));

        // Actualizar título
        int jugador = pokemonActualIndex < equipoJugador1.size() ? 1 : 2;
        String nombreJugador = jugador == 1 ? jugador1Nombre : jugador2Nombre;
        tituloLabel.setText("SELECCIÓN DE ATAQUES - " + nombreJugador);

        // Actualizar botones de navegación
        actualizarEstadoBotones();
    }

    /**
     * Obtiene el nombre para el CardLayout basado en el índice
     */
    private String getPokemonCardName(int indice) {
        if (indice < equipoJugador1.size()) {
            return "J1_" + equipoJugador1.get(indice).getNombre();
        } else {
            return "J2_" + equipoJugador2.get(indice - equipoJugador1.size()).getNombre();
        }
    }

    /**
     * Muestra el Pokémon anterior
     */
    private void mostrarPokemonAnterior() {
        if (pokemonActualIndex > 0) {
            pokemonActualIndex--;
            mostrarPokemonActual();
        }
    }

    /**
     * Muestra el Pokémon siguiente
     */
    private void mostrarPokemonSiguiente() {
        if (pokemonActualIndex < panelesPokemon.size() - 1) {
            pokemonActualIndex++;
            mostrarPokemonActual();
        }
    }

    /**
     * Actualiza el estado de los botones de navegación
     */
    private void actualizarEstadoBotones() {
        // Botón Anterior
        anteriorButton.setEnabled(pokemonActualIndex > 0);

        // Botón Siguiente
        siguienteButton.setEnabled(pokemonActualIndex < panelesPokemon.size() - 1);

        // Botón Finalizar sólo habilitado si es el último Pokémon y todos tienen al menos 1 ataque
        boolean todosConAtaques = true;

        for (JPanel panel : panelesPokemon) {
            @SuppressWarnings("unchecked")
            List<Ataque> ataques = (List<Ataque>) panel.getClientProperty("ataques_seleccionados");
            if (ataques.isEmpty()) {
                todosConAtaques = false;
                break;
            }
        }

        finalizarButton.setEnabled(pokemonActualIndex == panelesPokemon.size() - 1 && todosConAtaques);
    }

    /**
     * Finaliza la selección de ataques y comienza la batalla
     */
    @SuppressWarnings("unchecked")
    private void finalizarSeleccion() {
        // Verificar que todos los Pokémon tengan al menos un ataque
        for (int i = 0; i < panelesPokemon.size(); i++) {
            JPanel panel = panelesPokemon.get(i);
            List<Ataque> ataques = (List<Ataque>) panel.getClientProperty("ataques_seleccionados");
            Pokemon pokemon = (Pokemon) panel.getClientProperty("pokemon");

            if (ataques.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "El Pokémon " + pokemon.getNombre() + " no tiene ningún ataque seleccionado.",
                        "Selección incompleta",
                        JOptionPane.WARNING_MESSAGE
                );

                // Mostrar el panel de ese Pokémon
                pokemonActualIndex = i;
                mostrarPokemonActual();
                return;
            }
        }

        // Asignar los ataques a cada Pokémon
        for (JPanel panel : panelesPokemon) {
            Pokemon pokemon = (Pokemon) panel.getClientProperty("pokemon");
            List<Ataque> ataques = (List<Ataque>) panel.getClientProperty("ataques_seleccionados");

            // Asignar los ataques al Pokémon usando el nuevo método
            pokemon.setAtaques(new ArrayList<>(ataques));
        }

        // Iniciar la batalla
        JOptionPane.showMessageDialog(
                this,
                "¡Todos los Pokémon tienen sus ataques seleccionados!\nPreparando la batalla...",
                "Preparando batalla",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Aquí se iniciaría la batalla real
        // Por ahora sólo mostramos un mensaje
        JOptionPane.showMessageDialog(
                this,
                String.format("¡La batalla entre %s y %s está lista para comenzar!\n\n" +
                                "Equipo 1: %d Pokémon\n" +
                                "Equipo 2: %d Pokémon\n\n" +
                                "Esta parte de la aplicación está en desarrollo.",
                        jugador1Nombre, jugador2Nombre,
                        equipoJugador1.size(), equipoJugador2.size()),
                "¡Que comience la batalla!",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Volver al menú principal
        parentFrame.volverDesdeSeleccionAtaques();
    }

    /**
     * Estiliza un botón
     */
    private void estilizarBoton(JButton boton, Color color) {
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(8, 30, 8, 30)
        ));
        boton.setFocusPainted(false);

        // Efectos hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (boton.isEnabled()) {
                    boton.setBackground(color.brighter());
                    boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
                boton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    /**
     * Libera los recursos al cerrar el panel
     */
    public void dispose() {
        // Liberar CustomGifRenderer
        if (pokemonRenderer != null) {
            pokemonRenderer.dispose();
        }

        // Recorrer todos los paneles y liberar recursos
        for (JPanel panel : panelesPokemon) {
            Component[] components = panel.getComponents();
            for (Component comp : components) {
                if (comp instanceof CustomGifRenderer) {
                    ((CustomGifRenderer) comp).dispose();
                }
            }
        }
    }
}