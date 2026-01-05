package cat.mosaic.app;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import static cat.mosaic.DataProcessing.finalImageWidth;
import static cat.mosaic.app.Mosaic.findNearestRGBIndex;
import static cat.mosaic.constants.InOutConstants.TileSize;


public class MosaicLineWriter implements Runnable {

    private final int x;
    private final int width;
    private final BufferedImage resizedInputImage;
    private final BufferedImage outputImage;
    private final Map<Integer, RGB> myTileDict;
    private final BufferedImage[] tiles;

    // Thread-safe counters for timing
    private final LongAdder tNearestSearch;
    private final LongAdder tSetRect;

    public MosaicLineWriter(int x, int width, BufferedImage resizedInputImage, BufferedImage outputImage, Map<Integer, RGB> myTileDict, BufferedImage[] tiles, LongAdder tNearestSearch, LongAdder tSetRect) {
        this.x = x;
        this.width = width;
        this.resizedInputImage = resizedInputImage;
        this.outputImage = outputImage;
        this.myTileDict = myTileDict;
        this.tiles = tiles;

        this.tNearestSearch = tNearestSearch;
        this.tSetRect = tSetRect;
    }

    @Override
    public void run() {
        for (int y = 0; y < width; y++) {

            int rgb = resizedInputImage.getRGB(x, y);
            int R = (rgb >> 16) & 0xFF;
            int G = (rgb >> 8) & 0xFF;
            int B = rgb & 0xFF;

            // Track time for nearest tile search
            long tStartSearch = System.nanoTime();
            int index = findNearestRGBIndex(new RGB(R, G, B), myTileDict);
            tNearestSearch.add(System.nanoTime() - tStartSearch);

            BufferedImage tile = tiles[index];

            int x0 = x * TileSize;
            int y0 = y * TileSize;
            long tStartSet = System.nanoTime();
            outputImage.getRaster().setRect(x0, y0, tile.getRaster());
            tSetRect.add(System.nanoTime() - tStartSet);
        }

    }
}
