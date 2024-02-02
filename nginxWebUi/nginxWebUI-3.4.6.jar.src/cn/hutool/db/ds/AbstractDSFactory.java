/*     */ package cn.hutool.db.ds;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.DbUtil;
/*     */ import cn.hutool.db.GlobalDbConfig;
/*     */ import cn.hutool.db.dialect.DriverUtil;
/*     */ import cn.hutool.setting.Setting;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.sql.DataSource;
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
/*     */ public abstract class AbstractDSFactory
/*     */   extends DSFactory
/*     */ {
/*     */   private static final long serialVersionUID = -6407302276272379881L;
/*     */   private final Setting setting;
/*     */   private final Map<String, DataSourceWrapper> dsMap;
/*     */   
/*     */   public AbstractDSFactory(String dataSourceName, Class<? extends DataSource> dataSourceClass, Setting setting) {
/*  45 */     super(dataSourceName);
/*     */     
/*  47 */     Assert.notNull(dataSourceClass);
/*     */     
/*  49 */     if (null == setting) {
/*  50 */       setting = GlobalDbConfig.createDbSetting();
/*     */     }
/*     */ 
/*     */     
/*  54 */     DbUtil.setShowSqlGlobal(setting);
/*     */     
/*  56 */     this.setting = setting;
/*  57 */     this.dsMap = new ConcurrentHashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting getSetting() {
/*  67 */     return this.setting;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized DataSource getDataSource(String group) {
/*  72 */     if (group == null) {
/*  73 */       group = "";
/*     */     }
/*     */ 
/*     */     
/*  77 */     DataSourceWrapper existedDataSource = this.dsMap.get(group);
/*  78 */     if (existedDataSource != null) {
/*  79 */       return existedDataSource;
/*     */     }
/*     */     
/*  82 */     DataSourceWrapper ds = createDataSource(group);
/*     */     
/*  84 */     this.dsMap.put(group, ds);
/*  85 */     return ds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DataSourceWrapper createDataSource(String group) {
/*  95 */     if (group == null) {
/*  96 */       group = "";
/*     */     }
/*     */     
/*  99 */     Setting config = this.setting.getSetting(group);
/* 100 */     if (MapUtil.isEmpty((Map)config)) {
/* 101 */       throw new DbRuntimeException("No config for group: [{}]", new Object[] { group });
/*     */     }
/*     */ 
/*     */     
/* 105 */     String url = config.getAndRemoveStr(KEY_ALIAS_URL);
/* 106 */     if (StrUtil.isBlank(url)) {
/* 107 */       throw new DbRuntimeException("No JDBC URL for group: [{}]", new Object[] { group });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 112 */     DbUtil.removeShowSqlParams(config);
/*     */ 
/*     */     
/* 115 */     String driver = config.getAndRemoveStr(KEY_ALIAS_DRIVER);
/* 116 */     if (StrUtil.isBlank(driver)) {
/* 117 */       driver = DriverUtil.identifyDriver(url);
/*     */     }
/* 119 */     String user = config.getAndRemoveStr(KEY_ALIAS_USER);
/* 120 */     String pass = config.getAndRemoveStr(KEY_ALIAS_PASSWORD);
/*     */     
/* 122 */     return DataSourceWrapper.wrap(createDataSource(url, driver, user, pass, config), driver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract DataSource createDataSource(String paramString1, String paramString2, String paramString3, String paramString4, Setting paramSetting);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(String group) {
/* 139 */     if (group == null) {
/* 140 */       group = "";
/*     */     }
/*     */     
/* 143 */     DataSourceWrapper ds = this.dsMap.get(group);
/* 144 */     if (ds != null) {
/* 145 */       ds.close();
/*     */       
/* 147 */       this.dsMap.remove(group);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 153 */     if (MapUtil.isNotEmpty(this.dsMap)) {
/* 154 */       Collection<DataSourceWrapper> values = this.dsMap.values();
/* 155 */       for (DataSourceWrapper ds : values) {
/* 156 */         ds.close();
/*     */       }
/* 158 */       this.dsMap.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 164 */     int prime = 31;
/* 165 */     int result = 1;
/* 166 */     result = 31 * result + ((this.dataSourceName == null) ? 0 : this.dataSourceName.hashCode());
/* 167 */     result = 31 * result + ((this.setting == null) ? 0 : this.setting.hashCode());
/* 168 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 173 */     if (this == obj) {
/* 174 */       return true;
/*     */     }
/* 176 */     if (obj == null) {
/* 177 */       return false;
/*     */     }
/* 179 */     if (getClass() != obj.getClass()) {
/* 180 */       return false;
/*     */     }
/* 182 */     AbstractDSFactory other = (AbstractDSFactory)obj;
/* 183 */     if (this.dataSourceName == null) {
/* 184 */       if (other.dataSourceName != null) {
/* 185 */         return false;
/*     */       }
/* 187 */     } else if (!this.dataSourceName.equals(other.dataSourceName)) {
/* 188 */       return false;
/*     */     } 
/* 190 */     if (this.setting == null) {
/* 191 */       return (other.setting == null);
/*     */     }
/* 193 */     return this.setting.equals(other.setting);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\AbstractDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */