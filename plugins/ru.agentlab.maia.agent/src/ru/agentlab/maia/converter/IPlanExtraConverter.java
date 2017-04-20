package ru.agentlab.maia.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import ru.agentlab.maia.agent.IPlan;

public interface IPlanExtraConverter {

	List<IPlan<?>> getPlans(Object role, Method method, Annotation annotation, Map<String, Object> customData);

}