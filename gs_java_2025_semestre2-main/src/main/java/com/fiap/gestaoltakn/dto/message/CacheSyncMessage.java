package com.fiap.gestaoltakn.dto.message;

import java.io.Serializable;

public class CacheSyncMessage implements Serializable {
    private String cacheName;
    private String cacheKey;
    private String operation;
    private Long entityId;
    private String entityType;

    public CacheSyncMessage() {}

    public CacheSyncMessage(String cacheName, String cacheKey, String operation, Long entityId, String entityType) {
        this.cacheName = cacheName;
        this.cacheKey = cacheKey;
        this.operation = operation;
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public String getCacheName() { return cacheName; }
    public void setCacheName(String cacheName) { this.cacheName = cacheName; }

    public String getCacheKey() { return cacheKey; }
    public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

}
