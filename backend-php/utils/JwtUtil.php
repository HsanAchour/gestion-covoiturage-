<?php
/**
 * Génération et validation des tokens JWT (HS256), implémentation manuelle.
 * Access token : 15 min — Refresh token : 7 jours.
 * Équivalent PHP de com.ihecride.utils.JwtUtil.
 */

class JwtUtil {

    private const SECRET      = 'IHECRideSecretKey2026ChangeMeInProductionPlease!!';
    private const ACCESS_EXP  = 15 * 60;            // 15 minutes
    private const REFRESH_EXP = 7 * 24 * 60 * 60;   // 7 jours

    public static function generateAccessToken(int $userId, string $email, string $role): string {
        return self::buildToken($userId, $email, $role, self::ACCESS_EXP);
    }

    public static function generateRefreshToken(int $userId, string $email, string $role): string {
        return self::buildToken($userId, $email, $role, self::REFRESH_EXP);
    }

    private static function buildToken(int $userId, string $email, string $role, int $ttl): string {
        $now = time();
        $header  = ['alg' => 'HS256', 'typ' => 'JWT'];
        $payload = [
            'sub'   => (string) $userId,
            'email' => $email,
            'role'  => $role,
            'iat'   => $now,
            'exp'   => $now + $ttl,
        ];
        $h = self::base64url(json_encode($header,  JSON_UNESCAPED_SLASHES));
        $p = self::base64url(json_encode($payload, JSON_UNESCAPED_SLASHES));
        $s = self::base64url(hash_hmac('sha256', "$h.$p", self::SECRET, true));
        return "$h.$p.$s";
    }

    /**
     * Valide un token et retourne ses claims (tableau associatif), null sinon.
     */
    public static function validate(?string $token): ?array {
        if ($token === null) return null;
        $parts = explode('.', $token);
        if (count($parts) !== 3) return null;
        [$h, $p, $s] = $parts;

        $expected = self::base64url(hash_hmac('sha256', "$h.$p", self::SECRET, true));
        if (!hash_equals($expected, $s)) return null;

        $claims = json_decode(self::base64url_decode($p), true);
        if (!is_array($claims)) return null;
        if (!isset($claims['exp']) || $claims['exp'] < time()) return null;
        return $claims;
    }

    public static function extractUserId(?string $token): ?int {
        $c = self::validate($token);
        return $c === null ? null : (int) $c['sub'];
    }

    public static function extractRole(?string $token): ?string {
        $c = self::validate($token);
        return $c === null ? null : ($c['role'] ?? null);
    }

    private static function base64url(string $bin): string {
        return rtrim(strtr(base64_encode($bin), '+/', '-_'), '=');
    }

    private static function base64url_decode(string $b64): string {
        $pad = (4 - strlen($b64) % 4) % 4;
        return base64_decode(strtr($b64, '-_', '+/') . str_repeat('=', $pad));
    }
}
