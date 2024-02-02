/*    */ package cn.hutool.setting.profile;
/*    */ 
/*    */ import cn.hutool.core.lang.Singleton;
/*    */ import cn.hutool.setting.Setting;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GlobalProfile
/*    */ {
/*    */   public static Profile setProfile(String profile) {
/* 24 */     return (Profile)Singleton.get(Profile.class, new Object[] { profile });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Setting getSetting(String settingName) {
/* 33 */     return ((Profile)Singleton.get(Profile.class, new Object[0])).getSetting(settingName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\profile\GlobalProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */