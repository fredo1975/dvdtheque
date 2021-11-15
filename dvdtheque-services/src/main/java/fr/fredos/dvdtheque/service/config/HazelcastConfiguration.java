package fr.fredos.dvdtheque.service.config;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
@ComponentScan
@Profile({ "prod1","prod2","dev1","dev2" })
public class HazelcastConfiguration {
	/*@Value("${hazelcast.networkconfig.interface}")
	private String interfaces;*/
	@Bean
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();
		config.getNetworkConfig().getInterfaces().setEnabled(false);
		config.getNetworkConfig().getJoin().setMulticastConfig(new MulticastConfig().setEnabled(true));
		config.setInstanceName(RandomStringUtils.random(8, true, false))
				.addMapConfig(new MapConfig().setName("films"));
						//.setMaxSizeConfig(new MaxSizeConfig(10000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
						//.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(300)).addMapConfig(new MapConfig().setName("films"));
		return Hazelcast.newHazelcastInstance(config);
	}
}
