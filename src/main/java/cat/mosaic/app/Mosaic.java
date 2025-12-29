package cat.mosaic.app;

import cat.mosaic.utils.HashMapIO;
import cat.mosaic.utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static cat.mosaic.DataProcessing.finalImageWidth;
import static cat.mosaic.constants.InOutConstants.*;

public class Mosaic {

    private BufferedImage inputImage;
    private int nTilesOutput = 100;
    private String PathOutputImage = "D:\\fichiers_pa\\dev\\MosaicCatsJava\\output_mosaic";

//    outputHeight = TileSize * InputHeight
//    outputWidth = TileSize * InputWidth

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

        return nearestIndex; // index of the closest RGB
    }


    public Mosaic() throws IOException {
        this.inputImage = ImageUtils.openImage(new File(inputImagePath));
    }

    public void main() throws IOException, ClassNotFoundException {
//        Open the hashmap with tile coordinates
        Map<Integer, RGB> myTileDict = HashMapIO.load(hashOutputPath);
//        open the tile image
        BufferedImage TilesImage = ImageUtils.openImage(new File(mosaicTilesOutputPath + ".jpg"));

//        create new image to receive
        BufferedImage resizedInputImage = new BufferedImage(nTilesOutput, nTilesOutput, BufferedImage.TYPE_INT_RGB);
        BufferedImage outputImage = new BufferedImage(nTilesOutput * TileSize, nTilesOutput * TileSize, BufferedImage.TYPE_INT_RGB);

//        resize input image
        resizedInputImage = ImageUtils.resize(this.inputImage, nTilesOutput);
        int width = resizedInputImage.getWidth();
        int height = resizedInputImage.getHeight();

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
//                trouver la tile dont la valeur moyenne est la plus proche de la valeur du pixel en x, y
                int rgb = resizedInputImage.getRGB(x, y);
                int R = (rgb >> 16) & 0xFF;
                int G = (rgb >> 8) & 0xFF;
                int B = rgb & 0xFF;
                int index = findNearestRGBIndex(new RGB(R, G, B), myTileDict);
                int row = index / (finalImageWidth / TileSize);
                int col = index % (finalImageWidth / TileSize);
                int i = col * TileSize;
                int j = row * TileSize;
                BufferedImage tile = TilesImage.getSubimage(i, j, TileSize, TileSize);

//                x0,y0 sont les coordonnees du point du haut gauche dans l'image output
                int x0 = x * TileSize;
                int y0 = y * TileSize;
                outputImage.getRaster().setRect(x0, y0, tile.getRaster());
            }
        }
        ImageUtils.writeImageJPG(outputImage, PathOutputImage);

    }

}
