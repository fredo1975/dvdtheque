package fr.fredos.dvdtheque.service.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.AutoDetectionConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
@ComponentScan
public class HazelcastConfiguration {
	@Value("${hazelcast.networkconfig.interface}")
	private String interfaces;
	@Bean
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();
		config.getNetworkConfig().getInterfaces().setEnabled(true);
		List<String> interfacesList = new ArrayList<>();
		interfacesList.add(interfaces);
		config.getNetworkConfig().getInterfaces().setInterfaces(interfacesList);
		config.getNetworkConfig().setJoin(new JoinConfig().setAutoDetectionConfig(new AutoDetectionConfig().setEnabled(false)));
		config.setInstanceName(RandomStringUtils.random(8, true, false))
				.addMapConfig(new MapConfig().setName("films"));
						//.setMaxSizeConfig(new MaxSizeConfig(10000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
						//.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(300)).addMapConfig(new MapConfig().setName("films"));
		return Hazelcast.newHazelcastInstance(config);
	}
/*
	@Bean
	CacheManager cacheManager() {
		return new HazelcastCacheManager(hazelcastInstance());
	}*/
}
