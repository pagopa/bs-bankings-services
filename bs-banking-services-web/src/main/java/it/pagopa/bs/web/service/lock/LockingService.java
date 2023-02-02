package it.pagopa.bs.web.service.lock;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@Service
@CustomLog
@RequiredArgsConstructor
public class LockingService {

    @Value("${pagopa.bs.hazelcast.cluster-name}")
    private String clusterName;

    private final HazelcastCluster hazelcastCluster;

    public boolean acquireLock(String schedulerMapName, String mapKey, int ttl, TimeUnit timeUnit) {
        try {
            return hazelcastCluster.getInstance(clusterName)
                .getMap(schedulerMapName)
                .putIfAbsent(mapKey, false, ttl, timeUnit) == null;
        } catch (Throwable e) {
            log.error("Cannot obtain lock from Hazelcast!", e);
            throw e;
        }
    }
}
