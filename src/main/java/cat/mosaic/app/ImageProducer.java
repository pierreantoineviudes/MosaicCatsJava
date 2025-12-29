package cat.mosaic.app;

import cat.mosaic.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static cat.mosaic.constants.InOutConstants.N_IMAGES;
import static cat.mosaic.constants.InOutConstants.N_THREAD_CONSUMER;


public class ImageProducer implements Runnable {
    private final List<File> files;
    private final BlockingQueue<Tile> queue;
    private final AtomicInteger counter;



    public ImageProducer(List<File> files, BlockingQueue<Tile> queue, AtomicInteger counter) {
        this.files = files;
        this.queue = queue;
        this.counter = counter;

    }

    @Override
    public void run() {
        for (File file : files) {

            int current = counter.getAndIncrement();
            if (current >= N_IMAGES) {
//                le thread qui contient le poison coupe tout avant la fin d'execution des autres threads ?
//                il est possible que le poison arrive avant les autres threads
                break;
            }

            BufferedImage img = ImageUtils.openImage(file);

            try {
                queue.put(new Tile(img, current));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
