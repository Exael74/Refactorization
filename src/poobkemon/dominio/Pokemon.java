package poobkemon.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un Pokémon en el juego
 */
public class Pokemon {
    private String nombre;
    private String tipo;
    private int nivel;
    private int hp;
    private int ataque;
    private int defensa;
    private int velocidad;
    private String[] movimientos; // Mantener el campo existente para compatibilidad
    private List<Ataque> ataques; // Nuevo campo para la funcionalidad de selección de ataques

    /**
     * Constructor para un Pokémon
     */
    public Pokemon(String nombre, String tipo, int nivel, int hp, int ataque, int defensa, int velocidad, String[] movimientos) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.nivel = nivel;
        this.hp = hp;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.movimientos = movimientos;
        this.ataques = new ArrayList<>(); // Inicializar la lista de ataques vacía
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public int getNivel() { return nivel; }
    public int getHp() { return hp; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getVelocidad() { return velocidad; }

    /**
     * Devuelve los movimientos como array de strings (para compatibilidad)
     */
    public String[] getMovimientos() {
        return movimientos;
    }

    /**
     * Establece los ataques del Pokémon con objetos Ataque
     */
    public void setAtaques(List<Ataque> ataques) {
        this.ataques = ataques;

        // Actualizar también el array de movimientos para mantener coherencia
        if (ataques != null && !ataques.isEmpty()) {
            movimientos = new String[ataques.size()];
            for (int i = 0; i < ataques.size(); i++) {
                movimientos[i] = ataques.get(i).getNombre();
            }
        }
    }

    /**
     * Obtiene los ataques del Pokémon como objetos Ataque
     */
    public List<Ataque> getAtaquesObjetos() {
        return ataques != null ? ataques : new ArrayList<>();
    }

    /**
     * Convierte de String[] a List<Ataque> cuando sea necesario
     * (método auxiliar para la transición)
     */
    public void convertirMovimientosAAtaques(List<Ataque> todosLosAtaques) {
        if (ataques == null || ataques.isEmpty()) {
            ataques = new ArrayList<>();

            if (movimientos != null) {
                for (String nombreMovimiento : movimientos) {
                    for (Ataque ataque : todosLosAtaques) {
                        if (ataque.getNombre().equals(nombreMovimiento)) {
                            ataques.add(ataque);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return nombre + " (Nivel " + nivel + ")";
    }
}