package cat.mosaic.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public class ImageUtils {
    private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

    public static BufferedImage openImage(File file) throws IOException {
        try {
            BufferedImage img = ImageIO.read(file);

            if (img == null) {
                throw new IOException("ImageIO returned null");
            }

            // Force RGB to avoid CMYK / grayscale issues
            BufferedImage rgb = new BufferedImage(
                    img.getWidth(),
                    img.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            rgb.getGraphics().drawImage(img, 0, 0, null);

            return rgb;

        } catch (Exception e) {
            logger.severe("Failed to open image: " + file.getAbsolutePath());
            throw e;
        }
    }


    public static int[] getPixelsImage(BufferedImage bufferedImage, int x, int y) {
        int rgb = bufferedImage.getRGB(x, y);

        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return new int[]{r, g, b};
    }


    public static void writeImageJPG(BufferedImage bufferedImage, String outputPath) throws IOException {
        logger.info("saving to " + outputPath);
        ImageIO.write(bufferedImage, "png", new File(outputPath));
    }


    public static BufferedImage resize(BufferedImage img, int TileSize) {
        BufferedImage resized = new BufferedImage(TileSize, TileSize, img.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, TileSize, TileSize, null);
        g.dispose();
        return resized;
    }
}
