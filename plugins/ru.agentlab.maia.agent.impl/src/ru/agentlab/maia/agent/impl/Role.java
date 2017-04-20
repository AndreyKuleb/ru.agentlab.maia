package ru.agentlab.maia.agent.impl;

import java.util.Map;

import ru.agentlab.maia.agent.IPlan;
import ru.agentlab.maia.agent.IRole;

public class Role implements IRole {

	protected boolean active = false;

	protected Map<String, Object> extra;

	protected IPlan<?>[] plans;

	protected Object roleObject;

//	public Role(Object roleObject, IPlan<?>[] plans, Map<String, Object> extra) {
//		super();
//		this.roleObject = roleObject;
//		this.plans = plans;
//		this.extra = extra;
//	}

	@Override
	public Map<String, Object> getExtra() {
		return extra;
	}

	@Override
	public IPlan<?>[] getPlans() {
		return plans;
	}

	@Override
	public Object getRoleObject() {
		return roleObject;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
}
