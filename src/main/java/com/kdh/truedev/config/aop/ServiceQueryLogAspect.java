package com.kdh.truedev.config.aop;


import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceQueryLogAspect {

    @Around("execution(* com.kdh.truedev..service..*(..))")
    public Object logServiceMetrics(ProceedingJoinPoint pjp) throws Throwable {
        QueryCountHolder.clear();                  // 요청 단위 초기화
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            QueryCount qc = QueryCountHolder.getGrandTotal();
            log.info("[SERVICE] {} took {}ms queries total/select/ins/upd/del = {}/{}/{}/{}/{}",
                    pjp.getSignature().toShortString(), elapsedMs,
                    qc.getTotal(), qc.getSelect(), qc.getInsert(), qc.getUpdate(), qc.getDelete());
        }
    }
}
