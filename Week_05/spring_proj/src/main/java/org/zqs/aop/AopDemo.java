package org.zqs.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class AopDemo {

    @Pointcut(value="execution(* org.zqs.aop.Kclass.*dong(..))")
    public void point() {

    }

    @Before(value = "point()")
    public void before() {
        System.out.println("====> begin kclass dong...");
    }

    @AfterReturning(value = "point()")
    public void after() {
        System.out.println("===> after kclass dong...");
    }

    @Around(value = "point()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("===> around begin kclass dong...");
        joinPoint.proceed();
        System.out.println("===> around after kclass dong...");
    }
}
