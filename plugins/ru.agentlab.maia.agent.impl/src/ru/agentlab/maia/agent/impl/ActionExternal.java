package ru.agentlab.maia.agent.impl;

final class ActionExternal extends Action {

	private static final long serialVersionUID = 1L;

	Runnable runnabale;

	ActionExternal(Agent agent, Runnable runnabale) {
		super(agent);
		this.runnabale = runnabale;
	}

	@Override
	protected void compute() {
		runnabale.run();
	}

}
