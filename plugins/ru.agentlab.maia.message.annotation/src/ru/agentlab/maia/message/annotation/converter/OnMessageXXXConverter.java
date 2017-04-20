package ru.agentlab.maia.message.annotation.converter;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static ru.agentlab.maia.filter.impl.PlanEventFilters.hamcrest;
import static ru.agentlab.maia.message.match.Matchers.hasContent;
import static ru.agentlab.maia.message.match.Matchers.hasConversationId;
import static ru.agentlab.maia.message.match.Matchers.hasEncoding;
import static ru.agentlab.maia.message.match.Matchers.hasInReplyTo;
import static ru.agentlab.maia.message.match.Matchers.hasLanguage;
import static ru.agentlab.maia.message.match.Matchers.hasOntology;
import static ru.agentlab.maia.message.match.Matchers.hasPerformative;
import static ru.agentlab.maia.message.match.Matchers.hasProtocol;
import static ru.agentlab.maia.message.match.Matchers.hasReplyWith;
import static ru.agentlab.maia.message.match.Matchers.hasSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hamcrest.Matcher;

import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.converter.IPlanEventFilterConverter;
import ru.agentlab.maia.converter.Util;
import ru.agentlab.maia.filter.IPlanEventFilter;

public class OnMessageXXXConverter implements IPlanEventFilterConverter {

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public IPlanEventFilter<?> getMatcher(Object role, Method method, Annotation annotation,
			Map<String, Object> customData) {
		Annotation ann = annotation;
		List<Matcher<? super IMessage>> matchers = new ArrayList<>();

		// performative
		String performative = Util.getMethodActualValue(ann, "performative", String.class);
		if (!performative.isEmpty()) {
			matchers.add(hasPerformative(equalTo(performative)));
		}

		// sender
		String sender = Util.getMethodActualValue(ann, "sender", String.class);
		if (!sender.isEmpty()) {
			matchers.add(hasSender(equalTo(UUID.fromString(sender))));
		}

		// receivers
		// TODO
		Matcher<? super List<UUID>> receiversMatcher;

		// replyTo
		// TODO
		Matcher<? super List<UUID>> replyToMatcher;

		// content
		String content = Util.getMethodActualValue(ann, "content", String.class);
		if (!content.isEmpty()) {
			matchers.add(hasContent(equalTo(performative)));
		}

		// replyWith
		String replyWith = Util.getMethodActualValue(ann, "replyWith", String.class);
		if (!replyWith.isEmpty()) {
			matchers.add(hasReplyWith(equalTo(performative)));
		}

		// inReplyTo
		String inReplyTo = Util.getMethodActualValue(ann, "inReplyTo", String.class);
		if (!inReplyTo.isEmpty()) {
			matchers.add(hasInReplyTo(equalTo(performative)));
		}

		// encoding
		String encoding = Util.getMethodActualValue(ann, "encoding", String.class);
		if (!encoding.isEmpty()) {
			matchers.add(hasEncoding(equalTo(performative)));
		}

		// language
		String language = Util.getMethodActualValue(ann, "language", String.class);
		if (!language.isEmpty()) {
			matchers.add(hasLanguage(equalTo(performative)));
		}

		// ontology
		String ontology = Util.getMethodActualValue(ann, "ontology", String.class);
		if (!ontology.isEmpty()) {
			matchers.add(hasOntology(equalTo(performative)));
		}

		// replyBy
		// TODO
		Matcher<? super LocalDateTime> replyByMatcher;

		// protocol
		String protocol = Util.getMethodActualValue(ann, "protocol", String.class);
		if (!protocol.isEmpty()) {
			matchers.add(hasProtocol(equalTo(performative)));
		}

		// conversationId
		String conversationId = Util.getMethodActualValue(ann, "conversationId", String.class);
		if (!conversationId.isEmpty()) {
			matchers.add(hasConversationId(equalTo(performative)));
		}

		// postTimeStamp
		// TODO
		Matcher<? super LocalDateTime> postTimeStampMatcher;

		return hamcrest(allOf(matchers.toArray(new Matcher[matchers.size()])));
	}

}
