package com.gupao.vip.myspringframework.v0.util;

import com.gupao.vip.myspringframework.v0.servlet.DispatcherServlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesResourceLoader {

    public static Properties loadClassPathProperties(String propertiesFileName) {
        Properties p = new Properties();
        String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String propertiesFilePath = classPath + propertiesFileName;

        System.out.println(propertiesFilePath);FileInputStream fis = null;
        try {
            fis = new FileInputStream(propertiesFilePath);
            p.load(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return p;
    }

    public static void main(String[] args) {
//        String contextConfigLocation = "context.properties";
////        contextConfigLocation = contextConfigLocation.replaceAll("\\.","\\/");
////        contextConfigLocation = contextConfigLocation.substring(1);
//        System.out.println(contextConfigLocation);
//        String str = (String) loadClassPathProperties(contextConfigLocation).get("scanBasePackage");
//        System.out.println(str);
        System.out.println(PropertiesResourceLoader.class.getClassLoader().getResource("").getPath() + "com.gupao.vip.webdemo".replaceAll("\\.","/"));

    }
}
