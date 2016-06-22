package com.ipacc.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
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