package com.example.fitpassserver.domain.review.scheduler;

import com.example.fitpassserver.domain.review.entity.ReviewNotification;
import com.example.fitpassserver.domain.review.repsotiory.ReviewNotificationRepository;
import com.example.fitpassserver.domain.review.service.ReviewReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewNotificationScheduler {

    private final ReviewNotificationRepository reviewNotificationRepository;
    private final ReviewReminderService reviewReminderService;

    @Scheduled(fixedRate = 60000) // 1분마다
    public void sendReminders() {
        List<ReviewNotification> targets =
                reviewNotificationRepository.findBySentFalseAndNotifyAtBefore(LocalDateTime.now());

        for (ReviewNotification notification : targets) {
            reviewReminderService.sendReminder(notification);
        }
    }
}

