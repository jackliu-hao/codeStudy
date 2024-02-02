/*    */ package com.cym.service;
/*    */ 
/*    */ import com.cym.model.Setting;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ @Service
/*    */ public class SettingService {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public void set(String key, String value) {
/* 16 */     Setting setting = (Setting)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("key", key), Setting.class);
/* 17 */     if (setting == null) {
/* 18 */       setting = new Setting();
/*    */     }
/*    */     
/* 21 */     setting.setKey(key);
/* 22 */     setting.setValue(value);
/*    */     
/* 24 */     this.sqlHelper.insertOrUpdate(setting);
/*    */   }
/*    */   
/*    */   public String get(String key) {
/* 28 */     Setting setting = (Setting)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("key", key), Setting.class);
/*    */     
/* 30 */     if (setting == null) {
/* 31 */       return null;
/*    */     }
/* 33 */     return setting.getValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(String key) {
/* 38 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("key", key), Setting.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\SettingService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */