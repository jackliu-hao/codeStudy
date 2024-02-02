package cn.hutool.cron.listener;

import cn.hutool.cron.TaskExecutor;
import cn.hutool.log.StaticLog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskListenerManager implements Serializable {
   private static final long serialVersionUID = 1L;
   private final List<TaskListener> listeners = new ArrayList();

   public TaskListenerManager addListener(TaskListener listener) {
      synchronized(this.listeners) {
         this.listeners.add(listener);
         return this;
      }
   }

   public TaskListenerManager removeListener(TaskListener listener) {
      synchronized(this.listeners) {
         this.listeners.remove(listener);
         return this;
      }
   }

   public void notifyTaskStart(TaskExecutor executor) {
      synchronized(this.listeners) {
         Iterator var4 = this.listeners.iterator();

         while(var4.hasNext()) {
            TaskListener taskListener = (TaskListener)var4.next();
            if (null != taskListener) {
               taskListener.onStart(executor);
            }
         }

      }
   }

   public void notifyTaskSucceeded(TaskExecutor executor) {
      synchronized(this.listeners) {
         Iterator var3 = this.listeners.iterator();

         while(var3.hasNext()) {
            TaskListener listener = (TaskListener)var3.next();
            listener.onSucceeded(executor);
         }

      }
   }

   public void notifyTaskFailed(TaskExecutor executor, Throwable exception) {
      synchronized(this.listeners) {
         int size = this.listeners.size();
         if (size > 0) {
            Iterator var5 = this.listeners.iterator();

            while(var5.hasNext()) {
               TaskListener listener = (TaskListener)var5.next();
               listener.onFailed(executor, exception);
            }
         } else {
            StaticLog.error(exception, exception.getMessage());
         }

      }
   }
}
