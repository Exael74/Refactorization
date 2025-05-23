package poobkemon.dominio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase mejorada para la gestión de GIFs de Pokémon
 */
public class GifManager {

    private Map<String, ImageIcon> gifCache;
    private Map<String, Map<Dimension, ImageIcon>> resizedGifCache;
    private boolean usingFallbackImages = false;
    private String gifPath; // Ruta real a los GIFs
    private ExecutorService loaderPool;

    // Lista de Pokémon disponibles
    private static final String[] POKEMON_NAMES = {
            "Absol", "Articuno", "Banette", "Blaziken", "Charizard", "Crobat",
            "Dragonite", "Dugtrio", "Flygon", "Gardevoir", "Gengar", "Glalie",
            "Golem", "Gyarados", "Hariyama", "Heracross", "Machamp", "Metagross",
            "Mewtwo", "Nidoking", "Pidgeot", "Pikachu", "Raikou", "Rayquaza",
            "Salamence", "Sceptile", "Scyther", "Slaking", "Snorlax", "Steelix",
            "Swampert", "Tyranitar", "Umbreon", "Venusaur"
    };

    /**
     * Constructor que inicializa el sistema de manejo de GIFs
     */
    public GifManager() {
        gifCache = new HashMap<>();
        resizedGifCache = new HashMap<>();
        loaderPool = Executors.newFixedThreadPool(3);

        // Usar directamente la ruta conocida
        gifPath = "C:\\Users\\stive\\Desktop\\Poobkemon\\Recursos\\PokemonsPokedex\\";

        // Verificar si la ruta existe
        File dirFile = new File(gifPath);
        if (dirFile.exists() && dirFile.isDirectory()) {
            System.out.println("✅ Directorio de GIFs encontrado en: " + gifPath);
            File[] files = dirFile.listFiles();
            if (files != null) {
                System.out.println("Archivos encontrados: " + files.length);
                for (File file : files) {
                    System.out.println(" - " + file.getName());
                }
                usingFallbackImages = false;
            } else {
                System.err.println("❌ No se pudo listar los archivos en el directorio");
                usingFallbackImages = true;
            }
        } else {
            System.err.println("❌ Directorio de GIFs no encontrado: " + gifPath);
            // Intentar encontrar el directorio con otros métodos
            locateGifDirectory();
        }

        if (usingFallbackImages) {
            System.out.println("Generando imágenes de fallback para todos los Pokémon");
            preloadFallbackImages();
        }
    }

    /**
     * Intenta localizar el directorio de GIFs
     */
    private void locateGifDirectory() {
        // Posibles rutas donde pueden estar los GIFs
        String[] possiblePaths = {
                "C:\\Users\\stive\\Desktop\\Poobkemon\\Recursos\\PokemonsPokedex\\",
                "Recursos\\PokemonsPokedex\\",
                ".\\Recursos\\PokemonsPokedex\\",
                "..\\Recursos\\PokemonsPokedex\\",
                System.getProperty("user.dir") + "\\Recursos\\PokemonsPokedex\\"
        };

        for (String path : possiblePaths) {
            File dir = new File(path);
            System.out.println("Verificando ruta: " + dir.getAbsolutePath() + " - Existe: " + dir.exists());

            if (dir.exists() && dir.isDirectory()) {
                System.out.println("¡Encontrado directorio de GIFs en: " + dir.getAbsolutePath());

                // Listar archivos para depurar
                File[] files = dir.listFiles();
                if (files != null) {
                    System.out.println("Contenido del directorio:");
                    for (File file : files) {
                        System.out.println("  - " + file.getName());
                    }
                }

                // Verificar si hay archivos GIF
                File[] gifFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".gif"));
                if (gifFiles != null && gifFiles.length > 0) {
                    gifPath = dir.getAbsolutePath() + File.separator;
                    System.out.println("Usando directorio de GIFs: " + gifPath);
                    usingFallbackImages = false;
                    return;
                }
            }
        }

        System.err.println("⚠️ ERROR: No se pudo encontrar el directorio de GIFs. Usando imágenes generadas.");
        usingFallbackImages = true;
    }

    /**
     * Genera imágenes de fallback para todos los Pokémon
     */
    private void preloadFallbackImages() {
        for (String pokemonName : POKEMON_NAMES) {
            ImageIcon icon = createFallbackForPokemon(pokemonName);
            gifCache.put(pokemonName, icon);
        }
    }

    // Método para precarga asíncrona
    public void precargarPokemon(java.util.Collection<String> pokemonNames) {
        for (String name : pokemonNames) {
            loaderPool.submit(() -> {
                getPokemonGif(name); // Esto cargará y cacheará
            });
        }
    }

    /**
     * Obtiene un GIF del Pokémon especificado
     */
    public ImageIcon getPokemonGif(String pokemonName) {
        // Si ya tenemos este GIF en caché, devolverlo
        if (gifCache.containsKey(pokemonName)) {
            return gifCache.get(pokemonName);
        }

        // Si estamos en modo fallback, generar una imagen para este Pokémon
        if (usingFallbackImages) {
            ImageIcon icon = createFallbackForPokemon(pokemonName);
            gifCache.put(pokemonName, icon);
            return icon;
        }

        // Intentar cargar el GIF real
        try {
            // Ruta con nombre exacto del archivo
            String gifFileName = pokemonName + "_pokedex.gif";
            File gifFile = new File(gifPath + gifFileName);

            System.out.println("Intentando cargar: " + gifFile.getAbsolutePath());
            System.out.println("Archivo existe: " + gifFile.exists());

            if (gifFile.exists() && gifFile.canRead()) {
                // Cargar directamente con ruta absoluta
                ImageIcon icon = new ImageIcon(gifFile.getAbsolutePath());

                // Verificar que la imagen se haya cargado correctamente
                if (icon.getIconWidth() > 0) {
                    System.out.println("✅ GIF cargado correctamente: " + gifFileName);
                    gifCache.put(pokemonName, icon);
                    return icon;
                } else {
                    System.err.println("❌ Error: GIF cargado pero con ancho 0 - " + gifFileName);
                }
            } else {
                System.err.println("❌ Error: Archivo no existe o no se puede leer - " + gifFileName);
            }

            // Si llegamos aquí, intentar con versión en minúsculas
            gifFileName = pokemonName.toLowerCase() + "_pokedex.gif";
            gifFile = new File(gifPath + gifFileName);

            if (gifFile.exists() && gifFile.canRead()) {
                ImageIcon icon = new ImageIcon(gifFile.getAbsolutePath());
                if (icon.getIconWidth() > 0) {
                    System.out.println("✅ GIF cargado correctamente (lowercase): " + gifFileName);
                    gifCache.put(pokemonName, icon);
                    return icon;
                }
            }

            // Si todavía no tenemos éxito, buscar archivos que contengan el nombre
            File dir = new File(gifPath);
            File[] files = dir.listFiles((d, name) ->
                    name.toLowerCase().contains(pokemonName.toLowerCase()) && name.toLowerCase().endsWith(".gif"));

            if (files != null && files.length > 0) {
                ImageIcon icon = new ImageIcon(files[0].getAbsolutePath());
                if (icon.getIconWidth() > 0) {
                    System.out.println("✅ GIF encontrado por búsqueda: " + files[0].getName());
                    gifCache.put(pokemonName, icon);
                    return icon;
                }
            }

            // Como último recurso, devolver una imagen de fallback
            System.out.println("❌ No se encontró ningún GIF para: " + pokemonName);
            return createFallbackForPokemon(pokemonName);
        } catch (Exception e) {
            System.err.println("❌ Error cargando GIF: " + e.getMessage());
            e.printStackTrace();
            return createFallbackForPokemon(pokemonName);
        }
    }

    /**
     * Crea una imagen de fallback para un Pokémon específico
     */
    private ImageIcon createFallbackForPokemon(String pokemonName) {
        System.out.println("🔄 Creando imagen de fallback para: " + pokemonName);
        int hash = pokemonName.hashCode();
        Color color = new Color(
                Math.abs(hash % 256),
                Math.abs((hash / 256) % 256),
                Math.abs((hash / 65536) % 256)
        );
        return createPokemonFallbackIcon(pokemonName, color);
    }

    /**
     * Crea un icono colorido basado en el nombre del Pokémon
     */
    private ImageIcon createPokemonFallbackIcon(String pokemonName, Color primaryColor) {
        int size = 200;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        // Configurar un renderizado de alta calidad
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo del icono
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, size, size);

        // Dibujar una silueta estilo Pokémon
        g2.setColor(primaryColor);
        g2.fillOval(size/4, size/4, size/2, size/2);

        // Añadir un poco de dimensión
        g2.setColor(primaryColor.brighter());
        g2.fillOval(size/4 + 10, size/4 + 10, size/2 - 20, size/2 - 20);

        // Añadir detalle
        g2.setColor(Color.WHITE);
        g2.fillOval(size/2 - 10, size/2 - 10, 20, 20);

        // Añadir borde
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(size/4, size/4, size/2, size/2);

        // Añadir el nombre del Pokémon
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(pokemonName);
        g2.drawString(pokemonName, (size - textWidth) / 2, size - 20);

        g2.dispose();

        return new ImageIcon(img);
    }

    /**
     * Obtiene una versión redimensionada del GIF
     */
    public ImageIcon getResizedPokemonGif(String pokemonName, int width, int height) {
        // Si alguna dimensión es cero o negativa, usar valores mínimos
        width = Math.max(10, width);
        height = Math.max(10, height);

        Dimension size = new Dimension(width, height);

        // Verificar si ya tenemos esta versión redimensionada
        if (resizedGifCache.containsKey(pokemonName) &&
                resizedGifCache.get(pokemonName).containsKey(size)) {
            return resizedGifCache.get(pokemonName).get(size);
        }

        // Obtener el icono original
        ImageIcon originalIcon = getPokemonGif(pokemonName);

        // Imprimir el tamaño original para diagnóstico
        System.out.println("Redimensionando GIF de " + pokemonName + ": Original=" +
                originalIcon.getIconWidth() + "x" + originalIcon.getIconHeight() +
                " -> Nuevo=" + width + "x" + height);

        try {
            // Redimensionar con mayor calidad
            Image image = originalIcon.getImage();
            Image resizedImage = getScaledInstance(image, width, height);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            // Guardar en caché
            if (!resizedGifCache.containsKey(pokemonName)) {
                resizedGifCache.put(pokemonName, new HashMap<>());
            }
            resizedGifCache.get(pokemonName).put(size, resizedIcon);

            return resizedIcon;
        } catch (Exception e) {
            System.err.println("Error al redimensionar GIF: " + e.getMessage());
            e.printStackTrace();
            return originalIcon; // Devolver el original si hay error
        }
    }

    /**
     * Escala una imagen con buena calidad
     */
    private Image getScaledInstance(Image img, int width, int height) {
        // Para tamaños pequeños, usar bilinear es más rápido y suficiente
        if (width < 100 || height < 100) {
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(img, 0, 0, width, height, null);
            g2d.dispose();
            return resized;
        }

        // Para tamaños más grandes, usar escalado multi-paso para mejor calidad
        return img.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
    }

    /**
     * Obtiene la lista de nombres de Pokémon disponibles
     */
    public String[] getAvailablePokemonNames() {
        return POKEMON_NAMES; // Usar siempre la lista predefinida para consistencia
    }

    // Liberar recursos al cerrar
    public void dispose() {
        if (loaderPool != null) {
            loaderPool.shutdownNow();
            loaderPool = null;
        }
    }
}