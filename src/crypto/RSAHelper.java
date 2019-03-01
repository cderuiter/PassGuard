package crypto;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import logger.LoggerSetup;

/**
 * Helper methods for RSA encryption and decryption.
 * TODO: JavaDoc
 *
 * @author Dimitri Lialios 03/01/2019
 */
public class RSAHelper {

    /**
     * Makes available a logger whose output is written to a file that is the
     * same name as this class.
     */
    private static final Logger LOGGER = LoggerSetup.initLogger(RSAHelper.class.getName(),
            RSAHelper.class.getSimpleName() + ".log");

    private static final int KEY_SIZE = 2048;

    public static KeyPair generateKeyPair() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption algorithm. This should not have happened");
            return null;
        }
        kpg.initialize(KEY_SIZE);
        return kpg.genKeyPair();
    }

    public static RSAPublicKey getPubKeyFromPair(KeyPair kp) {
        if (kp != null) {
            return (RSAPublicKey) kp.getPublic();
        }
        return null;
    }

    public static RSAPrivateKey getPrivKeyFromPair(KeyPair kp) {
        if (kp != null) {
            return (RSAPrivateKey) kp.getPrivate();
        }
        return null;
    }

    public static byte[] encryptRSA(byte[] byteArray, RSAPublicKey pub) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption or padding algorithm. This should not have happened");
            return null;
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, pub);
        } catch (InvalidKeyException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize cipher because of bad RSA public key");
        }
        byte[] encryptedBytes;
        try {
            encryptedBytes = cipher.doFinal(byteArray);
            LOGGER.log(Level.INFO, "Encrypt operation success");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.log(Level.SEVERE, "Failed to encrypt because of bad data length or padding");
            return null;
        }
        return encryptedBytes;
    }

    public static byte[] decryptRSA(byte[] byteArray, RSAPrivateKey priv) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported encryption or padding algorithm. This should not have happened");
            return null;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, priv);
        } catch (InvalidKeyException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize cipher because of bad RSA private key");
        }
        byte[] decryptedBytes;
        try {
            decryptedBytes = cipher.doFinal(byteArray);
            LOGGER.log(Level.INFO, "Decrypt operation success");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.log(Level.SEVERE, "Failed to decrypt because of bad data length or padding");
            return null;
        }
        return decryptedBytes;
    }

}
