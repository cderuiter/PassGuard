package test;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper methods for AES-256 encryption and decryption. 
 * TODO: implement PBKDF2 instead of just hashing with SHA
 *
 * @author Dimitri Lialios 01/23/2019
 */
public class CipherHelper {

    /**
     * Makes available a logger whose output is written to a file that is the
     * same name as this class.
     */
    private static final Logger LOGGER = LoggerSetup.initLogger(CipherHelper.class.getName(),
            CipherHelper.class.getSimpleName() + ".log");
    /**
     * The number of iterations of SHA-256 that will be executed when digesting
     * a secret key with its salt.
     */
    private static final int HASHING_ITERATIONS = 200000;
    /**
     * The size of the salt, in bytes, to be appended to a secret key before
     * hashing.
     */
    private static final int SALT_SIZE = 8;

    /**
     * Encrypts the specified byte stream with the provided secret key.
     *
     * @param byteStream the byte stream to be encrypted
     * @param key        the secret key for encryption
     * @return           the encrypted byte stream or 
     *                   null if something went wrong
     */
    public static byte[] encrypt(byte[] byteStream, String key) {

        //prepare hashing algorithm
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Unsupported digest algorithm. This should not have happened");
        }

        //prepare encryption algorithm
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption or padding algorithm. This should not have happened");
        }

        //generate a salt and then append it to the secret key
        byte[] keyBytes = StreamHelper.writeToByteArray(key);
        byte[] saltBytes = new byte[SALT_SIZE];
        new Random().nextBytes(saltBytes);
        byte[] keyAndSaltBytes = new byte[keyBytes.length + saltBytes.length];
        System.arraycopy(keyBytes, 0, keyAndSaltBytes, 0, keyBytes.length);
        System.arraycopy(saltBytes, 0, keyAndSaltBytes, keyBytes.length, saltBytes.length);

        //digest the key+salt then iterate
        byte[] hashedKey = null;
        if (md != null) {
            hashedKey = md.digest(keyAndSaltBytes);
            for (int i = 0; i < HASHING_ITERATIONS - 1; i++) {
                hashedKey = md.digest(hashedKey);
            }
        }

        //init AES with md output
        if (cipher != null && hashedKey != null) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(hashedKey, "AES"));
            } catch (InvalidKeyException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize cipher because of bad AES secret key");
            }
        }

        //encrypt the input byte stream
        byte[] b = null;
        if (cipher != null) {
            try {
                b = cipher.doFinal(byteStream);
                LOGGER.log(Level.INFO, "Encrypt operation success");
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, "Failed to encrypt because of bad data length or padding");
            }
        }

        //return null if something went wrong
        if (b == null) {
            return null;
        }

        //append salt bytes to end of encrypted byte stream
        byte[] encryptedBytesWithSalt = new byte[b.length + saltBytes.length];
        System.arraycopy(b, 0, encryptedBytesWithSalt, 0, b.length);
        System.arraycopy(saltBytes, 0, encryptedBytesWithSalt, b.length, saltBytes.length);

        //return encrypted byte stream with an additional SALT_SIZE bytes
        return encryptedBytesWithSalt;

    }

    /**
     * Decrypts the specified byte stream with the provided secret key.
     *
     * @param byteStream the byte stream to be decrypted
     * @param key        the secret key for decryption
     * @return           the decrypted byte stream or 
     *                   null if something went wrong
     */
    public static byte[] decrypt(byte[] byteStream, String key) {

        //prepare hashing algorithm
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Unsupported digest algorithm. This should not have happened");
        }

        //prepare encryption algorithm
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption or padding algorithm. This should not have happened");
        }

        //extract the salt from the encrypted byte stream
        byte[] encryptedBytesWithSalt = byteStream;
        byte[] extractedSalt = new byte[SALT_SIZE];
        for (int i = 0; i < extractedSalt.length; i++) {
            extractedSalt[i] = encryptedBytesWithSalt[encryptedBytesWithSalt.length - SALT_SIZE + i];
        }
        byte[] keyBytes = StreamHelper.writeToByteArray(key);
        byte[] keyBytesWithSalt = new byte[keyBytes.length + SALT_SIZE];
        System.arraycopy(keyBytes, 0, keyBytesWithSalt, 0, keyBytes.length);
        System.arraycopy(extractedSalt, 0, keyBytesWithSalt, keyBytes.length, extractedSalt.length);
        byte[] encryptedBytesMinusSalt = new byte[byteStream.length - SALT_SIZE];
        System.arraycopy(byteStream, 0, encryptedBytesMinusSalt, 0, encryptedBytesMinusSalt.length);

        //digest the key+salt then iterate
        byte[] hashedKey = null;
        if (md != null) {
            hashedKey = md.digest(keyBytesWithSalt);
            for (int i = 0; i < HASHING_ITERATIONS - 1; i++) {
                hashedKey = md.digest(hashedKey);
            }
        }

        //init AES with md output
        if (cipher != null && hashedKey != null) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hashedKey, "AES"));
            } catch (InvalidKeyException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize cipher because of bad AES secret key");
            }
        }

        //decrypt the input byte stream
        byte[] b = null;
        if (cipher != null) {
            try {
                b = cipher.doFinal(encryptedBytesMinusSalt);
                LOGGER.log(Level.INFO, "Decrypt operation success");
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, "Failed to decrypt because of incorrect secret key");
            }
        }

        //return null if something went wrong
        if (b == null) {
            return null;
        }

        //return decrypted byte stream
        return b;

    }

}
