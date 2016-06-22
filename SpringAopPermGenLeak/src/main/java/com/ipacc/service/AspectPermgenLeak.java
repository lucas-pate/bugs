package com.ipacc.service;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 
 * @author PateL
 *
 */
@Aspect
@Component
public class AspectPermgenLeak {
    
    private long count = 0l;

    @Before("execution(* com.ipacc.service.PrototypeGarbageCollectionService.gc(..))")
    public void aroundService() {
        // just do something to prevent jvm optimization
        count++;
    }
    
    public long getCount() {
        return count;
    }
}
