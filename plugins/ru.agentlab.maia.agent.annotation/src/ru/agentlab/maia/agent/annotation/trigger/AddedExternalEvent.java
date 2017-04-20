package ru.agentlab.maia.agent.annotation.trigger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.agentlab.maia.agent.annotation.converter.GenericEventFilterConverter;
import ru.agentlab.maia.agent.annotation.converter.GenericEventTypeConverter;
import ru.agentlab.maia.converter.PlanEventFilterConverter;
import ru.agentlab.maia.converter.PlanEventTypeConverter;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@PlanEventTypeConverter(GenericEventTypeConverter.class)
@PlanEventFilterConverter(GenericEventFilterConverter.class)
public @interface AddedExternalEvent {

	Class<?> value();

}
