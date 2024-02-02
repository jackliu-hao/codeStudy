/*     */ package cn.hutool.db.ds;
/*     */ 
/*     */ import cn.hutool.db.ds.bee.BeeDSFactory;
/*     */ import cn.hutool.db.ds.c3p0.C3p0DSFactory;
/*     */ import cn.hutool.db.ds.dbcp.DbcpDSFactory;
/*     */ import cn.hutool.db.ds.druid.DruidDSFactory;
/*     */ import cn.hutool.db.ds.hikari.HikariDSFactory;
/*     */ import cn.hutool.db.ds.pooled.PooledDSFactory;
/*     */ import cn.hutool.db.ds.tomcat.TomcatDSFactory;
/*     */ import cn.hutool.log.Log;
/*     */ import cn.hutool.log.LogFactory;
/*     */ import cn.hutool.setting.Setting;
/*     */ import java.io.Closeable;
/*     */ import java.io.Serializable;
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
/*     */ public abstract class DSFactory
/*     */   implements Closeable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -8789780234095234765L;
/*  30 */   private static final Log log = LogFactory.get();
/*     */ 
/*     */   
/*  33 */   public static final String[] KEY_CONN_PROPS = new String[] { "remarks", "useInformationSchema" };
/*     */ 
/*     */   
/*  36 */   public static final String[] KEY_ALIAS_URL = new String[] { "url", "jdbcUrl" };
/*     */   
/*  38 */   public static final String[] KEY_ALIAS_DRIVER = new String[] { "driver", "driverClassName" };
/*     */   
/*  40 */   public static final String[] KEY_ALIAS_USER = new String[] { "user", "username" };
/*     */   
/*  42 */   public static final String[] KEY_ALIAS_PASSWORD = new String[] { "pass", "password" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String dataSourceName;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DSFactory(String dataSourceName) {
/*  53 */     this.dataSourceName = dataSourceName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataSource getDataSource() {
/*  62 */     return getDataSource("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract DataSource getDataSource(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  78 */     close("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void close(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void destroy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSource get() {
/* 101 */     return get(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSource get(String group) {
/* 111 */     return GlobalDSFactory.get().getDataSource(group);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DSFactory setCurrentDSFactory(DSFactory dsFactory) {
/* 128 */     return GlobalDSFactory.set(dsFactory);
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
/*     */   public static DSFactory create(Setting setting) {
/* 140 */     DSFactory dsFactory = doCreate(setting);
/* 141 */     log.debug("Use [{}] DataSource As Default", new Object[] { dsFactory.dataSourceName });
/* 142 */     return dsFactory;
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
/*     */   
/*     */   private static DSFactory doCreate(Setting setting) {
/*     */     try {
/* 156 */       return (DSFactory)new HikariDSFactory(setting);
/* 157 */     } catch (NoClassDefFoundError noClassDefFoundError) {
/*     */ 
/*     */       
/*     */       try {
/* 161 */         return (DSFactory)new DruidDSFactory(setting);
/* 162 */       } catch (NoClassDefFoundError noClassDefFoundError1) {
/*     */ 
/*     */         
/*     */         try {
/* 166 */           return (DSFactory)new TomcatDSFactory(setting);
/* 167 */         } catch (NoClassDefFoundError noClassDefFoundError2) {
/*     */ 
/*     */           
/*     */           try {
/*     */ 
/*     */             
/* 173 */             return (DSFactory)new BeeDSFactory(setting);
/* 174 */           } catch (NoClassDefFoundError noClassDefFoundError3) {
/*     */ 
/*     */             
/*     */             try {
/* 178 */               return (DSFactory)new DbcpDSFactory(setting);
/* 179 */             } catch (NoClassDefFoundError noClassDefFoundError4) {
/*     */ 
/*     */               
/*     */               try {
/* 183 */                 return (DSFactory)new C3p0DSFactory(setting);
/* 184 */               } catch (NoClassDefFoundError noClassDefFoundError5) {
/*     */ 
/*     */                 
/* 187 */                 return (DSFactory)new PooledDSFactory(setting);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\DSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */