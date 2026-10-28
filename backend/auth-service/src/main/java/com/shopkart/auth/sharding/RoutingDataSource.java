package com.shopkart.auth.sharding;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


/**
 * Custom DataSource router extending Spring's AbstractRoutingDataSource.
 * Called by Spring on every DB transaction to decide which shard to connect to.
 */

public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
       ShardType currentShard = ShardContextHolder.getShard();
        return (currentShard != null) ? currentShard : ShardType.SHARD_0;
    }
}
