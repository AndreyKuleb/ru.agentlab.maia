
package ru.agentlab.maia.message.impl;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

import ru.agentlab.maia.agent.IMessage;

public class AclMessage implements IMessage {

	private UUID sender;

	private UUID receiver;

	private UUID replyTo;

	private String content;

	private byte[] byteSequenceContent;

	private String replyWith;

	private String inReplyTo;

	private String encoding;

	private String language;

	private String ontology;

	private String performative;

	private LocalDateTime replyBy;

	private String protocol;

	private String conversationId;

	private Properties userDefinedProps;

	private LocalDateTime postTimeStamp;

	@Override
	public IMessage getReply() {
		IMessage reply = new AclMessage();
		reply.setConversationId(getConversationId());
		reply.setProtocol(getProtocol());
		if (getReplyTo() != null) {
			reply.setReceiver(getReplyTo());
		} else {
			reply.setReceiver(getSender());
		}
		reply.setSender(getReceiver());
		return reply;
	}

	@Override
	public IMessage getReply(String performative) {
		IMessage reply = getReply();
		reply.setPerformative(performative);
		return reply;
	}

	public UUID getSender() {
		return sender;
	}

	public void setSender(UUID sender) {
		this.sender = sender;
	}

	public UUID getReceiver() {
		return receiver;
	}

	public void setReceiver(UUID receiver) {
		this.receiver = receiver;
	}

	public UUID getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(UUID replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public byte[] getByteSequenceContent() {
		return byteSequenceContent;
	}

	public void setByteSequenceContent(byte[] byteSequenceContent) {
		this.byteSequenceContent = byteSequenceContent;
	}

	public String getReplyWith() {
		return replyWith;
	}

	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getPerformative() {
		return performative;
	}

	public void setPerformative(String performative) {
		this.performative = performative;
	}

	public LocalDateTime getReplyBy() {
		return replyBy;
	}

	public void setReplyBy(LocalDateTime replyBy) {
		this.replyBy = replyBy;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public Properties getUserDefinedProps() {
		return userDefinedProps;
	}

	public void setUserDefinedProps(Properties userDefinedProps) {
		this.userDefinedProps = userDefinedProps;
	}

	public LocalDateTime getPostTimeStamp() {
		return postTimeStamp;
	}

	public void setPostTimeStamp(LocalDateTime postTimeStamp) {
		this.postTimeStamp = postTimeStamp;
	}

	@Override
	public String toString() {
		return "[" + getProtocol() + "]::[" + getPerformative() + "]::[" + getContent() + "]";
	}

}
