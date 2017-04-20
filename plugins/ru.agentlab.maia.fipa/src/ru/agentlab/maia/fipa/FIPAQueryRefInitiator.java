/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.fipa;

import static ru.agentlab.maia.fipa.FIPAPerformativeNames.AGREE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.CANCEL;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.FAILURE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.INFORM;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.NOT_UNDERSTOOD;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.QUERY_REF;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.REFUSE;
import static ru.agentlab.maia.fipa.FIPAProtocolNames.FIPA_QUERY;
import static ru.agentlab.maia.fipa.FIPAProtocolNames.FIPA_REQUEST;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.semanticweb.owlapi.model.OWLAxiom;

import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.annotation.trigger.AddedExternalEvent;
import ru.agentlab.maia.agent.event.RoleRemovedEvent;
import ru.agentlab.maia.message.annotation.OnMessageReceived;
import ru.agentlab.maia.message.impl.AclMessage;
import ru.agentlab.maia.time.TimerEvent;

public class FIPAQueryRefInitiator extends AbstractInitiator {

	private State state = null;

	@PostConstruct
	public void onStart() {
		send(FIPA_QUERY, QUERY_REF, template);
		startTimer();
		state = State.REQUEST_SENT;
	}

	@AddedExternalEvent(TimerEvent.class)
	public void onDeadline(TimerEvent event) {
		if (notMyEvent(event)) {
			return;
		}
		if (state != State.FINISHED) {
			addEvent(new ProtocolDeadlineEvent());
			addGoal(new RoleRemovedEvent(role));
			state = State.FINISHED;
		}
	}

	@OnMessageReceived
	public void onMessage(AclMessage message) {
		if (notMyMessage(message)) {
			return;
		}
		switch (message.getPerformative()) {
		case AGREE:
			stopTimer();
			state = State.WAIT_FOR_RESULT;
			return;
		case INFORM:
			stopTimer();
			IBeliefParser parser = getBeliefParser(message.getLanguage());
			if (parser == null) {
				reply(message, NOT_UNDERSTOOD, "Unknown language [" + message.getLanguage() + "]");
				abortProtocol(message);
				return;
			}
			try {
				OWLAxiom axiom = parser.parse(message.getContent());
				successProtocol(axiom);
			} catch (Exception e) {
				reply(message, NOT_UNDERSTOOD, e.getMessage());
				abortProtocol(message);
			}
			return;
		case NOT_UNDERSTOOD:
		case REFUSE:
		case FAILURE:
			stopTimer();
			abortProtocol(message);
			return;
		}
	}

	@PreDestroy
	public void onDestroy() {
		stopTimer();
		if (state == State.WAIT_FOR_RESULT) {
			send(FIPA_REQUEST, CANCEL);
		}
	}

	private void successProtocol(Object result) {
		addEvent(new ProtocolSuccessEvent(role, result));
		addGoal(new RoleRemovedEvent(role));
		state = State.FINISHED;
	}

	private void abortProtocol(IMessage message) {
		addEvent(new ProtocolAbortedEvent(role, message));
		addGoal(new RoleRemovedEvent(role));
		state = State.FINISHED;
	}

	private boolean notMyEvent(TimerEvent event) {
		return event.getEventKey() != conversationId;
	}

	private boolean notMyMessage(AclMessage message) {
		return !message.checkConversationId(conversationId.toString()) || !message.checkProtocol(FIPA_QUERY);
	}

	private static enum State {

		REQUEST_SENT,

		WAIT_FOR_RESULT,

		FINISHED

	}

}
