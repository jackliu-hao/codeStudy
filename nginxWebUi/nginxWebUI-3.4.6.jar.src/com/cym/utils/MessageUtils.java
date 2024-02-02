/*    */ package com.cym.utils;
/*    */ 
/*    */ import com.cym.service.SettingService;
/*    */ import java.util.Properties;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.noear.solon.annotation.Init;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class MessageUtils
/*    */ {
/*    */   @Inject
/*    */   PropertiesUtils propertiesUtils;
/* 20 */   Properties properties = null;
/* 21 */   Properties propertiesEN = null;
/*    */   
/*    */   @Init
/*    */   private void ini() {
/* 25 */     this.propertiesEN = this.propertiesUtils.getPropertis("messages_en_US.properties");
/* 26 */     this.properties = this.propertiesUtils.getPropertis("messages.properties");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject
/*    */   SettingService settingService;
/*    */ 
/*    */ 
/*    */   
/*    */   public String get(String msgKey) {
/* 37 */     if (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US")) {
/* 38 */       return this.propertiesEN.getProperty(msgKey);
/*    */     }
/* 40 */     return this.properties.getProperty(msgKey);
/*    */   }
/*    */ 
/*    */   
/*    */   public Properties getProperties() {
/* 45 */     return this.properties;
/*    */   }
/*    */   
/*    */   public void setProperties(Properties properties) {
/* 49 */     this.properties = properties;
/*    */   }
/*    */   
/*    */   public Properties getPropertiesEN() {
/* 53 */     return this.propertiesEN;
/*    */   }
/*    */   
/*    */   public void setPropertiesEN(Properties propertiesEN) {
/* 57 */     this.propertiesEN = propertiesEN;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\MessageUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */