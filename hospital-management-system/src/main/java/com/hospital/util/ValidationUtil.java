package com.hospital.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;
import com.hospital.model.PaymentMethod;

/**
 * Centralised validation rules for the entire application.
 * Every menu must call these methods instead of inlining regex / length checks.
 *
 * All methods are pure predicates (no side-effects, no printing) so that
 * callers can decide what error message to display.
 */
public final class ValidationUtil {

    // Regex constants
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^[0-9+\\-() ]{7,15}$";
        private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd")
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
        private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("h:mm a")
            .toFormatter()
            .withLocale(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    // Length limits
    public static final int NAME_MIN            = 2;
    public static final int NAME_MAX            = 60;
    public static final int DEPT_NAME_MIN       = 2;
    public static final int DEPT_NAME_MAX       = 50;
    public static final int DESC_MIN             = 5;
    public static final int DESC_MAX             = 200;
    public static final int PASSWORD_MIN        = 6;
    public static final int SPECIALIZATION_MIN  = 2;
    public static final int USERNAME_MIN         = 3;
    public static final int USERNAME_MAX         = 20;

    private ValidationUtil() { }

    /** Returns true if email matches standard email format. */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    /** Returns true if phone is 7-15 chars of digits / +\,- ,( ), space. */
    public static boolean isValidPhone(String phone) {
        return phone != null && !phone.isBlank() && phone.matches(PHONE_REGEX)
            && phone.matches(".*[0-9].*");
    }

    /** Returns true if name is non-blank and within [NAME_MIN, NAME_MAX]. */
    public static boolean isValidName(String name) {
        return name != null && !name.isBlank()
                && name.length() >= NAME_MIN && name.length() <= NAME_MAX;
    }

    /** Returns true if name is within [DEPT_NAME_MIN, DEPT_NAME_MAX]. */
    public static boolean isValidDeptName(String name) {
        return name != null && !name.isBlank()
                && name.length() >= DEPT_NAME_MIN && name.length() <= DEPT_NAME_MAX;
    }

    /** Returns true if description is within [DESC_MIN, DESC_MAX]. */
    public static boolean isValidDescription(String description) {
        return description != null && !description.isBlank()
                && description.length() >= DESC_MIN && description.length() <= DESC_MAX;
    }

    /** Returns true if date matches yyyy-MM-dd. */
    public static boolean isValidDate(String date) {
        return parseDate(date) != null;
    }

    /** Returns true for 12-hour time such as 2:30 PM. */
    public static boolean isValidTime(String time) {
        return parseTime(time) != null;
    }

    /** Returns true if payment method is one of the supported methods. */
    public static boolean isValidPaymentMethod(String method) {
        if (method == null || method.isBlank()) return false;
        try {
            PaymentMethod.valueOf(method.trim().toUpperCase(Locale.ROOT));
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /** Returns true if password is at least PASSWORD_MIN characters. */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= PASSWORD_MIN;
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= USERNAME_MIN
                && username.length() <= USERNAME_MAX
                && username.matches("[A-Za-z0-9_]+");
    }

    public static boolean isStrongPassword(String password) {
        return password != null && password.length() >= PASSWORD_MIN
                && password.matches(".*[A-Za-z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^A-Za-z0-9].*");
    }

    public static boolean isPositiveNumber(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isValidId(Integer id) {
        return id != null && id > 0;
    }

    public static LocalDate parseDate(String date) {
        if (date == null) return null;
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    public static LocalTime parseTime(String time) {
        if (time == null) return null;
        try {
            return LocalTime.parse(time, TIME_FORMATTER);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    /** Returns true if specialization is non-blank and at least SPECIALIZATION_MIN chars. */
    public static boolean isValidSpecialization(String specialization) {
        return specialization != null && !specialization.isBlank()
                && specialization.length() >= SPECIALIZATION_MIN;
    }

    public static boolean isValidAddress(String address) {
        return address != null && !address.isBlank() && address.trim().length() >= 5
                && address.trim().length() <= 100;
    }

    /** Returns true if value is non-null and not blank. */
    public static boolean isNonBlank(String value) {
        return value != null && !value.isBlank();
    }
}
