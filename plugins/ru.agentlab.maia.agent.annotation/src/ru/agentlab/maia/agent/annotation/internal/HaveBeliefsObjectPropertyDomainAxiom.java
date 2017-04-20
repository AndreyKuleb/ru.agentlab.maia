package ru.agentlab.maia.agent.annotation.internal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.agentlab.maia.agent.annotation.converter.OnBeliefXXXConverter;
import ru.agentlab.maia.agent.annotation.state.HaveBeliefObjectPropertyDomainAxiom;
import ru.agentlab.maia.converter.PlanEventFilterConverter;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@PlanEventFilterConverter(OnBeliefXXXConverter.class)
public @interface HaveBeliefsObjectPropertyDomainAxiom {

	HaveBeliefObjectPropertyDomainAxiom[] value();

}
