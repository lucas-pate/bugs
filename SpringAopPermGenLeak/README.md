# Spring AOP Perm Gen Leak

There is a perm gen memory leak with Spring 4.2.6, 4.3.0 and CGLIB 3.2.2 and 3.2.3. This project recreates that issue by using a prototype bean that is wrapped by AOP. The prototype bean calls System.gc() because the bug with CGLIB causes the references to the proxy cache to not survive garbage collection and get recreated after each GC.

The SpringAopPermgenApplicationTests junit test class does a warm up of the application, and then iterates creating prototype beans and calling System.gc. At the end of the run, it compares the permgen usage to after the warm up and fails if permgen has increased by more than 20 percent.

Reference: https://github.com/cglib/cglib/issues/80
