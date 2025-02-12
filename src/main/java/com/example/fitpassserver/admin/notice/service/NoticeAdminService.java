package com.example.fitpassserver.admin.notice.service;

import com.example.fitpassserver.admin.notice.converter.NoticeAdminConverter;
import com.example.fitpassserver.admin.notice.dto.response.NoticeAdminResDTO;
import com.example.fitpassserver.admin.notice.exception.NoticeAdminErrorCode;
import com.example.fitpassserver.admin.notice.exception.NoticeAdminException;
import com.example.fitpassserver.domain.notice.entity.Notice;
import com.example.fitpassserver.domain.notice.exception.NoticeErrorCode;
import com.example.fitpassserver.domain.notice.exception.NoticeException;
import com.example.fitpassserver.domain.notice.repository.NoticeRepository;
import com.example.fitpassserver.domain.notice.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeAdminService {
    private final NoticeRepository noticeRepository;
    private final NoticeService noticeService;

    public NoticeAdminService(NoticeRepository noticeRepository, NoticeService noticeService) {
        this.noticeRepository = noticeRepository;
        this.noticeService = noticeService;
    }
    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getNoticeAdminList(String keyword,Pageable pageable) {
        Page<Notice> noticePage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            noticePage = noticeRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
        } else {
            noticePage = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        List<NoticeAdminResDTO> noticeList = NoticeAdminConverter.toNoticeAdminResDTOList(noticePage.getContent(), noticeService);

        Map<String, Object> response = new HashMap<>();
        response.put("totalPages", noticePage.getTotalPages());
        response.put("totalElements", noticePage.getTotalElements());
        response.put("currentPage", noticePage.getNumber()+ 1);
        response.put("pageSize", noticePage.getSize());
        response.put("content", noticeList);

        return response;
    }
    // 🔹 홈 슬라이드 업데이트 (체크 시 최대 3개 제한)
    @Transactional
    public void updateHomeSlideStatus(Long noticeId, boolean isHomeSlide) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND));
        if (notice.isDraft()) {
            throw new NoticeAdminException(NoticeAdminErrorCode.HOME_SLIDE_DRAFT_NOT_ALLOWED);
        }
        if (isHomeSlide && noticeRepository.countByIsHomeSlideTrue() >= 3) {
            throw new NoticeAdminException(NoticeAdminErrorCode.HOME_SLIDE_LIMIT_EXCEEDED);
        }
        notice.setHomeSlide(isHomeSlide);
        noticeRepository.save(notice);
    }
}