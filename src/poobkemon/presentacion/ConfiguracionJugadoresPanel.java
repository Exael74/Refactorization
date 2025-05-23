package poobkemon.presentacion;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;
/**
 * Panel para configurar los jugadores antes de iniciar una batalla
 */
public class ConfiguracionJugadoresPanel extends JPanel {

    private InterfazPrincipal parentFrame;
    private JTextField jugador1NombreField;
    private JTextField jugador2NombreField;

    // Botones de color
    private ButtonGroup jugador1ColorGroup;
    private ButtonGroup jugador2ColorGroup;
    private Map<String, Color> colorOptions;

    // Selectores de ítems
    private JSpinner[] jugador1ItemSpinners;
    private JSpinner[] jugador2ItemSpinners;

    // Nombres de ítems y descripciones
    private String[] itemNames = {"Potion", "SuperPotion", "HyperPotion", "Revive"};
    private String[] itemDescriptions = {
            "Recupera 20 PS. No usable con Pokémon debilitado.",
            "Recupera 50 PS. No usable con Pokémon debilitado.",
            "Recupera 200 PS. No usable con Pokémon debilitado.",
            "Restaura PS a la mitad de su valor original."
    };

    // Botones principales
    private JButton iniciarBatallaButton;
    private JButton cancelarButton;

    /**
     * Constructor del panel de configuración
     */
    public ConfiguracionJugadoresPanel(InterfazPrincipal parent) {
        this.parentFrame = parent;

        // Inicializar opciones de color
        colorOptions = new HashMap<>();
        colorOptions.put("Rojo", new Color(220, 40, 40));
        colorOptions.put("Azul", new Color(40, 120, 220));
        colorOptions.put("Verde", new Color(40, 200, 40));
        colorOptions.put("Amarillo", new Color(220, 220, 40));

        // Configurar el panel principal
        setLayout(new BorderLayout());
        setBackground(new Color(0, 32, 96)); // Azul oscuro
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Inicializar componentes
        initComponents();
    }

    /**
     * Inicializa todos los componentes del panel
     */
    private void initComponents() {
        // Panel del título
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Panel central con configuraciones de jugadores
        JPanel configPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        configPanel.setOpaque(false);

        // Configuración para cada jugador
        JPanel jugador1Panel = createPlayerPanel(1);
        JPanel jugador2Panel = createPlayerPanel(2);

        configPanel.add(jugador1Panel);
        configPanel.add(jugador2Panel);
        add(configPanel, BorderLayout.CENTER);

        // Panel de botones inferior
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel del título
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("CONFIGURACIÓN DE JUGADORES");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        panel.add(titleLabel);
        return panel;
    }

    /**
     * Crea un panel de configuración para un jugador
     */
    private JPanel createPlayerPanel(int playerNumber) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        panel.setBackground(new Color(0, 32, 96)); // Azul oscuro

        // Título del jugador
        JLabel titleLabel = new JLabel("JUGADOR " + playerNumber);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel para el nombre
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Nombre:");
        nameLabel.setForeground(Color.WHITE);
        JTextField nameField = new JTextField("Jugador " + playerNumber, 15);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Guardar referencia al campo de nombre
        if (playerNumber == 1) {
            jugador1NombreField = nameField;
        } else {
            jugador2NombreField = nameField;
        }

        // Panel para la selección de color
        JPanel colorPanel = new JPanel();
        colorPanel.setOpaque(false);
        JLabel colorLabel = new JLabel("Selecciona tu color:");
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel colorButtonsPanel = new JPanel(new FlowLayout());
        colorButtonsPanel.setOpaque(false);

        ButtonGroup colorGroup = new ButtonGroup();
        if (playerNumber == 1) {
            jugador1ColorGroup = colorGroup;
        } else {
            jugador2ColorGroup = colorGroup;
        }

        // Crear botones de radio para cada color
        for (String colorName : new String[]{"Rojo", "Azul", "Verde", "Amarillo"}) {
            JRadioButton radioButton = new JRadioButton(colorName);
            radioButton.setOpaque(false);
            radioButton.setForeground(Color.WHITE);
            colorGroup.add(radioButton);

            // Preseleccionar colores default (Rojo para jugador 1, Azul para jugador 2)
            if ((playerNumber == 1 && colorName.equals("Rojo")) ||
                    (playerNumber == 2 && colorName.equals("Azul"))) {
                radioButton.setSelected(true);
            }

            // Muestra de color
            JPanel colorSquare = new JPanel();
            colorSquare.setPreferredSize(new Dimension(15, 15));
            colorSquare.setBackground(colorOptions.get(colorName));
            colorSquare.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            radioPanel.setOpaque(false);
            radioPanel.add(radioButton);
            radioPanel.add(colorSquare);

            colorButtonsPanel.add(radioPanel);
        }

        // Panel para la selección de ítems
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        JLabel itemsLabel = new JLabel("Selecciona tus items (máximo 10 en total):");
        itemsLabel.setForeground(Color.WHITE);
        itemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSpinner[] itemSpinners = new JSpinner[itemNames.length];

        // Crear paneles para cada ítem
        for (int i = 0; i < itemNames.length; i++) {
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            itemPanel.setOpaque(false);

            // Icono del ítem (simulado)
            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(createItemIcon(i));
            iconLabel.setPreferredSize(new Dimension(24, 24));

            // Nombre del ítem
            JLabel nameItemLabel = new JLabel(itemNames[i]);
            nameItemLabel.setForeground(Color.WHITE);
            nameItemLabel.setPreferredSize(new Dimension(120, 20));

            // Spinner para la cantidad
            SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 10, 1);
            JSpinner spinner = new JSpinner(model);
            spinner.setPreferredSize(new Dimension(60, 25));
            itemSpinners[i] = spinner;

            // Descripción del ítem
            JLabel descLabel = new JLabel(itemDescriptions[i]);
            descLabel.setForeground(Color.LIGHT_GRAY);
            descLabel.setFont(new Font("Arial", Font.ITALIC, 11));

            itemPanel.add(iconLabel);
            itemPanel.add(nameItemLabel);
            itemPanel.add(spinner);
            itemPanel.add(descLabel);

            itemsPanel.add(itemPanel);
        }

        // Guardar referencia a los spinners
        if (playerNumber == 1) {
            jugador1ItemSpinners = itemSpinners;
        } else {
            jugador2ItemSpinners = itemSpinners;
        }

        // Agregar componentes al panel principal
        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(namePanel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(colorLabel);
        panel.add(colorButtonsPanel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(itemsLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(itemsPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Crea un icono para representar un ítem
     */
    private Icon createItemIcon(int itemIndex) {
        ImageIcon icon = null;

        try {
            // Iconos según el tipo de ítem
            switch (itemIndex) {
                case 0: // Potion
                    icon = new ImageIcon(new ImageIcon("Recursos/Items/potion.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    break;
                case 1: // SuperPotion
                    icon = new ImageIcon(new ImageIcon("Recursos/Items/superpotion.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    break;
                case 2: // HyperPotion
                    icon = new ImageIcon(new ImageIcon("Recursos/Items/hyperpotion.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    break;
                case 3: // Revive
                    icon = new ImageIcon(new ImageIcon("Recursos/Items/revive.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    break;
            }
        } catch (Exception e) {
            // Si no se puede cargar la imagen, crear un icono simple
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, 20, 20);
                    g.drawLine(x, y, x + 20, y + 20);
                    g.drawLine(x + 20, y, x, y + 20);
                }

                @Override
                public int getIconWidth() {
                    return 20;
                }

                @Override
                public int getIconHeight() {
                    return 20;
                }
            };
        }

        return icon != null ? icon : new ImageIcon();
    }

    /**
     * Crea el panel de botones inferior
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        panel.setOpaque(false);

        iniciarBatallaButton = new JButton("INICIAR BATALLA");
        estilizarBoton(iniciarBatallaButton, new Color(200, 30, 30));

        cancelarButton = new JButton("CANCELAR");
        estilizarBoton(cancelarButton, new Color(200, 30, 30));

        // Agregar acciones a los botones
        iniciarBatallaButton.addActionListener(e -> iniciarBatalla());
        cancelarButton.addActionListener(e -> cancelarConfiguracion());

        panel.add(iniciarBatallaButton);
        panel.add(cancelarButton);

        return panel;
    }

    /**
     * Aplica estilo a un botón
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
     * Recolecta la información de configuración y comienza la batalla
     */
    private void iniciarBatalla() {
        // Validar que la suma de ítems no supere 10 para cada jugador
        if (!validarItems(jugador1ItemSpinners) || !validarItems(jugador2ItemSpinners)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cada jugador debe tener un máximo de 10 ítems en total.",
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Recoger información del jugador 1
        String nombre1 = jugador1NombreField.getText().trim();
        String color1 = getSelectedButtonText(jugador1ColorGroup);
        Map<String, Integer> items1 = getSelectedItems(jugador1ItemSpinners);

        // Recoger información del jugador 2
        String nombre2 = jugador2NombreField.getText().trim();
        String color2 = getSelectedButtonText(jugador2ColorGroup);
        Map<String, Integer> items2 = getSelectedItems(jugador2ItemSpinners);

        // Validar nombres de jugadores
        if (nombre1.isEmpty() || nombre2.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Los nombres de los jugadores no pueden estar vacíos.",
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Pasar a la pantalla de selección de Pokémon
        parentFrame.mostrarSeleccionPokemon(nombre1, nombre2, colorOptions.get(color1), colorOptions.get(color2));
    }


    /**
     * Cancela la configuración y vuelve al menú anterior
     */
    private void cancelarConfiguracion() {
        parentFrame.volverDesdeBatalla();
    }

    /**
     * Valida que la suma de ítems no supere el máximo
     */
    private boolean validarItems(JSpinner[] spinners) {
        int total = 0;
        for (JSpinner spinner : spinners) {
            total += (Integer) spinner.getValue();
        }
        return total <= 10;
    }

    /**
     * Obtiene el texto del botón seleccionado en un grupo
     */
    private String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return "";
    }

    /**
     * Recolecta los ítems seleccionados
     */
    private Map<String, Integer> getSelectedItems(JSpinner[] spinners) {
        Map<String, Integer> items = new HashMap<>();
        for (int i = 0; i < spinners.length; i++) {
            int cantidad = (Integer) spinners[i].getValue();
            if (cantidad > 0) {
                items.put(itemNames[i], cantidad);
            }
        }
        return items;
    }
}
