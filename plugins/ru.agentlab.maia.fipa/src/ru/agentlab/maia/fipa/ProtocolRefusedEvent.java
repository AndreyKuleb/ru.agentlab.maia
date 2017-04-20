package ru.agentlab.maia.fipa;

import ru.agentlab.maia.agent.Event;
import ru.agentlab.maia.agent.IRole;

public class ProtocolRefusedEvent extends Event<IRole> {

	public ProtocolRefusedEvent(IRole payload) {
		super(payload);
	}

}
