/*    */ package cn.hutool.setting;
/*    */ 
/*    */ import cn.hutool.core.io.file.FileNameUtil;
/*    */ import cn.hutool.core.io.resource.NoResourceException;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ public class SettingUtil
/*    */ {
/* 20 */   private static final Map<String, Setting> SETTING_MAP = new ConcurrentHashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Setting get(String name) {
/* 30 */     return SETTING_MAP.computeIfAbsent(name, filePath -> {
/*    */           String extName = FileNameUtil.extName(filePath);
/*    */           if (StrUtil.isEmpty(extName)) {
/*    */             filePath = filePath + "." + "setting";
/*    */           }
/*    */           return new Setting(filePath, true);
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
/*    */   public static Setting getFirstFound(String... names) {
/* 49 */     for (String name : names) {
/*    */       try {
/* 51 */         return get(name);
/* 52 */       } catch (NoResourceException noResourceException) {}
/*    */     } 
/*    */ 
/*    */     
/* 56 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\SettingUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */