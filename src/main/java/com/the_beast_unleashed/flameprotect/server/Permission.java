package com.the_beast_unleashed.flameprotect.server;

public class Permission {
	private String node;
	private boolean defaultForOps;
	
	public Permission(String node, boolean defaultForOps) {
		this.node = node;
		this.defaultForOps = defaultForOps;
	}
	
	public String getNode() {
		return this.node;
	}
	
	public boolean getDefaultForOps() {
		return this.defaultForOps;
	}
}
