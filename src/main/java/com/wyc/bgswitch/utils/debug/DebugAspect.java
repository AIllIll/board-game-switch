package com.wyc.bgswitch.utils.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jakarta.servlet.http.HttpServletRequest;


/**
 * @author wyc
 */
@Aspect
@Component
public class DebugAspect {

    private final Logger logger = LogManager.getLogger(DebugAspect.class);

    /**
     * @Description 获取注解中对方法的描述信息 用于Controller层注解
     */
    private static String getMethodDescription(JoinPoint joinPoint) throws ClassNotFoundException {
        StringBuilder description = new StringBuilder();
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();//目标方法名
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);

        // 获取方法上的注解
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    description.append("\n");
                    description.append(parameters[i].getName());
                    description.append(": ");
                    description.append(arguments[i]);
                    description.append(" <");
                    description.append(parameters[i].getType());
                    description.append(">");
                }
            }
        }
        return description.toString();
    }

    @Pointcut("@annotation(com.wyc.bgswitch.utils.debug.Debug)")
    public void methodToDebug() {
    }

    @Pointcut("@within(com.wyc.bgswitch.utils.debug.Debug)")
    public void classToDebug() {
    }

    @Before("methodToDebug() || classToDebug()") // 在切入点的方法run之前要干的
    public void doBefore(JoinPoint joinPoint) throws ClassNotFoundException {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes(); // 这个RequestContextHolder是Springmvc提供来获得请求的东西
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();


        // 记录request
        logger.info("****************************************************[HANDY_DEBUG]****************************************************");

        logger.info("---------------- MARKER: ");
        logger.info("---------------- REQUEST_INFO: ");
        logger.info("################ URL : " + request.getRequestURI());
        logger.info("################ HTTP_METHOD : " + request.getMethod());
        logger.info("################ IP : " + request.getRemoteAddr());
        logger.info("################ QUERY_STRING : " + request.getQueryString());

        // 记录方法
        logger.info("################ DETAIL: ");
        // logger.info("################SIGNITURE : " + joinPoint.getSignature().getDeclaringTypeName() + "#" + joinPoint.getSignature().getName());
        logger.info("################ SIGNATURE : " + joinPoint.getSignature().toString());
        logger.info("################ PARAMS : " + getMethodDescription(joinPoint));
        // logger.info("################TARGET: " + joinPoint.getTarget());//返回的是需要加强的目标类的对象
        // logger.info("################THIS: " + joinPoint.getThis());//返回的是经过加强后的代理类的对象

        logger.info("********************************************************[END]********************************************************");

    }
}
