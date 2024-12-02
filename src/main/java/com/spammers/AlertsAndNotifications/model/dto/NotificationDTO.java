package com.spammers.AlertsAndNotifications.model.dto;

import com.spammers.AlertsAndNotifications.model.NotificationModel;
import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class NotificationDTO {
    private String id;
    private LocalDate sentDate;
    private NotificationType notificationType;
    private String bookName;
    private boolean hasBeenSeen;

    /**
     * Converts a FineModel object into a FineOutputDTO.
     *
     * @param notificationModel The notification model entity containing all the notification's information.
     * @return A NotificationDTO object that encapsulates the relevant fine details.
     */
    private static NotificationDTO notificationModelToNotificationDTO(NotificationModel notificationModel) {
        return new NotificationDTO(
                notificationModel.getIdNotification(),
                notificationModel.getSentDate(),
                notificationModel.getNotificationType(),
                notificationModel.getBookName(),
                notificationModel.isHasBeenSeen()
        );
    }

    /**
     * Encapsulates the data from a page of FineModel objects into a PaginatedResponseDTO.
     *
     * @param page The page of FineModel objects to be converted.
     * @return A PaginatedResponseDTO containing the list of FineOutputDTOs and pagination details.
     */
    public static PaginatedResponseDTO<NotificationDTO> encapsulateFineModelOnDTO(Page<NotificationModel> page) {
        List<NotificationDTO> fineOutputDTOList = page.getContent().stream()
                .map(NotificationDTO::notificationModelToNotificationDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                fineOutputDTOList,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
