package cat.mosaic.app;

import cat.mosaic.constants.InOutConstants;
import cat.mosaic.utils.HashMapIO;
import cat.mosaic.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cat.mosaic.DataProcessing.*;
import static cat.mosaic.constants.InOutConstants.*;

public class Mosaic {

    private final BufferedImage inputImage;
    private final Logger logger = Logger.getLogger(Mosaic.class.getName());

    public static int findNearestRGBIndex(RGB target, Map<Integer, RGB> tileDict) {
//        TODO: would be better to invert the tileDict than performing a search ...
//        Search for value of target, while no value furnished prendre les voisins, sinon prndre une tile aléatoire
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

    public void main() throws IOException, ClassNotFoundException, InterruptedException {

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
        BufferedImage resizedInputImage = new BufferedImage(nTilesOutput, nTilesOutput, BufferedImage.TYPE_INT_RGB);
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

        // Cache tile Images
        int numTiles = numberTilesWidth * numberTilesHeight;
        BufferedImage[] tiles = new BufferedImage[numTiles];
        for (int idx = 0; idx < numTiles; idx++) {
            int row = idx / numberTilesHeight;
            int col = idx % numberTilesWidth;
            tiles[idx] = TilesImage.getSubimage(
                    col * TileSize,
                    row * TileSize,
                    TileSize,
                    TileSize
            );
        }


        // Mosaic generation loop
        LongAdder tNearestSearch = new LongAdder();
        LongAdder tSetRect = new LongAdder();
        long tconcurrentloopStart = System.nanoTime();
        ExecutorService mosaicPool = Executors.newFixedThreadPool(N_THREAD_CONSUMER);
        for (int x = 0; x < height; x++) {
            mosaicPool.submit(new MosaicLineWriter(x, width, resizedInputImage, outputImage, myTileDict, tiles, tNearestSearch, tSetRect));
        }
        mosaicPool.shutdown();
        mosaicPool.awaitTermination(5, TimeUnit.MINUTES);
        System.out.println("Total findNearestRGBIndex time (ms): " + tNearestSearch.sum() / 1_000_000);
        System.out.println("Total setRect time (ms): " + tSetRect.sum() / 1_000_000);
        long tconcurrentloopEnd = System.nanoTime();
        logger.log(Level.INFO,
                "  └─ time for concurrent loop to proceed {0,number,#.##} ms",
                (tconcurrentloopEnd - tconcurrentloopStart) / 1e6);

        // Write output image
        long t8 = System.nanoTime();
//        ImageUtils.writeImageJPG(outputImage, String.valueOf(InOutConstants.OUTPUT_IMAGE_PATH));
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
