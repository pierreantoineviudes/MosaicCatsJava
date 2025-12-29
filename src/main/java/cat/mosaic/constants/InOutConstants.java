package cat.mosaic.constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class InOutConstants {

    private static final Path BASE_DIR =
            Paths.get(System.getProperty("user.dir"));

    public static final Path CAT_INPUT_PATH = Paths.get("D:\\fichiers_pa\\dev\\data_cats\\cats");

    public static final Path MOSAIC_TILES_PATH = BASE_DIR.resolve("atlas.png");

    public static final Path HASH_OUTPUT_PATH = BASE_DIR.resolve("TileDict.sr");

    public static final Path INPUT_DIR = BASE_DIR.resolve("input_images");

    public static final Path OUTPUT_DIR = BASE_DIR.resolve("output_images");

    public static final Path INPUT_IMAGE_PATH = INPUT_DIR.resolve("img.png");

    public static final Path OUTPUT_IMAGE_PATH = OUTPUT_DIR.resolve("output_mosaic.png");

    public static final int TileSize = 64;
    public static final int N_THREAD_PRODUCER = 8;
    public static final int N_THREAD_CONSUMER = 8;
    public static final int N_IMAGES = 8000;
    public static int nTilesOutput = 200;
}
