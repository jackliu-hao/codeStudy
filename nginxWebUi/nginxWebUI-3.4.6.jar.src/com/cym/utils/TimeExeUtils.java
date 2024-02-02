/*    */ package com.cym.utils;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ @Component
/*    */ public class TimeExeUtils
/*    */ {
/* 15 */   Logger logger = LoggerFactory.getLogger(getClass());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject
/*    */   MessageUtils m;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String execCMD(String cmd, String[] envs, long timeout) {
/* 30 */     Process process = null;
/* 31 */     StringBuilder sbStd = new StringBuilder();
/*    */     
/* 33 */     long start = System.currentTimeMillis();
/*    */     try {
/* 35 */       if (envs == null) {
/* 36 */         process = Runtime.getRuntime().exec(cmd);
/*    */       } else {
/* 38 */         process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", cmd }, envs);
/*    */       } 
/*    */       
/* 41 */       BufferedReader brStd = new BufferedReader(new InputStreamReader(process.getInputStream()));
/* 42 */       String line = null;
/*    */       
/*    */       while (true) {
/* 45 */         while (brStd.ready()) {
/* 46 */           line = brStd.readLine();
/* 47 */           sbStd.append(line + "\n");
/* 48 */           this.logger.info(line);
/*    */         } 
/*    */ 
/*    */         
/* 52 */         if (process != null) {
/*    */           try {
/* 54 */             process.exitValue();
/*    */             break;
/* 56 */           } catch (IllegalThreadStateException e) {
/* 57 */             System.err.println(e.getMessage());
/*    */           } 
/*    */         }
/*    */         
/* 61 */         if (System.currentTimeMillis() - start > timeout) {
/* 62 */           line = this.m.get("certStr.timeout");
/*    */           
/* 64 */           sbStd.append(line + "\n");
/* 65 */           this.logger.info(line);
/*    */           
/*    */           break;
/*    */         } 
/*    */         try {
/* 70 */           TimeUnit.MILLISECONDS.sleep(500L);
/* 71 */         } catch (InterruptedException interruptedException) {}
/*    */       }
/*    */     
/* 74 */     } catch (IOException e) {
/* 75 */       this.logger.error(e.getMessage(), e);
/*    */     } finally {
/* 77 */       if (process != null) {
/* 78 */         process.destroy();
/*    */       }
/*    */     } 
/*    */     
/* 82 */     return sbStd.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\TimeExeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */