/*    */ package com.cym.utils;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.thread.ThreadUtil;
/*    */ import cn.hutool.core.util.RuntimeUtil;
/*    */ import java.io.File;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class UpdateUtils
/*    */ {
/*    */   @Inject("${server.port}")
/*    */   String port;
/*    */   @Inject("${project.home}")
/*    */   String home;
/*    */   @Inject("${spring.database.type:}")
/*    */   String type;
/*    */   @Inject("${spring.datasource.url:}")
/*    */   String url;
/*    */   @Inject("${spring.datasource.username:}")
/*    */   String username;
/*    */   @Inject("${spring.datasource.password:}")
/*    */   String password;
/* 30 */   private static final Logger LOG = LoggerFactory.getLogger(UpdateUtils.class);
/*    */   
/*    */   public void run(String path) {
/* 33 */     ThreadUtil.safeSleep(2000L);
/*    */     
/* 35 */     String newPath = path.replace(".update", "");
/* 36 */     FileUtil.rename(new File(path), newPath, true);
/*    */     
/* 38 */     String param = " --server.port=" + this.port + " --project.home=" + this.home;
/*    */     
/* 40 */     if ("mysql".equals(this.type.toLowerCase())) {
/* 41 */       param = param + " --spring.database.type=" + this.type + " --spring.datasource.url=" + this.url + " --spring.datasource.username=" + this.username + " --spring.datasource.password=" + this.password;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 47 */     String cmd = null;
/* 48 */     if (SystemTool.isWindows().booleanValue()) {
/* 49 */       cmd = "java -jar -Dfile.encoding=UTF-8 " + newPath + param;
/*    */     } else {
/* 51 */       cmd = "nohup java -jar -Dfile.encoding=UTF-8 " + newPath + param + " > /dev/null &";
/*    */     } 
/*    */     
/* 54 */     LOG.info(cmd);
/* 55 */     RuntimeUtil.exec(new String[] { cmd });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\UpdateUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */