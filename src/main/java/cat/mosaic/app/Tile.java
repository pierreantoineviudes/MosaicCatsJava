package cat.mosaic.app;

import cat.mosaic.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class Tile {
    private final BufferedImage img;
    private final int index;

    public Tile(BufferedImage img, int index) {
        this.index = index;
        this.img = img;
    }

    public int getIndex() {
        return this.index;
    }

    public BufferedImage getImg() {
        return this.img;
    }

    public int[] getMeanRGB() {
        if (this.getImg() == null) return new int[]{0, 0, 0};
        long sumR = 0, sumG = 0, sumB = 0;
        final int width = this.getImg().getWidth();
        final int height = this.getImg().getHeight();
        int total = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = this.getImg().getRGB(x, y);
                sumR += (rgb >> 16) & 0xFF;
                sumG += (rgb >> 8) & 0xFF;
                sumB += rgb & 0xFF;
            }
        }

        int meanR = (int) Math.round(sumR / (double) total);
        int meanG = (int) Math.round(sumG / (double) total);
        int meanB = (int) Math.round(sumB / (double) total);
        return new int[]{meanR, meanG, meanB};
    }
}
