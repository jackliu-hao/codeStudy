/*     */ package org.noear.solon;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.noear.solon.core.JarClassLoader;
/*     */ import org.noear.solon.core.NvMap;
/*     */ import org.noear.solon.core.PluginEntity;
/*     */ import org.noear.solon.core.Props;
/*     */ import org.noear.solon.core.util.PluginUtil;
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
/*     */ public final class SolonProps
/*     */   extends Props
/*     */ {
/*     */   private NvMap args;
/*     */   private Class<?> source;
/*     */   private URL sourceLocation;
/*  33 */   private final List<PluginEntity> plugs = new ArrayList<>();
/*     */   
/*     */   private boolean isDebugMode;
/*     */   
/*     */   private boolean isDriftMode;
/*     */   private boolean isFilesMode;
/*     */   private boolean isWhiteMode;
/*     */   private boolean isSetupMode;
/*     */   private boolean isAloneMode;
/*     */   private String env;
/*     */   private Locale locale;
/*     */   private String extend;
/*     */   private String extendFilter;
/*     */   private String appName;
/*     */   private String appGroup;
/*     */   private String appNamespace;
/*     */   private String appTitle;
/*     */   private int serverPort;
/*     */   private String serverHost;
/*     */   
/*     */   public SolonProps() {
/*  54 */     super(System.getProperties());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SolonProps load(Class<?> source, NvMap args) throws Exception {
/*  64 */     this.args = args;
/*     */     
/*  66 */     this.source = source;
/*     */     
/*  68 */     this.sourceLocation = source.getProtectionDomain().getCodeSource().getLocation();
/*     */ 
/*     */ 
/*     */     
/*  72 */     this.args.forEach((k, v) -> {
/*     */           if (k.contains(".")) {
/*     */             System.setProperty(k, v);
/*     */           }
/*     */         });
/*     */ 
/*     */     
/*  79 */     Properties sysPropOrg = new Properties();
/*  80 */     System.getProperties().forEach((k, v) -> sysPropOrg.put(k, v));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  85 */     loadInit(Utils.getResource("application.properties"), sysPropOrg);
/*     */     
/*  87 */     loadInit(Utils.getResource("application.yml"), sysPropOrg);
/*  88 */     loadInit(Utils.getResource("app.properties"), sysPropOrg);
/*  89 */     loadInit(Utils.getResource("app.yml"), sysPropOrg);
/*     */ 
/*     */     
/*  92 */     loadEnv("solon.");
/*     */ 
/*     */     
/*  95 */     this.env = getArg("env");
/*     */     
/*  97 */     if (Utils.isEmpty(this.env))
/*     */     {
/*  99 */       this.env = getArg("profiles.active");
/*     */     }
/*     */     
/* 102 */     if (Utils.isNotEmpty(this.env)) {
/*     */       
/* 104 */       loadInit(Utils.getResource("application-" + this.env + ".properties"), sysPropOrg);
/*     */       
/* 106 */       loadInit(Utils.getResource("application-" + this.env + ".yml"), sysPropOrg);
/* 107 */       loadInit(Utils.getResource("app-" + this.env + ".properties"), sysPropOrg);
/* 108 */       loadInit(Utils.getResource("app-" + this.env + ".yml"), sysPropOrg);
/*     */     } 
/*     */ 
/*     */     
/* 112 */     String extConfig = getArg("extend.config");
/* 113 */     if (Utils.isNotEmpty(extConfig)) {
/* 114 */       loadInit((new File(extConfig)).toURI().toURL(), sysPropOrg);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     this
/*     */ 
/*     */       
/* 124 */       .isFilesMode = (!this.sourceLocation.getPath().endsWith(".jar") && !this.sourceLocation.getPath().contains(".jar!/") && !this.sourceLocation.getPath().endsWith(".zip") && !this.sourceLocation.getPath().contains(".zip!/"));
/*     */ 
/*     */     
/* 127 */     this.isDebugMode = "1".equals(getArg("debug"));
/*     */     
/* 129 */     this.isSetupMode = "1".equals(getArg("setup"));
/*     */     
/* 131 */     this.isWhiteMode = "1".equals(getArg("white"));
/*     */     
/* 133 */     this.isDriftMode = "1".equals(getArg("drift"));
/*     */     
/* 135 */     this.isAloneMode = "1".equals(getArg("alone"));
/*     */ 
/*     */     
/* 138 */     if (isDebugMode()) {
/* 139 */       System.setProperty("debug", "1");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 144 */     this.extend = getArg("extend");
/* 145 */     this.extendFilter = getArg("extend.filter");
/*     */ 
/*     */ 
/*     */     
/* 149 */     String localeStr = getArg("locale");
/* 150 */     if (Utils.isNotEmpty(localeStr)) {
/* 151 */       this.locale = Utils.toLocale(localeStr);
/* 152 */       Locale.setDefault(this.locale);
/*     */     } else {
/* 154 */       this.locale = Locale.getDefault();
/*     */     } 
/*     */ 
/*     */     
/* 158 */     this.appName = getArg("app.name");
/* 159 */     this.appGroup = getArg("app.group");
/* 160 */     this.appNamespace = getArg("app.namespace");
/* 161 */     this.appTitle = getArg("app.title");
/*     */     
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getArg(String name) {
/* 173 */     return getArg(name, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getArg(String name, String def) {
/* 184 */     String tmp = (String)this.args.get(name);
/* 185 */     if (Utils.isEmpty(tmp))
/*     */     {
/* 187 */       tmp = get("solon." + name);
/*     */     }
/*     */     
/* 190 */     if (Utils.isEmpty(tmp)) {
/* 191 */       return def;
/*     */     }
/* 193 */     return tmp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SolonProps loadEnv(String keyStarts) {
/* 203 */     System.getenv().forEach((k, v) -> {
/*     */           if (k.startsWith(keyStarts)) {
/*     */             setProperty(k, v);
/*     */             
/*     */             System.setProperty(k, v);
/*     */           } 
/*     */         });
/* 210 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void loadAdd(String url) {
/* 220 */     loadAdd(Utils.getResource(url));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void loadAdd(Properties props) {
/* 230 */     loadAddDo(props, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void loadInit(URL url, Properties sysPropOrg) {
/* 240 */     if (url != null) {
/* 241 */       Properties props = Utils.loadProperties(url);
/*     */       
/* 243 */       if (props == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 248 */       for (Map.Entry<Object, Object> kv : sysPropOrg.entrySet()) {
/* 249 */         if (kv.getKey() instanceof String) {
/* 250 */           String key = (String)kv.getKey();
/*     */           
/* 252 */           if (Utils.isEmpty(key)) {
/*     */             continue;
/*     */           }
/*     */           
/* 256 */           if (props.containsKey(key)) {
/* 257 */             props.put(key, kv.getValue());
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 262 */       loadAdd(props);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void plugsScan(List<ClassLoader> classLoaders) {
/* 271 */     for (ClassLoader classLoader : classLoaders)
/*     */     {
/* 273 */       PluginUtil.scanPlugins(classLoader, null, this.plugs::add);
/*     */     }
/*     */ 
/*     */     
/* 277 */     PluginUtil.findPlugins((ClassLoader)JarClassLoader.global(), (Properties)this, this.plugs::add);
/*     */ 
/*     */     
/* 280 */     plugsSort();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> source() {
/* 288 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL sourceLocation() {
/* 295 */     return this.sourceLocation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NvMap argx() {
/* 302 */     return this.args;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<PluginEntity> plugs() {
/* 309 */     return this.plugs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void plugsSort() {
/* 316 */     if (this.plugs.size() > 0)
/*     */     {
/*     */       
/* 319 */       this.plugs.sort(Comparator.<PluginEntity>comparingInt(PluginEntity::getPriority).reversed());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int serverPort() {
/* 330 */     if (this.serverPort == 0) {
/* 331 */       this.serverPort = getInt("server.port", 8080);
/*     */     }
/*     */     
/* 334 */     return this.serverPort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String serverHost() {
/* 342 */     if (this.serverHost == null) {
/* 343 */       this.serverHost = get("server.host", "");
/*     */     }
/*     */     
/* 346 */     return this.serverHost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String env() {
/* 354 */     return this.env;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Locale locale() {
/* 361 */     return this.locale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String extend() {
/* 368 */     return this.extend;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String extendFilter() {
/* 375 */     return this.extendFilter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String appName() {
/* 382 */     return this.appName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String appGroup() {
/* 389 */     return this.appGroup;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String appNamespace() {
/* 396 */     return this.appNamespace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String appTitle() {
/* 403 */     return this.appTitle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String version() {
/* 411 */     return "1.9.4";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugMode() {
/* 418 */     return this.isDebugMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSetupMode() {
/* 425 */     return this.isSetupMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFilesMode() {
/* 432 */     return this.isFilesMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void isFilesMode(boolean isFilesMode) {
/* 439 */     this.isFilesMode = isFilesMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDriftMode() {
/* 446 */     return this.isDriftMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void isDriftMode(boolean isDriftMode) {
/* 453 */     this.isDriftMode = isDriftMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAloneMode() {
/* 460 */     return this.isAloneMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void isAloneMode(boolean isAloneMode) {
/* 467 */     this.isAloneMode = isAloneMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWhiteMode() {
/* 474 */     return this.isWhiteMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void isWhiteMode(boolean isWhiteMode) {
/* 481 */     this.isWhiteMode = isWhiteMode;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\SolonProps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */