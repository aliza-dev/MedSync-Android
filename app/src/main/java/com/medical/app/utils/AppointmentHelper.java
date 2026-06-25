package com.medical.app.utils;

import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Helper class with shared methods for reading and filtering appointment data from Firebase.
// It's not an Activity; it just holds reusable static methods so the screens don't repeat code.
public final class AppointmentHelper {

    // Date formats we accept, since users might type the date in different ways
    private static final String[] DATE_FORMATS = {
            "dd/MM/yyyy",
            "d/M/yyyy",
            "dd-MM-yyyy",
            "yyyy-MM-dd"
    };

    // Private constructor so no one can create an object of this utility class
    private AppointmentHelper() {
    }

    // Returns a dash instead of blank text so the UI never shows an empty value
    public static String safeText(String value) {
        return TextUtils.isEmpty(value) ? "-" : value.trim();
    }

    // Builds a map of user ID -> patient name. We check the role so doctor records are skipped.
    public static Map<String, String> loadPatientNames(DataSnapshot usersSnapshot) {
        Map<String, String> patientNames = new HashMap<>();

        if (usersSnapshot == null || !usersSnapshot.exists()) {
            return patientNames;
        }

        // Go through every user and keep only the patients' names
        for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
            String role = userSnapshot.child("role").getValue(String.class);
            if (!"patient".equals(role)) {
                continue;
            }

            String name = userSnapshot.child("name").getValue(String.class);
            if (!TextUtils.isEmpty(name)) {
                patientNames.put(userSnapshot.getKey(), name.trim());
            }
        }

        return patientNames;
    }

    // Returns true if an appointment belongs to the logged-in doctor.
    // We require both names to exist and compare them carefully to avoid showing the wrong appointments.
    public static boolean isAppointmentForDoctor(String doctorName, String appointmentDoctorName) {
        if (TextUtils.isEmpty(doctorName) || TextUtils.isEmpty(appointmentDoctorName)) {
            return false;
        }

        String normalizedDoctor = normalizeDoctorName(doctorName);
        String normalizedAppointment = normalizeDoctorName(appointmentDoctorName);

        if (normalizedDoctor.equalsIgnoreCase(normalizedAppointment)) {
            return true;
        }

        // This lets "Ali Khan" still match "Dr Ali Khan" after we strip the "Dr" prefix above
        return normalizedDoctor.startsWith(normalizedAppointment)
                || normalizedAppointment.startsWith(normalizedDoctor);
    }

    // Treats a missing status as Pending, matching the default we set on a new Appointment
    public static boolean isPendingStatus(String status) {
        if (TextUtils.isEmpty(status)) {
            return true;
        }
        return "Pending".equalsIgnoreCase(status.trim());
    }

    // Removes any "Dr"/"Dr." prefix and extra spaces so doctor names compare fairly
    private static String normalizeDoctorName(String name) {
        return name.replace("Dr.", "")
                .replace("dr.", "")
                .replace("Dr", "")
                .replace("dr", "")
                .trim()
                .replaceAll("\\s+", " ");
    }

    // Returns true if the appointment date is today, trying each accepted date format
    public static boolean isSameDayAsToday(String appointmentDate) {
        if (TextUtils.isEmpty(appointmentDate)) {
            return false;
        }

        String trimmedDate = appointmentDate.trim();
        String today = formatToday("dd/MM/yyyy");

        // Quick check for the most common format
        if (today.equals(trimmedDate)) {
            return true;
        }

        // Otherwise parse both dates and compare just the day/month/year
        Date appointmentParsed = parseDate(trimmedDate);
        Date todayParsed = parseDate(today);

        if (appointmentParsed != null && todayParsed != null) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            return dayFormat.format(appointmentParsed).equals(dayFormat.format(todayParsed));
        }

        return false;
    }

    // Gives today's date as text in the given format
    private static String formatToday(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }

    // Tries each accepted format until one successfully parses the date, otherwise returns null
    private static Date parseDate(String value) {
        for (String pattern : DATE_FORMATS) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
                format.setLenient(false);
                return format.parse(value);
            } catch (ParseException ignored) {
                // Not this format, try the next one
            }
        }
        return null;
    }
}
