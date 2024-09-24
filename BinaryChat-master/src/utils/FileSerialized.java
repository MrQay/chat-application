package utils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * A class representing holding a file that can be transferred over a network and serialized.
 *
 * @author David
 */
public class FileSerialized implements Serializable {
    private final byte[] data;

    /**
     * @pre assumes filepath != null
     * @param filepath, the path to the file to be serialized.
     * @post Creates a new file with given filepath
     *       Creates a new byte[] data
     *       Creates a new fileinputstream and reads the file into byte[] data.
     * @throws IOException if the fileinputstream couldn't read the file.
     */
    public FileSerialized(String filepath) throws IOException {
        File file = new File(filepath);
        data = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(data);
        }
    }
    /**
     * @pre None
     * @return the byte[] data holding the file.
     * @post data is unchanged.
     */
    public byte[] getData() {
        return data;
    }
}