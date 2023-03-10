package it.pagopa.bs.web.service.lock;

import org.springframework.beans.factory.DisposableBean;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastCluster implements DisposableBean {

    private final HazelcastInstance hazelcastInstance;

    public HazelcastCluster(String clusterName) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("hazelcast");
        clientConfig.setClusterName(clusterName);
        this.hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
    }

    public HazelcastInstance getInstance() {
        return this.hazelcastInstance;
    }

    @Override
    public void destroy() throws Exception {
        HazelcastClient.shutdownAll();
    }
}
