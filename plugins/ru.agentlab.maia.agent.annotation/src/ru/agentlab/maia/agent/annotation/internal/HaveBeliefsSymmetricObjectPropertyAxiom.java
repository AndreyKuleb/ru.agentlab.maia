package ru.agentlab.maia.agent.annotation.internal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.agentlab.maia.agent.annotation.state.HaveBeliefSymmetricObjectPropertyAxiom;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HaveBeliefsSymmetricObjectPropertyAxiom {

	HaveBeliefSymmetricObjectPropertyAxiom[] value();

}
