package org.jboss.threads.management;

import java.util.concurrent.ExecutorService;
import org.wildfly.common.annotation.NotNull;

public interface ManageableThreadPoolExecutorService extends ExecutorService {
  @NotNull
  StandardThreadPoolMXBean getThreadPoolMXBean();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\management\ManageableThreadPoolExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */