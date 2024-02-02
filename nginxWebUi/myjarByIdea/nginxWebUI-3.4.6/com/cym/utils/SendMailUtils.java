package com.cym.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.cym.service.SettingService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SendMailUtils {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   SettingService settingService;

   public void sendMailSmtp(String to, String title, String msg) {
      if (StrUtil.isEmpty(to)) {
         this.logger.info("目标邮件为空, 无法发送");
      } else {
         MailAccount account = new MailAccount();
         account.setHost(this.settingService.get("mail_host"));
         if (this.settingService.get("mail_port") != null) {
            account.setPort(Integer.parseInt(this.settingService.get("mail_port")));
         }

         account.setAuth(true);
         account.setFrom(this.settingService.get("mail_from"));
         account.setUser(this.settingService.get("mail_user"));
         account.setPass(this.settingService.get("mail_pass"));
         if (this.settingService.get("mail_ssl") != null) {
            account.setSslEnable(Boolean.parseBoolean(this.settingService.get("mail_ssl")));
         }

         MailUtil.send(account, to, title, msg, false);
         this.logger.info("发送邮件: " + to);
      }
   }
}
