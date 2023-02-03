package it.pagopa.bs.web.service.lock;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@CustomLog
@RequiredArgsConstructor
public class ClusteredLockingService {

    private final HazelcastCluster hazelcastCluster;

    @Value("${pagopa.bs.hazelcast.cluster-name}")
    private String hazelcastInstanceName;

    private static final String mapBaseName = "pagopa###distributed-locks";

    public boolean executeImmediateAndRelease(
        String reference, 
        Runnable criticalSectionAcquired, 
        Runnable criticalSectionNotAcquired, 
        Runnable fallback, 
        long waitingTime, 
        TimeUnit waitingTimeUnit
    ) {
        return this.executeImmediate(
            reference, criticalSectionAcquired, criticalSectionNotAcquired, 
            fallback, waitingTime, waitingTimeUnit, true
        );
    }

    public <T> Mono<T> executeImmediatePublisherAndRelease(
        String reference, 
        Supplier<Mono<T>> criticalSectionAcquired, 
        Supplier<Mono<T>> criticalSectionNotAcquired, 
        Supplier<Mono<T>> fallback, 
        long waitingTime, 
        TimeUnit waitingTimeUnit
    ) {
        return this.executeImmediatePublisher(
            reference, criticalSectionAcquired, criticalSectionNotAcquired, 
            fallback, waitingTime, waitingTimeUnit, true
        );
    }

    private boolean executeImmediate(
        String reference, 
        Runnable criticalSectionAcquired, 
        Runnable criticalSectionNotAcquired, 
        Runnable fallback, 
        long waitingTime, 
        TimeUnit waitingTimeUnit, 
        boolean doReleaseLock
    ) {
        boolean isLockAcquired = false;
        ZonedDateTime endDatetime = ZonedDateTime.now();

        try {
            isLockAcquired = this.acquireLock(reference, waitingTime, waitingTimeUnit);
            endDatetime = endDatetime.plus(waitingTimeUnit.toMillis(waitingTime), ChronoUnit.MILLIS);
            if (isLockAcquired) {
                criticalSectionAcquired.run();
                boolean var11 = true;
                return var11;
            }

            criticalSectionNotAcquired.run();
        } catch (Throwable var15) {
            log.error("Failed to acquire lock", var15);
            fallback.run();
        } finally {
            if (isLockAcquired && doReleaseLock) {
                this.releaseLock(reference, endDatetime);
            }
        }

        return false;
    }

    private <T> Mono<T> executeImmediatePublisher(
        String reference, 
        Supplier<Mono<T>> criticalSectionAcquired, 
        Supplier<Mono<T>> criticalSectionNotAcquired, 
        Supplier<Mono<T>> fallback, 
        long waitingTime, 
        TimeUnit waitingTimeUnit, 
        boolean doReleaseLock
    ) {
        try {
            boolean isLockAcquired = this.acquireLock(reference, waitingTime, waitingTimeUnit);
            if (isLockAcquired) {
                ZonedDateTime endDatetime = ZonedDateTime.now().plus(waitingTimeUnit.toMillis(waitingTime), ChronoUnit.MILLIS);
                return ((Mono)criticalSectionAcquired.get()).doFinally((signalType) -> {
                    if (doReleaseLock) {
                        this.releaseLock(reference, endDatetime);
                    }

                }).onErrorResume((throwable) -> {
                    log.error("Failed to execute critical section", throwable);
                    return (Mono)fallback.get();
                });
            } else {
                return ((Mono)criticalSectionNotAcquired.get()).onErrorResume((throwable) -> {
                    log.error("Failed to execute outside of critical section", throwable);
                    return (Mono)fallback.get();
                });
            }
        } catch (Throwable var11) {
            log.error("Failed to acquire lock", var11);
            return (Mono)fallback.get();
        }
    }

    private boolean releaseLock(String reference, ZonedDateTime endDatetime) {
        if (ZonedDateTime.now().isAfter(endDatetime)) {
            return true;
        } else {
            try {
                IMap<String, Boolean> iMap = this.getLock();
                iMap.delete(reference);
                return true;
            } catch (Throwable var4) {
                log.error("Failed to release the lock", var4);
                return false;
            }
        }
    }

    private boolean acquireLock(String reference, long waitingTime, TimeUnit waitingTimeUnit) {
        IMap<String, Boolean> iMap = this.getLock();
        return iMap.putIfAbsent(reference, true, waitingTime, waitingTimeUnit) == null;
    }

    private IMap<String, Boolean> getLock() {
        HazelcastInstance hazelcastInstance = this.hazelcastCluster.getInstance();
        return hazelcastInstance.getMap(mapBaseName);
    }
}
