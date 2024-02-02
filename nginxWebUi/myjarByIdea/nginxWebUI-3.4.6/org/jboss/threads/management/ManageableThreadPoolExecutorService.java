package org.jboss.threads.management;

import java.util.concurrent.ExecutorService;
import org.wildfly.common.annotation.NotNull;

public interface ManageableThreadPoolExecutorService extends ExecutorService {
   @NotNull
   StandardThreadPoolMXBean getThreadPoolMXBean();
}
