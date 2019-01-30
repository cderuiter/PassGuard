package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper methods for serializable classes.
 *
 * @author Dimitri Lialios 01/23/2019
 */
public class StreamHelper {

    private static final Logger LOGGER = LoggerSetup.initLogger(StreamHelper.class.getName(),
            StreamHelper.class.getSimpleName() + ".log");

    /**
     * Produces a byte array from the specified object.
     *
     * @param <T> generic type
     * @param obj the object to be written to a byte array
     * @return the byte array representation of the object
     */
    public static <T> byte[] writeToByteArray(T obj) {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = null;
        try {
            objOut = new ObjectOutputStream(bytesOut);
            objOut.writeObject(obj);
            LOGGER.log(Level.INFO, "Serialize to byte array operation success");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write to byte array output stream");
        } finally {
            try {
                bytesOut.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to close byte array output stream");
            }
            if (objOut != null) {
                try {
                    objOut.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close object output stream");
                }
            }
        }
        return bytesOut.toByteArray();
    }

    /**
     * Writes a generic type object to the file specified.
     *
     * @param <T> generic type
     * @param obj the object to be written to a file
     * @param filePath the serialized object file path
     */
    public static <T> void writeToFile(T obj, String filePath) {
        FileOutputStream fileOut = null;
        ObjectOutputStream objOut = null;
        try {
            fileOut = new FileOutputStream(filePath);
            objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(obj);
            LOGGER.log(Level.INFO, "Serialize to file operation success");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to create serialized file");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish object output stream");
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close file output stream");
                }
            }
            if (objOut != null) {
                try {
                    objOut.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close object output stream");
                }
            }
        }
    }

    /**
     * Produces an object from the specified byte array.
     *
     * @param buf the byte array representation of the object
     * @return the object contained in the byte array
     */
    public static Object readFromByteArray(byte[] buf) {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
        ObjectInputStream objIn = null;
        Object obj = null;
        try {
            objIn = new ObjectInputStream(bytesIn);
            obj = objIn.readObject();
            LOGGER.log(Level.INFO, "Deserialize from byte array operation success");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish object input stream");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Loaded class with no definition. This should not have happened");
        } finally {
            try {
                bytesIn.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to close byte array input stream");
            }
            if (objIn != null) {
                try {
                    objIn.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close object input stream");
                }
            }
        }
        return obj;
    }

    /**
     * Reads an object from the file specified.
     *
     * @param filePath the serialized object file path
     * @return the object contained in the serialized file
     */
    public static Object readFromFile(String filePath) {
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;
        Object obj = null;
        try {
            fileIn = new FileInputStream(filePath);
            objIn = new ObjectInputStream(fileIn);
            obj = objIn.readObject();
            LOGGER.log(Level.INFO, "Deserialize from file operation success");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to read serialized file");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish object input stream");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Loaded class with no definition. This should not have happened");
        } finally {
            if (fileIn != null) {
                try {
                    fileIn.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close file input stream");
                }
            }
            if (objIn != null) {
                try {
                    objIn.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close object input stream");
                }
            }
        }
        return obj;
    }

}
