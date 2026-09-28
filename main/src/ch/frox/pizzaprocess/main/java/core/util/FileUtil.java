package ch.frox.pizzaprocess.main.java.core.util;

import java.io.IOException;
import java.io.InputStream;

import ch.frox.pizzaprocess.main.java.core.exception.system.ResourceReadException;



public final class FileUtil {

    private FileUtil() {}



    /**
     * Reads a file from the classpath.
     *
     * @param anchor The class the path is relative to. An absolute path ignores the anchor.
    **/
    public static byte[] readClasspathFile(Class<?> anchor, String path) {
        try (InputStream in = anchor.getResourceAsStream(path)) {
            if (in == null) throw new ResourceReadException(path, "it is not on the classpath");
            byte[] bytes = in.readAllBytes();
            if (bytes.length == 0) throw new ResourceReadException(path, "the file is empty");
            return bytes;
        } catch (IOException ex) {
            throw new ResourceReadException(path, ex);
        }
    }

    public static String extensionOf(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex < 0)? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }
}