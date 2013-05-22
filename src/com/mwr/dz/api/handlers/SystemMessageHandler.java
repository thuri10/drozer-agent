package com.mwr.dz.api.handlers;

import com.mwr.cinnibar.api.InvalidMessageException;
import com.mwr.cinnibar.api.Protobuf.Message;
import com.mwr.cinnibar.api.builders.MessageFactory;
import com.mwr.cinnibar.api.builders.SystemResponseFactory;
import com.mwr.cinnibar.api.handlers.AbstractSystemMessageHandler;
import com.mwr.dz.Agent;
import com.mwr.dz.connector.Connection;
import com.mwr.dz.connector.Session;

public class SystemMessageHandler extends AbstractSystemMessageHandler {
	
	private Connection connection = null;
	
	public SystemMessageHandler(Connection connection) {
		this.connection = connection;
	}
	
	protected Message handleListDevices(Message message) throws InvalidMessageException {
		MessageFactory factory = new MessageFactory(SystemResponseFactory.deviceList(message).addDevice(
				Agent.getInstance().getUID(),
				android.os.Build.MANUFACTURER,
				android.os.Build.MODEL,
				android.os.Build.VERSION.RELEASE));
		
		factory.inReplyTo(message);
		
		return factory.build();
	}
	
	protected Message handleListSessions(Message message) throws InvalidMessageException {
		SystemResponseFactory response = SystemResponseFactory.sessionList(message);
		
		//for(Session session : this.connection.getSessions())
		//	response.addSession(session);
		
		MessageFactory factory = new MessageFactory(response);
		
		factory.inReplyTo(message);
		
		return factory.build();
	}
	
	protected Message handlePing(Message message) throws InvalidMessageException {
		MessageFactory factory = new MessageFactory(SystemResponseFactory.pong(message));
		
		factory.inReplyTo(message);
		
		return factory.build();
	}
	
	protected Message startSession(Message message) throws InvalidMessageException {
		Session session = (Session)this.connection.startSession(message.getSystemRequest().getPassword());
		
		if(session != null) {
			MessageFactory factory = new MessageFactory(SystemResponseFactory.session(session.getSessionId()));
			
			factory.inReplyTo(message);
			
			return factory.build();
		}
		else {
			MessageFactory factory = new MessageFactory(SystemResponseFactory.session(Session.nullSession().getSessionId()).isError());
			
			factory.inReplyTo(message);
			
			return factory.build();
		}
	}
	
	protected Message stopSession(Message message) throws InvalidMessageException {
		Session session = (Session)this.connection.stopSession(message.getSystemRequest().getSessionId());
		
		if(session != null) {
			MessageFactory factory = new MessageFactory(SystemResponseFactory.session(session.getSessionId()));
			
			factory.inReplyTo(message);
			
			return factory.build();
		}
		else {
			MessageFactory factory = new MessageFactory(SystemResponseFactory.session(Session.nullSession().getSessionId()).isError());
			
			factory.inReplyTo(message);
			
			return factory.build();
		}
	}
	
}