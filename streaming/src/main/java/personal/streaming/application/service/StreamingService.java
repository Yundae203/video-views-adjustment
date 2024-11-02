package personal.streaming.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.application.dto.post_with_interaction.PostWithInteraction;
import personal.streaming.application.port.advertisement.AdvertisementService;
import personal.streaming.content_post.domain.ContentPost;
import personal.streaming.content_post.service.ContentPostService;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;
import personal.streaming.content_post_watch_history.service.ContentPostWatchHistoryService;
import personal.streaming.advertisement.domain.Advertisement;
import personal.streaming.user_content_interaction.domain.UserContentInteraction;
import personal.streaming.user_content_interaction.service.UserContentInteractionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private final UserContentInteractionService userContentInteractionService;
    private final ContentPostService contentPostService;
    private final AdvertisementService advertisementService;
    private final ContentPostWatchHistoryService contentPostWatchHistoryService;

    public PostWithInteraction getPostWithInteraction(Long postId, Long userId) {
        UserContentInteraction interaction = userContentInteractionService.findByContentPostIdAndUserId(postId, userId);
        if (interaction == null) {
            interaction = userContentInteractionService.init(postId, userId);
        }
        ContentPost post = contentPostService.findById(postId);
        List<Advertisement> ads = advertisementService.insertAdvertisements(post.getCategory(), post.getLength());

        return PostWithInteraction.of(interaction, post, ads);
    }

    @Transactional
    public void saveContentPostWatchHistory(ContentPostWatchHistory history) {
        contentPostWatchHistoryService.save(history);
        UserContentInteraction interaction = userContentInteractionService
                .findByContentPostIdAndUserId(history.getContentPostId(), history.getUserId());

        interaction.updatePausedAt(history.getPausedAt());
        userContentInteractionService.save(interaction);
    }
}
