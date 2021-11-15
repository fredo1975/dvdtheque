package fr.fredos.dvdtheque.service.config;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
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
@Profile({ "prod1","prod2","dev1","dev2","local1","local2" })
public class HazelcastConfiguration {
	@Value("${hazelcast.group.name}")
	private String groupName;
	@Value("#{'${hazelcast.networkconfig.tcpipconfig.members}'.split(',')}")
	private List<String> listOfMembers;
	@Bean
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();
		//config.getNetworkConfig().getInterfaces().setEnabled(false);
		MulticastConfig multicastConfig = new MulticastConfig().setEnabled(true);
		//multicastConfig.setMulticastGroup(groupName);
		if(CollectionUtils.isNotEmpty(listOfMembers)) {
			listOfMembers.stream().map(trustedInterface -> multicastConfig.addTrustedInterface(trustedInterface));
		}
		config.getNetworkConfig().getJoin().setMulticastConfig(multicastConfig);
		/*
		TcpIpConfig tcpIpConfig = new TcpIpConfig().setEnabled(true);
		if(CollectionUtils.isNotEmpty(listOfMembers)) {
			listOfMembers.stream().map(tcpM -> tcpIpConfig.addMember(tcpM));
		}
		config.getNetworkConfig().getJoin().setTcpIpConfig(tcpIpConfig);*/
		config.setInstanceName(RandomStringUtils.random(8, true, false))
				.addMapConfig(new MapConfig().setName("films"));
						//.setMaxSizeConfig(new MaxSizeConfig(10000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
						//.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(300)).addMapConfig(new MapConfig().setName("films"));
		return Hazelcast.newHazelcastInstance(config);
	}
}
