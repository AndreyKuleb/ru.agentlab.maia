/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

public interface IMessage {

	UUID getSender();

	void setSender(UUID sender);

	UUID getReceiver();

	void setReceiver(UUID receiver);

	UUID getReplyTo();

	void setReplyTo(UUID replyTo);

	String getContent();

	void setContent(String content);

	byte[] getByteSequenceContent();

	void setByteSequenceContent(byte[] byteSequenceContent);

	String getReplyWith();

	void setReplyWith(String reply_with);

	String getInReplyTo();

	void setInReplyTo(String inReplyTo);

	String getEncoding();

	void setEncoding(String encoding);

	String getLanguage();

	void setLanguage(String language);

	String getOntology();

	void setOntology(String ontology);

	LocalDateTime getReplyBy();

	void setReplyBy(LocalDateTime replyByInMillisec);

	String getProtocol();

	void setProtocol(String protocol);

	String getConversationId();

	void setConversationId(String conversationId);

	Properties getUserDefinedProps();

	void setUserDefinedProps(Properties userDefinedProps);

	LocalDateTime getPostTimeStamp();

	void setPostTimeStamp(LocalDateTime postTimeStamp);

	String getPerformative();

	void setPerformative(String performative);

	IMessage getReply();

	IMessage getReply(String performative);

	default boolean checkConversationId(String conversationId) {
		return getConversationId().equals(conversationId);
	}

	default boolean checkProtocol(String protocol) {
		return getProtocol().equalsIgnoreCase(protocol);
	}

	default boolean checkPerformative(String performative) {
		return getPerformative().equalsIgnoreCase(performative);
	}

}
