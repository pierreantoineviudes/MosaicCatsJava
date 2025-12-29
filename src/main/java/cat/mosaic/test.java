package cat.mosaic;

import cat.mosaic.app.RGB;
import cat.mosaic.constants.InOutConstants;
import cat.mosaic.utils.HashMapIO;

import java.io.IOException;
import java.util.Map;

public class test {

    public static void main() throws IOException, ClassNotFoundException {
        Map<Integer, RGB> myMap = HashMapIO.load(String.valueOf(InOutConstants.HASH_OUTPUT_PATH));
        System.out.println(myMap);
        RGB a = (RGB) myMap.get(0);
        System.out.println(a);
    }
}
