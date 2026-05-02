package com.ihecride.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Chiffrement AES-256 (CBC/PKCS5) pour les données sensibles.
 */
public class AesUtil {

    private static final String ALGO = "AES/CBC/PKCS5Padding";
    private static final String SECRET = "IHECRideAesKey2026ChangeInProduction";

    private static SecretKeySpec getKey() throws Exception {
        byte[] key = MessageDigest.getInstance("SHA-256")
                .digest(SECRET.getBytes("UTF-8"));
        return new SecretKeySpec(key, "AES");
    }

    public static String encrypt(String value) {
        try {
            byte[] iv = new byte[16];
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, getKey(), new IvParameterSpec(iv));
            return Base64.getEncoder().encodeToString(c.doFinal(value.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException("Erreur chiffrement AES", e);
        }
    }

    public static String decrypt(String cipherText) {
        try {
            byte[] iv = new byte[16];
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, getKey(), new IvParameterSpec(iv));
            return new String(c.doFinal(Base64.getDecoder().decode(cipherText)), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Erreur déchiffrement AES", e);
        }
    }
}
