package ch.qos.logback.core;

import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.spi.LogbackLock;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BasicStatusManager implements StatusManager {
   public static final int MAX_HEADER_COUNT = 150;
   public static final int TAIL_SIZE = 150;
   int count = 0;
   protected final List<Status> statusList = new ArrayList();
   protected final CyclicBuffer<Status> tailBuffer = new CyclicBuffer(150);
   protected final LogbackLock statusListLock = new LogbackLock();
   int level = 0;
   protected final List<StatusListener> statusListenerList = new ArrayList();
   protected final LogbackLock statusListenerListLock = new LogbackLock();

   public void add(Status newStatus) {
      this.fireStatusAddEvent(newStatus);
      ++this.count;
      if (newStatus.getLevel() > this.level) {
         this.level = newStatus.getLevel();
      }

      synchronized(this.statusListLock) {
         if (this.statusList.size() < 150) {
            this.statusList.add(newStatus);
         } else {
            this.tailBuffer.add(newStatus);
         }

      }
   }

   public List<Status> getCopyOfStatusList() {
      synchronized(this.statusListLock) {
         List<Status> tList = new ArrayList(this.statusList);
         tList.addAll(this.tailBuffer.asList());
         return tList;
      }
   }

   private void fireStatusAddEvent(Status status) {
      synchronized(this.statusListenerListLock) {
         Iterator var3 = this.statusListenerList.iterator();

         while(var3.hasNext()) {
            StatusListener sl = (StatusListener)var3.next();
            sl.addStatusEvent(status);
         }

      }
   }

   public void clear() {
      synchronized(this.statusListLock) {
         this.count = 0;
         this.statusList.clear();
         this.tailBuffer.clear();
      }
   }

   public int getLevel() {
      return this.level;
   }

   public int getCount() {
      return this.count;
   }

   public boolean add(StatusListener listener) {
      synchronized(this.statusListenerListLock) {
         if (listener instanceof OnConsoleStatusListener) {
            boolean alreadyPresent = this.checkForPresence(this.statusListenerList, listener.getClass());
            if (alreadyPresent) {
               return false;
            }
         }

         this.statusListenerList.add(listener);
         return true;
      }
   }

   private boolean checkForPresence(List<StatusListener> statusListenerList, Class<?> aClass) {
      Iterator var3 = statusListenerList.iterator();

      StatusListener e;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         e = (StatusListener)var3.next();
      } while(e.getClass() != aClass);

      return true;
   }

   public void remove(StatusListener listener) {
      synchronized(this.statusListenerListLock) {
         this.statusListenerList.remove(listener);
      }
   }

   public List<StatusListener> getCopyOfStatusListenerList() {
      synchronized(this.statusListenerListLock) {
         return new ArrayList(this.statusListenerList);
      }
   }
}
