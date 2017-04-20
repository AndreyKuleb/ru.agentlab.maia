package ru.agentlab.maia.agent.annotation.converter;

public class LiteralNotInValueSpaceException extends LiteralFormatException {

	private static final long serialVersionUID = 1L;

	public LiteralNotInValueSpaceException() {
		super();
	}

	public LiteralNotInValueSpaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public LiteralNotInValueSpaceException(String message) {
		super(message);
	}

	public LiteralNotInValueSpaceException(Throwable cause) {
		super(cause);
	}

}
