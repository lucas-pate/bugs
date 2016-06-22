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
	    System.out.println("warmup:  " + permGenUsageAfterWarmup);
	    System.out.println("iterate: " + permGenUsageAfterIterate);
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