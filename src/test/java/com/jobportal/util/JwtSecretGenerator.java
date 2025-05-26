package com.jobportal.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecretGenerator {

    public static void main(String[] args) {
        try {
            // 1. Create a SecureRandom instance (you can specify the algorithm if you like)
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();

            // 2. Generate 32 bytes of random data
            byte[] seed = secureRandom.generateSeed(32);

            // 3. Base64-encode the random bytes
            String base64Seed = Base64.getEncoder().encodeToString(seed);

            // 4. Print the result
            System.out.println("Generated JWT Secret (Base64 Encoded):");
            System.out.println(base64Seed);
            System.out.println("\nInstructions:");
            System.out.println("1. Copy this key.");
            System.out.println("2. Paste it into your 'application.properties' for the 'jwt.secret' property.");
            System.out.println("3. IMPORTANT: If this is for a production environment, manage this secret securely (e.g., environment variables, secrets manager) and do not commit the actual secret to version control in 'application.properties'.");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("No strong SecureRandom algorithm available: " + e.getMessage());
        }
    }
}
