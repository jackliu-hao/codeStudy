/*     */ package org.h2.message;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public final class Trace
/*     */ {
/*     */   public static final int COMMAND = 0;
/*     */   public static final int CONSTRAINT = 1;
/*     */   public static final int DATABASE = 2;
/*     */   public static final int FUNCTION = 3;
/*     */   public static final int FILE_LOCK = 4;
/*     */   public static final int INDEX = 5;
/*     */   public static final int JDBC = 6;
/*     */   public static final int LOCK = 7;
/*     */   public static final int SCHEMA = 8;
/*     */   public static final int SEQUENCE = 9;
/*     */   public static final int SETTING = 10;
/*     */   public static final int TABLE = 11;
/*     */   public static final int TRIGGER = 12;
/*     */   public static final int USER = 13;
/*     */   public static final int JDBCX = 14;
/*  97 */   public static final String[] MODULE_NAMES = new String[] { "command", "constraint", "database", "function", "fileLock", "index", "jdbc", "lock", "schema", "sequence", "setting", "table", "trigger", "user", "JDBCX" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final TraceWriter traceWriter;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String module;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String lineSeparator;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 118 */   private int traceLevel = -1;
/*     */   
/*     */   Trace(TraceWriter paramTraceWriter, int paramInt) {
/* 121 */     this(paramTraceWriter, MODULE_NAMES[paramInt]);
/*     */   }
/*     */   
/*     */   Trace(TraceWriter paramTraceWriter, String paramString) {
/* 125 */     this.traceWriter = paramTraceWriter;
/* 126 */     this.module = paramString;
/* 127 */     this.lineSeparator = System.lineSeparator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevel(int paramInt) {
/* 137 */     this.traceLevel = paramInt;
/*     */   }
/*     */   
/*     */   private boolean isEnabled(int paramInt) {
/* 141 */     if (this.traceLevel == -1) {
/* 142 */       return this.traceWriter.isEnabled(paramInt);
/*     */     }
/* 144 */     return (paramInt <= this.traceLevel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 153 */     return isEnabled(2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 162 */     return isEnabled(3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(Throwable paramThrowable, String paramString) {
/* 172 */     if (isEnabled(1)) {
/* 173 */       this.traceWriter.write(1, this.module, paramString, paramThrowable);
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
/*     */   public void error(Throwable paramThrowable, String paramString, Object... paramVarArgs) {
/* 185 */     if (isEnabled(1)) {
/* 186 */       paramString = MessageFormat.format(paramString, paramVarArgs);
/* 187 */       this.traceWriter.write(1, this.module, paramString, paramThrowable);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(String paramString) {
/* 197 */     if (isEnabled(2)) {
/* 198 */       this.traceWriter.write(2, this.module, paramString, (Throwable)null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(String paramString, Object... paramVarArgs) {
/* 209 */     if (isEnabled(2)) {
/* 210 */       paramString = MessageFormat.format(paramString, paramVarArgs);
/* 211 */       this.traceWriter.write(2, this.module, paramString, (Throwable)null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void info(Throwable paramThrowable, String paramString) {
/* 222 */     if (isEnabled(2)) {
/* 223 */       this.traceWriter.write(2, this.module, paramString, paramThrowable);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatParams(ArrayList<? extends ParameterInterface> paramArrayList) {
/* 234 */     if (paramArrayList.isEmpty()) {
/* 235 */       return "";
/*     */     }
/* 237 */     StringBuilder stringBuilder = new StringBuilder();
/* 238 */     byte b = 0;
/* 239 */     for (ParameterInterface parameterInterface : paramArrayList) {
/* 240 */       if (parameterInterface.isValueSet()) {
/* 241 */         stringBuilder.append(!b ? " {" : ", ")
/* 242 */           .append(++b).append(": ")
/* 243 */           .append(parameterInterface.getParamValue().getTraceSQL());
/*     */       }
/*     */     } 
/* 246 */     if (b != 0) {
/* 247 */       stringBuilder.append('}');
/*     */     }
/* 249 */     return stringBuilder.toString();
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
/*     */   public void infoSQL(String paramString1, String paramString2, long paramLong1, long paramLong2) {
/* 261 */     if (!isEnabled(2)) {
/*     */       return;
/*     */     }
/* 264 */     StringBuilder stringBuilder = new StringBuilder(paramString1.length() + paramString2.length() + 20);
/* 265 */     stringBuilder.append(this.lineSeparator).append("/*SQL");
/* 266 */     boolean bool = false;
/* 267 */     if (paramString2.length() > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 274 */       bool = true;
/* 275 */       stringBuilder.append(" l:").append(paramString1.length());
/*     */     } 
/* 277 */     if (paramLong1 > 0L) {
/* 278 */       bool = true;
/* 279 */       stringBuilder.append(" #:").append(paramLong1);
/*     */     } 
/* 281 */     if (paramLong2 > 0L) {
/* 282 */       bool = true;
/* 283 */       stringBuilder.append(" t:").append(paramLong2);
/*     */     } 
/* 285 */     if (!bool) {
/* 286 */       stringBuilder.append(' ');
/*     */     }
/* 288 */     stringBuilder.append("*/");
/* 289 */     StringUtils.javaEncode(paramString1, stringBuilder, false);
/* 290 */     StringUtils.javaEncode(paramString2, stringBuilder, false);
/* 291 */     stringBuilder.append(';');
/* 292 */     paramString1 = stringBuilder.toString();
/* 293 */     this.traceWriter.write(2, this.module, paramString1, (Throwable)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(String paramString, Object... paramVarArgs) {
/* 303 */     if (isEnabled(3)) {
/* 304 */       paramString = MessageFormat.format(paramString, paramVarArgs);
/* 305 */       this.traceWriter.write(3, this.module, paramString, (Throwable)null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(String paramString) {
/* 315 */     if (isEnabled(3)) {
/* 316 */       this.traceWriter.write(3, this.module, paramString, (Throwable)null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(Throwable paramThrowable, String paramString) {
/* 326 */     if (isEnabled(3)) {
/* 327 */       this.traceWriter.write(3, this.module, paramString, paramThrowable);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void infoCode(String paramString) {
/* 338 */     if (isEnabled(2)) {
/* 339 */       this.traceWriter.write(2, this.module, this.lineSeparator + "/**/" + paramString, (Throwable)null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void debugCode(String paramString) {
/* 350 */     if (isEnabled(3))
/* 351 */       this.traceWriter.write(3, this.module, this.lineSeparator + "/**/" + paramString, (Throwable)null); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\message\Trace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */