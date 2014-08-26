package com.the_beast_unleashed.flameprotect;

import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

public class FlameProtectLogger {	
	private static Logger logger;
	
	public static Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger("FlameProtect");
			logger.setParent(FMLLog.getLogger());
			logger.setUseParentHandlers(true);
		}
		
		return logger;
	}
}
