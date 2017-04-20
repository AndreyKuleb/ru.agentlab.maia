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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import ru.agentlab.maia.agent.IGoal;
import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.annotation.OnGoalFailed;
import ru.agentlab.maia.agent.annotation.OnGoalSuccess;
import ru.agentlab.maia.message.annotation.OnMessageReceived;
import ru.agentlab.maia.message.impl.AclMessage;

public class FIPAContractNetResponder extends AbstractResponder {

	private Map<String, IGoal> proposals = new HashMap<>();

	private Map<IGoal, IMessage> initials = new HashMap<>();

	IGoalEstimator estimator;

	@OnMessageReceived
	public void onMessage(AclMessage message) {
		if (notMyMessage(message)) {
			return;
		}
		if (!filter.match(message.getSender())) {
			reply(message, REFUSE);
			return;
		}
		switch (message.getPerformative()) {
		case CFP:
			String lang = message.getLanguage();
			IGoalParser parser = getGoalParser(lang);
			if (parser == null) {
				reply(message, NOT_UNDERSTOOD, "Unknown language [" + lang + "]");
				return;
			}
			try {
				IGoal goal = parser.parse(message.getContent());
				proposals.put(message.getConversationId(), goal);
				initials.put(goal, message);
				double price = estimator.estimate(goal);
				reply(message, PROPOSE, Double.toString(price));
			} catch (Exception e) {
				reply(message, NOT_UNDERSTOOD, "Exception was thrown " + e.getClass() + " " + e.getMessage());
			}
			return;
		case ACCEPT_PROPOSAL:
			IGoal goal = proposals.get(message.getConversationId());
			goalBase.add(goal);
			return;
		case REJECT_PROPOSAL:
		case CANCEL:
		case NOT_UNDERSTOOD:
			IGoal g = proposals.remove(message.getConversationId());
			if (g != null) {
				initials.remove(g);
			}
			break;
		}
	}

	@PreDestroy
	public void onDestroy() {
		initials.forEach((goal, message) -> {
			goalBase.remove(goal);
			reply(message, FAILURE, "Destroying role.. Bye..");
		});
	}

	@OnGoalSuccess
	public void onGoalSuccess(IGoal goal) {
		IMessage request = initials.get(goal);
		if (request != null) {
			reply(request, INFORM, "Goal success");
		}
	}

	@OnGoalFailed
	public void onGoalFailed(IGoal goal) {
		IMessage request = initials.get(goal);
		if (request != null) {
			reply(request, FAILURE, "Goal failed");
		}
	}

	private boolean notMyMessage(IMessage message) {
		return !message.checkConversationId(conversationId.toString()) || !message.checkProtocol(FIPA_CONTRACT_NET);
	}

}
