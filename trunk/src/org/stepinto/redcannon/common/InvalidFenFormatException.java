package org.stepinto.redcannon.common;

public class InvalidFenFormatException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidFenFormatException(String msg) {
		super(msg);
	}
}
