package ru.agentlab.maia.agent.annotation.state;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.agentlab.maia.agent.annotation.converter.OnBeliefXXXConverter;
import ru.agentlab.maia.agent.annotation.internal.HaveBeliefsSameIndividualAxiom;
import ru.agentlab.maia.converter.PlanEventFilterConverter;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(HaveBeliefsSameIndividualAxiom.class)
@PlanEventFilterConverter(OnBeliefXXXConverter.class)
public @interface HaveBeliefSameIndividualAxiom {

	String[] value() default { "?$1", "?$2", "?$3" };

}
