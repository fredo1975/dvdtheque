package fr.fredos.dvdtheque.allocine.config;

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
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
@ComponentScan
@Profile({ "prod1","prod2","dev1","dev2","local1","local2" })
public class HazelcastConfiguration {
	@Value("${hazelcast.cluster-name}")
	private String clusterName;
	@Value("#{'${hazelcast.networkconfig.tcpipconfig.members}'.split(',')}")
	private List<String> listOfMembers;
	@Bean
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();
		TcpIpConfig tcpIpConfig = new TcpIpConfig().setEnabled(true);
		if(CollectionUtils.isNotEmpty(listOfMembers)) {
			for(String tcpM : listOfMembers) {
				tcpIpConfig.addMember(tcpM);
			}
		}
		config.getNetworkConfig().getJoin().setTcpIpConfig(tcpIpConfig);
		//config.getNetworkConfig().getInterfaces().setEnabled(true).addInterface("192.168.1.133");
		config.setClusterName(clusterName);
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.setInstanceName(RandomStringUtils.random(8, true, false))
				.addMapConfig(new MapConfig().setName("ficheFilms")).addMapConfig(new MapConfig().setName("ficheFilmsByTitle"));
						//.setMaxSizeConfig(new MaxSizeConfig(10000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
						//.setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(300)).addMapConfig(new MapConfig().setName("films"));
		return Hazelcast.newHazelcastInstance(config);
	}
}
