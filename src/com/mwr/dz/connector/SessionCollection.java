package com.mwr.dz.connector;

import com.mwr.cinnibar.connection.AbstractSession;
import com.mwr.cinnibar.connection.AbstractSessionCollection;
import com.mwr.dz.Agent;
import com.mwr.dz.connector.ConnectorParameters.Status;
import com.mwr.dz.service_connectors.SessionServiceConnection;
import com.mwr.dz.services.SessionService;

public class SessionCollection extends AbstractSessionCollection {
	
	private Connector connector = null;
	private SessionServiceConnection session_service_connection = null;
	
	public SessionCollection(Connector connector) {
		this.connector = connector;
		this.session_service_connection = new SessionServiceConnection();
		
		SessionService.startAndBindToService(Agent.getInstance().getMercuryContext(), this.session_service_connection);
	}
	
	@Override
	public Session create() {
		return (Session)this.storeSession(new Session(this.connector));
	}
	
	public SessionServiceConnection getSessionService() {
		return this.session_service_connection;
	}
	
	@Override
	public void onSessionStarted(AbstractSession session) {
		this.connector.setStatus(Status.ACTIVE);
		
		this.getSessionService().notifySessionStarted(session.getSessionId());
	}
	
	@Override
	public void onSessionStopped(AbstractSession session) {
		this.getSessionService().notifySessionStopped(session.getSessionId());
		this.connector.setStatus(Status.ONLINE);
		
		if(!this.any())
			this.connector.lastSessionStopped();
	}
	
}