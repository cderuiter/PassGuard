package crypto;

import logger.LoggerSetup;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper methods for AES-256 encryption and decryption.
 * TODO: detecting a suitable # of iterations
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
     * The number of hash function iterations that will be executed when
     * digesting a secret key with its salt.
     */
    private static final int SHA_ITERATIONS = 200000;
    private static final int PBKDF2_ITERATIONS = 20000;

    /**
     * The size of the salt, in bytes, to be appended to a secret key before hashing.
     */
    private static final int SALT_SIZE = 8;

    /**
     * Encrypts the specified byte array with the provided secret key. 
     * NOTE: Do not use with low entropy secret keys.
     *
     * @param byteArray  the byte array to be encrypted
     * @param key        the secret key for encryption
     * @return           the encrypted byte array
     */
    public static byte[] encryptSHA_AES(byte[] byteArray, String key) {

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
            for (int i = 0; i < SHA_ITERATIONS - 1; i++) {
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

        //encrypt the input byte array
        byte[] b = null;
        if (cipher != null) {
            try {
                b = cipher.doFinal(byteArray);
                LOGGER.log(Level.INFO, "Encrypt operation success");
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, "Failed to encrypt because of bad data length or padding");
                return null;
            }
        }

        //append salt bytes to end of encrypted byte array
        byte[] encryptedBytesWithSalt = new byte[b.length + saltBytes.length];
        System.arraycopy(b, 0, encryptedBytesWithSalt, 0, b.length);
        System.arraycopy(saltBytes, 0, encryptedBytesWithSalt, b.length, saltBytes.length);

        //return encrypted byte array with an additional SALT_SIZE bytes
        return encryptedBytesWithSalt;

    }

    /**
     * Decrypts the specified byte array with the provided secret key.
     *
     * @param byteArray  the byte array to be decrypted
     * @param key        the secret key for decryption
     * @return           the decrypted byte array or null if something went wrong
     */
    public static byte[] decryptSHA_AES(byte[] byteArray, String key) {

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

        //extract the salt from the encrypted byte array
        byte[] encryptedBytesWithSalt = byteArray;
        byte[] extractedSalt = new byte[SALT_SIZE];
        for (int i = 0; i < extractedSalt.length; i++) {
            extractedSalt[i] = encryptedBytesWithSalt[encryptedBytesWithSalt.length - SALT_SIZE + i];
        }
        byte[] keyBytes = StreamHelper.writeToByteArray(key);
        byte[] keyBytesWithSalt = new byte[keyBytes.length + SALT_SIZE];
        System.arraycopy(keyBytes, 0, keyBytesWithSalt, 0, keyBytes.length);
        System.arraycopy(extractedSalt, 0, keyBytesWithSalt, keyBytes.length, extractedSalt.length);
        byte[] encryptedBytesMinusSalt = new byte[byteArray.length - SALT_SIZE];
        System.arraycopy(byteArray, 0, encryptedBytesMinusSalt, 0, encryptedBytesMinusSalt.length);

        //digest the key+salt then iterate
        byte[] hashedKey = null;
        if (md != null) {
            hashedKey = md.digest(keyBytesWithSalt);
            for (int i = 0; i < SHA_ITERATIONS - 1; i++) {
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

        //decrypt the input byte array
        byte[] b = null;
        if (cipher != null) {
            try {
                b = cipher.doFinal(encryptedBytesMinusSalt);
                LOGGER.log(Level.INFO, "Decrypt operation success");
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, "Failed to decrypt because of incorrect secret key");
            }
        }

        //return decrypted byte array
        return b;

    }

    /**
     * Encrypts the specified byte array with the provided secret key.
     * NOTE: Use this with user passwords.
     *
     * @param byteArray  the byte array to be encrypted
     * @param key        the secret key for encryption
     * @return           the encrypted byte array or null if something went wrong
     */
    public static byte[] encryptPBKDF2_AES(byte[] byteArray, String key) {

        //prepare hashing algorithm
        SecretKeyFactory skf = null;
        String algo = "PBKDF2WithHmacSHA256";
        try {
            skf = SecretKeyFactory.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "{0} is unsupported on this platform", algo);
        }

        //generate a salt
        byte[] saltBytes = new byte[SALT_SIZE];
        new Random().nextBytes(saltBytes);

        //produce a key specification
        PBEKeySpec pbeKeySpec = new PBEKeySpec(key.toCharArray(), saltBytes, PBKDF2_ITERATIONS, 256);

        //create a container for the generated key
        SecretKey sk = null;
        if (skf != null) {
            try {
                sk = skf.generateSecret(pbeKeySpec);
            } catch (InvalidKeySpecException e) {
                LOGGER.log(Level.SEVERE, "Invalid key specification");
            }
        }

        //prepare encryption algorithm
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption or padding algorithm. This should not have happened");
        }

        //init AES with md output
        if (cipher != null && sk != null) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sk.getEncoded(), "AES"));
            } catch (InvalidKeyException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize cipher because of bad AES secret key");
            }
        }

        //encrypt the input byte array
        byte[] b = null;
        if (cipher != null) {
            try {
                b = cipher.doFinal(byteArray);
                LOGGER.log(Level.INFO, "Encrypt operation success");
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, "Failed to encrypt because of bad data length or padding");
                return null;
            }
        }

        //append salt bytes to end of encrypted byte array
        byte[] encryptedBytesWithSalt = new byte[b.length + saltBytes.length];
        System.arraycopy(b, 0, encryptedBytesWithSalt, 0, b.length);
        System.arraycopy(saltBytes, 0, encryptedBytesWithSalt, b.length, saltBytes.length);

        //return encrypted byte array with an additional SALT_SIZE bytes
        return encryptedBytesWithSalt;

    }

    /**
     * Decrypts the specified byte array with the provided secret key.
     *
     * @param byteArray  the byte array to be decrypted
     * @param key        the secret key for decryption
     * @return           the decrypted byte array or null if something went wrong
     */
    public static byte[] decryptPBKDF2_AES(byte[] byteArray, String key) {

        //prepare hashing algorithm
        SecretKeyFactory skf = null;
        String algo = "PBKDF2WithHmacSHA256";
        try {
            skf = SecretKeyFactory.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "{0} is unsupported on this platform", algo);
        }

        //prepare the byte array inputs
        byte[] encryptedBytesWithSalt = byteArray;//@@@@
        byte[] extractedSalt = new byte[SALT_SIZE];//@@@@
        for (int i = 0; i < extractedSalt.length; i++) {
            extractedSalt[i] = encryptedBytesWithSalt[encryptedBytesWithSalt.length - SALT_SIZE + i];
        }
        byte[] encryptedBytesMinusSalt = new byte[byteArray.length - SALT_SIZE];//@@@@
        System.arraycopy(byteArray, 0, encryptedBytesMinusSalt, 0, encryptedBytesMinusSalt.length);

        //produce a key specification
        PBEKeySpec pbeKeySpec = new PBEKeySpec(key.toCharArray(), extractedSalt, PBKDF2_ITERATIONS, 256);

        //create a container for the generated key
        SecretKey sk = null;
        if (skf != null) {
            try {
                sk = skf.generateSecret(pbeKeySpec);
            } catch (InvalidKeySpecException e) {
                LOGGER.log(Level.SEVERE, "Invalid key specification");
            }
        }

        //prepare encryption algorithm
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption or padding algorithm. This should not have happened");
        }

        //init AES with md output
        if (cipher != null && sk != null) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sk.getEncoded(), "AES"));
            } catch (InvalidKeyException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize cipher because of bad AES secret key");
            }
        }

        //decrypt the input byte array
        byte[] b = null;
        if (cipher != null) {
            try {
                b = cipher.doFinal(encryptedBytesMinusSalt);
                LOGGER.log(Level.INFO, "Decrypt operation success");
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, "Failed to decrypt because of incorrect secret key");
            }
        }

        //return decrypted byte array
        return b;

    }

}
