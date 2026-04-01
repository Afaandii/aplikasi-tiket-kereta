package org.example.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    
    /**
     * Hashes a plain text password using BCrypt.
     * @param password The plain text password.
     * @return The hashed password.
     */
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Checks if a plain text password matches a hashed password.
     * @param password The plain text password.
     * @param hashed The hashed password to compare against.
     * @return true if it matches, false otherwise.
     */
    public static boolean check(String password, String hashed) {
        if (hashed == null || !hashed.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(password, hashed);
    }
}
