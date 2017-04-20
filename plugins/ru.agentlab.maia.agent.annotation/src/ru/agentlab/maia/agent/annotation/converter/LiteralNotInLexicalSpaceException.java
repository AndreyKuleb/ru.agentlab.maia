package ru.agentlab.maia.agent.annotation.converter;

public class LiteralNotInLexicalSpaceException extends LiteralFormatException {

	private static final long serialVersionUID = 1L;

	public LiteralNotInLexicalSpaceException() {
		super();
	}

	public LiteralNotInLexicalSpaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public LiteralNotInLexicalSpaceException(String message) {
		super(message);
	}

	public LiteralNotInLexicalSpaceException(Throwable cause) {
		super(cause);
	}

}
