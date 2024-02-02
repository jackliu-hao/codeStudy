/*    */ package com.cym.config;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.utils.FilePermissionUtil;
/*    */ import com.cym.utils.JarUtil;
/*    */ import com.cym.utils.SystemTool;
/*    */ import com.cym.utils.ToolUtils;
/*    */ import java.io.File;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.noear.solon.annotation.Init;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class HomeConfig
/*    */ {
/*    */   @Inject("${project.home}")
/*    */   public String home;
/*    */   public String acmeShDir;
/*    */   public String acmeSh;
/* 27 */   Logger logger = LoggerFactory.getLogger(getClass());
/*    */   
/*    */   @Init
/*    */   public void init() {
/* 31 */     if (StrUtil.isEmpty(this.home)) {
/*    */       
/* 33 */       File file = new File(JarUtil.getCurrentFilePath());
/*    */       
/* 35 */       if (file.getPath().contains("target") && file.getPath().contains("classes")) {
/* 36 */         this.home = FileUtil.getUserHomePath() + File.separator + "svnWebUI";
/*    */       } else {
/* 38 */         this.home = file.getParent();
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 43 */     if (SystemTool.isWindows().booleanValue() && !this.home.contains(":")) {
/* 44 */       this.home = JarUtil.getCurrentFilePath().split(":")[0] + ":" + this.home;
/*    */     }
/*    */ 
/*    */     
/* 48 */     this.home = ToolUtils.endDir(ToolUtils.handlePath(this.home));
/*    */ 
/*    */     
/* 51 */     if (!FilePermissionUtil.canWrite(new File(this.home)).booleanValue()) {
/* 52 */       this.logger.error(this.home + " directory does not have writable permission. Please specify it again.");
/* 53 */       this.logger.error(this.home + " 目录没有可写权限,请重新指定.");
/* 54 */       System.exit(1);
/*    */     } 
/*    */ 
/*    */     
/* 58 */     this.acmeShDir = this.home + ".acme.sh/";
/* 59 */     this.acmeSh = this.home + ".acme.sh/acme.sh";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\config\HomeConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */