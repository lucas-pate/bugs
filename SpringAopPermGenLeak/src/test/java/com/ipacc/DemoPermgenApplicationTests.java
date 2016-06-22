package com.ipacc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipacc.DemoPermgenLeakApplication;
import com.ipacc.service.PrototypeGarbageCollectionService;

/**
 * This test recreates a perm gen leak caused by Spring 4.2.6+ and CGLIB 3.2.2 and 3.2.3.
 * 
 * The leak is manifested by using a prototype scope service which has an AOP interceptor
 * on it's public method. I think the bug with CGLIB causes the reference to the proxy cache 
 * to be lost on garbage collection, but the cache remains in memory. A new cache is created
 * for each call which fills up perm gen.
 * 
 * @author PateL
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoPermgenLeakApplication.class)
public class DemoPermgenApplicationTests {
    
    @Autowired
    private ObjectFactory<PrototypeGarbageCollectionService> prototypeGarbageCollectionServiceFactory;

	@Test
	public void verify_permGen_does_not_grow_more_than_20_percent_after_warmup() {
	    iterate(100);
	    long permGenUsageAfterWarmup = getPermGenUsage();
	    iterate(200);
	    long permGenUsageAfterIterate = getPermGenUsage();
	    assertThat(permGenUsageAfterIterate).isCloseTo(permGenUsageAfterWarmup, withPercentage(20));
	}

    private String iterate(int n) {
        String response = null;
        for (int i = 0; i < n; i++) {
            prototypeGarbageCollectionServiceFactory.getObject().gc();
        }
        return response;
    }
    
    private long getPermGenUsage() {
        long permGenUsage = 0l;
        Iterator<MemoryPoolMXBean> iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
        while (iter.hasNext()) {
            MemoryPoolMXBean item = iter.next();
            String name = item.getName();
            if (name.equals("PS Perm Gen")) {
                permGenUsage = item.getUsage().getUsed();
                break;
            }
        }
        return permGenUsage;
    }
}