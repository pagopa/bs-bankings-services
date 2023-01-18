package it.pagopa.bs.web.conf;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import it.pagopa.bs.web.properties.kafka.KafkaConsumerProperties;
import it.pagopa.bs.web.properties.kafka.KafkaProducerProperties;
import lombok.CustomLog;

@Configuration
@EnableConfigurationProperties({
        KafkaConsumerProperties.class,
        KafkaProducerProperties.class
})
@CustomLog
public class KafkaAutoConfiguration {
    
    private static final String RUN_EXECUTOR_NAME = "kafka-helper-run-executor";
    private static final String LISTENER_EXECUTOR_NAME = "kafka-helper-listener-executor";

    @Bean(destroyMethod = "shutdown")
    @Qualifier(RUN_EXECUTOR_NAME)
    public ThreadPoolTaskScheduler kafkaHelperRunExecutor() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix(RUN_EXECUTOR_NAME);
        return threadPoolTaskScheduler;
    }

    @Bean(destroyMethod = "shutdown")
    @Qualifier(LISTENER_EXECUTOR_NAME)
    public ThreadPoolTaskScheduler kafkaHelperListenerExecutor() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix(LISTENER_EXECUTOR_NAME);
        return threadPoolTaskScheduler;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public KafkaHelper kafkaHelper(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, KafkaConsumerProperties properties,
                                   @Qualifier(RUN_EXECUTOR_NAME) ThreadPoolTaskScheduler kafkaHelperRunExecutor,
                                   @Qualifier(LISTENER_EXECUTOR_NAME) ThreadPoolTaskScheduler kafkaHelperListenerExecutor) {
        return new KafkaHelper(kafkaListenerEndpointRegistry, properties, kafkaHelperRunExecutor, kafkaHelperListenerExecutor);
    }

    private class KafkaHelper {

        private final KafkaListenerEndpointRegistry registry;
        private final KafkaConsumerProperties properties;
        private final ThreadPoolTaskScheduler runExecutor;
        private final ThreadPoolTaskScheduler listenerExecutor;
    
        public KafkaHelper(KafkaListenerEndpointRegistry registry, KafkaConsumerProperties properties, ThreadPoolTaskScheduler runExecutor, ThreadPoolTaskScheduler listenerExecutor) {
            this.registry = registry;
            this.properties = properties;
            this.runExecutor = runExecutor;
            this.listenerExecutor = listenerExecutor;
        }
    
        /**
         * Start kafka listener
         */
        public void start() {
    
            if (!this.properties.isInitAtStartup()) return;
    
            final int runs = this.properties.getStartupRuns();
            final int incrementalDelay = this.properties.getStartupIncrementalDelay();
    
            Function<KafkaListenerEndpointRegistry, Boolean> kafkaStarter = kafkaListenerEndpointRegistry -> {
                registry.getAllListenerContainers().forEach(c -> {
                    this.listenerExecutor.execute(() -> {
                        try {
                            c.getContainerProperties().setMissingTopicsFatal(false);
                            c.getContainerProperties().setIdleBetweenPolls(1000);
                            c.setAutoStartup(true);
                        } catch (Throwable e) {
                            log.warn("interrupt exception", e);
                        }
                        if (!c.isRunning()) {
                            c.start();
                        }
                    });
                });
                return true;
            };
    
            Function<Integer, Boolean> incrementalRuns = duration -> {
                this.runExecutor.execute(() -> {
                    try {
                        int run = 1;
                        while (run <= runs) {
                            Thread.sleep(TimeUnit.SECONDS.toMillis((long) duration*run));
                            kafkaStarter.apply(registry);
                            run++;
                        }
                    } catch (Throwable e) {
                        log.warn("interrupt exception", e);
                    }
                });
                return true;
            };
    
            incrementalRuns.apply(incrementalDelay);
        }
    
        /**
         * Stop kafka listener
         */
        public void stop() {
            registry.getAllListenerContainers().forEach(c -> {
                c.setAutoStartup(false);
                if (c.isRunning()) {
                    log.warn("Stopping listener...");
                    c.stop();
                }
            });
        }
    }
}
