package cat.mosaic.app;

import cat.mosaic.constants.InOutConstants;
import cat.mosaic.utils.HashMapIO;
import cat.mosaic.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cat.mosaic.DataProcessing.finalImageWidth;
import static cat.mosaic.constants.InOutConstants.*;

public class Mosaic {

    private final BufferedImage inputImage;
    private final Logger logger = Logger.getLogger(Mosaic.class.getName());

    public static int findNearestRGBIndex(RGB target, Map<Integer, RGB> tileDict) {
        int nearestIndex = -1;
        double minDistanceSquared = Double.MAX_VALUE;

        for (Map.Entry<Integer, RGB> entry : tileDict.entrySet()) {
            RGB current = entry.getValue();
            double distanceSquared =
                    Math.pow(current.getR() - target.getR(), 2) +
                            Math.pow(current.getG() - target.getG(), 2) +
                            Math.pow(current.getB() - target.getB(), 2);

            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                nearestIndex = entry.getKey();
            }
        }

        return nearestIndex;
    }

    public Mosaic() throws IOException {
        long t0 = System.nanoTime();
        this.inputImage = ImageUtils.openImage(new File(String.valueOf(InOutConstants.INPUT_IMAGE_PATH)));
        long t1 = System.nanoTime();

        logger.log(Level.INFO,
                "Input image loaded in {0,number,#.##} ms",
                (t1 - t0) / 1e6);
    }

    public void main() throws IOException, ClassNotFoundException {

        long tStartTotal = System.nanoTime();

        // Load hashmap
        long t0 = System.nanoTime();
        Map<Integer, RGB> myTileDict = HashMapIO.load(String.valueOf(InOutConstants.HASH_OUTPUT_PATH));
        long t1 = System.nanoTime();
        logger.log(Level.INFO,
                "Tile hashmap loaded in {0,number,#.##} ms",
                (t1 - t0) / 1e6);

        // Load tiles image
        long t2 = System.nanoTime();
        BufferedImage TilesImage = ImageUtils.openImage(new File(String.valueOf(InOutConstants.MOSAIC_TILES_PATH)));
        long t3 = System.nanoTime();
        logger.log(Level.INFO,
                "Tiles image loaded in {0,number,#.##} ms",
                (t3 - t2) / 1e6);

        // Create output images
        long t4 = System.nanoTime();
        BufferedImage resizedInputImage =
                new BufferedImage(nTilesOutput, nTilesOutput, BufferedImage.TYPE_INT_RGB);
        BufferedImage outputImage =
                new BufferedImage(nTilesOutput * TileSize,
                        nTilesOutput * TileSize,
                        BufferedImage.TYPE_INT_RGB);
        long t5 = System.nanoTime();
        logger.log(Level.INFO,
                "Output images allocated in {0,number,#.##} ms",
                (t5 - t4) / 1e6);

        // Resize input image
        long t6 = System.nanoTime();
        resizedInputImage = ImageUtils.resize(this.inputImage, nTilesOutput);
        long t7 = System.nanoTime();
        logger.log(Level.INFO,
                "Input image resized in {0,number,#.##} ms",
                (t7 - t6) / 1e6);

        int width = resizedInputImage.getWidth();
        int height = resizedInputImage.getHeight();

        // Mosaic generation loop
        long tLoopStart = System.nanoTime();
        long tNearestSearch = 0;

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {

                int rgb = resizedInputImage.getRGB(x, y);
                int R = (rgb >> 16) & 0xFF;
                int G = (rgb >> 8) & 0xFF;
                int B = rgb & 0xFF;

                long tSearchStart = System.nanoTime();
                int index = findNearestRGBIndex(new RGB(R, G, B), myTileDict);
                tNearestSearch += System.nanoTime() - tSearchStart;

                int row = index / (finalImageWidth / TileSize);
                int col = index % (finalImageWidth / TileSize);
                int i = col * TileSize;
                int j = row * TileSize;

                BufferedImage tile =
                        TilesImage.getSubimage(i, j, TileSize, TileSize);

                int x0 = x * TileSize;
                int y0 = y * TileSize;
                outputImage.getRaster().setRect(x0, y0, tile.getRaster());
            }
        }

        long tLoopEnd = System.nanoTime();

        logger.log(Level.INFO,
                "Mosaic generation loop: {0,number,#.##} ms",
                (tLoopEnd - tLoopStart) / 1e6);
        logger.log(Level.INFO,
                "  └─ Nearest RGB search only: {0,number,#.##} ms",
                tNearestSearch / 1e6);

        // Write output image
        long t8 = System.nanoTime();
        ImageUtils.writeImageJPG(outputImage, String.valueOf(InOutConstants.OUTPUT_IMAGE_PATH));
        long t9 = System.nanoTime();
        logger.log(Level.INFO,
                "Output image written in {0,number,#.##} ms",
                (t9 - t8) / 1e6);

        long tEndTotal = System.nanoTime();

        logger.log(Level.INFO,
                "TOTAL execution time: {0,number,#.##} ms",
                (tEndTotal - tStartTotal) / 1e6);
    }
}
