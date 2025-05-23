package poobkemon.dominio;

import java.util.*;

/**
 * Clase que gestiona el catálogo de Pokémon disponibles
 */
public class PokemonCatalogo {

    private Map<String, Pokemon> pokemones;

    /**
     * Constructor que inicializa el catálogo
     */
    public PokemonCatalogo() {
        pokemones = new HashMap<>();
        inicializarCatalogo();
    }

    /**
     * Inicializa el catálogo con los datos de los Pokémon
     */
    private void inicializarCatalogo() {
        // Aquí se cargan los datos de ejemplo para cada Pokémon
        // En una aplicación real, estos datos vendrían de una base de datos

        // Pikachu
        pokemones.put("Pikachu", crearPokemon(
                "Pikachu",
                "Eléctrico",
                "Cuando Pikachu se encuentra con algo nuevo, le lanza una descarga eléctrica. Si ves alguna baya chamuscada, es prueba palpable de que este Pokémon ha estado cerca.",
                25,
                35,
                55,
                40,
                90,
                new String[]{"Impactrueno", "Ataque Rápido", "Rayo", "Cola Férrea"}
        ));

        // Charizard
        pokemones.put("Charizard", crearPokemon(
                "Charizard",
                "Fuego/Volador",
                "Charizard se dedica a volar por los cielos en busca de oponentes fuertes. Echa fuego por la boca y es capaz de derretir cualquier cosa.",
                36,
                78,
                84,
                78,
                100,
                new String[]{"Lanzallamas", "Garra Dragón", "Vuelo", "Sofoco"}
        ));

        // Mewtwo
        pokemones.put("Mewtwo", crearPokemon(
                "Mewtwo",
                "Psíquico",
                "Su ADN es casi el mismo que el de Mew, pero su tamaño y carácter son muy diferentes.",
                70,
                106,
                110,
                90,
                130,
                new String[]{"Psíquico", "Bola Sombra", "Rayo Hielo", "Premonición"}
        ));

        // Gengar
        pokemones.put("Gengar", crearPokemon(
                "Gengar",
                "Fantasma/Veneno",
                "Para quitarle la vida a su presa, se desliza en su sombra y espera su oportunidad en silencio.",
                40,
                60,
                65,
                60,
                110,
                new String[]{"Bola Sombra", "Puño Sombra", "Hipnosis", "Tinieblas"}
        ));

        // Añadimos datos para el resto de Pokémon de la lista
        String[] pokemonNames = {"Absol", "Articuno", "Banette", "Blaziken", "Crobat",
                "Dragonite", "Dugtrio", "Flygon", "Gardevoir", "Glalie",
                "Golem", "Gyarados", "Hariyama", "Heracross", "Machamp",
                "Metagross", "Nidoking", "Pidgeot", "Raikou", "Rayquaza",
                "Salamence", "Sceptile", "Scyther", "Slaking", "Snorlax",
                "Steelix", "Swampert", "Tyranitar", "Umbreon", "Venusaur"};

        for (String name : pokemonNames) {
            if (!pokemones.containsKey(name)) {
                pokemones.put(name, createDefaultPokemon(name));
            }
        }
    }

    /**
     * Crea un Pokémon con la nueva estructura
     */
    private Pokemon crearPokemon(String nombre, String tipo, String descripcion, int nivel,
                                 int hp, int ataque, int defensa, int velocidad, String[] movimientos) {
        // Guardar la descripción para acceso posterior
        guardarDescripcion(nombre, descripcion);

        // Usar el constructor compatible con el nuevo modelo de Pokemon
        return new Pokemon(nombre, tipo, nivel, hp, ataque, defensa, velocidad, movimientos);
    }

    /**
     * Almacenamiento de descripciones para mantener compatibilidad con Poobkedex
     */
    private static final Map<String, String> descripciones = new HashMap<>();

    private void guardarDescripcion(String nombre, String descripcion) {
        descripciones.put(nombre.toLowerCase(), descripcion);
    }

    /**
     * Obtiene la descripción de un Pokémon (método estático para compatibilidad)
     */
    public static String getDescripcion(String nombre) {
        return descripciones.getOrDefault(nombre.toLowerCase(), "No hay información disponible");
    }

    /**
     * Crea un Pokémon con datos predeterminados para ejemplos
     */
    private Pokemon createDefaultPokemon(String nombre) {
        return crearPokemon(
                nombre,
                "Desconocido",
                "Información no disponible para " + nombre + ". Actualizar datos de la Poobkedex.",
                50,
                80,
                80,
                80,
                80,
                new String[]{"Ataque 1", "Ataque 2", "Ataque 3", "Ataque 4"}
        );
    }

    /**
     * Obtiene un Pokémon del catálogo por su nombre
     */
    public Pokemon getPokemon(String nombre) {
        return pokemones.getOrDefault(nombre, null);
    }

    /**
     * Obtiene la lista de nombres de todos los Pokémon en el catálogo
     */
    public Set<String> getNombresPokemon() {
        return pokemones.keySet();
    }

    /**
     * Obtiene todos los Pokémon del catálogo
     */
    public Collection<Pokemon> getTodosPokemon() {
        return pokemones.values();
    }
}