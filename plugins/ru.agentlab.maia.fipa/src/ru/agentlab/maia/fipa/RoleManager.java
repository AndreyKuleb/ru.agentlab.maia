package ru.agentlab.maia.fipa;

import javax.inject.Inject;

import ru.agentlab.maia.agent.IRole;
import ru.agentlab.maia.agent.IRoleBase;
import ru.agentlab.maia.agent.annotation.trigger.AddedExternalEvent;
import ru.agentlab.maia.agent.event.GoalAddedEvent;
import ru.agentlab.maia.agent.event.RoleRemovedEvent;

public class RoleManager {

	@Inject
	protected IRoleBase roleBase;

	@AddedExternalEvent(GoalAddedEvent.class)
	public void onRoleRemoveGoal(GoalAddedEvent event) {
		Object removeRole = event.getPayload();
		if (removeRole instanceof RoleRemovedEvent) {
			IRole role = ((RoleRemovedEvent) removeRole).getPayload();
			roleBase.remove(role);
		}
	}

}
