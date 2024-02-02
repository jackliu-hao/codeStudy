package com.mysql.cj.util;

public class LogUtils {
   public static final String CALLER_INFORMATION_NOT_AVAILABLE = "Caller information not available";
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static final int LINE_SEPARATOR_LENGTH;

   public static String findCallingClassAndMethod(Throwable t) {
      String stackTraceAsString = Util.stackTraceToString(t);
      String callingClassAndMethod = "Caller information not available";
      int endInternalMethods = Math.max(Math.max(stackTraceAsString.lastIndexOf("com.mysql.cj"), stackTraceAsString.lastIndexOf("com.mysql.cj.core")), stackTraceAsString.lastIndexOf("com.mysql.cj.jdbc"));
      if (endInternalMethods != -1) {
         int endOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endInternalMethods);
         if (endOfLine != -1) {
            int nextEndOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endOfLine + LINE_SEPARATOR_LENGTH);
            callingClassAndMethod = nextEndOfLine != -1 ? stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH, nextEndOfLine) : stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH);
         }
      }

      return !callingClassAndMethod.startsWith("\tat ") && !callingClassAndMethod.startsWith("at ") ? "at " + callingClassAndMethod : callingClassAndMethod;
   }

   static {
      LINE_SEPARATOR_LENGTH = LINE_SEPARATOR.length();
   }
}
