package ru.agentlab.maia.agent;

public interface IAgentStateChangeListener {

	void changed(AgentState oldState, AgentState newState);

}
