package personal.streaming.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personal.streaming.application.common.redis.dto.AbusingKey;
import personal.streaming.application.port.redis.AbusingService;
import personal.streaming.application.port.redis.LoggingContentService;
import personal.streaming.application.port.redis.ViewCacheService;
import personal.streaming.application.dto.content_post_view_report.ContentPostViewReport;
import personal.streaming.application.dto.post_with_interaction.PostWithInteraction;
import personal.streaming.application.service.StreamingService;

@RestController
@RequestMapping("/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;
    private final AbusingService abusingService;
    private final ViewCacheService viewCacheService;
    private final LoggingContentService loggingContentService;

    @GetMapping("/contentPosts/{contentPostId}/users/{userId}")
    public ResponseEntity<PostWithInteraction> startContent(
            HttpServletRequest request,
            @PathVariable Long contentPostId,
            @PathVariable Long userId
    ) {
        AbusingKey key = getAbusingKey(request, contentPostId, userId);

        PostWithInteraction response = streamingService.getPostWithInteraction(contentPostId, userId);

        // 분산락 적용 예정
        if (!abusingService.isAbusing(key)) { // 어뷰징 확인
            abusingService.setAbusing(key); // 어뷰징 등록
            if (!response.userId().equals(userId)) { // 크리에이터인지 확인
                viewCacheService.incrementView(contentPostId); // 조회수 1 증가
            }
            loggingContentService.addLog(contentPostId); // 로그 발생 포스트 키만 저장
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/contentPosts/{contentPostId}/users/{userId}")
    public void endContent(
            @PathVariable Long contentPostId,
            @PathVariable Long userId,
            @RequestBody ContentPostViewReport report
    ) {
        streamingService.saveContentPostWatchHistory(report.toContentPostWatchHistory(contentPostId, userId));
    }

    private AbusingKey getAbusingKey(HttpServletRequest request, Long contentPostId, Long userId) {
        String userIp = getUserIp(request);
        return AbusingKey.builder()
                .contentPostId(contentPostId)
                .userId(userId)
                .ip(userIp)
                .build();
    }

    public String getUserIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
