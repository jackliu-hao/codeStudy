/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.io.resource.NoResourceException;
/*     */ import cn.hutool.db.sql.SqlLog;
/*     */ import cn.hutool.log.level.Level;
/*     */ import cn.hutool.setting.Setting;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GlobalDbConfig
/*     */ {
/*     */   private static final String DEFAULT_DB_SETTING_PATH = "config/db.setting";
/*     */   private static final String DEFAULT_DB_SETTING_PATH2 = "db.setting";
/*     */   protected static boolean caseInsensitive = true;
/*     */   protected static boolean returnGeneratedKey = true;
/*  37 */   private static String dbSettingPath = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCaseInsensitive(boolean isCaseInsensitive) {
/*  46 */     caseInsensitive = isCaseInsensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setReturnGeneratedKey(boolean isReturnGeneratedKey) {
/*  57 */     returnGeneratedKey = isReturnGeneratedKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setDbSettingPath(String customDbSettingPath) {
/*  67 */     dbSettingPath = customDbSettingPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Setting createDbSetting() {
/*     */     Setting setting;
/*  78 */     if (null != dbSettingPath) {
/*     */       
/*     */       try {
/*  81 */         setting = new Setting(dbSettingPath, false);
/*  82 */       } catch (NoResourceException e3) {
/*  83 */         throw new NoResourceException("Customize db setting file [{}] not found !", new Object[] { dbSettingPath });
/*     */       } 
/*     */     } else {
/*     */       try {
/*  87 */         setting = new Setting("config/db.setting", true);
/*  88 */       } catch (NoResourceException e) {
/*     */         
/*     */         try {
/*  91 */           setting = new Setting("db.setting", true);
/*  92 */         } catch (NoResourceException e2) {
/*  93 */           throw new NoResourceException("Default db setting [{}] or [{}] in classpath not found !", new Object[] { "config/db.setting", "db.setting" });
/*     */         } 
/*     */       } 
/*     */     } 
/*  97 */     return setting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setShowSql(boolean isShowSql, boolean isFormatSql, boolean isShowParams, Level level) {
/* 109 */     SqlLog.INSTANCE.init(isShowSql, isFormatSql, isShowParams, level);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\GlobalDbConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */