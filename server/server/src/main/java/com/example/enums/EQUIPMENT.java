package com.example.enums;

public enum EQUIPMENT {
    STANDARD,
    PREMIUM,
    VIP;

    @Override
    public String toString() {
        return switch (this) {
            case STANDARD -> "Standard: Podstawowe wyposażenie.";
            case PREMIUM -> "Premium: Dodatkowe udogodnienia i lepsze widoki.";
            case VIP -> "VIP: Luksusowe udogodnienia i najlepszy widok.";
            default -> "Nieznane";
        };
    }
}
