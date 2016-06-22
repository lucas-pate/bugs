package com.ipacc.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The bug in CGLIB occurs because their proxy cache references do not survive
 * a garbage collection, but the caches remain in memory. This class will cause
 * a gc for every call that is made to it. Another important part of this leak
 * seems to be the use of a prototype bean in conjunction with an AOP interceptor.
 * 
 * @author PateL
 *
 */
@Component
@Scope("prototype")
public class PrototypeGarbageCollectionService {
    
    public void gc() {
        System.gc();
    }
}