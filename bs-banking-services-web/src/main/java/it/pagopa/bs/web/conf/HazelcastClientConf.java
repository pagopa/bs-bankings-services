package it.pagopa.bs.web.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class HazelcastClientConf {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config helloWorldConfig = new Config();
        helloWorldConfig.setClusterName("pagopa");

        return Hazelcast.newHazelcastInstance(helloWorldConfig);
    }
}
