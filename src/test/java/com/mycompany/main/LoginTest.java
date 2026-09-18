package com.mycompany.main;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    // ---------- checkUsername ----------
    // Current logic: returns true only when the username BOTH
    // contains an underscore AND is shorter than 5 characters.

    @Test
    void checkUsername_validWhenShortAndHasUnderscore() {
        assertTrue(Login.checkUsername("k_1"));
    }

    @Test
    void checkUsername_invalidWhenNoUnderscore() {
        assertFalse(Login.checkUsername("kyle"));
    }

    @Test
    void checkUsername_invalidWhenTooLongEvenWithUnderscore() {
        assertFalse(Login.checkUsername("kyle_smith"));
    }

    @Test
    void checkUsername_invalidWhenEmpty() {
        assertFalse(Login.checkUsername(""));
    }

    // ---------- checkPasswordComplexity ----------
    // Requires at least one uppercase letter, one digit, and one
    // special (non letter-or-digit) character.

    @Test
    void checkPasswordComplexity_validWhenAllRequirementsMet() {
        assertTrue(Login.checkPasswordComplexity("Ch@t123"));
    }

    @Test
    void checkPasswordComplexity_invalidWhenMissingCapital() {
        assertFalse(Login.checkPasswordComplexity("ch@t123"));
    }

    @Test
    void checkPasswordComplexity_invalidWhenMissingDigit() {
        assertFalse(Login.checkPasswordComplexity("Ch@tabc"));
    }

    @Test
    void checkPasswordComplexity_invalidWhenMissingSpecialCharacter() {
        assertFalse(Login.checkPasswordComplexity("Chat123"));
    }

    @Test
    void checkPasswordComplexity_invalidWhenEmpty() {
        assertFalse(Login.checkPasswordComplexity(""));
    }

    // ---------- checkCellPhoneNumber ----------
    // Requires exactly 12 characters and a "+27" prefix.

    @Test
    void checkCellPhoneNumber_validWhenCorrectFormat() {
        assertTrue(Login.checkCellPhoneNumber("+27821234567"));
    }

    @Test
    void checkCellPhoneNumber_invalidWhenMissingCountryCode() {
        assertFalse(Login.checkCellPhoneNumber("082123456789"));
    }

    @Test
    void checkCellPhoneNumber_invalidWhenWrongLength() {
        assertFalse(Login.checkCellPhoneNumber("+2782123456"));
    }

    @Test
    void checkCellPhoneNumber_invalidWhenEmpty() {
        assertFalse(Login.checkCellPhoneNumber(""));
    }
}
