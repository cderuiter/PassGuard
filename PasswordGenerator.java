/**
 * This code exists to generate random characters to be 
 * used as a password by the generatePassword function.
 * @author Lucas Carver
 */

package passwordgenerator;

import java.util.Random;

public class PasswordGenerator {

    static Random randomObject = new Random();
    
    /**
     * Creating a string of selectable characters.
     */
    static String uppers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String lowers = "abcdefghijklmnopqrstuvwxyz";
    static String numbers = "0123456789";
    static String specials = "@%+\\/'!#$^?:,(){}[]~`-_.";
    static String selectables = uppers + lowers + numbers + specials;
    
    /**
     * Creates a password with the default length of 20 with special characters.
     * @return                          Returns the password as a char array.
     */
    public static char[] PasswordGenerator() {
        return generatePassword();
    }
    
    /**
     * Creates a password with the specified length with or without special characters.
     * @param length               The length of the password in characters.
     * @param allowSpecial      To allow or disallow special characters.
     * @return                          Returns the password as a char array.
     */
    public static char[] PasswordGenerator(int length, boolean allowSpecial) {
        return generatePassword(length, allowSpecial);
    }
    
    /**
     * Creates a password with the default length of 20 with or without special characters.
     * @param allowSpecial      To allow or disallow special characters.
     * @return                          Returns the password as a char array.
     */
    public static char[] PasswordGenerator(boolean allowSpecial) {
        return generatePassword(allowSpecial);
    }
    
    /**
     * Creates a password with specified length with special characters.
     * @param length               The length of the password in characters.
     * @return                          Returns the password as a char array.
     */
    public static char[] PasswordGenerator(int length) {
        return generatePassword(length);
    }

    /**
     * Creates a password with the specified length with or without special characters.
     * @param length               The length of the password in characters.
     * @param allowSpecial      To allow or disallow special characters.
     * @return                          Returns the password as a char array.
     */
    public static char[] generatePassword(int length, boolean allowSpecial) {

        char[] password = new char[length];

        if (allowSpecial == false) {
            for (int i = 0; i < length; i++) {
                password[i] = selectables.charAt(randomObject.nextInt(62));
            }
        } else {
            for (int i = 0; i < length; i++) {
                password[i] = selectables.charAt(randomObject.nextInt(86));
            }
        }
        return password;
    }
    
    /**
     * Creates a password with the default length of 20 with or without special characters.
     * @param allowSpecial      To allow or disallow special characters.
     * @return                          Returns the password as a char array.
     */
    public static char[] generatePassword(boolean allowSpecial) {
        int length = 20;

        char[] password = new char[length];

        if (allowSpecial == false) {
            for (int i = 0; i < length; i++) {
                password[i] = selectables.charAt(randomObject.nextInt(62));
            }
        } else {
            for (int i = 0; i < length; i++) {
                password[i] = selectables.charAt(randomObject.nextInt(86));
            }
        }
        return password;
    }
    
    /**
     * Creates a password with the specified length with special characters.
     * @param length              The length of the password in characters.
     * @return                         Returns the password as a char array.
     */
    public static char[] generatePassword(int length) {

        boolean allowSpecial = true;

        char[] password = new char[length];

        if (allowSpecial == false) {
            for (int i = 0; i < length; i++) {
                password[i] = selectables.charAt(randomObject.nextInt(62));
            }
        } else {
            for (int i = 0; i < length; i++) {
                password[i] = selectables.charAt(randomObject.nextInt(86));
            }
        }
        return password;
    }
    
    /**
     * Creates a password with default length 20 and special charaters.
     * @return                      Returns the password a char array.
     */
    public static char[] generatePassword() {
        int length = 20;
        boolean allowSpecial = true;

        char[] password = new char[length];

        if (allowSpecial == false) {
            for (int i = 0; i < length; i++) {
                password[i] = selectables.charAt(randomObject.nextInt(62));
            }
        } else {
            for (int i = 0; i < length; i++) {
                password[i] = selectables.charAt(randomObject.nextInt(86));
            }
        }
        return password;
    }
}
