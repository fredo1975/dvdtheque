package fr.fredos.dvdtheque.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;

@Configuration
public class HazelcastConfiguration {
	@Value("${hazelcast.group.name}")
	private String groupConfigName;
	@Bean
	public Config hazelCastConfig() {
		Config config = new Config();
		config.getGroupConfig().setName(groupConfigName);
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getInterfaces().setEnabled(true);
		List<String> interfaces = new ArrayList<>();
		interfaces.add("192.168.1.*");
		config.setProperty("hazelcast.initial.min.cluster.size","2");
		config.getNetworkConfig().getInterfaces().setInterfaces(interfaces);
		config.setInstanceName("hazelcast-instance").addMapConfig(new MapConfig().setName("films")
				.setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
				.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(200));
		return config;
	}
}
