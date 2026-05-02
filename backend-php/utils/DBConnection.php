<?php
/**
 * Gestion centralisée de la connexion MySQL via PDO (WAMP port 3301).
 * Équivalent PHP de com.ihecride.utils.DBConnection.
 */

class DBConnection {

    private static $pdo = null;

    public static function getConnection(): PDO {
        if (self::$pdo === null) {
            $cfg = require __DIR__ . '/../config/database.php';
            $dsn = sprintf(
                'mysql:host=%s;port=%d;dbname=%s;charset=%s',
                $cfg['host'], $cfg['port'], $cfg['database'], $cfg['charset']
            );
            try {
                self::$pdo = new PDO($dsn, $cfg['username'], $cfg['password'], [
                    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_EMULATE_PREPARES   => false,
                    PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4",
                ]);
            } catch (PDOException $e) {
                throw new RuntimeException("Connexion MySQL échouée : " . $e->getMessage());
            }
        }
        return self::$pdo;
    }

    public static function close(): void {
        self::$pdo = null;
    }
}
