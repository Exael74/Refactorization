package poobkemon.presentacion;

import poobkemon.dominio.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Panel para la selección de Pokémon antes de iniciar una batalla
 */
public class SeleccionPokemonPanel extends JPanel {

    private InterfazPrincipal parentFrame;
    private PokemonCatalogo catalogo;
    private String jugador1Nombre, jugador2Nombre;
    private Color jugador1Color, jugador2Color;

    // Paneles principales
    private JPanel panelPokemonDisponibles;
    private JPanel panelDetallePokemon;
    private JPanel panelEquipoJugador1;
    private JPanel panelEquipoJugador2;

    // Contenedor para la cuadrícula de Pokémon disponibles
    private JPanel gridPokemonDisponibles;
    private JScrollPane scrollPokemonDisponibles;

    // Para mostrar el Pokémon seleccionado
    private JPanel pokemonViewerContainer;
    private CustomGifRenderer pokemonRenderer;
    private JLabel nombrePokemonLabel;
    private JLabel tipoPokemonLabel;
    private JLabel nivelPokemonLabel;
    private JLabel hpPokemonLabel;
    private JButton agregarJugador1Button;
    private JButton agregarJugador2Button;

    // Equipos de Pokémon
    private List<Pokemon> equipoJugador1;
    private List<Pokemon> equipoJugador2;
    private static final int MAX_POKEMON_POR_EQUIPO = 6;

    // Pokémon actualmente seleccionado
    private Pokemon pokemonSeleccionado;

    // Lista de todos los Pokémon disponibles
    private List<String> pokemonNames;

    // Botones y controles inferiores
    private JButton volverButton;
    private JButton iniciarBatallaButton;
    private JCheckBox activarAnimacionesCheckbox;

    // Contadores de Pokémon seleccionados
    private JLabel contadorJugador1Label;
    private JLabel contadorJugador2Label;

    /**
     * Constructor del panel de selección de Pokémon
     */
    public SeleccionPokemonPanel(InterfazPrincipal parent, String j1Nombre, String j2Nombre, Color j1Color, Color j2Color) {
        this.parentFrame = parent;
        this.jugador1Nombre = j1Nombre;
        this.jugador2Nombre = j2Nombre;
        this.jugador1Color = j1Color;
        this.jugador2Color = j2Color;

        equipoJugador1 = new ArrayList<>();
        equipoJugador2 = new ArrayList<>();

        catalogo = new PokemonCatalogo();

        // Inicializar la lista de nombres de Pokémon
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

        // Configurar el panel principal
        setLayout(new BorderLayout());
        setBackground(new Color(0, 32, 96)); // Azul oscuro
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Inicializar componentes
        initComponents();

        // Seleccionar el primer Pokémon al inicio
        if (!pokemonNames.isEmpty()) {
            SwingUtilities.invokeLater(() -> seleccionarPokemon(pokemonNames.get(0)));
        }
    }

    /**
     * Inicializa todos los componentes del panel
     */
    private void initComponents() {
        // Panel de título
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Panel principal con los 3 paneles principales
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Panel de Pokémon disponibles (izquierda)
        panelPokemonDisponibles = createPokemonDisponiblesPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainContent.add(panelPokemonDisponibles, gbc);

        // Panel de detalle del Pokémon (centro)
        panelDetallePokemon = createDetallePokemonPanel();
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        mainContent.add(panelDetallePokemon, gbc);

        // Panel de equipos (derecha)
        JPanel equiposPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        equiposPanel.setOpaque(false);

        panelEquipoJugador1 = createEquipoJugadorPanel(1, jugador1Nombre, jugador1Color);
        panelEquipoJugador2 = createEquipoJugadorPanel(2, jugador2Nombre, jugador2Color);

        equiposPanel.add(panelEquipoJugador1);
        equiposPanel.add(panelEquipoJugador2);

        gbc.gridx = 2;
        gbc.weightx = 0.3;
        mainContent.add(equiposPanel, gbc);

        add(mainContent, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel del título
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("SELECCIÓN DE POKÉMON");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Checkbox para activar/desactivar animaciones
        activarAnimacionesCheckbox = new JCheckBox("Activar animaciones");
        activarAnimacionesCheckbox.setForeground(Color.WHITE);
        activarAnimacionesCheckbox.setOpaque(false);
        activarAnimacionesCheckbox.setSelected(true);
        activarAnimacionesCheckbox.addActionListener(e -> toggleAnimaciones(activarAnimacionesCheckbox.isSelected()));

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(activarAnimacionesCheckbox, BorderLayout.EAST);

        return panel;
    }

    /**
     * Crea el panel de Pokémon disponibles
     */
    private JPanel createPokemonDisponiblesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.setBackground(new Color(0, 30, 80)); // Azul marino oscuro

        JLabel titleLabel = new JLabel("POKÉMON DISPONIBLES");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Cuadrícula de Pokémon
        gridPokemonDisponibles = new JPanel(new GridLayout(0, 5, 5, 5));
        gridPokemonDisponibles.setBackground(new Color(0, 30, 80));

        // Llenar la cuadrícula con los Pokémon disponibles
        loadPokemonGrid();

        scrollPokemonDisponibles = new JScrollPane(gridPokemonDisponibles);
        scrollPokemonDisponibles.setBorder(null);
        scrollPokemonDisponibles.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPokemonDisponibles, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de detalle del Pokémon
     */
    private JPanel createDetallePokemonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.setBackground(new Color(0, 30, 80)); // Azul marino oscuro

        JLabel titleLabel = new JLabel("DETALLE DEL POKÉMON");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Panel para el GIF del Pokémon
        pokemonViewerContainer = new JPanel(new BorderLayout());
        pokemonViewerContainer.setBackground(Color.BLACK);
        pokemonViewerContainer.setPreferredSize(new Dimension(200, 200));

        // Crear un renderer de GIF con Charizard inicialmente
        pokemonRenderer = new CustomGifRenderer("Charizard", true);
        pokemonViewerContainer.add(pokemonRenderer, BorderLayout.CENTER);

        // Panel para la información del Pokémon
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Etiquetas para mostrar información
        nombrePokemonLabel = new JLabel("CHARIZARD");
        nombrePokemonLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        nombrePokemonLabel.setForeground(Color.WHITE);
        nombrePokemonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        tipoPokemonLabel = new JLabel("Tipo: Fuego/Volador");
        tipoPokemonLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        tipoPokemonLabel.setForeground(Color.WHITE);
        tipoPokemonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nivelPokemonLabel = new JLabel("Nivel: 36");
        nivelPokemonLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        nivelPokemonLabel.setForeground(Color.WHITE);
        nivelPokemonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        hpPokemonLabel = new JLabel("HP: 78");
        hpPokemonLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        hpPokemonLabel.setForeground(Color.WHITE);
        hpPokemonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(nombrePokemonLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(tipoPokemonLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(nivelPokemonLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(hpPokemonLabel);
        infoPanel.add(Box.createVerticalGlue());

        // Panel para los botones de agregar a equipo
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        agregarJugador1Button = new JButton("Agregar a Jugador 1");
        agregarJugador1Button.setBackground(jugador1Color);
        agregarJugador1Button.setForeground(Color.WHITE);
        agregarJugador1Button.setFocusPainted(false);
        agregarJugador1Button.addActionListener(e -> agregarPokemonAEquipo(1));

        agregarJugador2Button = new JButton("Agregar a Jugador 2");
        agregarJugador2Button.setBackground(jugador2Color);
        agregarJugador2Button.setForeground(Color.WHITE);
        agregarJugador2Button.setFocusPainted(false);
        agregarJugador2Button.addActionListener(e -> agregarPokemonAEquipo(2));

        buttonPanel.add(agregarJugador1Button);
        buttonPanel.add(agregarJugador2Button);

        // Añadir componentes al panel principal
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(pokemonViewerContainer, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea el panel para mostrar el equipo de un jugador
     */
    private JPanel createEquipoJugadorPanel(int jugadorNum, String nombreJugador, Color colorJugador) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        // Color personalizado según el jugador
        panel.setBackground(new Color(
                Math.max(colorJugador.getRed() / 3, 0),
                Math.max(colorJugador.getGreen() / 3, 0),
                Math.max(colorJugador.getBlue() / 3, 0)
        ));

        // Título del panel
        JLabel titleLabel = new JLabel("EQUIPO DE JUGADOR " + jugadorNum);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Panel para la cuadrícula de Pokémon seleccionados
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        gridPanel.setOpaque(false);

        // Crear espacios vacíos para los Pokémon
        for (int i = 0; i < MAX_POKEMON_POR_EQUIPO; i++) {
            JPanel slotPanel = createEmptyPokemonSlot();
            if (jugadorNum == 1) {
                slotPanel.setName("slot1_" + i);
            } else {
                slotPanel.setName("slot2_" + i);
            }
            gridPanel.add(slotPanel);
        }

        // Panel inferior con contador de Pokémon
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel counterLabel = new JLabel("0/" + MAX_POKEMON_POR_EQUIPO + " Pokémon seleccionados");
        counterLabel.setForeground(Color.WHITE);
        counterLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        counterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Guardar referencia al contador
        if (jugadorNum == 1) {
            contadorJugador1Label = counterLabel;
        } else {
            contadorJugador2Label = counterLabel;
        }

        bottomPanel.add(counterLabel, BorderLayout.CENTER);

        // Añadir componentes al panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea un slot vacío para Pokémon en el equipo
     */
    private JPanel createEmptyPokemonSlot() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        panel.setBackground(Color.BLACK);

        return panel;
    }

    /**
     * Crea el panel inferior con botones
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panel.setOpaque(false);

        volverButton = new JButton("VOLVER");
        estilizarBoton(volverButton, new Color(200, 30, 30));
        volverButton.addActionListener(e -> volverAlMenuAnterior());

        iniciarBatallaButton = new JButton("INICIAR BATALLA");
        estilizarBoton(iniciarBatallaButton, new Color(200, 30, 30));
        iniciarBatallaButton.addActionListener(e -> iniciarBatalla());

        panel.add(volverButton);
        panel.add(iniciarBatallaButton);

        return panel;
    }

    /**
     * Carga la cuadrícula de Pokémon disponibles
     */
    private void loadPokemonGrid() {
        gridPokemonDisponibles.removeAll();

        for (String pokemonName : pokemonNames) {
            JPanel pokemonCard = createPokemonCard(pokemonName);
            gridPokemonDisponibles.add(pokemonCard);
        }

        gridPokemonDisponibles.revalidate();
        gridPokemonDisponibles.repaint();
    }

    /**
     * Crea una celda para un Pokémon en la cuadrícula
     */
    private JPanel createPokemonCard(final String pokemonName) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.BLACK);
        container.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        // Usar el renderer personalizado para mostrar el GIF
        CustomGifRenderer renderer = new CustomGifRenderer(pokemonName, false);
        container.add(renderer, BorderLayout.CENTER);

        // Etiqueta con el nombre
        JLabel nameLabel = new JLabel(pokemonName);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setOpaque(true);
        nameLabel.setBackground(Color.BLACK);
        container.add(nameLabel, BorderLayout.SOUTH);

        // Evento de clic para seleccionar este Pokémon
        container.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarPokemon(pokemonName);
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
     * Selecciona un Pokémon y muestra sus detalles
     */
    private void seleccionarPokemon(String pokemonName) {
        // Obtener datos del Pokémon
        Pokemon pokemon = catalogo.getPokemon(pokemonName);
        if (pokemon == null) return;

        pokemonSeleccionado = pokemon;

        // Actualizar el visor de Pokémon
        if (pokemonRenderer != null) {
            pokemonRenderer.dispose();
            pokemonViewerContainer.remove(pokemonRenderer);
        }

        pokemonRenderer = new CustomGifRenderer(pokemonName, true);
        pokemonViewerContainer.add(pokemonRenderer, BorderLayout.CENTER);

        // Actualizar la información
        nombrePokemonLabel.setText(pokemon.getNombre().toUpperCase());
        tipoPokemonLabel.setText("Tipo: " + pokemon.getTipo());
        nivelPokemonLabel.setText("Nivel: " + pokemon.getNivel());
        hpPokemonLabel.setText("HP: " + pokemon.getHp());

        // Refrescar la interfaz
        panelDetallePokemon.revalidate();
        panelDetallePokemon.repaint();

        // Actualizar estado de botones
        actualizarBotones();
    }

    /**
     * Agrega el Pokémon seleccionado al equipo del jugador
     */
    private void agregarPokemonAEquipo(int jugadorNum) {
        if (pokemonSeleccionado == null) return;

        List<Pokemon> equipo = (jugadorNum == 1) ? equipoJugador1 : equipoJugador2;
        JPanel equipoPanel = (jugadorNum == 1) ? panelEquipoJugador1 : panelEquipoJugador2;

        // Verificar si ya está completo el equipo
        if (equipo.size() >= MAX_POKEMON_POR_EQUIPO) {
            JOptionPane.showMessageDialog(
                    this,
                    "El equipo del Jugador " + jugadorNum + " ya está completo.",
                    "Equipo completo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Verificar si este Pokémon ya está en el equipo
        for (Pokemon p : equipo) {
            if (p.getNombre().equals(pokemonSeleccionado.getNombre())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Este Pokémon ya está en el equipo del Jugador " + jugadorNum + ".",
                        "Pokémon duplicado",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
        }

        // Agregar al equipo
        equipo.add(pokemonSeleccionado);

        // Actualizar la interfaz
        JPanel gridPanel = (JPanel) equipoPanel.getComponent(1); // La cuadrícula es el segundo componente
        JPanel slotPanel = (JPanel) gridPanel.getComponent(equipo.size() - 1);

        // Limpiar el slot
        slotPanel.removeAll();

        // Agregar el renderer del Pokémon al slot
        CustomGifRenderer renderer = new CustomGifRenderer(pokemonSeleccionado.getNombre(), false);
        slotPanel.add(renderer, BorderLayout.CENTER);

        // Actualizar contadores
        if (jugadorNum == 1) {
            contadorJugador1Label.setText(equipo.size() + "/" + MAX_POKEMON_POR_EQUIPO + " Pokémon seleccionados");
        } else {
            contadorJugador2Label.setText(equipo.size() + "/" + MAX_POKEMON_POR_EQUIPO + " Pokémon seleccionados");
        }

        // Refrescar la interfaz
        slotPanel.revalidate();
        slotPanel.repaint();

        // Actualizar estado de botones
        actualizarBotones();
    }

    /**
     * Actualiza el estado de los botones según la selección actual
     */
    private void actualizarBotones() {
        boolean puedeAgregarJ1 = equipoJugador1.size() < MAX_POKEMON_POR_EQUIPO &&
                !contieneNombre(equipoJugador1, pokemonSeleccionado.getNombre());

        boolean puedeAgregarJ2 = equipoJugador2.size() < MAX_POKEMON_POR_EQUIPO &&
                !contieneNombre(equipoJugador2, pokemonSeleccionado.getNombre());

        agregarJugador1Button.setEnabled(puedeAgregarJ1);
        agregarJugador2Button.setEnabled(puedeAgregarJ2);

        // Activar el botón de iniciar batalla solo cuando ambos equipos tengan al menos 1 Pokémon
        iniciarBatallaButton.setEnabled(!equipoJugador1.isEmpty() && !equipoJugador2.isEmpty());
    }

    /**
     * Comprueba si una lista de Pokémon contiene uno con el nombre dado
     */
    private boolean contieneNombre(List<Pokemon> lista, String nombre) {
        for (Pokemon p : lista) {
            if (p.getNombre().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Activa o desactiva las animaciones de GIF
     */
    private void toggleAnimaciones(boolean activar) {
        // Implementar la lógica para activar/desactivar animaciones
        // Esto puede variar según cómo esté implementada la clase CustomGifRenderer
        System.out.println("Animaciones " + (activar ? "activadas" : "desactivadas"));
    }

    /**
     * Vuelve al menú anterior
     */
    private void volverAlMenuAnterior() {
        parentFrame.volverDesdeSeleccionPokemon();
    }

    /**
     * Inicia la batalla con los equipos seleccionados
     */
    private void iniciarBatalla() {
        if (equipoJugador1.isEmpty() || equipoJugador2.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ambos jugadores deben seleccionar al menos un Pokémon.",
                    "Selección incompleta",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Pasar a la selección de ataques
        parentFrame.mostrarSeleccionAtaques(jugador1Nombre, jugador2Nombre,
                jugador1Color, jugador2Color,
                equipoJugador1, equipoJugador2);
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
                boton.setBackground(color.brighter());
                boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
                boton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    /**
     * Libera recursos
     */
    public void dispose() {
        if (pokemonRenderer != null) {
            pokemonRenderer.dispose();
        }

        // Liberar todos los CustomGifRenderer en la cuadrícula
        for (Component card : gridPokemonDisponibles.getComponents()) {
            if (card instanceof JPanel) {
                for (Component comp : ((JPanel) card).getComponents()) {
                    if (comp instanceof CustomGifRenderer) {
                        ((CustomGifRenderer) comp).dispose();
                    }
                }
            }
        }

        // Liberar CustomGifRenderer en los slots de equipo
        disposeTeamSlots(panelEquipoJugador1);
        disposeTeamSlots(panelEquipoJugador2);
    }

    /**
     * Libera los recursos de los slots de equipo
     */
    private void disposeTeamSlots(JPanel teamPanel) {
        Component gridPanel = teamPanel.getComponent(1);
        if (gridPanel instanceof JPanel) {
            for (Component slot : ((JPanel) gridPanel).getComponents()) {
                if (slot instanceof JPanel) {
                    for (Component comp : ((JPanel) slot).getComponents()) {
                        if (comp instanceof CustomGifRenderer) {
                            ((CustomGifRenderer) comp).dispose();
                        }
                    }
                }
            }
        }
    }
}