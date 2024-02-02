package org.codehaus.plexus.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ExceptionUtils {
   static final String WRAPPED_MARKER = " [wrapped] ";
   protected static String[] CAUSE_METHOD_NAMES = new String[]{"getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested"};
   // $FF: synthetic field
   static Class class$java$lang$Throwable;

   protected ExceptionUtils() {
   }

   public static void addCauseMethodName(String methodName) {
      if (methodName != null && methodName.length() > 0) {
         List list = new ArrayList(Arrays.asList(CAUSE_METHOD_NAMES));
         list.add(methodName);
         CAUSE_METHOD_NAMES = (String[])list.toArray(new String[list.size()]);
      }

   }

   public static Throwable getCause(Throwable throwable) {
      return getCause(throwable, CAUSE_METHOD_NAMES);
   }

   public static Throwable getCause(Throwable throwable, String[] methodNames) {
      Throwable cause = getCauseUsingWellKnownTypes(throwable);
      if (cause == null) {
         for(int i = 0; i < methodNames.length; ++i) {
            cause = getCauseUsingMethodName(throwable, methodNames[i]);
            if (cause != null) {
               break;
            }
         }

         if (cause == null) {
            cause = getCauseUsingFieldName(throwable, "detail");
         }
      }

      return cause;
   }

   public static Throwable getRootCause(Throwable throwable) {
      Throwable cause = getCause(throwable);
      if (cause != null) {
         for(throwable = cause; (throwable = getCause(throwable)) != null; cause = throwable) {
         }
      }

      return cause;
   }

   private static Throwable getCauseUsingWellKnownTypes(Throwable throwable) {
      if (throwable instanceof SQLException) {
         return ((SQLException)throwable).getNextException();
      } else {
         return throwable instanceof InvocationTargetException ? ((InvocationTargetException)throwable).getTargetException() : null;
      }
   }

   private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
      Method method = null;

      try {
         method = throwable.getClass().getMethod(methodName, (Class[])null);
      } catch (NoSuchMethodException var7) {
      } catch (SecurityException var8) {
      }

      if (method != null && (class$java$lang$Throwable == null ? (class$java$lang$Throwable = class$("java.lang.Throwable")) : class$java$lang$Throwable).isAssignableFrom(method.getReturnType())) {
         try {
            return (Throwable)method.invoke(throwable);
         } catch (IllegalAccessException var4) {
         } catch (IllegalArgumentException var5) {
         } catch (InvocationTargetException var6) {
         }
      }

      return null;
   }

   private static Throwable getCauseUsingFieldName(Throwable throwable, String fieldName) {
      Field field = null;

      try {
         field = throwable.getClass().getField(fieldName);
      } catch (NoSuchFieldException var6) {
      } catch (SecurityException var7) {
      }

      if (field != null && (class$java$lang$Throwable == null ? (class$java$lang$Throwable = class$("java.lang.Throwable")) : class$java$lang$Throwable).isAssignableFrom(field.getType())) {
         try {
            return (Throwable)field.get(throwable);
         } catch (IllegalAccessException var4) {
         } catch (IllegalArgumentException var5) {
         }
      }

      return null;
   }

   public static int getThrowableCount(Throwable throwable) {
      int count;
      for(count = 0; throwable != null; throwable = getCause(throwable)) {
         ++count;
      }

      return count;
   }

   public static Throwable[] getThrowables(Throwable throwable) {
      ArrayList list;
      for(list = new ArrayList(); throwable != null; throwable = getCause(throwable)) {
         list.add(throwable);
      }

      return (Throwable[])list.toArray(new Throwable[list.size()]);
   }

   public static int indexOfThrowable(Throwable throwable, Class type) {
      return indexOfThrowable(throwable, type, 0);
   }

   public static int indexOfThrowable(Throwable throwable, Class type, int fromIndex) {
      if (fromIndex < 0) {
         throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex);
      } else {
         Throwable[] throwables = getThrowables(throwable);
         if (fromIndex >= throwables.length) {
            throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex);
         } else {
            for(int i = fromIndex; i < throwables.length; ++i) {
               if (throwables[i].getClass().equals(type)) {
                  return i;
               }
            }

            return -1;
         }
      }
   }

   public static void printRootCauseStackTrace(Throwable t, PrintStream stream) {
      String[] trace = getRootCauseStackTrace(t);

      for(int i = 0; i < trace.length; ++i) {
         stream.println(trace[i]);
      }

      stream.flush();
   }

   public static void printRootCauseStackTrace(Throwable t) {
      printRootCauseStackTrace(t, System.err);
   }

   public static void printRootCauseStackTrace(Throwable t, PrintWriter writer) {
      String[] trace = getRootCauseStackTrace(t);

      for(int i = 0; i < trace.length; ++i) {
         writer.println(trace[i]);
      }

      writer.flush();
   }

   public static String[] getRootCauseStackTrace(Throwable t) {
      Throwable[] throwables = getThrowables(t);
      int count = throwables.length;
      ArrayList frames = new ArrayList();
      List nextTrace = getStackFrameList(throwables[count - 1]);
      int i = count;

      while(true) {
         --i;
         if (i < 0) {
            return (String[])frames.toArray(new String[0]);
         }

         List trace = nextTrace;
         if (i != 0) {
            nextTrace = getStackFrameList(throwables[i - 1]);
            removeCommonFrames(trace, nextTrace);
         }

         if (i == count - 1) {
            frames.add(throwables[i].toString());
         } else {
            frames.add(" [wrapped] " + throwables[i].toString());
         }

         for(int j = 0; j < trace.size(); ++j) {
            frames.add(trace.get(j));
         }
      }
   }

   private static void removeCommonFrames(List causeFrames, List wrapperFrames) {
      int causeFrameIndex = causeFrames.size() - 1;

      for(int wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; --wrapperFrameIndex) {
         String causeFrame = (String)causeFrames.get(causeFrameIndex);
         String wrapperFrame = (String)wrapperFrames.get(wrapperFrameIndex);
         if (causeFrame.equals(wrapperFrame)) {
            causeFrames.remove(causeFrameIndex);
         }

         --causeFrameIndex;
      }

   }

   public static String getStackTrace(Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      t.printStackTrace(pw);
      return sw.getBuffer().toString();
   }

   public static String getFullStackTrace(Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      Throwable[] ts = getThrowables(t);

      for(int i = 0; i < ts.length; ++i) {
         ts[i].printStackTrace(pw);
         if (isNestedThrowable(ts[i])) {
            break;
         }
      }

      return sw.getBuffer().toString();
   }

   public static boolean isNestedThrowable(Throwable throwable) {
      if (throwable == null) {
         return false;
      } else if (throwable instanceof SQLException) {
         return true;
      } else if (throwable instanceof InvocationTargetException) {
         return true;
      } else {
         int sz = CAUSE_METHOD_NAMES.length;

         for(int i = 0; i < sz; ++i) {
            try {
               Method method = throwable.getClass().getMethod(CAUSE_METHOD_NAMES[i], (Class[])null);
               if (method != null) {
                  return true;
               }
            } catch (NoSuchMethodException var6) {
            } catch (SecurityException var7) {
            }
         }

         try {
            Field field = throwable.getClass().getField("detail");
            if (field != null) {
               return true;
            }
         } catch (NoSuchFieldException var4) {
         } catch (SecurityException var5) {
         }

         return false;
      }
   }

   public static String[] getStackFrames(Throwable t) {
      return getStackFrames(getStackTrace(t));
   }

   static String[] getStackFrames(String stackTrace) {
      String linebreak = System.getProperty("line.separator");
      StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
      List list = new LinkedList();

      while(frames.hasMoreTokens()) {
         list.add(frames.nextToken());
      }

      return (String[])list.toArray(new String[0]);
   }

   static List getStackFrameList(Throwable t) {
      String stackTrace = getStackTrace(t);
      String linebreak = System.getProperty("line.separator");
      StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
      List list = new LinkedList();
      boolean traceStarted = false;

      while(frames.hasMoreTokens()) {
         String token = frames.nextToken();
         int at = token.indexOf("at");
         if (at != -1 && token.substring(0, at).trim().length() == 0) {
            traceStarted = true;
            list.add(token);
         } else if (traceStarted) {
            break;
         }
      }

      return list;
   }

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }
}
