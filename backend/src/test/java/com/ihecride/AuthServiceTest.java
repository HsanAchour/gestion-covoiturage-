package com.ihecride;

import com.ihecride.utils.PasswordUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires JUnit pour PasswordUtil et validations d'AuthService.
 */
public class AuthServiceTest {

    @Test
    public void testPasswordHashingAndVerify() {
        String pwd = "Ihec@2025";
        String hash = PasswordUtil.hash(pwd);
        assertNotNull(hash);
        assertTrue(PasswordUtil.verify(pwd, hash));
        assertFalse(PasswordUtil.verify("wrong", hash));
    }

    @Test
    public void testStrongPasswordRule() {
        assertTrue(PasswordUtil.isStrong("Ihec@2025"));
        assertTrue(PasswordUtil.isStrong("Azerty#1"));
        assertFalse(PasswordUtil.isStrong("azerty"));         // pas de maj/chiffre/special
        assertFalse(PasswordUtil.isStrong("Azerty12"));        // pas de special
        assertFalse(PasswordUtil.isStrong("AZER@12"));         // pas de min  → acceptable, mais trop court
        assertFalse(PasswordUtil.isStrong("Az@1"));            // trop court
    }
}
