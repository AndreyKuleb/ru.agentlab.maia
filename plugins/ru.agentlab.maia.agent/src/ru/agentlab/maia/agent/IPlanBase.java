/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent;

import java.util.Collection;
import java.util.stream.Stream;

public interface IPlanBase {

	void add(IPlan<?> plan);

	void remove(IPlan<?> plan);

	Collection<IPlan<?>> getPlans();

	Stream<IPlan<?>> getPlansStream();

	void addAll(Collection<IPlan<?>> plans);

	void addAll(IPlan<?>[] plans);

	void addAll(Stream<IPlan<?>> plans);

	void removeAll(Collection<IPlan<?>> plans);

	void removeAll(IPlan<?>[] plans);

	void removeAll(Stream<IPlan<?>> plans);

	Stream<Option> getOptions(IEvent<?> event);

}
