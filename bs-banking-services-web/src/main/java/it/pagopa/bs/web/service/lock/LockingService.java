package it.pagopa.bs.web.service.lock;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@Service
@CustomLog
@RequiredArgsConstructor
public class LockingService {

    private final HazelcastCluster hazelcastCluster;

    public boolean acquireLock(String schedulerMapName, String mapKey, int ttl, TimeUnit timeUnit) {
        try {
            return hazelcastCluster.getInstance()
                .getMap(schedulerMapName)
                .putIfAbsent(mapKey, false, ttl, timeUnit) == null;
        } catch (Throwable e) {
            log.error("Cannot obtain lock from Hazelcast!", e);
            throw e;
        }
    }
}
