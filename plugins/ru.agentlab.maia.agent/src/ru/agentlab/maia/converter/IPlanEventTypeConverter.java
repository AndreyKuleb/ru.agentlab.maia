package ru.agentlab.maia.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public interface IPlanEventTypeConverter {

	Class<?> getEventType(Object role, Method method, Annotation annotation, Map<String, Object> customData);

}