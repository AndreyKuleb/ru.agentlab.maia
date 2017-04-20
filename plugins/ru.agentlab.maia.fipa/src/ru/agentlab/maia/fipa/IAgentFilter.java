package ru.agentlab.maia.fipa;

import java.util.UUID;

public interface IAgentFilter {
	
	boolean match(UUID uuid);

}
