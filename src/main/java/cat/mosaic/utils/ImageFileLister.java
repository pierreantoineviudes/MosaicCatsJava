package cat.mosaic.utils;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ImageFileLister {

    public static int nFiles = 0;
    public static List<File> imageFiles = new ArrayList<>();
    private static Logger logger = Logger.getLogger(ImageFileLister.class.getName());


    public static void listImage(String directoryPath) {
        File folder = new File(directoryPath);

        // Check if the folder exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
            // List all image files in the directory
            listImageFiles(folder);
        } else {
            logger.severe("The provided path is not a valid directory.");
        }
        logger.info("number of files in the folder : " + nFiles);
    }

    // Method to list image files in a directory (including subdirectories)
    public static void listImageFiles(File folder) {
        // Get all files in the directory
        File[] files = folder.listFiles();

        // Check if there are files to list
        if (files != null) {
            for (File file : files) {
                // If it's a directory, recurse into it
                if (file.isDirectory()) {
                    listImageFiles(file);
                } else {
                    // Check if the file is an image based on its extension
                    if (isImageFile(file)) {
                        imageFiles.add(file);
                        nFiles += 1;
//                        if (nFiles > 1000) {
//                            break;
//                        }
                    }
                }
            }
        }
    }

    // Method to check if the file is an image (by extension)
    public static boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")
                || fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".tiff");
    }


    public static List<List<File>> partitionFiles(
            List<File> files, int parts) {

        List<List<File>> result = new ArrayList<>();
        int chunk = (files.size() + parts - 1) / parts;

        for (int i = 0; i < files.size(); i += chunk) {
            result.add(files.subList(
                    i,
                    Math.min(files.size(), i + chunk)
            ));
        }
        return result;
    }

}
