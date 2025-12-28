package cat.mosaic.app;

import java.io.Serializable;

public class RGB implements Serializable {
    private int R;
    private int G;
    private int B;

    public int getG() {
        return G;
    }

    public int getB() {
        return B;
    }

    public RGB(int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;
    }

    public int getR() {
        return R;
    }

    @Override
    public String toString() {
        return "RGB(" + R + ", " + G + ", " + B + ")";
    }
}
