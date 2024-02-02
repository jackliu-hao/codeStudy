/*    */ package com.cym.utils;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.mail.MailAccount;
/*    */ import cn.hutool.extra.mail.MailUtil;
/*    */ import com.cym.service.SettingService;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class SendMailUtils
/*    */ {
/* 17 */   Logger logger = LoggerFactory.getLogger(getClass());
/*    */   @Inject
/*    */   SettingService settingService;
/*    */   
/*    */   public void sendMailSmtp(String to, String title, String msg) {
/* 22 */     if (StrUtil.isEmpty(to)) {
/* 23 */       this.logger.info("目标邮件为空, 无法发送");
/*    */       return;
/*    */     } 
/* 26 */     MailAccount account = new MailAccount();
/* 27 */     account.setHost(this.settingService.get("mail_host"));
/* 28 */     if (this.settingService.get("mail_port") != null) {
/* 29 */       account.setPort(Integer.valueOf(Integer.parseInt(this.settingService.get("mail_port"))));
/*    */     }
/* 31 */     account.setAuth(true);
/* 32 */     account.setFrom(this.settingService.get("mail_from"));
/* 33 */     account.setUser(this.settingService.get("mail_user"));
/* 34 */     account.setPass(this.settingService.get("mail_pass"));
/* 35 */     if (this.settingService.get("mail_ssl") != null) {
/* 36 */       account.setSslEnable(Boolean.valueOf(Boolean.parseBoolean(this.settingService.get("mail_ssl"))));
/*    */     }
/*    */     
/* 39 */     MailUtil.send(account, to, title, msg, false, new java.io.File[0]);
/* 40 */     this.logger.info("发送邮件: " + to);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\SendMailUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */