/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.the_beast_unleashed.flameprotect.server;

/**
 *
 * @author Anedaar
 */
public class Server {
    public static class SQL{
        public static boolean enabled;
        public static String host;
        public static String user;
        public static String pw;
        public static String database;
    }
    
    public static class Log{
        public static boolean console;
        public static byte blockBreak;
        public static byte leftBlock;
        public static byte rightBlock;
        public static byte rightAir;
        public static byte pickup;
    }
    public static String whatCmd;
    public static String whatCmd_default="what";
    
    public static String logCmd;
    public static String logCmd_default="log";

    public static String protCmd;
    public static String protCmd_default="prot";
    public static String noPickup;
    public static String noPickup_default="You are not allowed to pick up items here.";
    public static String noInteract;
    public static String noInteract_default="You are not allowed to interact here.";
    
    public static boolean enabled;

}
