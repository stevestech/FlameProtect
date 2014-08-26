package com.the_beast_unleashed.flameprotect.server;

public class PermissionManagerException extends Exception {
	public PermissionManagerException() {
		super();
	}
	
	public PermissionManagerException(String message) {
		super(message);
	}
	
	public PermissionManagerException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PermissionManagerException(Throwable cause) {
		super(cause);
	}	
}