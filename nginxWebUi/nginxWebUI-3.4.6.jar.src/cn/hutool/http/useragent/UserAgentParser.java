/*     */ package cn.hutool.http.useragent;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ public class UserAgentParser
/*     */ {
/*     */   public static UserAgent parse(String userAgentString) {
/*  20 */     if (StrUtil.isBlank(userAgentString)) {
/*  21 */       return null;
/*     */     }
/*  23 */     UserAgent userAgent = new UserAgent();
/*     */ 
/*     */     
/*  26 */     Browser browser = parseBrowser(userAgentString);
/*  27 */     userAgent.setBrowser(browser);
/*  28 */     userAgent.setVersion(browser.getVersion(userAgentString));
/*     */ 
/*     */     
/*  31 */     Engine engine = parseEngine(userAgentString);
/*  32 */     userAgent.setEngine(engine);
/*  33 */     userAgent.setEngineVersion(engine.getVersion(userAgentString));
/*     */ 
/*     */     
/*  36 */     OS os = parseOS(userAgentString);
/*  37 */     userAgent.setOs(os);
/*  38 */     userAgent.setOsVersion(os.getVersion(userAgentString));
/*     */ 
/*     */     
/*  41 */     Platform platform = parsePlatform(userAgentString);
/*  42 */     userAgent.setPlatform(platform);
/*  43 */     userAgent.setMobile((platform.isMobile() || browser.isMobile()));
/*     */ 
/*     */     
/*  46 */     return userAgent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Browser parseBrowser(String userAgentString) {
/*  56 */     for (Browser browser : Browser.browers) {
/*  57 */       if (browser.isMatch(userAgentString)) {
/*  58 */         return browser;
/*     */       }
/*     */     } 
/*  61 */     return Browser.Unknown;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Engine parseEngine(String userAgentString) {
/*  71 */     for (Engine engine : Engine.engines) {
/*  72 */       if (engine.isMatch(userAgentString)) {
/*  73 */         return engine;
/*     */       }
/*     */     } 
/*  76 */     return Engine.Unknown;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static OS parseOS(String userAgentString) {
/*  86 */     for (OS os : OS.oses) {
/*  87 */       if (os.isMatch(userAgentString)) {
/*  88 */         return os;
/*     */       }
/*     */     } 
/*  91 */     return OS.Unknown;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Platform parsePlatform(String userAgentString) {
/* 101 */     for (Platform platform : Platform.platforms) {
/* 102 */       if (platform.isMatch(userAgentString)) {
/* 103 */         return platform;
/*     */       }
/*     */     } 
/* 106 */     return Platform.Unknown;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\htt\\useragent\UserAgentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */