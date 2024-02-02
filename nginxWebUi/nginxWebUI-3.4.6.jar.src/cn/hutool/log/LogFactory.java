/*     */ package cn.hutool.log;
/*     */ 
/*     */ import cn.hutool.core.io.resource.ResourceUtil;
/*     */ import cn.hutool.core.lang.caller.CallerUtil;
/*     */ import cn.hutool.core.util.ServiceLoaderUtil;
/*     */ import cn.hutool.log.dialect.console.ConsoleLogFactory;
/*     */ import cn.hutool.log.dialect.jdk.JdkLogFactory;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public abstract class LogFactory
/*     */ {
/*     */   protected String name;
/*     */   private final Map<Object, Log> logCache;
/*     */   
/*     */   public LogFactory(String name) {
/*  35 */     this.name = name;
/*  36 */     this.logCache = new ConcurrentHashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  46 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Log getLog(String name) {
/*  56 */     return this.logCache.computeIfAbsent(name, o -> createLog((String)o));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Log getLog(Class<?> clazz) {
/*  66 */     return this.logCache.computeIfAbsent(clazz, o -> createLog((Class)o));
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
/*     */   protected void checkLogExist(Class<?> logClassName) {}
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
/*     */   public static LogFactory getCurrentLogFactory() {
/* 102 */     return GlobalLogFactory.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LogFactory setCurrentLogFactory(Class<? extends LogFactory> logFactoryClass) {
/* 112 */     return GlobalLogFactory.set(logFactoryClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LogFactory setCurrentLogFactory(LogFactory logFactory) {
/* 122 */     return GlobalLogFactory.set(logFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Log get(String name) {
/* 132 */     return getCurrentLogFactory().getLog(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Log get(Class<?> clazz) {
/* 142 */     return getCurrentLogFactory().getLog(clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Log get() {
/* 149 */     return get(CallerUtil.getCallerCaller());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LogFactory create() {
/* 160 */     LogFactory factory = doCreate();
/* 161 */     factory.getLog(LogFactory.class).debug("Use [{}] Logger As Default.", new Object[] { factory.name });
/* 162 */     return factory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static LogFactory doCreate() {
/* 173 */     LogFactory factory = (LogFactory)ServiceLoaderUtil.loadFirstAvailable(LogFactory.class);
/* 174 */     if (null != factory) {
/* 175 */       return factory;
/*     */     }
/*     */ 
/*     */     
/* 179 */     URL url = ResourceUtil.getResource("logging.properties");
/* 180 */     return (null != url) ? (LogFactory)new JdkLogFactory() : (LogFactory)new ConsoleLogFactory();
/*     */   }
/*     */   
/*     */   public abstract Log createLog(String paramString);
/*     */   
/*     */   public abstract Log createLog(Class<?> paramClass);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\LogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */