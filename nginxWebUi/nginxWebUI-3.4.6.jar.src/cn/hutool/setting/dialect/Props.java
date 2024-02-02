/*     */ package cn.hutool.setting.dialect;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.getter.BasicTypeGetter;
/*     */ import cn.hutool.core.getter.OptBasicTypeGetter;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.resource.ClassPathResource;
/*     */ import cn.hutool.core.io.resource.FileResource;
/*     */ import cn.hutool.core.io.resource.Resource;
/*     */ import cn.hutool.core.io.resource.ResourceUtil;
/*     */ import cn.hutool.core.io.resource.UrlResource;
/*     */ import cn.hutool.core.io.watch.SimpleWatcher;
/*     */ import cn.hutool.core.io.watch.WatchMonitor;
/*     */ import cn.hutool.core.io.watch.WatchUtil;
/*     */ import cn.hutool.core.io.watch.Watcher;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.StaticLog;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.WatchEvent;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Props
/*     */   extends Properties
/*     */   implements BasicTypeGetter<String>, OptBasicTypeGetter<String>
/*     */ {
/*     */   private static final long serialVersionUID = 1935981579709590740L;
/*     */   public static final String EXT_NAME = "properties";
/*     */   private Resource resource;
/*     */   private WatchMonitor watchMonitor;
/*     */   
/*     */   public static Props create() {
/*  59 */     return new Props();
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
/*  72 */   private transient Charset charset = CharsetUtil.CHARSET_ISO_8859_1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Props getProp(String resource) {
/*  82 */     return new Props(resource);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Props getProp(String resource, String charsetName) {
/*  93 */     return new Props(resource, charsetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Props getProp(String resource, Charset charset) {
/* 104 */     return new Props(resource, charset);
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
/*     */   public Props(String path) {
/* 121 */     this(path, CharsetUtil.CHARSET_ISO_8859_1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(String path, String charsetName) {
/* 131 */     this(path, CharsetUtil.charset(charsetName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(String path, Charset charset) {
/* 141 */     Assert.notBlank(path, "Blank properties file path !", new Object[0]);
/* 142 */     if (null != charset) {
/* 143 */       this.charset = charset;
/*     */     }
/* 145 */     load(ResourceUtil.getResourceObj(path));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(File propertiesFile) {
/* 154 */     this(propertiesFile, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(File propertiesFile, String charsetName) {
/* 164 */     this(propertiesFile, Charset.forName(charsetName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(File propertiesFile, Charset charset) {
/* 174 */     Assert.notNull(propertiesFile, "Null properties file!", new Object[0]);
/* 175 */     this.charset = charset;
/* 176 */     load((Resource)new FileResource(propertiesFile));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(String path, Class<?> clazz) {
/* 186 */     this(path, clazz, "ISO-8859-1");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(String path, Class<?> clazz, String charsetName) {
/* 197 */     this(path, clazz, CharsetUtil.charset(charsetName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(String path, Class<?> clazz, Charset charset) {
/* 208 */     Assert.notBlank(path, "Blank properties file path !", new Object[0]);
/* 209 */     if (null != charset) {
/* 210 */       this.charset = charset;
/*     */     }
/* 212 */     load((Resource)new ClassPathResource(path, clazz));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(URL propertiesUrl) {
/* 221 */     this(propertiesUrl, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(URL propertiesUrl, String charsetName) {
/* 231 */     this(propertiesUrl, CharsetUtil.charset(charsetName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(URL propertiesUrl, Charset charset) {
/* 241 */     Assert.notNull(propertiesUrl, "Null properties URL !", new Object[0]);
/* 242 */     if (null != charset) {
/* 243 */       this.charset = charset;
/*     */     }
/* 245 */     load(propertiesUrl);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props(Properties properties) {
/* 254 */     if (MapUtil.isNotEmpty(properties)) {
/* 255 */       putAll(properties);
/*     */     }
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
/*     */   public void load(URL url) {
/* 268 */     load((Resource)new UrlResource(url));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load(Resource resource) {
/* 277 */     Assert.notNull(resource, "Props resource must be not null!", new Object[0]);
/* 278 */     this.resource = resource;
/*     */     
/* 280 */     try (BufferedReader reader = resource.getReader(this.charset)) {
/* 281 */       load(reader);
/* 282 */     } catch (IOException e) {
/* 283 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load() {
/* 291 */     load(this.resource);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void autoLoad(boolean autoReload) {
/* 300 */     if (autoReload) {
/* 301 */       Assert.notNull(this.resource, "Properties resource must be not null!", new Object[0]);
/* 302 */       if (null != this.watchMonitor)
/*     */       {
/* 304 */         this.watchMonitor.close();
/*     */       }
/* 306 */       this.watchMonitor = WatchUtil.createModify(this.resource.getUrl(), (Watcher)new SimpleWatcher()
/*     */           {
/*     */             public void onModify(WatchEvent<?> event, Path currentPath) {
/* 309 */               Props.this.load();
/*     */             }
/*     */           });
/* 312 */       this.watchMonitor.start();
/*     */     } else {
/* 314 */       IoUtil.close((Closeable)this.watchMonitor);
/* 315 */       this.watchMonitor = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getObj(String key, Object defaultValue) {
/* 322 */     return getStr(key, (null == defaultValue) ? null : defaultValue.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getObj(String key) {
/* 327 */     return getObj(key, (Object)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStr(String key, String defaultValue) {
/* 332 */     return getProperty(key, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStr(String key) {
/* 337 */     return getProperty(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getInt(String key, Integer defaultValue) {
/* 342 */     return Convert.toInt(getStr(key), defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getInt(String key) {
/* 347 */     return getInt(key, (Integer)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean getBool(String key, Boolean defaultValue) {
/* 352 */     return Convert.toBool(getStr(key), defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean getBool(String key) {
/* 357 */     return getBool(key, (Boolean)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getLong(String key, Long defaultValue) {
/* 362 */     return Convert.toLong(getStr(key), defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getLong(String key) {
/* 367 */     return getLong(key, (Long)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Character getChar(String key, Character defaultValue) {
/* 372 */     String value = getStr(key);
/* 373 */     if (StrUtil.isBlank(value)) {
/* 374 */       return defaultValue;
/*     */     }
/* 376 */     return Character.valueOf(value.charAt(0));
/*     */   }
/*     */ 
/*     */   
/*     */   public Character getChar(String key) {
/* 381 */     return getChar(key, (Character)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Float getFloat(String key) {
/* 386 */     return getFloat(key, (Float)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Float getFloat(String key, Float defaultValue) {
/* 391 */     return Convert.toFloat(getStr(key), defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Double getDouble(String key, Double defaultValue) throws NumberFormatException {
/* 396 */     return Convert.toDouble(getStr(key), defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Double getDouble(String key) throws NumberFormatException {
/* 401 */     return getDouble(key, (Double)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getShort(String key, Short defaultValue) {
/* 406 */     return Convert.toShort(getStr(key), defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getShort(String key) {
/* 411 */     return getShort(key, (Short)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte getByte(String key, Byte defaultValue) {
/* 416 */     return Convert.toByte(getStr(key), defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte getByte(String key) {
/* 421 */     return getByte(key, (Byte)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
/* 426 */     String valueStr = getStr(key);
/* 427 */     if (StrUtil.isBlank(valueStr)) {
/* 428 */       return defaultValue;
/*     */     }
/*     */     
/*     */     try {
/* 432 */       return new BigDecimal(valueStr);
/* 433 */     } catch (Exception e) {
/* 434 */       return defaultValue;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal(String key) {
/* 440 */     return getBigDecimal(key, (BigDecimal)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getBigInteger(String key, BigInteger defaultValue) {
/* 445 */     String valueStr = getStr(key);
/* 446 */     if (StrUtil.isBlank(valueStr)) {
/* 447 */       return defaultValue;
/*     */     }
/*     */     
/*     */     try {
/* 451 */       return new BigInteger(valueStr);
/* 452 */     } catch (Exception e) {
/* 453 */       return defaultValue;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getBigInteger(String key) {
/* 459 */     return getBigInteger(key, (BigInteger)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public <E extends Enum<E>> E getEnum(Class<E> clazz, String key, E defaultValue) {
/* 464 */     return (E)Convert.toEnum(clazz, getStr(key), (Enum)defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) {
/* 469 */     return getEnum(clazz, key, (E)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getDate(String key, Date defaultValue) {
/* 474 */     return Convert.toDate(getStr(key), defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getDate(String key) {
/* 479 */     return getDate(key, (Date)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAndRemoveStr(String... keys) {
/* 490 */     Object value = null;
/* 491 */     for (String key : keys) {
/* 492 */       value = remove(key);
/* 493 */       if (null != value) {
/*     */         break;
/*     */       }
/*     */     } 
/* 497 */     return (String)value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties toProperties() {
/* 507 */     Properties properties = new Properties();
/* 508 */     properties.putAll(this);
/* 509 */     return properties;
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
/*     */   public <T> T toBean(Class<T> beanClass) {
/* 530 */     return toBean(beanClass, (String)null);
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
/*     */   
/*     */   public <T> T toBean(Class<T> beanClass, String prefix) {
/* 552 */     T bean = (T)ReflectUtil.newInstanceIfPossible(beanClass);
/* 553 */     return fillBean(bean, prefix);
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
/*     */   
/*     */   public <T> T fillBean(T bean, String prefix) {
/* 575 */     prefix = StrUtil.nullToEmpty(StrUtil.addSuffixIfNot(prefix, "."));
/*     */ 
/*     */     
/* 578 */     for (Map.Entry<Object, Object> entry : entrySet()) {
/* 579 */       String key = (String)entry.getKey();
/* 580 */       if (false == StrUtil.startWith(key, prefix)) {
/*     */         continue;
/*     */       }
/*     */       
/*     */       try {
/* 585 */         BeanUtil.setProperty(bean, StrUtil.subSuf(key, prefix.length()), entry.getValue());
/* 586 */       } catch (Exception e) {
/*     */         
/* 588 */         StaticLog.debug("Ignore property: [{}]", new Object[] { key });
/*     */       } 
/*     */     } 
/*     */     
/* 592 */     return bean;
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
/*     */   public void setProperty(String key, Object value) {
/* 606 */     setProperty(key, value.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void store(String absolutePath) throws IORuntimeException {
/* 616 */     Writer writer = null;
/*     */     try {
/* 618 */       writer = FileUtil.getWriter(absolutePath, this.charset, false);
/* 619 */       store(writer, (String)null);
/* 620 */     } catch (IOException e) {
/* 621 */       throw new IORuntimeException(e, "Store properties to [{}] error!", new Object[] { absolutePath });
/*     */     } finally {
/* 623 */       IoUtil.close(writer);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void store(String path, Class<?> clazz) {
/* 634 */     store(FileUtil.getAbsolutePath(path, clazz));
/*     */   }
/*     */   
/*     */   public Props() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\dialect\Props.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */