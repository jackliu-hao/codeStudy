package ch.qos.logback.core.status;

import ch.qos.logback.core.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusUtil {
   StatusManager sm;

   public StatusUtil(StatusManager sm) {
      this.sm = sm;
   }

   public StatusUtil(Context context) {
      this.sm = context.getStatusManager();
   }

   public static boolean contextHasStatusListener(Context context) {
      StatusManager sm = context.getStatusManager();
      if (sm == null) {
         return false;
      } else {
         List<StatusListener> listeners = sm.getCopyOfStatusListenerList();
         return listeners != null && listeners.size() != 0;
      }
   }

   public static List<Status> filterStatusListByTimeThreshold(List<Status> rawList, long threshold) {
      List<Status> filteredList = new ArrayList();
      Iterator var4 = rawList.iterator();

      while(var4.hasNext()) {
         Status s = (Status)var4.next();
         if (s.getDate() >= threshold) {
            filteredList.add(s);
         }
      }

      return filteredList;
   }

   public void addStatus(Status status) {
      if (this.sm != null) {
         this.sm.add(status);
      }

   }

   public void addInfo(Object caller, String msg) {
      this.addStatus(new InfoStatus(msg, caller));
   }

   public void addWarn(Object caller, String msg) {
      this.addStatus(new WarnStatus(msg, caller));
   }

   public void addError(Object caller, String msg, Throwable t) {
      this.addStatus(new ErrorStatus(msg, caller, t));
   }

   public boolean hasXMLParsingErrors(long threshold) {
      return this.containsMatch(threshold, 2, "XML_PARSING");
   }

   public boolean noXMLParsingErrorsOccurred(long threshold) {
      return !this.hasXMLParsingErrors(threshold);
   }

   public int getHighestLevel(long threshold) {
      List<Status> filteredList = filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
      int maxLevel = 0;
      Iterator var5 = filteredList.iterator();

      while(var5.hasNext()) {
         Status s = (Status)var5.next();
         if (s.getLevel() > maxLevel) {
            maxLevel = s.getLevel();
         }
      }

      return maxLevel;
   }

   public boolean isErrorFree(long threshold) {
      return 2 > this.getHighestLevel(threshold);
   }

   public boolean isWarningOrErrorFree(long threshold) {
      return 1 > this.getHighestLevel(threshold);
   }

   public boolean containsMatch(long threshold, int level, String regex) {
      List<Status> filteredList = filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
      Pattern p = Pattern.compile(regex);
      Iterator var7 = filteredList.iterator();

      while(var7.hasNext()) {
         Status status = (Status)var7.next();
         if (level == status.getLevel()) {
            String msg = status.getMessage();
            Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsMatch(int level, String regex) {
      return this.containsMatch(0L, level, regex);
   }

   public boolean containsMatch(String regex) {
      Pattern p = Pattern.compile(regex);
      Iterator var3 = this.sm.getCopyOfStatusList().iterator();

      Matcher matcher;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         Status status = (Status)var3.next();
         String msg = status.getMessage();
         matcher = p.matcher(msg);
      } while(!matcher.lookingAt());

      return true;
   }

   public int matchCount(String regex) {
      int count = 0;
      Pattern p = Pattern.compile(regex);
      Iterator var4 = this.sm.getCopyOfStatusList().iterator();

      while(var4.hasNext()) {
         Status status = (Status)var4.next();
         String msg = status.getMessage();
         Matcher matcher = p.matcher(msg);
         if (matcher.lookingAt()) {
            ++count;
         }
      }

      return count;
   }

   public boolean containsException(Class<?> exceptionType) {
      Iterator<Status> stati = this.sm.getCopyOfStatusList().iterator();

      while(stati.hasNext()) {
         Status status = (Status)stati.next();

         for(Throwable t = status.getThrowable(); t != null; t = t.getCause()) {
            if (t.getClass().getName().equals(exceptionType.getName())) {
               return true;
            }
         }
      }

      return false;
   }

   public long timeOfLastReset() {
      List<Status> statusList = this.sm.getCopyOfStatusList();
      if (statusList == null) {
         return -1L;
      } else {
         int len = statusList.size();

         for(int i = len - 1; i >= 0; --i) {
            Status s = (Status)statusList.get(i);
            if ("Will reset and reconfigure context ".equals(s.getMessage())) {
               return s.getDate();
            }
         }

         return -1L;
      }
   }
}
