package personal.streaming.content_post.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import personal.streaming.application.common.enums.Category;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;
    private String url;

    @Min(0)
    private Long length;
    @Min(0)
    private Long totalViews;

    private Boolean active;
    private Boolean deleted;

    @Builder
    public ContentPost(
            Long id,
            Long userId,
            String title,
            String description,
            Category category,
            String url,
            Long length,
            Long totalViews,
            Boolean active,
            Boolean deleted
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
        this.length = length;
        this.totalViews = totalViews;
        this.active = active;
        this.deleted = deleted;
    }
}
