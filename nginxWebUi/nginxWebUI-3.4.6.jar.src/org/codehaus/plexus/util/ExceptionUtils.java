/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExceptionUtils
/*     */ {
/*     */   static final String WRAPPED_MARKER = " [wrapped] ";
/*  92 */   protected static String[] CAUSE_METHOD_NAMES = new String[] { "getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void addCauseMethodName(String methodName) {
/* 119 */     if (methodName != null && methodName.length() > 0) {
/*     */       
/* 121 */       List list = new ArrayList(Arrays.asList((Object[])CAUSE_METHOD_NAMES));
/* 122 */       list.add(methodName);
/* 123 */       CAUSE_METHOD_NAMES = list.<String>toArray(new String[list.size()]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable getCause(Throwable throwable) {
/* 157 */     return getCause(throwable, CAUSE_METHOD_NAMES);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable getCause(Throwable throwable, String[] methodNames) {
/* 171 */     Throwable cause = getCauseUsingWellKnownTypes(throwable);
/* 172 */     if (cause == null) {
/*     */       
/* 174 */       for (int i = 0; i < methodNames.length; i++) {
/*     */         
/* 176 */         cause = getCauseUsingMethodName(throwable, methodNames[i]);
/* 177 */         if (cause != null) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 183 */       if (cause == null)
/*     */       {
/* 185 */         cause = getCauseUsingFieldName(throwable, "detail");
/*     */       }
/*     */     } 
/* 188 */     return cause;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable getRootCause(Throwable throwable) {
/* 201 */     Throwable cause = getCause(throwable);
/* 202 */     if (cause != null) {
/*     */       
/* 204 */       throwable = cause;
/* 205 */       while ((throwable = getCause(throwable)) != null)
/*     */       {
/* 207 */         cause = throwable;
/*     */       }
/*     */     } 
/* 210 */     return cause;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Throwable getCauseUsingWellKnownTypes(Throwable throwable) {
/* 224 */     if (throwable instanceof SQLException)
/*     */     {
/* 226 */       return ((SQLException)throwable).getNextException();
/*     */     }
/* 228 */     if (throwable instanceof InvocationTargetException)
/*     */     {
/* 230 */       return ((InvocationTargetException)throwable).getTargetException();
/*     */     }
/*     */ 
/*     */     
/* 234 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
/* 248 */     Method method = null;
/*     */     
/*     */     try {
/* 251 */       method = throwable.getClass().getMethod(methodName, null);
/*     */     }
/* 253 */     catch (NoSuchMethodException ignored) {
/*     */ 
/*     */     
/* 256 */     } catch (SecurityException ignored) {}
/*     */ 
/*     */ 
/*     */     
/* 260 */     if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
/*     */       
/*     */       try {
/*     */         
/* 264 */         return (Throwable)method.invoke(throwable, new Object[0]);
/*     */       }
/* 266 */       catch (IllegalAccessException ignored) {
/*     */ 
/*     */       
/* 269 */       } catch (IllegalArgumentException ignored) {
/*     */ 
/*     */       
/* 272 */       } catch (InvocationTargetException ignored) {}
/*     */     }
/*     */ 
/*     */     
/* 276 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Throwable getCauseUsingFieldName(Throwable throwable, String fieldName) {
/* 289 */     Field field = null;
/*     */     
/*     */     try {
/* 292 */       field = throwable.getClass().getField(fieldName);
/*     */     }
/* 294 */     catch (NoSuchFieldException ignored) {
/*     */ 
/*     */     
/* 297 */     } catch (SecurityException ignored) {}
/*     */ 
/*     */ 
/*     */     
/* 301 */     if (field != null && Throwable.class.isAssignableFrom(field.getType())) {
/*     */       
/*     */       try {
/*     */         
/* 305 */         return (Throwable)field.get(throwable);
/*     */       }
/* 307 */       catch (IllegalAccessException ignored) {
/*     */ 
/*     */       
/* 310 */       } catch (IllegalArgumentException ignored) {}
/*     */     }
/*     */ 
/*     */     
/* 314 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getThrowableCount(Throwable throwable) {
/* 327 */     int count = 0;
/* 328 */     while (throwable != null) {
/*     */       
/* 330 */       count++;
/* 331 */       throwable = getCause(throwable);
/*     */     } 
/* 333 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable[] getThrowables(Throwable throwable) {
/* 345 */     List list = new ArrayList();
/* 346 */     while (throwable != null) {
/*     */       
/* 348 */       list.add(throwable);
/* 349 */       throwable = getCause(throwable);
/*     */     } 
/* 351 */     return list.<Throwable>toArray(new Throwable[list.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indexOfThrowable(Throwable throwable, Class type) {
/* 362 */     return indexOfThrowable(throwable, type, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indexOfThrowable(Throwable throwable, Class type, int fromIndex) {
/* 384 */     if (fromIndex < 0)
/*     */     {
/* 386 */       throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex);
/*     */     }
/* 388 */     Throwable[] throwables = getThrowables(throwable);
/* 389 */     if (fromIndex >= throwables.length)
/*     */     {
/* 391 */       throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex);
/*     */     }
/* 393 */     for (int i = fromIndex; i < throwables.length; i++) {
/*     */       
/* 395 */       if (throwables[i].getClass().equals(type))
/*     */       {
/* 397 */         return i;
/*     */       }
/*     */     } 
/* 400 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void printRootCauseStackTrace(Throwable t, PrintStream stream) {
/* 415 */     String[] trace = getRootCauseStackTrace(t);
/* 416 */     for (int i = 0; i < trace.length; i++)
/*     */     {
/* 418 */       stream.println(trace[i]);
/*     */     }
/* 420 */     stream.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void printRootCauseStackTrace(Throwable t) {
/* 428 */     printRootCauseStackTrace(t, System.err);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void printRootCauseStackTrace(Throwable t, PrintWriter writer) {
/* 437 */     String[] trace = getRootCauseStackTrace(t);
/* 438 */     for (int i = 0; i < trace.length; i++)
/*     */     {
/* 440 */       writer.println(trace[i]);
/*     */     }
/* 442 */     writer.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] getRootCauseStackTrace(Throwable t) {
/* 453 */     Throwable[] throwables = getThrowables(t);
/* 454 */     int count = throwables.length;
/* 455 */     ArrayList frames = new ArrayList();
/* 456 */     List nextTrace = getStackFrameList(throwables[count - 1]);
/* 457 */     for (int i = count; --i >= 0; ) {
/*     */       
/* 459 */       List trace = nextTrace;
/* 460 */       if (i != 0) {
/*     */         
/* 462 */         nextTrace = getStackFrameList(throwables[i - 1]);
/* 463 */         removeCommonFrames(trace, nextTrace);
/*     */       } 
/* 465 */       if (i == count - 1) {
/*     */         
/* 467 */         frames.add(throwables[i].toString());
/*     */       }
/*     */       else {
/*     */         
/* 471 */         frames.add(" [wrapped] " + throwables[i].toString());
/*     */       } 
/* 473 */       for (int j = 0; j < trace.size(); j++)
/*     */       {
/* 475 */         frames.add(trace.get(j));
/*     */       }
/*     */     } 
/* 478 */     return frames.<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void removeCommonFrames(List causeFrames, List wrapperFrames) {
/* 489 */     int causeFrameIndex = causeFrames.size() - 1;
/* 490 */     int wrapperFrameIndex = wrapperFrames.size() - 1;
/* 491 */     while (causeFrameIndex >= 0 && wrapperFrameIndex >= 0) {
/*     */ 
/*     */ 
/*     */       
/* 495 */       String causeFrame = causeFrames.get(causeFrameIndex);
/* 496 */       String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
/* 497 */       if (causeFrame.equals(wrapperFrame))
/*     */       {
/* 499 */         causeFrames.remove(causeFrameIndex);
/*     */       }
/* 501 */       causeFrameIndex--;
/* 502 */       wrapperFrameIndex--;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getStackTrace(Throwable t) {
/* 516 */     StringWriter sw = new StringWriter();
/* 517 */     PrintWriter pw = new PrintWriter(sw, true);
/* 518 */     t.printStackTrace(pw);
/* 519 */     return sw.getBuffer().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getFullStackTrace(Throwable t) {
/* 530 */     StringWriter sw = new StringWriter();
/* 531 */     PrintWriter pw = new PrintWriter(sw, true);
/* 532 */     Throwable[] ts = getThrowables(t);
/* 533 */     for (int i = 0; i < ts.length; i++) {
/*     */       
/* 535 */       ts[i].printStackTrace(pw);
/* 536 */       if (isNestedThrowable(ts[i])) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 541 */     return sw.getBuffer().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNestedThrowable(Throwable throwable) {
/* 552 */     if (throwable == null)
/*     */     {
/* 554 */       return false;
/*     */     }
/*     */     
/* 557 */     if (throwable instanceof SQLException)
/*     */     {
/* 559 */       return true;
/*     */     }
/* 561 */     if (throwable instanceof InvocationTargetException)
/*     */     {
/* 563 */       return true;
/*     */     }
/*     */     
/* 566 */     int sz = CAUSE_METHOD_NAMES.length;
/* 567 */     for (int i = 0; i < sz; i++) {
/*     */ 
/*     */       
/*     */       try {
/* 571 */         Method method = throwable.getClass().getMethod(CAUSE_METHOD_NAMES[i], null);
/* 572 */         if (method != null)
/*     */         {
/* 574 */           return true;
/*     */         }
/*     */       }
/* 577 */       catch (NoSuchMethodException ignored) {
/*     */ 
/*     */       
/* 580 */       } catch (SecurityException ignored) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 587 */       Field field = throwable.getClass().getField("detail");
/* 588 */       if (field != null)
/*     */       {
/* 590 */         return true;
/*     */       }
/*     */     }
/* 593 */     catch (NoSuchFieldException ignored) {
/*     */ 
/*     */     
/* 596 */     } catch (SecurityException ignored) {}
/*     */ 
/*     */ 
/*     */     
/* 600 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] getStackFrames(Throwable t) {
/* 613 */     return getStackFrames(getStackTrace(t));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String[] getStackFrames(String stackTrace) {
/* 623 */     String linebreak = System.getProperty("line.separator");
/* 624 */     StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
/* 625 */     List list = new LinkedList();
/* 626 */     while (frames.hasMoreTokens())
/*     */     {
/* 628 */       list.add(frames.nextToken());
/*     */     }
/* 630 */     return list.<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static List getStackFrameList(Throwable t) {
/* 644 */     String stackTrace = getStackTrace(t);
/* 645 */     String linebreak = System.getProperty("line.separator");
/* 646 */     StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
/* 647 */     List list = new LinkedList();
/* 648 */     boolean traceStarted = false;
/* 649 */     while (frames.hasMoreTokens()) {
/*     */       
/* 651 */       String token = frames.nextToken();
/*     */       
/* 653 */       int at = token.indexOf("at");
/* 654 */       if (at != -1 && token.substring(0, at).trim().length() == 0) {
/*     */         
/* 656 */         traceStarted = true;
/* 657 */         list.add(token); continue;
/*     */       } 
/* 659 */       if (traceStarted) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 664 */     return list;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\ExceptionUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */