package ru.agentlab.maia.fipa;

import static ru.agentlab.maia.fipa.FIPAPerformativeNames.AGREE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.CANCEL;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.FAILURE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.INFORM;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.NOT_UNDERSTOOD;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.REFUSE;
import static ru.agentlab.maia.fipa.FIPAPerformativeNames.REQUEST;
import static ru.agentlab.maia.fipa.FIPAProtocolNames.FIPA_REQUEST;

import javax.annotation.PreDestroy;

import org.semanticweb.owlapi.model.OWLIndividualAxiom;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ru.agentlab.maia.agent.IGoal;
import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.message.annotation.OnMessageReceived;
import ru.agentlab.maia.message.impl.AclMessage;

public class FIPARequestResponder extends AbstractResponder {

	private final BiMap<IMessage, OWLIndividualAxiom> goals = HashBiMap.create();

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
		case REQUEST:
			String lang = message.getLanguage();
			IGoalParser parser = getGoalParser(lang);
			if (parser == null) {
				reply(message, NOT_UNDERSTOOD, "Unknown language [" + lang + "]");
				return;
			}
			try {
				OWLIndividualAxiom goal = parser.parse(message.getContent());
				goalBase.add(goal);
				goals.put(message, goal);
				reply(message, AGREE);
				return;
			} catch (Exception e) {
				reply(message, NOT_UNDERSTOOD, "Exception was thrown " + e.getClass() + " " + e.getMessage());
				return;
			}
		case NOT_UNDERSTOOD:
		case CANCEL:
			OWLIndividualAxiom goal = goals.get(message);
			if (goal != null) {
				goalBase.remove(goal);
			}
			return;
		}
	}

	@PreDestroy
	public void onDestroy() {
		goals.forEach((message, goal) -> {
			goalBase.remove(goal);
			reply(message, FAILURE, "Destroying role.. Bye..");
		});
	}

	public void onGoalSuccess(IGoal goal) {
		IMessage request = goals.inverse().get(goal);
		if (request != null) {
			reply(request, INFORM, "Goal success");
		}
	}

	public void onGoalFailed(IGoal goal) {
		IMessage request = goals.inverse().get(goal);
		if (request != null) {
			reply(request, FAILURE, "Goal failed");
		}
	}

	private boolean notMyMessage(AclMessage message) {
		return !message.checkProtocol(FIPA_REQUEST);
	}

}
