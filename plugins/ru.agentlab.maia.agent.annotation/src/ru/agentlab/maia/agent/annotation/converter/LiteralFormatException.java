package ru.agentlab.maia.agent.annotation.converter;

public abstract class LiteralFormatException extends AnnotationFormatException {

	private static final long serialVersionUID = 1L;

	public LiteralFormatException() {
		super();
	}

	public LiteralFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public LiteralFormatException(String message) {
		super(message);
	}

	public LiteralFormatException(Throwable cause) {
		super(cause);
	}

}
