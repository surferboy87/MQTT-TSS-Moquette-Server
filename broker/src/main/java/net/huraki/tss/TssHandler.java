package net.huraki.tss;

import java.util.List;

import io.moquette.spi.ClientSession;
import io.moquette.spi.impl.subscriptions.Subscription;
import io.moquette.spi.impl.subscriptions.SubscriptionsStore;
import net.huraki.tss.persistence.MapDBTssTopicStore;

public class TssHandler {
	
	private MapDBTssTopicStore tssTopicStore;
	private SubscriptionsStore subscriptions;
	private String tssStateTopic = "tss/state";
	
	public TssHandler(){
		
	}
	
	public void init(MapDBTssTopicStore tssTopicStore, SubscriptionsStore subscriptions){
		this.tssTopicStore = tssTopicStore;
		this.subscriptions = subscriptions;
	}
	
	public void createBinding(String mqttTopic, String shortTopic){
		this.tssTopicStore.storeBinding(mqttTopic, shortTopic);
	}
	
	public String translateBeforeSend(String mqttTopic){
		if(this.tssTopicStore.hasShortTopic(mqttTopic)){
			return this.tssTopicStore.getShortTopic(mqttTopic);
		}
		return mqttTopic;
	}
	
	public String translateBeforeCallback(String shortTopic){
		if(this.tssTopicStore.hasMqttTopic(shortTopic)){
			return this.tssTopicStore.getMqttTopic(shortTopic);
		}
		return shortTopic;
	}
	
	public void removeBinding(String topic){
		this.tssTopicStore.removeBinding(topic);
	}
	
	public String representStore(){
		return this.tssTopicStore.toString();
	}

	public boolean isRegistedTssClient(ClientSession cs) {
		List<Subscription> topicMatchingSubscriptions = subscriptions.matches(this.tssStateTopic);
		for (final Subscription sub : topicMatchingSubscriptions){
			if(sub.getClientId() == cs.clientID){
				return true;
			}
		}
		return false;
	}

}
