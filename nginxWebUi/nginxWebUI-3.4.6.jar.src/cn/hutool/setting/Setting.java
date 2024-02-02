/*     */ package cn.hutool.setting;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.io.FileUtil;
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
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.log.StaticLog;
/*     */ import cn.hutool.setting.dialect.Props;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.WatchEvent;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.function.Consumer;
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
/*     */ public class Setting
/*     */   extends AbsSetting
/*     */   implements Map<String, String>
/*     */ {
/*     */   private static final long serialVersionUID = 3618305164959883393L;
/*  54 */   public static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String EXT_NAME = "setting";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Setting create() {
/*  67 */     return new Setting();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   private final GroupedMap groupedMap = new GroupedMap();
/*     */ 
/*     */ 
/*     */   
/*     */   protected Charset charset;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isUseVariable;
/*     */ 
/*     */ 
/*     */   
/*     */   protected Resource resource;
/*     */ 
/*     */ 
/*     */   
/*     */   private SettingLoader settingLoader;
/*     */ 
/*     */   
/*     */   private WatchMonitor watchMonitor;
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting() {
/*  97 */     this.charset = DEFAULT_CHARSET;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting(String path) {
/* 106 */     this(path, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting(String path, boolean isUseVariable) {
/* 116 */     this(path, DEFAULT_CHARSET, isUseVariable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting(String path, Charset charset, boolean isUseVariable) {
/* 127 */     Assert.notBlank(path, "Blank setting path !", new Object[0]);
/* 128 */     init(ResourceUtil.getResourceObj(path), charset, isUseVariable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting(File configFile, Charset charset, boolean isUseVariable) {
/* 139 */     Assert.notNull(configFile, "Null setting file define!", new Object[0]);
/* 140 */     init((Resource)new FileResource(configFile), charset, isUseVariable);
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
/*     */   public Setting(String path, Class<?> clazz, Charset charset, boolean isUseVariable) {
/* 152 */     Assert.notBlank(path, "Blank setting path !", new Object[0]);
/* 153 */     init((Resource)new ClassPathResource(path, clazz), charset, isUseVariable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting(URL url, Charset charset, boolean isUseVariable) {
/* 164 */     Assert.notNull(url, "Null setting url define!", new Object[0]);
/* 165 */     init((Resource)new UrlResource(url), charset, isUseVariable);
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
/*     */   public Setting(Resource resource, Charset charset, boolean isUseVariable) {
/* 177 */     init(resource, charset, isUseVariable);
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
/*     */   public boolean init(Resource resource, Charset charset, boolean isUseVariable) {
/* 190 */     Assert.notNull(resource, "Setting resource must be not null!", new Object[0]);
/* 191 */     this.resource = resource;
/* 192 */     this.charset = charset;
/* 193 */     this.isUseVariable = isUseVariable;
/*     */     
/* 195 */     return load();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean load() {
/* 204 */     if (null == this.settingLoader) {
/* 205 */       this.settingLoader = new SettingLoader(this.groupedMap, this.charset, this.isUseVariable);
/*     */     }
/* 207 */     return this.settingLoader.load(this.resource);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void autoLoad(boolean autoReload) {
/* 216 */     autoLoad(autoReload, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void autoLoad(boolean autoReload, final Consumer<Boolean> callback) {
/* 226 */     if (autoReload) {
/* 227 */       Assert.notNull(this.resource, "Setting resource must be not null !", new Object[0]);
/* 228 */       if (null != this.watchMonitor)
/*     */       {
/* 230 */         this.watchMonitor.close();
/*     */       }
/* 232 */       this.watchMonitor = WatchUtil.createModify(this.resource.getUrl(), (Watcher)new SimpleWatcher()
/*     */           {
/*     */             public void onModify(WatchEvent<?> event, Path currentPath) {
/* 235 */               boolean success = Setting.this.load();
/*     */               
/* 237 */               if (callback != null) {
/* 238 */                 callback.accept(Boolean.valueOf(success));
/*     */               }
/*     */             }
/*     */           });
/* 242 */       this.watchMonitor.start();
/* 243 */       StaticLog.debug("Auto load for [{}] listenning...", new Object[] { this.resource.getUrl() });
/*     */     } else {
/* 245 */       IoUtil.close((Closeable)this.watchMonitor);
/* 246 */       this.watchMonitor = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL getSettingUrl() {
/* 257 */     return (null == this.resource) ? null : this.resource.getUrl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSettingPath() {
/* 266 */     URL settingUrl = getSettingUrl();
/* 267 */     return (null == settingUrl) ? null : settingUrl.getPath();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 277 */     return this.groupedMap.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getByGroup(String key, String group) {
/* 282 */     return this.groupedMap.get(group, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getAndRemove(String... keys) {
/* 293 */     Object value = null;
/* 294 */     for (String key : keys) {
/* 295 */       value = remove(key);
/* 296 */       if (null != value) {
/*     */         break;
/*     */       }
/*     */     } 
/* 300 */     return value;
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
/* 311 */     String value = null;
/* 312 */     for (String key : keys) {
/* 313 */       value = remove(key);
/* 314 */       if (null != value) {
/*     */         break;
/*     */       }
/*     */     } 
/* 318 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getMap(String group) {
/* 328 */     LinkedHashMap<String, String> map = this.groupedMap.get(group);
/* 329 */     return (null != map) ? map : new LinkedHashMap<>(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting getSetting(String group) {
/* 339 */     Setting setting = new Setting();
/* 340 */     setting.putAll(getMap(group));
/* 341 */     return setting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties getProperties(String group) {
/* 351 */     Properties properties = new Properties();
/* 352 */     properties.putAll(getMap(group));
/* 353 */     return properties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props getProps(String group) {
/* 364 */     Props props = new Props();
/* 365 */     props.putAll(getMap(group));
/* 366 */     return props;
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
/*     */   public void store() {
/* 378 */     URL resourceUrl = getSettingUrl();
/* 379 */     Assert.notNull(resourceUrl, "Setting path must be not null !", new Object[0]);
/* 380 */     store(FileUtil.file(resourceUrl));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void store(String absolutePath) {
/* 390 */     store(FileUtil.touch(absolutePath));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void store(File file) {
/* 401 */     if (null == this.settingLoader) {
/* 402 */       this.settingLoader = new SettingLoader(this.groupedMap, this.charset, this.isUseVariable);
/*     */     }
/* 404 */     this.settingLoader.store(file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties toProperties() {
/* 413 */     Properties properties = new Properties();
/*     */     
/* 415 */     for (Map.Entry<String, LinkedHashMap<String, String>> groupEntry : this.groupedMap.entrySet()) {
/* 416 */       String group = groupEntry.getKey();
/* 417 */       for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)((LinkedHashMap)groupEntry.getValue()).entrySet()) {
/* 418 */         properties.setProperty(StrUtil.isEmpty(group) ? entry.getKey() : (group + '.' + (String)entry.getKey()), entry.getValue());
/*     */       }
/*     */     } 
/* 421 */     return properties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupedMap getGroupedMap() {
/* 431 */     return this.groupedMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getGroups() {
/* 440 */     return CollUtil.newArrayList(this.groupedMap.keySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting setVarRegex(String regex) {
/* 451 */     if (null == this.settingLoader) {
/* 452 */       throw new NullPointerException("SettingLoader is null !");
/*     */     }
/* 454 */     this.settingLoader.setVarRegex(regex);
/* 455 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting setCharset(Charset charset) {
/* 466 */     this.charset = charset;
/* 467 */     return this;
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
/*     */   public boolean isEmpty(String group) {
/* 479 */     return this.groupedMap.isEmpty(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(String group, String key) {
/* 490 */     return this.groupedMap.containsKey(group, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsValue(String group, String value) {
/* 501 */     return this.groupedMap.containsValue(group, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(String group, String key) {
/* 512 */     return this.groupedMap.get(group, key);
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
/*     */   public String putByGroup(String key, String group, String value) {
/* 524 */     return this.groupedMap.put(group, key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String remove(String group, Object key) {
/* 535 */     return this.groupedMap.remove(group, Convert.toStr(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting putAll(String group, Map<? extends String, ? extends String> m) {
/* 546 */     this.groupedMap.putAll(group, m);
/* 547 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting addSetting(Setting setting) {
/* 558 */     for (Map.Entry<String, LinkedHashMap<String, String>> e : setting.getGroupedMap().entrySet()) {
/* 559 */       putAll(e.getKey(), e.getValue());
/*     */     }
/* 561 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Setting clear(String group) {
/* 571 */     this.groupedMap.clear(group);
/* 572 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> keySet(String group) {
/* 582 */     return this.groupedMap.keySet(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> values(String group) {
/* 592 */     return this.groupedMap.values(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, String>> entrySet(String group) {
/* 602 */     return this.groupedMap.entrySet(group);
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
/*     */   public Setting set(String key, String value) {
/* 614 */     put(key, value);
/* 615 */     return this;
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
/*     */   public Setting setByGroup(String key, String group, String value) {
/* 629 */     putByGroup(key, group, value);
/* 630 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 636 */     return this.groupedMap.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 647 */     return this.groupedMap.containsKey("", Convert.toStr(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 658 */     return this.groupedMap.containsValue("", Convert.toStr(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(Object key) {
/* 669 */     return this.groupedMap.get("", Convert.toStr(key));
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
/*     */   public String put(String key, String value) {
/* 681 */     return this.groupedMap.put("", key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String remove(Object key) {
/* 692 */     return this.groupedMap.remove("", Convert.toStr(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends String> m) {
/* 703 */     this.groupedMap.putAll("", m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 711 */     this.groupedMap.clear("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 722 */     return this.groupedMap.keySet("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> values() {
/* 733 */     return this.groupedMap.values("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, String>> entrySet() {
/* 744 */     return this.groupedMap.entrySet("");
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 749 */     int prime = 31;
/* 750 */     int result = 1;
/* 751 */     result = 31 * result + ((this.charset == null) ? 0 : this.charset.hashCode());
/* 752 */     result = 31 * result + this.groupedMap.hashCode();
/* 753 */     result = 31 * result + (this.isUseVariable ? 1231 : 1237);
/* 754 */     result = 31 * result + ((this.resource == null) ? 0 : this.resource.hashCode());
/* 755 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 760 */     if (this == obj) {
/* 761 */       return true;
/*     */     }
/* 763 */     if (obj == null) {
/* 764 */       return false;
/*     */     }
/* 766 */     if (getClass() != obj.getClass()) {
/* 767 */       return false;
/*     */     }
/* 769 */     Setting other = (Setting)obj;
/* 770 */     if (this.charset == null) {
/* 771 */       if (other.charset != null) {
/* 772 */         return false;
/*     */       }
/* 774 */     } else if (false == this.charset.equals(other.charset)) {
/* 775 */       return false;
/*     */     } 
/* 777 */     if (false == this.groupedMap.equals(other.groupedMap)) {
/* 778 */       return false;
/*     */     }
/* 780 */     if (this.isUseVariable != other.isUseVariable) {
/* 781 */       return false;
/*     */     }
/* 783 */     if (this.resource == null) {
/* 784 */       return (other.resource == null);
/*     */     }
/* 786 */     return this.resource.equals(other.resource);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 792 */     return this.groupedMap.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\Setting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */