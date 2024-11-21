package com.spammers.AlertsAndNotifications.model.enums;

public enum FineDescription {
    RETARDMENT(
            "Se genera una multa cuando el tiempo de prestamo expira."
    ),
    DAMAGED_MATERIAL(
            "El libro presenta daños visibles (páginas rasgadas, cubiertas rotas, marcadores, manchas, etc.)."
    ),
    LOST_MATERIAL(
            "Material perdido."
    );
    private final String description;
    FineDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
