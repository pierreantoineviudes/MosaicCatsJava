package cat.mosaic.app;

import cat.mosaic.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static cat.mosaic.constants.InOutConstants.inputImagePath;
import static cat.mosaic.constants.InOutConstants.TileSize;

public class Mosaic {

    private BufferedImage inputImage;
    private int inputWidth;
    private int inputHeight;
    private int outputWidth;
    private int outputHeight;

//    outputHeight = TileSize * InputHeight
//    outputWidth = TileSize * InputWidth


    public Mosaic() throws IOException {
        this.inputImage = ImageUtils.openImage(new File(inputImagePath));
        this.inputHeight = this.inputImage.getHeight();
        this.inputWidth = this.inputImage.getWidth();
    }

    public void main(){

    }

}
