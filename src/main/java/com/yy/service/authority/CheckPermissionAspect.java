package com.yy.service.authority;


import com.yy.dao.repository.WxAccountRepository;
import com.yy.dao.entity.WxAccount;
import com.yy.other.domain.RespMessage;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class CheckPermissionAspect {

    private static final Logger LOGGER = Logger.getLogger(CheckPermissionAspect.class);

    @Autowired
    private WxAccountRepository wxAccountRepository;

    /**
     * 对cookie进行校验，验证用户是否已登录
     */
    @Around("@annotation(com.yy.service.authority.CheckPermission)")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            HttpServletRequest request = sra.getRequest();
            String openID = CookieManager.getOpenIDFromRequest(request);
            if (openID == null) {
                return new RespMessage(false, "请先调用login_wx接口进行登陆");
            }
            WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
            if (wxAccount == null){
                return new RespMessage(false, "用户不存在");
            }
        }
        Object result;
        try {
            //让代理方法执行
            result = pjp.proceed();
        } catch (Exception e) {
            return new RespMessage(false, "系统内部错误");
        }
        return result;
    }
}