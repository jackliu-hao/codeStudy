/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.Scanner;
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
/*     */ public class Console
/*     */ {
/*     */   private static final String TEMPLATE_VAR = "{}";
/*     */   
/*     */   public static void log() {
/*  29 */     System.out.println();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void log(Object obj) {
/*  39 */     if (obj instanceof Throwable) {
/*  40 */       Throwable e = (Throwable)obj;
/*  41 */       log(e, e.getMessage(), new Object[0]);
/*     */     } else {
/*  43 */       log("{}", new Object[] { obj });
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
/*     */   public static void log(Object obj1, Object... otherObjs) {
/*  56 */     if (ArrayUtil.isEmpty(otherObjs)) {
/*  57 */       log(obj1);
/*     */     } else {
/*  59 */       log(buildTemplateSplitBySpace(otherObjs.length + 1), ArrayUtil.insert(otherObjs, 0, new Object[] { obj1 }));
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
/*     */   public static void log(String template, Object... values) {
/*  71 */     if (ArrayUtil.isEmpty(values) || StrUtil.contains(template, "{}")) {
/*  72 */       logInternal(template, values);
/*     */     } else {
/*  74 */       logInternal(buildTemplateSplitBySpace(values.length + 1), ArrayUtil.insert(values, 0, new Object[] { template }));
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
/*     */   public static void log(Throwable t, String template, Object... values) {
/*  86 */     System.out.println(StrUtil.format(template, values));
/*  87 */     if (null != t) {
/*     */       
/*  89 */       t.printStackTrace();
/*  90 */       System.out.flush();
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
/*     */   private static void logInternal(String template, Object... values) {
/* 102 */     log(null, template, values);
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
/*     */   public static void table(ConsoleTable consoleTable) {
/* 114 */     print(consoleTable.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void print(Object obj) {
/* 124 */     print("{}", new Object[] { obj });
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
/*     */   public static void print(Object obj1, Object... otherObjs) {
/* 136 */     if (ArrayUtil.isEmpty(otherObjs)) {
/* 137 */       print(obj1);
/*     */     } else {
/* 139 */       print(buildTemplateSplitBySpace(otherObjs.length + 1), ArrayUtil.insert(otherObjs, 0, new Object[] { obj1 }));
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
/*     */   public static void print(String template, Object... values) {
/* 151 */     if (ArrayUtil.isEmpty(values) || StrUtil.contains(template, "{}")) {
/* 152 */       printInternal(template, values);
/*     */     } else {
/* 154 */       printInternal(buildTemplateSplitBySpace(values.length + 1), ArrayUtil.insert(values, 0, new Object[] { template }));
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
/*     */   public static void printProgress(char showChar, int len) {
/* 166 */     print("{}{}", new Object[] { Character.valueOf('\r'), StrUtil.repeat(showChar, len) });
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
/*     */   public static void printProgress(char showChar, int totalLen, double rate) {
/* 178 */     Assert.isTrue((rate >= 0.0D && rate <= 1.0D), "Rate must between 0 and 1 (both include)", new Object[0]);
/* 179 */     printProgress(showChar, (int)(totalLen * rate));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void printInternal(String template, Object... values) {
/* 190 */     System.out.print(StrUtil.format(template, values));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void error() {
/* 199 */     System.err.println();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void error(Object obj) {
/* 208 */     if (obj instanceof Throwable) {
/* 209 */       Throwable e = (Throwable)obj;
/* 210 */       error(e, e.getMessage(), new Object[0]);
/*     */     } else {
/* 212 */       error("{}", new Object[] { obj });
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
/*     */   public static void error(Object obj1, Object... otherObjs) {
/* 225 */     if (ArrayUtil.isEmpty(otherObjs)) {
/* 226 */       error(obj1);
/*     */     } else {
/* 228 */       error(buildTemplateSplitBySpace(otherObjs.length + 1), ArrayUtil.insert(otherObjs, 0, new Object[] { obj1 }));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void error(String template, Object... values) {
/* 239 */     if (ArrayUtil.isEmpty(values) || StrUtil.contains(template, "{}")) {
/* 240 */       errorInternal(template, values);
/*     */     } else {
/* 242 */       errorInternal(buildTemplateSplitBySpace(values.length + 1), ArrayUtil.insert(values, 0, new Object[] { template }));
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
/*     */   public static void error(Throwable t, String template, Object... values) {
/* 254 */     System.err.println(StrUtil.format(template, values));
/* 255 */     if (null != t) {
/* 256 */       t.printStackTrace(System.err);
/* 257 */       System.err.flush();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void errorInternal(String template, Object... values) {
/* 268 */     error(null, template, values);
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
/*     */   public static Scanner scanner() {
/* 280 */     return new Scanner(System.in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String input() {
/* 290 */     return scanner().nextLine();
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
/*     */   public static String where() {
/* 303 */     StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
/* 304 */     String className = stackTraceElement.getClassName();
/* 305 */     String methodName = stackTraceElement.getMethodName();
/* 306 */     String fileName = stackTraceElement.getFileName();
/* 307 */     Integer lineNumber = Integer.valueOf(stackTraceElement.getLineNumber());
/* 308 */     return String.format("%s.%s(%s:%s)", new Object[] { className, methodName, fileName, lineNumber });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Integer lineNumber() {
/* 318 */     return Integer.valueOf((new Throwable()).getStackTrace()[1].getLineNumber());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String buildTemplateSplitBySpace(int count) {
/* 328 */     return StrUtil.repeatAndJoin("{}", count, " ");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Console.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */