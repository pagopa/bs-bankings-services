package it.pagopa.bs.web.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.pagopa.bs.web.service.lock.HazelcastCluster;

@Configuration
public class HazelcastInstanceConf {

    @Value("${pagopa.bs.hazelcast.cluster-name}")
    private String clusterName;

    @Bean(destroyMethod = "destroy")
    public HazelcastCluster hazelcastCluster() {
        return new HazelcastCluster(clusterName);
    }
}
