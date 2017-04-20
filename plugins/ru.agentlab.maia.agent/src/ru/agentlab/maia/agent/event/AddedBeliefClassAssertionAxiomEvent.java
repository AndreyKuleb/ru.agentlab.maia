package ru.agentlab.maia.agent.event;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;

<<<<<<< HEAD
import ru.agentlab.maia.Event;
=======
import ru.agentlab.maia.agent.Event;
>>>>>>> refs/remotes/origin/feature/#32-beliefbase-centric

public class AddedBeliefClassAssertionAxiomEvent extends Event<OWLClassAssertionAxiom> {

	public AddedBeliefClassAssertionAxiomEvent(OWLClassAssertionAxiom payload) {
		super(payload);
	}

}
