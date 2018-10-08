package com.tibco.customwizard.action;

public class ActionException extends RuntimeException {
	private static final long serialVersionUID = -319838042867474988L;

	public ActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionException(String message) {
        super(message);
    }
    
    public ActionException(Throwable cause) {
        super(cause);
    }
}
