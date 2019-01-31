package test;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper methods for AES-256 encryption and decryption. TODO: salts
 *
 * @author Dimitri Lialios 01/23/2019
 */
public class CipherHelper {

    private static final Logger LOGGER = LoggerSetup.initLogger(CipherHelper.class.getName(),
            CipherHelper.class.getSimpleName() + ".log");
    private static final int HASHING_ITERATIONS = 400000;

    /**
     * Encrypts the specified object with the provided secret key.
     *
     * @param byteStream the byte stream to be encrypted
     * @param key the secret key for encryption
     * @return the object in an encrypted byte stream
     */
    public static byte[] encrypt(byte[] byteStream, String key) {
        MessageDigest md = null;
        Cipher cipher = null;
        byte[] b = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Unsupported digest algorithm. This should not have happened");
        }
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption or padding algorithm. This should not have happened");
        }
        byte[] hashedKey = null;
        if (md != null) {
            hashedKey = md.digest(StreamHelper.writeToByteArray(key));
            for (int i = 0; i < HASHING_ITERATIONS - 1; i++) {
                hashedKey = md.digest(hashedKey);
            }
        }
        if (cipher != null && hashedKey != null) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE,
                        new SecretKeySpec(hashedKey, "AES"));
                LOGGER.log(Level.INFO, "Encrypt operation success");
            } catch (InvalidKeyException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize cipher because of bad AES secret key");
            }
        }
        if (cipher != null) {
            try {
                b = cipher.doFinal(byteStream);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, "Failed to encrypt because of bad data length or padding");
            }
        }
        return b;
    }

    /**
     * Decrypts the specified byte stream with the provided secret key.
     *
     * @param byteStream the byte stream to be decrypted
     * @param key the secret key for decryption
     * @return the decrypted byte stream
     */
    public static byte[] decrypt(byte[] byteStream, String key) {
        MessageDigest md = null;
        Cipher cipher = null;
        byte[] b = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Unsupported digest algorithm. This should not have happened");
        }
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption or padding algorithm. This should not have happened");
        }
        byte[] hashedKey = null;
        if (md != null) {
            hashedKey = md.digest(StreamHelper.writeToByteArray(key));
            for (int i = 0; i < HASHING_ITERATIONS - 1; i++) {
                hashedKey = md.digest(hashedKey);
            }
        }
        if (cipher != null && hashedKey != null) {
            try {
                cipher.init(Cipher.DECRYPT_MODE,
                        new SecretKeySpec(hashedKey, "AES"));
                LOGGER.log(Level.INFO, "Decrypt operation success");
            } catch (InvalidKeyException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize cipher because of bad AES secret key");
            }
        }
        if (cipher != null) {
            try {
                b = cipher.doFinal(byteStream);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, "Failed to decrypt because of incorrect secret key");
            }
        }
        return b;
    }

}
