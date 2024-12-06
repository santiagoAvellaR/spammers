package com.spammers.AlertsAndNotifications.repository;

import com.spammers.AlertsAndNotifications.model.NotificationModel;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface NotificationRepository extends JpaRepository<NotificationModel, String> {

    /**
     * Retrieves a paginated list of notifications associated with a specific user.
     *
     * @param userId the ID of the user whose notifications are to be retrieved.
     * @param pageable a Pageable object specifying pagination and sorting information.
     * @return a Page of NotificationModel objects associated with the given user ID.
     */
    @Query("SELECT n FROM NotificationModel n WHERE n.studentId = :userId")
    Page<NotificationModel> findByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * Counts the number of notifications that have not been seen by a specific user.
     *
     * @param userId the ID of the user whose unseen notifications are to be counted.
     * @param hasBeenSeen the boolean value indicating whether the notification has been seen (false for unseen).
     * @return the count of unseen notifications for the given user.
     */
    @Query("SELECT COUNT(n) FROM NotificationModel n WHERE n.studentId = :userId AND n.hasBeenSeen = :hasBeenSeen")
    long getNumberNotificationsNotSeenByUser(@Param("userId") String userId, @Param("hasBeenSeen") boolean hasBeenSeen);

    /**
     * Updates the `hasBeenSeen` status to true for a specific notification identified by its ID.
     *
     * This method executes a bulk update operation on the notification with the given ID.
     * The number of records updated (should be 1 if the ID exists) is returned as an integer.
     *
     * @param notificationId the ID of the notification to be marked as seen.
     * @return the number of notifications updated in the database (typically 1 or 0).
     */
    @Modifying
    @Query("UPDATE NotificationModel n SET n.hasBeenSeen = true WHERE n.idNotification = :notificationId")
    int markNotificationAsSeen(@Param("notificationId") String notificationId);

}
