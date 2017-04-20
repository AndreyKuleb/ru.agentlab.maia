package ru.agentlab.maia.fipa;

import java.util.UUID;

public class AllowAllAgentFilter implements IAgentFilter {

	@Override
	public boolean match(UUID uuid) {
		return true;
	}

}
