package ru.agentlab.maia.fipa;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.semanticweb.owlapi.model.OWLAxiom;

import ru.agentlab.maia.agent.IAgent;
import ru.agentlab.maia.agent.IGoalBase;
import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.IRole;
import ru.agentlab.maia.agent.annotation.Optional;
import ru.agentlab.maia.container.IContainer;
import ru.agentlab.maia.message.IMessageDeliveryService;
import ru.agentlab.maia.message.impl.AclMessage;
import ru.agentlab.maia.time.TimerEvent;

public class AbstractInitiator {

	protected final UUID conversationId = UUID.randomUUID();

	@Inject
	protected IMessageDeliveryService messaging;

	@Inject
	protected IAgent agent;

	@Inject
	protected IGoalBase goalBase;

	@Inject
	protected Queue<Object> eventQueue;

	@Inject
	protected IRole role;

	@Inject
	protected ScheduledExecutorService scheduler;

	@Inject
	protected IContainer container;

	@Inject
	@Named("receivers")
	protected Set<UUID> targetAgents;

	@Inject
	@Optional
	@Named("delay")
	protected long delay = 1000;

	@Inject
	@Optional
	@Named("unit")
	protected TimeUnit unit = TimeUnit.MILLISECONDS;

	@Inject
	@Optional
	@Named("language")
	protected String language = null;

	@Inject
	@Optional
	@Named("ontology")
	protected String ontology = null;

	@Inject
	@Named("content")
	protected String template;

	private ScheduledFuture<?> future;

	protected void addGoal(Object event) {
		goalBase.add(event);
	}

	protected void addEvent(Object event) {
		eventQueue.offer(event);
	}

	protected void startTimer() {
		future = scheduler.schedule(() -> eventQueue.offer(new TimerEvent(conversationId)), delay, unit);
	}

	protected void stopTimer() {
		future.cancel(true);
	}

	protected IBeliefParser getBeliefParser(String language) {
		// if (language != null) {
		// return (IBeliefParser) container.get(language);
		// } else {
		// return container.get(IBeliefParser.class);
		// }
		return new IBeliefParser() {

			@Override
			public OWLAxiom parse(String content) {
				return null;
			}
		};
	}

	protected IGoalParser getGoalParser(String language) {
		if (language != null) {
			return (IGoalParser) container.get(language);
		} else {
			return container.get(IGoalParser.class);
		}
	}

	LocalDateTime getDeadline() {
		long millis = TimeUnit.MILLISECONDS.convert(delay, unit);
		return LocalDateTime.now().plus(millis, ChronoUnit.MILLIS);
	}

	private Stream<IMessage> createMessagesToTargetAgents(String protocol, String performative) {
		return targetAgents.stream().map(uuid -> {
			IMessage message = new AclMessage();
			message.setSender(agent.getUuid());
			message.setReceiver(uuid);
			message.setProtocol(protocol);
			message.setPerformative(performative);
			message.setConversationId(conversationId.toString());
			return message;
		});
	}

	private Stream<IMessage> createMessagesToTargetAgents(String protocol, String performative, String content) {
		return createMessagesToTargetAgents(protocol, performative).map(message -> {
			message.setContent(content);
			return message;
		});
	}

	protected void send(String protocol, String performative, String content) {
		createMessagesToTargetAgents(protocol, performative, content).forEach(messaging::send);
	}

	protected void send(String protocol, String performative) {
		createMessagesToTargetAgents(protocol, performative).forEach(messaging::send);
	}

	protected void reply(IMessage message, String performative) {
		messaging.reply(message, performative);
	}

	protected void reply(IMessage message, String performative, String content) {
		messaging.reply(message, performative, content);
	}
}