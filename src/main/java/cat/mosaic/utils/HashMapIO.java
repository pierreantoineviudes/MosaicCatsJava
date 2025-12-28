package cat.mosaic.utils;

import cat.mosaic.app.RGB;

import java.io.*;
import java.util.Map;

public class HashMapIO {

    public static void save(Map<?, ?> map, String filePath) throws IOException {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(map);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, RGB> load(String filePath)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(filePath))) {
            return (Map<Integer, RGB>) in.readObject();
        }
    }
}
