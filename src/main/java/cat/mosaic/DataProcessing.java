package cat.mosaic;

import cat.mosaic.app.ImageProducer;
import cat.mosaic.app.ImageReader;
import cat.mosaic.app.RGB;
import cat.mosaic.app.Tile;
import cat.mosaic.constants.InOutConstants;
import cat.mosaic.utils.HashMapIO;
import cat.mosaic.utils.ImageFileLister;
import cat.mosaic.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static cat.mosaic.constants.InOutConstants.*;
import static cat.mosaic.utils.ImageFileLister.partitionFiles;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class DataProcessing {

    private static final Logger logger = Logger.getLogger(DataProcessing.class.getName());


    static final int numberTilesWidth = 100;
    static final int numberTilesHeight = 100;
    static final int finalImageWidth = numberTilesWidth * TileSize;
    static final int finalImageHeight = numberTilesHeight * TileSize;
    static Map<Integer, RGB> globalHash = new ConcurrentHashMap<>();

    static AtomicInteger imageCounter = new AtomicInteger(0);

    static void main() throws InterruptedException, IOException {
        logger.info("Application started");

        long startTime = System.nanoTime();
        BlockingQueue<Tile> imageQueue = new ArrayBlockingQueue<>(N_IMAGES);
        BufferedImage finalImage = new BufferedImage(finalImageWidth, finalImageHeight, BufferedImage.TYPE_INT_RGB);

        ExecutorService producerPool = Executors.newFixedThreadPool(N_THREAD_PRODUCER);
        ExecutorService readerPool = Executors.newFixedThreadPool(N_THREAD_CONSUMER);
//        init imageFiles please
        ImageFileLister.listImage(InOutConstants.catInputPath);
        List<List<File>> partitions = partitionFiles(ImageFileLister.imageFiles, N_THREAD_PRODUCER);

        for (List<File> part : partitions) {
            producerPool.submit(new ImageProducer(part, imageQueue, imageCounter));
        }
        logger.info("Process termine : " + producerPool.isTerminated());
        producerPool.shutdown();

//        on a submit toutes les images dans la queue, il faut un threadpool de readers
        for (int i = 0; i < N_THREAD_CONSUMER; i++) {
            readerPool.submit(new ImageReader(imageQueue, TileSize, finalImage, globalHash));
        }


        producerPool.awaitTermination(1, TimeUnit.HOURS);
        final Tile POISON = new Tile(new BufferedImage(1, 1, 2), -1);
        for (int i = 0; i < N_THREAD_CONSUMER; i++) {
            try {
                imageQueue.put(POISON);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Process producer termine : " + producerPool.isTerminated());
        readerPool.shutdown();
        readerPool.awaitTermination(1, TimeUnit.HOURS);
        logger.info("Process termine reader : " + readerPool.isTerminated());
        long endTime = System.nanoTime();
        long durationNs = endTime - startTime;
        double durationMs = durationNs / 1_000_000.0;
        double durationSec = durationMs / 1000.0;

        System.out.printf(
                "Total execution time: %.3f ms (%.3f s)%n",
                durationMs,
                durationSec
        );

        logger.info("saving image");
        ImageUtils.writeImageJPG(finalImage, mosaicTilesOutputPath);
        logger.info("after save");
        HashMapIO.save(globalHash, hashOutputPath);
    }
}
