package com.gupao.vip.myspringframework.v0.util;

public class GlobalLogger {

    private static boolean DEBUG_SWITCH = true;

    public static boolean isDebugEnabled(){
        return DEBUG_SWITCH;
    }

    public static void info(String msg){
        System.out.println("info:" + msg);
    }

}
