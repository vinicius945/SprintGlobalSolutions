package com.fiap.gestaoltakn.service.message;

import com.fiap.gestaoltakn.config.RabbitMQConfig;
import com.fiap.gestaoltakn.dto.message.CacheSyncMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheSyncConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CacheSyncConsumer.class);

    private final CacheManager cacheManager;

    public CacheSyncConsumer(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @RabbitListener(queues = RabbitMQConfig.CACHE_SYNC_QUEUE)
    public void processarCacheSync(CacheSyncMessage message) {
        logger.info("Processando sincronização de cache: {}", message);

        try {
            var cache = cacheManager.getCache(message.getCacheName());
            if (cache != null) {
                if ("EVICT".equals(message.getOperation())) {
                    cache.clear();
                    logger.info("Cache '{}' limpo completamente via mensageria", message.getCacheName());
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao processar sincronização de cache: {}", e.getMessage(), e);
        }
    }

}
