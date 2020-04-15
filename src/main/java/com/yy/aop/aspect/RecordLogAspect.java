package com.yy.aop.aspect;

import com.yy.dao.LogRepository;
import com.yy.dao.entity.Log;
import com.yy.service.util.CookieService;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@Component
@Aspect
public class RecordLogAspect {

    private static final Logger LOGGER = Logger.getLogger(RecordLogAspect.class);

    @Autowired
    CookieService cookieService;

    @Autowired
    private LogRepository logRepository;

    /**
     * 添加了RecordLog注释的方法，在执行时，会记录日志
     * 日志格式：ID,操作人,方法名,开始时间,结束时间,是否成功,失败原因
     */
    @Around("@annotation(com.yy.aop.interfaces.RecordLog)")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Log log = new Log();
        String username = "unknown" , methodName, caused = null;
        Timestamp start = null, stop = null;
        boolean success = true;
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (sra != null){
            HttpServletRequest request = sra.getRequest();
            username = cookieService.getOpenIDFromRequest(request);
        }
        Object result = null;
        try {
            //获取开始时间
            start = new Timestamp(System.currentTimeMillis());
            //让代理方法执行
            result = pjp.proceed();
            // 设置结束时间
            stop = new Timestamp(System.currentTimeMillis());
        } catch (Exception e) {
            success = false;
            caused = e.toString();
            LOGGER.error(caused);
        } finally {
            //获取方法名
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            methodName = methodSignature.getMethod().getDeclaringClass().getName() + "." + methodSignature.getMethod().getName();
            log.setUser(username);
            log.setMethod(methodName);
            log.setStart(start);
            log.setEnd(stop);
            log.setSuccess(success);
            log.setCaused(caused);
            if (stop != null) {
                LOGGER.info(String.format("【%s】耗时：【%d】毫秒",methodName,(stop.getTime()-start.getTime())));
            }
            // 添加日志记录
            logRepository.save(log);
        }
        return result;
    }
}