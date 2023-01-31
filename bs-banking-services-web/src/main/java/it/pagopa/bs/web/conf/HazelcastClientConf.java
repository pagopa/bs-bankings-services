package it.pagopa.bs.web.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class HazelcastClientConf {

    @Value("${pagopa.bs.hazelcast.cluster-name}")
    private String clusterName;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config helloWorldConfig = new Config();
        helloWorldConfig.setClusterName(clusterName);
        return Hazelcast.newHazelcastInstance(helloWorldConfig);
    }
}
