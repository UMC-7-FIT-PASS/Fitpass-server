package com.example.fitpassserver.domain.review.service;

import com.example.fitpassserver.domain.fitness.entity.MemberFitness;
import com.example.fitpassserver.domain.kakaoNotice.util.KakaoAlimtalkUtil;
import com.example.fitpassserver.domain.review.entity.ReviewNotification;
import com.example.fitpassserver.domain.review.repsotiory.ReviewNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ReviewReminderService {

    private final ReviewNotificationRepository reviewNotificationRepository;
    private final KakaoAlimtalkUtil kakaoAlimtalkUtil;

    public void reserve(MemberFitness memberFitness) {
        ReviewNotification notification = new ReviewNotification();
        notification.setMemberFitness(memberFitness);
        notification.setNotifyAt(LocalDateTime.now().plusHours(1));
        reviewNotificationRepository.save(notification);
    }

    @Transactional
    public void sendReminder(ReviewNotification notification) {
        String phoneNumber = notification.getMemberFitness().getMember().getPhoneNumber();

        kakaoAlimtalkUtil.sendReviewNotice(phoneNumber);

        notification.setSent(true);
        reviewNotificationRepository.save(notification);
    }
}
