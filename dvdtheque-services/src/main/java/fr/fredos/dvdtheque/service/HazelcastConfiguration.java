package fr.fredos.dvdtheque.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;

@Configuration
public class HazelcastConfiguration {
	@Value("${hazelcast.group.name}")
	private String groupConfigName;
	@Value("${hazelcast.networkconfig.tcpipconfig.members}")
	private String members;
	@Bean
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();
		config.getGroupConfig().setName(groupConfigName);
		/*
		config.getNetworkConfig().getInterfaces().setEnabled(true);
		List<String> interfaces = new ArrayList<>();
		interfaces.add("192.168.1.*");
		config.getNetworkConfig().getInterfaces().setInterfaces(interfaces);
		*/
		List<String> tcpIpConfigmembers = new ArrayList<String>();
		tcpIpConfigmembers.add(members);
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(tcpIpConfigmembers);
		config.setInstanceName(RandomStringUtils.random(8, true, false))
				.addMapConfig(new MapConfig().setName("films")
						.setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
						.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(200));
		return Hazelcast.newHazelcastInstance(config);
	}

	@Bean
	CacheManager cacheManager() {
		return new HazelcastCacheManager(hazelcastInstance());
	}
}
