package ru.agentlab.maia.fipa;

import ru.agentlab.maia.agent.IGoal;

public interface IGoalEstimator {

	double estimate(IGoal goal);

}
