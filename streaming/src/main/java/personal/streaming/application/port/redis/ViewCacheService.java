package personal.streaming.application.port.redis;

public interface ViewCacheService {

    void incrementView(Long contentPostId);

}
