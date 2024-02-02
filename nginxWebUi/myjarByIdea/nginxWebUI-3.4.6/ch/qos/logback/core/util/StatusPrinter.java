package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.ThrowableToStringArray;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public class StatusPrinter {
   private static PrintStream ps;
   static CachingDateFormatter cachingDateFormat;

   public static void setPrintStream(PrintStream printStream) {
      ps = printStream;
   }

   public static void printInCaseOfErrorsOrWarnings(Context context) {
      printInCaseOfErrorsOrWarnings(context, 0L);
   }

   public static void printInCaseOfErrorsOrWarnings(Context context, long threshold) {
      if (context == null) {
         throw new IllegalArgumentException("Context argument cannot be null");
      } else {
         StatusManager sm = context.getStatusManager();
         if (sm == null) {
            ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
         } else {
            StatusUtil statusUtil = new StatusUtil(context);
            if (statusUtil.getHighestLevel(threshold) >= 1) {
               print(sm, threshold);
            }
         }

      }
   }

   public static void printIfErrorsOccured(Context context) {
      if (context == null) {
         throw new IllegalArgumentException("Context argument cannot be null");
      } else {
         StatusManager sm = context.getStatusManager();
         if (sm == null) {
            ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
         } else {
            StatusUtil statusUtil = new StatusUtil(context);
            if (statusUtil.getHighestLevel(0L) == 2) {
               print(sm);
            }
         }

      }
   }

   public static void print(Context context) {
      print(context, 0L);
   }

   public static void print(Context context, long threshold) {
      if (context == null) {
         throw new IllegalArgumentException("Context argument cannot be null");
      } else {
         StatusManager sm = context.getStatusManager();
         if (sm == null) {
            ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
         } else {
            print(sm, threshold);
         }

      }
   }

   public static void print(StatusManager sm) {
      print(sm, 0L);
   }

   public static void print(StatusManager sm, long threshold) {
      StringBuilder sb = new StringBuilder();
      List<Status> filteredList = StatusUtil.filterStatusListByTimeThreshold(sm.getCopyOfStatusList(), threshold);
      buildStrFromStatusList(sb, filteredList);
      ps.println(sb.toString());
   }

   public static void print(List<Status> statusList) {
      StringBuilder sb = new StringBuilder();
      buildStrFromStatusList(sb, statusList);
      ps.println(sb.toString());
   }

   private static void buildStrFromStatusList(StringBuilder sb, List<Status> statusList) {
      if (statusList != null) {
         Iterator var2 = statusList.iterator();

         while(var2.hasNext()) {
            Status s = (Status)var2.next();
            buildStr(sb, "", s);
         }

      }
   }

   private static void appendThrowable(StringBuilder sb, Throwable t) {
      String[] stringRep = ThrowableToStringArray.convert(t);
      String[] var3 = stringRep;
      int var4 = stringRep.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String s = var3[var5];
         if (!s.startsWith("Caused by: ")) {
            if (Character.isDigit(s.charAt(0))) {
               sb.append("\t... ");
            } else {
               sb.append("\tat ");
            }
         }

         sb.append(s).append(CoreConstants.LINE_SEPARATOR);
      }

   }

   public static void buildStr(StringBuilder sb, String indentation, Status s) {
      String prefix;
      if (s.hasChildren()) {
         prefix = indentation + "+ ";
      } else {
         prefix = indentation + "|-";
      }

      if (cachingDateFormat != null) {
         String dateStr = cachingDateFormat.format(s.getDate());
         sb.append(dateStr).append(" ");
      }

      sb.append(prefix).append(s).append(CoreConstants.LINE_SEPARATOR);
      if (s.getThrowable() != null) {
         appendThrowable(sb, s.getThrowable());
      }

      if (s.hasChildren()) {
         Iterator<Status> ite = s.iterator();

         while(ite.hasNext()) {
            Status child = (Status)ite.next();
            buildStr(sb, indentation + "  ", child);
         }
      }

   }

   static {
      ps = System.out;
      cachingDateFormat = new CachingDateFormatter("HH:mm:ss,SSS");
   }
}
