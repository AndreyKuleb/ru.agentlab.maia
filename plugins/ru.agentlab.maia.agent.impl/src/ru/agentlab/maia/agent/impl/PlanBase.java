/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ru.agentlab.maia.agent.IEvent;
import ru.agentlab.maia.agent.IPlan;
import ru.agentlab.maia.agent.IPlanBase;
import ru.agentlab.maia.agent.Option;

public class PlanBase implements IPlanBase {

	protected final Multimap<Class<?>, IPlan<?>> plans = ArrayListMultimap.create();

	@Override
	public void add(IPlan<?> plan) {
		checkNotNull(plan);
		plans.put(plan.getType(), plan);
	}

	@Override
	public void addAll(Collection<IPlan<?>> plans) {
		checkNotNull(plans);
		plans.forEach(plan -> this.plans.put(plan.getType(), plan));
	}

	@Override
	public void addAll(IPlan<?>[] plans) {
		checkNotNull(plans);
		for (int i = 0; i < plans.length; i++) {
			IPlan<?> plan = plans[i];
			this.plans.put(plan.getType(), plan);
		}
	}

	@Override
	public void addAll(Stream<IPlan<?>> plans) {
		checkNotNull(plans);
		plans.forEach(plan -> this.plans.put(plan.getType(), plan));
	}

	@Override
	public void remove(IPlan<?> plan) {
		checkNotNull(plan);
		plans.remove(plan.getType(), plan);
	}

	@Override
	public void removeAll(Collection<IPlan<?>> plans) {
		checkNotNull(plans);
		plans.forEach(plan -> this.plans.remove(plan.getType(), plan));
	}

	@Override
	public void removeAll(IPlan<?>[] plans) {
		checkNotNull(plans);
		for (int i = 0; i < plans.length; i++) {
			IPlan<?> plan = plans[i];
			this.plans.remove(plan.getType(), plan);
		}
	}

	@Override
	public void removeAll(Stream<IPlan<?>> plans) {
		checkNotNull(plans);
		plans.forEach(plan -> this.plans.remove(plan.getType(), plan));
	}

	@Override
	public Stream<Option> getOptions(IEvent<?> event) {
		assert event != null;
		Class<?> eventType = event.getClass();
		Collection<IPlan<?>> collection = plans.get(eventType);
		List<Option> result = new ArrayList<>();
		for (IPlan<?> plan : collection) {
			Map<String, Object> variables = new HashMap<>();
			variables.put(eventType.getName(), event);
			if (plan.unify(event.getPayload(), variables)) {
				result.add(new Option(plan, variables));
			}
		}
		return result.stream();
	}

	@Override
	public Stream<IPlan<?>> getPlansStream() {
		return plans.values().stream();
	}

	@Override
	public Collection<IPlan<?>> getPlans() {
		return plans.values();
	}

}
