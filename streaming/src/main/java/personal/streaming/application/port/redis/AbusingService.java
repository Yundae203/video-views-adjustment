package personal.streaming.application.port.redis;

import personal.streaming.application.common.redis.dto.AbusingKey;

public interface AbusingService {

    boolean isAbusing(AbusingKey key);

    void setAbusing(AbusingKey key);
}
