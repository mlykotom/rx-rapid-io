package com.futupilot.android.rx.rapid;

public class RxRapidException extends Exception {
	public final Type type;


	public enum Type {
		ERROR_NOT_FOUND("Expected one item, but none found");


		public final String message;


		Type(String message) {
			this.message = message;
		}
	}


	public RxRapidException(Type type) {
		super(type.message);
		this.type = type;
	}
}
