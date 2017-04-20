package ru.agentlab.maia.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import ru.agentlab.maia.filter.IPlanStateFilter;

public interface IPlanStateFilterConverter {

	IPlanStateFilter getMatcher(Object role, Method method, Annotation annotation, Map<String, Object> customData);

}