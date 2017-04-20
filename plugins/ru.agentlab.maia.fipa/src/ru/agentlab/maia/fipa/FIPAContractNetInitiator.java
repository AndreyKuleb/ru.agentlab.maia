/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.fipa;

import static ru.agentlab.maia.fipa.FIPAPerformativeNames.ACCEPT_PROPOSAL;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.CANCEL;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.CFP;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.FAILURE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.INFORM;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.NOT_UNDERSTOOD;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.PROPOSE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.REFUSE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.REJECT_PROPOSAL;
import static ru.agentlab.maia.fipa.FIPAProtocolNames.FIPA_CONTRACT_NET;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.semanticweb.owlapi.model.OWLAxiom;

import ru.agentlab.maia.agent.IAgent;
import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.annotation.trigger.AddedExternalEvent;
import ru.agentlab.maia.agent.event.RoleRemovedEvent;
import ru.agentlab.maia.message.annotation.OnMessageReceived;
import ru.agentlab.maia.message.impl.AclMessage;
import ru.agentlab.maia.time.TimerEvent;

public class FIPAContractNetInitiator extends AbstractInitiator {

	// TODO: Check select minimum, not maximum
	protected Comparator<IMessage> proposalsComparator = (message1, message2) -> {
		double price1 = Double.valueOf(message1.getContent().trim());
		double price2 = Double.valueOf(message2.getContent().trim());
		return Double.compare(price1, price2);
	};

	private Collection<IMessage> proposals = new TreeSet<>(proposalsComparator);

	private State state = null;

	private int answers = 0;

	@PostConstruct
	public void onStart() {
		send(FIPA_CONTRACT_NET, CFP, template);
		startTimer();
		state = State.WAIT_FOR_PROPOSALS;
	}

	@AddedExternalEvent(TimerEvent.class)
	public void onDeadline(TimerEvent event) {
		if (notMyEvent(event)) {
			return;
		}
		if (state == State.WAIT_FOR_PROPOSALS) {
			estimateProposals();
		}
	}

	@OnMessageReceived
	public void onMessage(AclMessage message) {
		if (notMyMessage(message)) {
			return;
		}
		switch (message.getPerformative()) {
		case PROPOSE:
			proposals.add(message);
		case NOT_UNDERSTOOD:
		case REFUSE:
			answers++;
			if (targetAgents.size() == answers) {
				stopTimer();
				estimateProposals();
			}
			break;
		case INFORM:
			String lang = message.getLanguage();
			IBeliefParser parser = getBeliefParser(lang);
			if (parser == null) {
				reply(message, NOT_UNDERSTOOD, "Unknown language [" + lang + "]");
				abortProtocol(message);
			} else {
				try {
					OWLAxiom axiom = parser.parse(message.getContent());
					successProtocol(axiom);
				} catch (Exception e) {
					reply(message, NOT_UNDERSTOOD, e.getMessage());
					abortProtocol(message);
				}
			}
			break;
		case FAILURE:
			abortProtocol(message);
			break;
		}
	}

	@PreDestroy
	public void onDestroy(IAgent agent) {
		stopTimer();
		if (state == State.WAIT_FOR_PROPOSALS) {
			send(FIPA_CONTRACT_NET, CANCEL);
		}
	}

	protected void estimateProposals() {
		Iterator<IMessage> it = proposals.iterator();
		if (it.hasNext()) {
			// Accept best proposal
			reply(it.next(), ACCEPT_PROPOSAL);
			while (it.hasNext()) {
				// Reject all others
				reply(it.next(), REJECT_PROPOSAL);
			}
			state = State.WAIT_FOR_RESULT;
		} else {
			// Everybody refused or not understood
			abortProtocol(null);
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

	private boolean notMyMessage(IMessage message) {
		return !message.checkConversationId(conversationId.toString()) || !message.checkProtocol(FIPA_CONTRACT_NET);
	}

	static enum State {

		WAIT_FOR_PROPOSALS,

		WAIT_FOR_RESULT,

		FINISHED

	}

}
