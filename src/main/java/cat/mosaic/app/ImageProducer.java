package cat.mosaic.app;

import cat.mosaic.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static cat.mosaic.constants.InOutConstants.N_IMAGES;
import static cat.mosaic.constants.InOutConstants.N_THREAD_CONSUMER;


public class ImageProducer implements Runnable {
    private final List<File> files;
    private final BlockingQueue<Tile> queue;
    private final AtomicInteger counter;
    private final Logger logger = Logger.getLogger(ImageProducer.class.getName());


    public ImageProducer(List<File> files, BlockingQueue<Tile> queue, AtomicInteger counter) {
        this.files = files;
        this.queue = queue;
        this.counter = counter;

    }

    @Override
    public void run() {
        for (File file : files) {


            try {
                BufferedImage img = ImageUtils.openImage(file);
                int current = counter.getAndIncrement();
                if (current >= N_IMAGES) {
                    break;
                }
                queue.put(new Tile(img, current));
            } catch (Exception e) {
                logger.severe("img not opened, trying not to interrupt thread, err : " + e);
            }
        }
    }
}
