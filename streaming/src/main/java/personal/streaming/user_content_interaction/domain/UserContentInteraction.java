package personal.streaming.user_content_interaction.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserContentInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentPostId;
    private Long userId;

    private Long pausedAt;

    @Builder
    public UserContentInteraction(
            Long id,
            Long contentPostId,
            Long userId,
            Long pausedAt
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.pausedAt = pausedAt;
    }

    public void updatePausedAt(Long pausedAt) {
        this.pausedAt = pausedAt;
    }
}
