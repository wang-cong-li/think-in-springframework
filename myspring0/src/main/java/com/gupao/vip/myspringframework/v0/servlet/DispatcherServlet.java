package com.gupao.vip.myspringframework.v0.servlet;


import com.gupao.vip.myspringframework.v0.annotation.*;
import com.gupao.vip.myspringframework.v0.util.PropertiesResourceLoader;
import com.gupao.vip.myspringframework.v0.util.StringUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.util.*;

public class DispatcherServlet extends HttpServlet {

    private List<Class<?>> classList = new ArrayList<Class<?>>();

    private Map<String,Object> ioc = new HashMap<>();

    private List<String> singletoneCurrentlyInCreation = new ArrayList<>();

    private Map<String,HandlerMapping> handlerMappingMap = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contextPath = req.getContextPath();
        String requestUri = req.getRequestURI();
        String uri = requestUri.replaceAll(contextPath,"");
        System.out.println("uri:" + requestUri + "\tcontextPath:" + contextPath + "\turi" + uri);
        HandlerMapping handler = handlerMappingMap.get(uri);
        if (null == handler) {
            PrintWriter writer = resp.getWriter();
            writer.write("404");
            writer.flush();
            return;
        }
        Method m = handler.getMethod();
//        Class<?>[] paramClassArr = m.getParameterTypes();
        Parameter[] methodParameters = m.getParameters();
        Object[] parameterValues = new Object[methodParameters.length];

        for (int i = 0; i < methodParameters.length; i++) {
            Parameter p = methodParameters[i];
            Class clazz = methodParameters[i].getType();
            if (HttpServletRequest.class.equals(clazz)){
                parameterValues[i] = req;
                continue;
            } else if (HttpServletResponse.class.equals(clazz)) {
                parameterValues[i] = resp;
                continue;
            } else {
                String paramName = "";
                if (clazz.isAnnotationPresent(RequestParam.class)) {
                    paramName = p.getAnnotation(RequestParam.class).value();
                }
                parameterValues[i] = req.getParameter(paramName);
            }
            handler.setInvokeParams(parameterValues);
            Object result = null;
            try {
                result = handler.handleRequest();
            } catch (InvocationTargetException e) {
                result = e.getMessage();
            } catch (IllegalAccessException e) {
                result = e.getMessage();
            }
            PrintWriter writer = resp.getWriter();
            writer.write(result.toString());
            writer.flush();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            System.out.println("init!!!");
            String contextConfigLocation = config.getInitParameter("contextConfigLocation");
            // 加载配置文件和加载资源
            loadResource(contextConfigLocation);
            initBeans();
            dependencyInjection();
            initRequestHandlers();
//        super.init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRequestHandlers() {
        if (this.ioc.isEmpty()) {return;}
        Set<Object> objectSet = new HashSet<Object>(ioc.values());
        for (Object object:objectSet) {
            Class<?> clazz = object.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)){continue;}
            String url = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                url = url.concat(clazz.getAnnotation(RequestMapping.class).value());
            }
            Method[] methods = clazz.getMethods();
            for (Method method :methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)){continue;}
                url = url.concat(method.getAnnotation(RequestMapping.class).value());
                url = url.replaceAll("//+","/");
                HandlerMapping handler = new HandlerMapping();
                handler.setInstance(object);
                handler.setMethod(method);
                handlerMappingMap.put(url,handler);
            }
        }
        System.out.println("handlerMappingMap=" + handlerMappingMap);
    }

    private void dependencyInjection() throws IllegalAccessException {
        if (this.ioc.isEmpty()) {return;}
        Set<Object> objectSet = new HashSet<Object>(ioc.values());
        for (Object object:objectSet) {
            Class<?> clazz = object.getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field f:fields) {
                    if (!f.isAnnotationPresent(Autowired.class)) {
                        continue;
                    }
                    Class<?> fieldClass = f.getType();
                    String diName = null;
                    String customDIName = f.getAnnotation(Autowired.class).value();
                    if (StringUtil.isBlank(customDIName)) {
                        if (fieldClass.isInterface()) {
                            diName = fieldClass.getName();
                        } else {
                            diName = getBeanNameByClassName(fieldClass.getSimpleName());
                        }
                    } else {
                        diName = customDIName;
                    }
                    f.setAccessible(true);
                    Object instance = ioc.get(diName);
                    f.set(object,instance);
                }
            }
        }
        System.out.println("依赖注入;" + ioc);
    }

    private void initBeans() throws Exception {
        if (this.classList.isEmpty()) {return;}
        for (Class<?> clazz: classList) {
            String beanName = null;
            Object instance = null;
            if (clazz.isAnnotationPresent(Controller.class)){
                String customeBeanName = clazz.getAnnotation(Controller.class).value();
                if (StringUtil.isBlank(customeBeanName)) {
                    beanName = getBeanNameByClassName(clazz.getSimpleName());
                } else {
                    beanName = customeBeanName;
                }
                if (singletoneCurrentlyInCreation.contains(beanName)) {
                    throw new Exception("循环依赖:" + beanName);
                }
//                Constructor constructor = clazz.getConstructor(new Class<?>[]{});
                instance = clazz.newInstance();
            } else if (clazz.isAnnotationPresent(Service.class)) {
                String customeBeanName = clazz.getAnnotation(Service.class).value();
                if (StringUtil.isBlank(customeBeanName)) {
                    beanName = getBeanNameByClassName(clazz.getSimpleName());
                } else {
                    beanName = customeBeanName;
                }
                if (singletoneCurrentlyInCreation.contains(beanName)) {
                    throw new Exception("循环依赖:" + beanName);
                }
//                Constructor constructor = clazz.getConstructor(new Class<?>[]{});
                instance = clazz.newInstance();
                ioc.put(clazz.getInterfaces()[0].getName(),instance);
            } else {
                continue;
            }
            singletoneCurrentlyInCreation.add(beanName);
            ioc.put(beanName,instance);
        }
        System.out.println("initBean:" + ioc);
    }

    private String getBeanNameByClassName(String simpleName) {
        char[] simpleNameArr_upper_case = simpleName.toUpperCase().toCharArray();
        simpleNameArr_upper_case[0] = (char)(simpleNameArr_upper_case[0] + 32);
        return String.valueOf(simpleNameArr_upper_case);
    }

    private void loadResource(String contextConfigLocation) {
//        System.out.println("加载资源：配置文件名：" + contextConfigLocation);
        String basePackageName = null;
        if (null == contextConfigLocation) {
            System.out.println("未配置contextConfigLocation！");
            return;
        }
        if (contextConfigLocation.startsWith("classpath:")) {
            contextConfigLocation = contextConfigLocation.replaceAll("classpath:","");
            basePackageName = (String) PropertiesResourceLoader.loadClassPathProperties(contextConfigLocation).get("scanBasePackage");
        } else {
            if (contextConfigLocation.startsWith("/")) {
                contextConfigLocation = contextConfigLocation.substring(1);
            }
            basePackageName = (String) PropertiesResourceLoader.loadClassPathProperties(contextConfigLocation).get("scanBasePackage");
        }

        loadClassFromBasePackage(basePackageName);
    }

    private void loadClassFromBasePackage(String basePackageName) {
//        System.out.println("加载资源，包名：" + basePackageName);
        String basePackagePath = getClass().getClassLoader().getResource("").getPath() + basePackageName.replaceAll("\\.","/");
//        System.out.println("加载资源，包名处理后的路径：" + basePackagePath);
        File f = new File(basePackagePath);
        if (f.isDirectory()) {
            File[] subFiles = f.listFiles();
//            System.out.println("是文件夹，递归调用,子文件夹;" + Arrays.toString(subFiles));
            for (File subFile: subFiles) {
//                System.out.println("子文件夹;" + subFiles);
                if (subFile.isDirectory()) {
                    loadClassFromBasePackage(basePackageName + "." + subFile.getName());
                } else {
                    loadClass(subFile.getName(),basePackageName);
                }
            }

        } else if (f.isFile()) {
//            System.out.println("是文件，加载class");
            String fileName = f.getName();
            loadClass(fileName,basePackageName);
        }
    }

    private void loadClass(String fileName, String basePackageName) {
//        System.out.println("加载class：文件名：" + fileName + "\t基础包名：" + basePackageName);
        if (fileName.endsWith(".class")) {
            fileName = fileName.replace(".class","");
        }
        String className = basePackageName + "." + fileName;
//        System.out.println("加载class，classpath：" + className);
        try {
            Class<?> clazz = Class.forName(className);
            this.classList.add(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
