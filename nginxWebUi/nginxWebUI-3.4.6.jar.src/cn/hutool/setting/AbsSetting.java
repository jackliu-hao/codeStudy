/*     */ package cn.hutool.setting;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.bean.copier.CopyOptions;
/*     */ import cn.hutool.core.bean.copier.ValueProvider;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.getter.OptNullBasicTypeFromStringGetter;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.Log;
/*     */ import cn.hutool.log.LogFactory;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbsSetting
/*     */   implements OptNullBasicTypeFromStringGetter<String>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 6200156302595905863L;
/*  24 */   private static final Log log = LogFactory.get();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_DELIMITER = ",";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_GROUP = "";
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStr(String key, String defaultValue) {
/*  37 */     return getStr(key, "", defaultValue);
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
/*     */   public String getStr(String key, String group, String defaultValue) {
/*  49 */     String value = getByGroup(key, group);
/*  50 */     return (String)ObjectUtil.defaultIfNull(value, defaultValue);
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
/*     */   public String getStrNotEmpty(String key, String group, String defaultValue) {
/*  63 */     String value = getByGroup(key, group);
/*  64 */     return (String)ObjectUtil.defaultIfEmpty(value, defaultValue);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getWithLog(String key) {
/*  85 */     String value = getStr(key);
/*  86 */     if (value == null) {
/*  87 */       log.debug("No key define for [{}]!", new Object[] { key });
/*     */     }
/*  89 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getByGroupWithLog(String key, String group) {
/* 100 */     String value = getByGroup(key, group);
/* 101 */     if (value == null) {
/* 102 */       log.debug("No key define for [{}] of group [{}] !", new Object[] { key, group });
/*     */     }
/* 104 */     return value;
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
/*     */   public String[] getStrings(String key) {
/* 116 */     return getStrings(key, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getStringsWithDefault(String key, String[] defaultValue) {
/* 127 */     String[] value = getStrings(key, (String)null);
/* 128 */     if (null == value) {
/* 129 */       value = defaultValue;
/*     */     }
/*     */     
/* 132 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getStrings(String key, String group) {
/* 143 */     return getStrings(key, group, ",");
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
/*     */   public String[] getStrings(String key, String group, String delimiter) {
/* 155 */     String value = getByGroup(key, group);
/* 156 */     if (StrUtil.isBlank(value)) {
/* 157 */       return null;
/*     */     }
/* 159 */     return StrUtil.splitToArray(value, delimiter);
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
/*     */   public Integer getInt(String key, String group) {
/* 172 */     return getInt(key, group, (Integer)null);
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
/*     */   public Integer getInt(String key, String group, Integer defaultValue) {
/* 184 */     return Convert.toInt(getByGroup(key, group), defaultValue);
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
/*     */   public Boolean getBool(String key, String group) {
/* 197 */     return getBool(key, group, (Boolean)null);
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
/*     */   public Boolean getBool(String key, String group, Boolean defaultValue) {
/* 209 */     return Convert.toBool(getByGroup(key, group), defaultValue);
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
/*     */   public Long getLong(String key, String group) {
/* 222 */     return getLong(key, group, (Long)null);
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
/*     */   public Long getLong(String key, String group, Long defaultValue) {
/* 234 */     return Convert.toLong(getByGroup(key, group), defaultValue);
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
/*     */   public Character getChar(String key, String group) {
/* 247 */     String value = getByGroup(key, group);
/* 248 */     if (StrUtil.isBlank(value)) {
/* 249 */       return null;
/*     */     }
/* 251 */     return Character.valueOf(value.charAt(0));
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
/*     */   public Double getDouble(String key, String group) {
/* 264 */     return getDouble(key, group, (Double)null);
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
/*     */   public Double getDouble(String key, String group, Double defaultValue) {
/* 276 */     return Convert.toDouble(getByGroup(key, group), defaultValue);
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
/*     */   public <T> T toBean(final String group, T bean) {
/* 289 */     return (T)BeanUtil.fillBean(bean, new ValueProvider<String>()
/*     */         {
/*     */           public Object value(String key, Type valueType)
/*     */           {
/* 293 */             return AbsSetting.this.getByGroup(key, group);
/*     */           }
/*     */ 
/*     */           
/*     */           public boolean containsKey(String key) {
/* 298 */             return (null != AbsSetting.this.getByGroup(key, group));
/*     */           }
/* 300 */         }CopyOptions.create());
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
/*     */   public <T> T toBean(String group, Class<T> beanClass) {
/* 314 */     return toBean(group, (T)ReflectUtil.newInstanceIfPossible(beanClass));
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
/*     */   public <T> T toBean(T bean) {
/* 326 */     return toBean((String)null, bean);
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
/*     */   public <T> T toBean(Class<T> beanClass) {
/* 339 */     return toBean((String)null, beanClass);
/*     */   }
/*     */   
/*     */   public abstract String getByGroup(String paramString1, String paramString2);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\AbsSetting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */