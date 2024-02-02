/*    */ package com.cym;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.util.RuntimeUtil;
/*    */ import com.cym.utils.JarUtil;
/*    */ import com.cym.utils.SystemTool;
/*    */ import freemarker.template.Configuration;
/*    */ import java.io.File;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.lang.management.RuntimeMXBean;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.SolonApp;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.schedule.annotation.EnableScheduling;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ @EnableScheduling
/*    */ public class NginxWebUI
/*    */ {
/* 24 */   static Logger logger = LoggerFactory.getLogger(NginxWebUI.class);
/*    */ 
/*    */   
/*    */   public static void main(String[] args) {
/*    */     try {
/* 29 */       killSelf(args);
/*    */ 
/*    */       
/* 32 */       removeJar();
/* 33 */     } catch (Exception e) {
/* 34 */       logger.error(e.getMessage(), e);
/*    */     } 
/*    */     
/* 37 */     Solon.start(NginxWebUI.class, args, app -> {
/*    */           app.onError(());
/*    */           app.before(());
/*    */           app.onEvent(Configuration.class, ());
/*    */         });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void killSelf(String[] args) {
/* 57 */     RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
/* 58 */     String myPid = runtimeMXBean.getName().split("@")[0];
/*    */     
/* 60 */     List<String> list = new ArrayList<>();
/*    */     
/* 62 */     list = RuntimeUtil.execForLines(new String[] { "jps" });
/* 63 */     for (String line : list) {
/* 64 */       if (line.contains("nginxWebUI") && line.contains(".jar")) {
/* 65 */         String pid = line.split("\\s+")[0].trim();
/* 66 */         if (!pid.equals(myPid)) {
/* 67 */           logger.info("杀掉旧进程:" + pid);
/* 68 */           if (SystemTool.isWindows().booleanValue()) {
/* 69 */             RuntimeUtil.exec(new String[] { "taskkill /im " + pid + " /f" }); continue;
/* 70 */           }  if (SystemTool.isLinux().booleanValue()) {
/* 71 */             RuntimeUtil.exec(new String[] { "kill -9 " + pid });
/*    */           }
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static void removeJar() {
/* 81 */     File[] list = (new File(JarUtil.getCurrentFilePath())).getParentFile().listFiles();
/* 82 */     for (File file : list) {
/* 83 */       if (file.getName().startsWith("nginxWebUI") && file.getName().endsWith(".jar") && !file.getPath().equals(JarUtil.getCurrentFilePath())) {
/* 84 */         FileUtil.del(file);
/* 85 */         logger.info("删除文件:" + file);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\NginxWebUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */