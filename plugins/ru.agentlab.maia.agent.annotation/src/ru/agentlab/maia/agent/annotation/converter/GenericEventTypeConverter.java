package ru.agentlab.maia.agent.annotation.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import ru.agentlab.maia.agent.annotation.trigger.AddedExternalEvent;
import ru.agentlab.maia.converter.IPlanEventTypeConverter;

public class GenericEventTypeConverter implements IPlanEventTypeConverter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.agentlab.maia.agent.annotation.converter.IPlanEventTypeConverter#
	 * getEventType(java.lang.Object, java.lang.reflect.Method,
	 * java.lang.annotation.Annotation, java.util.Map)
	 */
	@Override
	public Class<?> getEventType(Object role, Method method, Annotation annotation, Map<String, Object> customData) {
		AddedExternalEvent onEvent = (AddedExternalEvent) annotation;
		return onEvent.value();
	}
}
