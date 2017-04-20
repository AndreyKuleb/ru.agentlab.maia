package ru.agentlab.maia.agent.annotation.internal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.agentlab.maia.agent.annotation.state.HaveBeliefDisjointObjectPropertiesAxiom;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HaveBeliefsDisjointObjectPropertiesAxiom {

	HaveBeliefDisjointObjectPropertiesAxiom[] value();

}
