package com.shopkart.auth.sharding;

public final class ShardContextHolder {

    private static final ThreadLocal<ShardType> CONTEXT = new ThreadLocal<>();

    private ShardContextHolder() {}

    public static ShardType getShard() {
        return CONTEXT.get();
    }
    public static void setShard(ShardType shardType) {
        CONTEXT.set(shardType);
    }

    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * Determines the shard based on the hash of the email.
     * Uses Math.abs because hashCode() can be negative.
     */

    public static ShardType determineShardByEmail(String email) {
        if(email == null || email.isEmpty()) {
            return ShardType.SHARD_0;
        }
        int hash = Math.abs(email.trim().toLowerCase().hashCode());
        int shardIndex = hash % 2; // 2 shards (0 or 1)
        return (shardIndex == 0) ? ShardType.SHARD_0 : ShardType.SHARD_1;
    }

}
