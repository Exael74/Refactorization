package poobkemon.dominio;

/**
 * Representa un ataque o movimiento que puede usar un Pokémon
 */
public class Ataque {
    private String nombre;
    private String tipo;       // Fuego, Agua, Planta, etc.
    private String categoria;  // Físico, Especial, Estado
    private int poder;
    private int precision;
    private int pp;            // Puntos de poder (usos disponibles)
    private String efecto;     // Descripción del efecto especial si lo tiene

    /**
     * Constructor completo
     */
    public Ataque(String nombre, String tipo, String categoria, int poder, int precision, int pp, String efecto) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.categoria = categoria;
        this.poder = poder;
        this.precision = precision;
        this.pp = pp;
        this.efecto = efecto;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public int getPoder() { return poder; }
    public int getPrecision() { return precision; }
    public int getPp() { return pp; }
    public String getEfecto() { return efecto; }

    @Override
    public String toString() {
        return nombre + " [" + tipo + "] - " + poder;
    }
}