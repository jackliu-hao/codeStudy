package ch.qos.logback.core.status;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public abstract class OnPrintStreamStatusListenerBase extends ContextAwareBase implements StatusListener, LifeCycle {
   boolean isStarted = false;
   static final long DEFAULT_RETROSPECTIVE = 300L;
   long retrospectiveThresold = 300L;
   String prefix;

   protected abstract PrintStream getPrintStream();

   private void print(Status status) {
      StringBuilder sb = new StringBuilder();
      if (this.prefix != null) {
         sb.append(this.prefix);
      }

      StatusPrinter.buildStr(sb, "", status);
      this.getPrintStream().print(sb);
   }

   public void addStatusEvent(Status status) {
      if (this.isStarted) {
         this.print(status);
      }
   }

   private void retrospectivePrint() {
      if (this.context != null) {
         long now = System.currentTimeMillis();
         StatusManager sm = this.context.getStatusManager();
         List<Status> statusList = sm.getCopyOfStatusList();
         Iterator var5 = statusList.iterator();

         while(var5.hasNext()) {
            Status status = (Status)var5.next();
            long timestampOfStatusMesage = status.getDate();
            if (this.isElapsedTimeLongerThanThreshold(now, timestampOfStatusMesage)) {
               this.print(status);
            }
         }

      }
   }

   private boolean isElapsedTimeLongerThanThreshold(long now, long timestamp) {
      long elapsedTime = now - timestamp;
      return elapsedTime < this.retrospectiveThresold;
   }

   public void start() {
      this.isStarted = true;
      if (this.retrospectiveThresold > 0L) {
         this.retrospectivePrint();
      }

   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public void setRetrospective(long retrospective) {
      this.retrospectiveThresold = retrospective;
   }

   public long getRetrospective() {
      return this.retrospectiveThresold;
   }

   public void stop() {
      this.isStarted = false;
   }

   public boolean isStarted() {
      return this.isStarted;
   }
}
