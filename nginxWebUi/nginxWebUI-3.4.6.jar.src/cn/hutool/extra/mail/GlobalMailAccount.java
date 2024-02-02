/*    */ package cn.hutool.extra.mail;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum GlobalMailAccount
/*    */ {
/* 12 */   INSTANCE;
/*    */ 
/*    */   
/*    */   private final MailAccount mailAccount;
/*    */ 
/*    */ 
/*    */   
/*    */   GlobalMailAccount() {
/* 20 */     this.mailAccount = createDefaultAccount();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MailAccount getAccount() {
/* 29 */     return this.mailAccount;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private MailAccount createDefaultAccount() {
/* 38 */     for (String mailSettingPath : MailAccount.MAIL_SETTING_PATHS) {
/*    */       try {
/* 40 */         return new MailAccount(mailSettingPath);
/* 41 */       } catch (IORuntimeException iORuntimeException) {}
/*    */     } 
/*    */ 
/*    */     
/* 45 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\mail\GlobalMailAccount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */