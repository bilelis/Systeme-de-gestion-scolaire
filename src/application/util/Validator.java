package application.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Central validation utility — every rule lives here.
 * Returns a ValidationResult so callers can display specific messages.
 */
public class Validator {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ──────────────────────────────────────────────────────────
    // Inner result type
    // ──────────────────────────────────────────────────────────
    public static class ValidationResult {
        public final boolean valid;
        public final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult ok() {
            return new ValidationResult(true, "");
        }

        public static ValidationResult fail(String m) {
            return new ValidationResult(false, m);
        }
    }

    // ──────────────────────────────────────────────────────────
    // Generic
    // ──────────────────────────────────────────────────────────

    /** Not null, not blank after trim */
    public static ValidationResult notEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty())
            return ValidationResult.fail(fieldName + " is required.");
        return ValidationResult.ok();
    }

    // ──────────────────────────────────────────────────────────
    // Student
    // ──────────────────────────────────────────────────────────

    /** Letters only (including accented), min 2 chars */
    public static ValidationResult validateName(String value, String fieldName) {
        ValidationResult empty = notEmpty(value, fieldName);
        if (!empty.valid)
            return empty;

        String trimmed = value.trim();
        if (!trimmed.matches("[\\p{L} '-]+"))
            return ValidationResult.fail(fieldName + ": letters only (no numbers or symbols).");
        if (trimmed.length() < 2)
            return ValidationResult.fail(fieldName + ": minimum 2 characters.");
        return ValidationResult.ok();
    }

    /**
     * Date string dd/MM/yyyy — valid date, resulting age between 5 and 25.
     */
    public static ValidationResult validateDate(String value) {
        ValidationResult empty = notEmpty(value, "Date of birth");
        if (!empty.valid)
            return empty;

        LocalDate dob;
        try {
            dob = LocalDate.parse(value.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return ValidationResult.fail("Date of birth: use format dd/MM/yyyy.");
        }

        if (dob.isAfter(LocalDate.now()))
            return ValidationResult.fail("Date of birth cannot be in the future.");

        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 5)
            return ValidationResult.fail("Student age must be at least 5.");
        if (age > 25)
            return ValidationResult.fail("Student age must not exceed 25.");

        return ValidationResult.ok();
    }

    /** Digits only, exactly 8 characters (Tunisian format) */
    public static ValidationResult validatePhone(String value) {
        ValidationResult empty = notEmpty(value, "Phone");
        if (!empty.valid)
            return empty;

        String trimmed = value.trim();
        if (!trimmed.matches("\\d+"))
            return ValidationResult.fail("Phone: digits only, no spaces or dashes.");
        if (trimmed.length() != 8)
            return ValidationResult.fail("Phone: must be exactly 8 digits.");
        return ValidationResult.ok();
    }

    // ──────────────────────────────────────────────────────────
    // Classe
    // ──────────────────────────────────────────────────────────

    /** 1 ≤ capacity ≤ 20 */
    public static ValidationResult validateCapacity(String value) {
        ValidationResult empty = notEmpty(value, "Capacity");
        if (!empty.valid)
            return empty;

        try {
            int cap = Integer.parseInt(value.trim());
            if (cap < 1)
                return ValidationResult.fail("Capacity must be at least 1.");
            if (cap > 20)
                return ValidationResult.fail("Capacity must not exceed 20 students.");
        } catch (NumberFormatException e) {
            return ValidationResult.fail("Capacity: must be a whole number.");
        }
        return ValidationResult.ok();
    }

    // ──────────────────────────────────────────────────────────
    // Teacher
    // ──────────────────────────────────────────────────────────

    /** Min 4 characters */
    public static ValidationResult validatePassword(String value) {
        ValidationResult empty = notEmpty(value, "Password");
        if (!empty.valid)
            return empty;

        if (value.length() < 4)
            return ValidationResult.fail("Password must be at least 4 characters.");
        return ValidationResult.ok();
    }

    // ──────────────────────────────────────────────────────────
    // Enrollment
    // ──────────────────────────────────────────────────────────

    /** Year between 2000 and 2100 */
    public static ValidationResult validateYear(String value) {
        ValidationResult empty = notEmpty(value, "Year");
        if (!empty.valid)
            return empty;

        try {
            int year = Integer.parseInt(value.trim());
            if (year < 2000 || year > 2100)
                return ValidationResult.fail("Year must be between 2000 and 2100.");
        } catch (NumberFormatException e) {
            return ValidationResult.fail("Year: must be a valid 4-digit year.");
        }
        return ValidationResult.ok();
    }

    // ──────────────────────────────────────────────────────────
    // Grades
    // ──────────────────────────────────────────────────────────

    /** 0.0 ≤ grade ≤ 20.0 */
    public static ValidationResult validateGrade(String value) {
        ValidationResult empty = notEmpty(value, "Grade");
        if (!empty.valid)
            return empty;

        try {
            double g = Double.parseDouble(value.trim().replace(",", "."));
            if (g < 0 || g > 20)
                return ValidationResult.fail("Grade must be between 0 and 20.");
        } catch (NumberFormatException e) {
            return ValidationResult.fail("Grade: must be a number (e.g. 14.5).");
        }
        return ValidationResult.ok();
    }

    /** Positive integer */
    public static ValidationResult validateCoefficient(String value) {
        ValidationResult empty = notEmpty(value, "Coefficient");
        if (!empty.valid)
            return empty;

        try {
            int c = Integer.parseInt(value.trim());
            if (c <= 0)
                return ValidationResult.fail("Coefficient must be a positive integer.");
        } catch (NumberFormatException e) {
            return ValidationResult.fail("Coefficient: must be a whole number.");
        }
        return ValidationResult.ok();
    }

    // ──────────────────────────────────────────────────────────
    // SQL-injection sanitisation (basic — layer of defence)
    // ──────────────────────────────────────────────────────────

    /** Strip characters that are dangerous in SQL contexts */
    public static String sanitize(String input) {
        if (input == null)
            return "";
        return input.trim()
                .replace("'", "''") // escape single quotes
                .replace("\\", "\\\\"); // escape backslash
    }
}
