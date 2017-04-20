package ru.agentlab.maia.examples;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

<<<<<<< HEAD
import ru.agentlab.maia.IPlanBase;
=======
import ru.agentlab.maia.agent.IPlan;
import ru.agentlab.maia.agent.IPlanBase;
import ru.agentlab.maia.agent.annotation.trigger.AddedBeliefClassAssertionAxiom;
import ru.agentlab.maia.agent.annotation.trigger.AddedGoalClassAssertionAxiom;
import ru.agentlab.maia.agent.annotation.trigger.FailedGoalClassAssertionAxiom;
import ru.agentlab.maia.agent.annotation.trigger.RemovedBeliefClassAssertionAxiom;
import ru.agentlab.maia.agent.annotation.trigger.RemovedGoalClassAssertionAxiom;
import ru.agentlab.maia.agent.event.BeliefAddedEvent;
import ru.agentlab.maia.agent.impl.Plan;
>>>>>>> refs/remotes/origin/feature/#32-beliefbase-centric

public class Example {

	@Inject
	IPlanBase planBase;

	@Inject
	String service;

	@PostConstruct
<<<<<<< HEAD
    //@AddedBeliefClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
    //@RemovedBeliefClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
    //@AddedGoalClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
    //@FailedGoalClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
    //@RemovedGoalClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
=======
	@AddedBeliefClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
	@RemovedBeliefClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
	@AddedGoalClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
	@FailedGoalClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
	@RemovedGoalClassAssertionAxiom({ "foaf:Teenager", "foaf:Tomas" })
>>>>>>> refs/remotes/origin/feature/#32-beliefbase-centric
	public void setup() {
        System.out.println("PLAN ADDED"); //$NON-NLS-1$
	}

}
