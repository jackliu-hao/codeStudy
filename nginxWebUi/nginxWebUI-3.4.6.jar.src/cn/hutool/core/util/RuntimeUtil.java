/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Pid;
/*     */ import cn.hutool.core.text.StrBuilder;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
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
/*     */ public class RuntimeUtil
/*     */ {
/*     */   public static String execForStr(String... cmds) throws IORuntimeException {
/*  33 */     return execForStr(CharsetUtil.systemCharset(), cmds);
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
/*     */   public static String execForStr(Charset charset, String... cmds) throws IORuntimeException {
/*  46 */     return getResult(exec(cmds), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> execForLines(String... cmds) throws IORuntimeException {
/*  57 */     return execForLines(CharsetUtil.systemCharset(), cmds);
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
/*     */   public static List<String> execForLines(Charset charset, String... cmds) throws IORuntimeException {
/*  70 */     return getResultLines(exec(cmds), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Process exec(String... cmds) {
/*     */     Process process;
/*     */     try {
/*  83 */       process = (new ProcessBuilder(handleCmds(cmds))).redirectErrorStream(true).start();
/*  84 */     } catch (IOException e) {
/*  85 */       throw new IORuntimeException(e);
/*     */     } 
/*  87 */     return process;
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
/*     */   public static Process exec(String[] envp, String... cmds) {
/* 100 */     return exec(envp, null, cmds);
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
/*     */   public static Process exec(String[] envp, File dir, String... cmds) {
/*     */     try {
/* 115 */       return Runtime.getRuntime().exec(handleCmds(cmds), envp, dir);
/* 116 */     } catch (IOException e) {
/* 117 */       throw new IORuntimeException(e);
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
/*     */   public static List<String> getResultLines(Process process) {
/* 130 */     return getResultLines(process, CharsetUtil.systemCharset());
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
/*     */   public static List<String> getResultLines(Process process, Charset charset) {
/* 142 */     InputStream in = null;
/*     */     try {
/* 144 */       in = process.getInputStream();
/* 145 */       return (List)IoUtil.readLines(in, charset, new ArrayList());
/*     */     } finally {
/* 147 */       IoUtil.close(in);
/* 148 */       destroy(process);
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
/*     */   public static String getResult(Process process) {
/* 160 */     return getResult(process, CharsetUtil.systemCharset());
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
/*     */   public static String getResult(Process process, Charset charset) {
/* 172 */     InputStream in = null;
/*     */     try {
/* 174 */       in = process.getInputStream();
/* 175 */       return IoUtil.read(in, charset);
/*     */     } finally {
/* 177 */       IoUtil.close(in);
/* 178 */       destroy(process);
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
/*     */   public static String getErrorResult(Process process) {
/* 190 */     return getErrorResult(process, CharsetUtil.systemCharset());
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
/*     */   public static String getErrorResult(Process process, Charset charset) {
/* 202 */     InputStream in = null;
/*     */     try {
/* 204 */       in = process.getErrorStream();
/* 205 */       return IoUtil.read(in, charset);
/*     */     } finally {
/* 207 */       IoUtil.close(in);
/* 208 */       destroy(process);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void destroy(Process process) {
/* 219 */     if (null != process) {
/* 220 */       process.destroy();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void addShutdownHook(Runnable hook) {
/* 231 */     Runtime.getRuntime().addShutdownHook((hook instanceof Thread) ? (Thread)hook : new Thread(hook));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getProcessorCount() {
/* 241 */     return Runtime.getRuntime().availableProcessors();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getFreeMemory() {
/* 251 */     return Runtime.getRuntime().freeMemory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getTotalMemory() {
/* 261 */     return Runtime.getRuntime().totalMemory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getMaxMemory() {
/* 271 */     return Runtime.getRuntime().maxMemory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getUsableMemory() {
/* 281 */     return getMaxMemory() - getTotalMemory() + getFreeMemory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getPid() throws UtilException {
/* 292 */     return Pid.INSTANCE.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] handleCmds(String... cmds) {
/* 302 */     if (ArrayUtil.isEmpty(cmds)) {
/* 303 */       throw new NullPointerException("Command is empty !");
/*     */     }
/*     */ 
/*     */     
/* 307 */     if (1 == cmds.length) {
/* 308 */       String cmd = cmds[0];
/* 309 */       if (StrUtil.isBlank(cmd)) {
/* 310 */         throw new NullPointerException("Command is blank !");
/*     */       }
/* 312 */       cmds = cmdSplit(cmd);
/*     */     } 
/* 314 */     return cmds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] cmdSplit(String cmd) {
/* 324 */     List<String> cmds = new ArrayList<>();
/*     */     
/* 326 */     int length = cmd.length();
/* 327 */     Stack<Character> stack = new Stack<>();
/* 328 */     boolean inWrap = false;
/* 329 */     StrBuilder cache = StrUtil.strBuilder();
/*     */ 
/*     */     
/* 332 */     for (int i = 0; i < length; i++) {
/* 333 */       char c = cmd.charAt(i);
/* 334 */       switch (c) {
/*     */         case '"':
/*     */         case '\'':
/* 337 */           if (inWrap) {
/* 338 */             if (c == ((Character)stack.peek()).charValue()) {
/*     */               
/* 340 */               stack.pop();
/* 341 */               inWrap = false;
/*     */             } 
/* 343 */             cache.append(c); break;
/*     */           } 
/* 345 */           stack.push(Character.valueOf(c));
/* 346 */           cache.append(c);
/* 347 */           inWrap = true;
/*     */           break;
/*     */         
/*     */         case ' ':
/* 351 */           if (inWrap) {
/*     */             
/* 353 */             cache.append(c); break;
/*     */           } 
/* 355 */           cmds.add(cache.toString());
/* 356 */           cache.reset();
/*     */           break;
/*     */         
/*     */         default:
/* 360 */           cache.append(c);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 365 */     if (cache.hasContent()) {
/* 366 */       cmds.add(cache.toString());
/*     */     }
/*     */     
/* 369 */     return cmds.<String>toArray(new String[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\RuntimeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */