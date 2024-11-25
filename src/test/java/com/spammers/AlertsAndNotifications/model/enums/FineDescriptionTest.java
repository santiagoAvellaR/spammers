package com.spammers.AlertsAndNotifications.model.enums;
import com.spammers.AlertsAndNotifications.service.implementations.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
class FineDescriptionTest {

    private FineDescription fineDescription;
    @Test
    void getDescription() {
        assertEquals("Se genera una multa cuando el tiempo de prestamo expira.", fineDescription.RETARDMENT.getDescription());
        assertEquals("El libro presenta daños visibles (páginas rasgadas, cubiertas rotas, marcadores, manchas, etc.).", fineDescription.DAMAGED_MATERIAL.getDescription());
        assertEquals("Material perdido.", fineDescription.LOST_MATERIAL.getDescription());
    }
}