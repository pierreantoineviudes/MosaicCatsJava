package cat.mosaic.app;

import cat.mosaic.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;


public class ImageReader implements Runnable {
    private final BlockingQueue<Tile> queue;
    private final int TileSize;
    private final BufferedImage finalImage;
    private static final Logger logger = Logger.getLogger(ImageReader.class.getName());
    private final Map<Integer, RGB> myGlobalHash;

    public ImageReader(BlockingQueue<Tile> queue, int TileSize, BufferedImage finalImage, Map<Integer, RGB> myGlobalHash) {
        this.queue = queue;
        this.TileSize = TileSize;
        this.finalImage = finalImage;
        this.myGlobalHash = myGlobalHash;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Tile tile = this.queue.take();
                int index = tile.getIndex();

                if (index == -1) {
                    logger.info("breaking");
//                    stop the thread
                    break;
                }
                BufferedImage img = tile.getImg();
                BufferedImage resized = ImageUtils.resize(img, this.TileSize);
                int row = index / (finalImage.getWidth() / TileSize);
                int col = index % (finalImage.getWidth() / TileSize);
                int x0 = col * TileSize;
                int y0 = row * TileSize;
//                logger.info(tile.getMeanRGB().toString());
                int[] rgb = tile.getMeanRGB();
                RGB rgbObject = new RGB(rgb[0], rgb[1], rgb[2]);
                myGlobalHash.put(index, rgbObject);

                synchronized (finalImage) {
                    finalImage.getRaster().setRect(x0, y0, resized.getRaster());
                }
            } catch (InterruptedException e) {
                logger.severe("error" + e);
                Thread.currentThread().interrupt();
                break;
            }
        }

    }
}
