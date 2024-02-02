package cn.hutool.extra.mail;

import cn.hutool.core.io.IORuntimeException;

public enum GlobalMailAccount {
   INSTANCE;

   private final MailAccount mailAccount = this.createDefaultAccount();

   public MailAccount getAccount() {
      return this.mailAccount;
   }

   private MailAccount createDefaultAccount() {
      String[] var1 = MailAccount.MAIL_SETTING_PATHS;
      int var2 = var1.length;
      int var3 = 0;

      while(var3 < var2) {
         String mailSettingPath = var1[var3];

         try {
            return new MailAccount(mailSettingPath);
         } catch (IORuntimeException var6) {
            ++var3;
         }
      }

      return null;
   }
}
