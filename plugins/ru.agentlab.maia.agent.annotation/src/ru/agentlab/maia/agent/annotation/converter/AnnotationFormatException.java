package ru.agentlab.maia.agent.annotation.converter;

public abstract class AnnotationFormatException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AnnotationFormatException() {
		super();
	}

	public AnnotationFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationFormatException(String message) {
		super(message);
	}

	public AnnotationFormatException(Throwable cause) {
		super(cause);
	}

}
