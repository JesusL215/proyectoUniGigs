package com.proyecto.unigigs.model;

public enum ApplicationStatus {
    PENDIENTE, // Initial state when student applies
    SELECCIONADO, // Company accepts the application
    EN_PROCESO, // Student confirms and starts the internship
    COMPLETADA, // Internship completed successfully
    RECHAZADA, // Company rejects the application
    CANCELADA // Student withdraws the application
}
