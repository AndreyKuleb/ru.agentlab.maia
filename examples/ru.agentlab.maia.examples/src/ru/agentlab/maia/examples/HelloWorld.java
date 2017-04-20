package ru.agentlab.maia.examples;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.agentlab.maia.agent.IGoalBase;
import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.annotation.AxiomType;
import ru.agentlab.maia.agent.annotation.InitialBelief;
import ru.agentlab.maia.agent.annotation.InitialGoal;
import ru.agentlab.maia.agent.annotation.OnBeliefAdded;
import ru.agentlab.maia.agent.annotation.OnGoalAdded;
import ru.agentlab.maia.agent.annotation.trigger.AddedBeliefDataPropertyAssertionAxiom;
import ru.agentlab.maia.agent.annotation.trigger.AddedGoalClassAssertionAxiom;
import ru.agentlab.maia.message.IMessageDeliveryService;
import ru.agentlab.maia.message.impl.AclMessage;

@InitialBelief(value = { ":this", ":havePosition", "_:pos" }, type = AxiomType.OBJECT_PROPERTY_ASSERTION)
@InitialBelief(value = { "_:pos", ":haveX", "2^^xsd:integer" }, type = AxiomType.DATA_PROPERTY_ASSERTION)
@InitialBelief(value = { "_:pos", ":haveY", "2^^xsd:integer" }, type = AxiomType.DATA_PROPERTY_ASSERTION)
@InitialGoal(value = { ":this", ":havePosition", "_:pos" }, type = AxiomType.OBJECT_PROPERTY_ASSERTION)
@InitialGoal(value = { "_:pos", ":haveX", "5^^xsd:integer" }, type = AxiomType.DATA_PROPERTY_ASSERTION)
@InitialGoal(value = { "_:pos", ":haveY", "5^^xsd:integer" }, type = AxiomType.DATA_PROPERTY_ASSERTION)
public class HelloWorld {

	@Inject
	IMessageDeliveryService messaging;

	@PostConstruct
	@AddedBeliefDataPropertyAssertionAxiom({ "?some", "hasLength", "value^^xsd:string" })
	public void setup(IGoalBase goalBase) {
	}

	@AddedGoalClassAssertionAxiom({ "init" })
	@AddedBeliefDataPropertyAssertionAxiom({ "?some", "hasLength", "2^^xsd:float" })
	public void onInit() {
		IMessage message = new AclMessage();
		message.setContent("Hello World");
		messaging.send(message);
	}

}
