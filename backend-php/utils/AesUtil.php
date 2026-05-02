<?php
/**
 * Chiffrement AES-256 (CBC/PKCS7) pour les données sensibles.
 * Équivalent PHP de com.ihecride.utils.AesUtil.
 */

class AesUtil {

    private const SECRET = 'IHECRideAesKey2026ChangeInProduction';
    private const ALGO   = 'aes-256-cbc';

    private static function getKey(): string {
        return hash('sha256', self::SECRET, true);   // 32 octets
    }

    public static function encrypt(string $value): string {
        $iv     = str_repeat("\0", 16);              // IV nul (mêmes conditions que le code Java)
        $cipher = openssl_encrypt($value, self::ALGO, self::getKey(), OPENSSL_RAW_DATA, $iv);
        if ($cipher === false) {
            throw new RuntimeException('Erreur chiffrement AES');
        }
        return base64_encode($cipher);
    }

    public static function decrypt(string $cipherText): string {
        $iv    = str_repeat("\0", 16);
        $clear = openssl_decrypt(base64_decode($cipherText), self::ALGO, self::getKey(), OPENSSL_RAW_DATA, $iv);
        if ($clear === false) {
            throw new RuntimeException('Erreur déchiffrement AES');
        }
        return $clear;
    }
}
