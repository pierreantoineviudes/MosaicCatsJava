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
    private final Tile POISON = new Tile(new BufferedImage(1, 1, 2), -1);


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
                for (int i = 0; i < N_THREAD_CONSUMER; i++) {
                    try {
                        queue.put(POISON);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
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
