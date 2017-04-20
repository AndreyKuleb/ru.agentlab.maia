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
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.REFUSE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.SUBSCRIBE;
import static ru.agentlab.maia.fipa.FIPAProtocolNames.FIPA_REQUEST;
import static ru.agentlab.maia.fipa.FIPAProtocolNames.FIPA_SUBSCRIBE;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.semanticweb.owlapi.model.OWLAxiom;

import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.annotation.trigger.AddedExternalEvent;
import ru.agentlab.maia.agent.event.RoleRemovedEvent;
import ru.agentlab.maia.message.annotation.OnMessageReceived;
import ru.agentlab.maia.message.impl.AclMessage;
import ru.agentlab.maia.time.TimerEvent;

/**
 * 
 * <pre>
 *                  Initiator                 Responder
 *                      |                         |
 *                 +----------+                   |
 *                 | onSetup  |-----SUBSCRIBE---->|
 *                 +----------+                   |
 *                      |                         |
 *                 +----------+                   |
 * [exception] <---| onNotUnd |<--NOT_UNDERSTOOD  |
 *                 +----------+                \  |
 *                      |                       \ |
 *                 +----------+                  \|
 * [exception] <---| onRefuse |<------REFUSE------|
 *                 +----------+                  /|
 *                      |                       / |
 *                 +----------+                /  |
 *                 | onAgree  |<-------AGREE--/   |
 *                 +----------+                   |
 *                      |                         |
 *                 +----------+                   |
 *    [result] <---| onInform |<------INFORM------|
 *       xN        +----------+         xN        |
 *                      |                         |
 *                      |                         |
 * </pre>
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00035/SC00035H.html">FIPA
 *      Subscribe Interaction Protocol Specification</a>
 * @see ru.agentlab.maia.fipa.FIPASubscribeResponder
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
public class FIPASubscribeInitiator extends AbstractInitiator {

	boolean waitForResponse = true;

	@PostConstruct
	public void onStart() {
		send(FIPA_SUBSCRIBE, SUBSCRIBE, template);
		startTimer();
	}

	@AddedExternalEvent(TimerEvent.class)
	public void onDeadline(TimerEvent event) {
		if (notMyEvent(event)) {
			return;
		}
		addEvent(new ProtocolDeadlineEvent());
		addGoal(new RoleRemovedEvent(role));
	}

	@OnMessageReceived
	public void onMessage(AclMessage message) {
		if (notMyMessage(message)) {
			return;
		}
		switch (message.getPerformative()) {
		case AGREE:
			stopTimer();
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
				return;
			} catch (Exception e) {
				reply(message, NOT_UNDERSTOOD, e.getMessage());
				abortProtocol(message);
				return;
			}
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
		send(FIPA_REQUEST, CANCEL);
	}

	private void successProtocol(Object result) {
		addEvent(new ProtocolSuccessEvent(role, result));
		addGoal(new RoleRemovedEvent(role));
		// state = State.FINISHED;
	}

	private void abortProtocol(IMessage message) {
		addEvent(new ProtocolAbortedEvent(role, message));
		addGoal(new RoleRemovedEvent(role));
		// state = State.FINISHED;
	}

	private boolean notMyEvent(TimerEvent event) {
		return event.getEventKey() != conversationId;
	}

	private boolean notMyMessage(AclMessage message) {
		return !message.checkConversationId(conversationId.toString()) || !message.checkProtocol(FIPA_SUBSCRIBE);
	}

}
