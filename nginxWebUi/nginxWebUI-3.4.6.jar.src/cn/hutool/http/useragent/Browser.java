/*     */ package cn.hutool.http.useragent;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import java.util.List;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class Browser
/*     */   extends UserAgentInfo
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  21 */   public static final Browser Unknown = new Browser("Unknown", null, null);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String Other_Version = "[\\/ ]([\\d\\w\\.\\-]+)";
/*     */ 
/*     */ 
/*     */   
/*  30 */   public static final List<Browser> browers = CollUtil.newArrayList((Object[])new Browser[] { new Browser("wxwork", "wxwork", "wxwork\\/([\\d\\w\\.\\-]+)"), new Browser("MicroMessenger", "MicroMessenger", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("miniProgram", "miniProgram", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("QQBrowser", "MQQBrowser", "MQQBrowser\\/([\\d\\w\\.\\-]+)"), new Browser("DingTalk", "DingTalk", "AliApp\\(DingTalk\\/([\\d\\w\\.\\-]+)\\)"), new Browser("Alipay", "AlipayClient", "AliApp\\(AP\\/([\\d\\w\\.\\-]+)\\)"), new Browser("Taobao", "taobao", "AliApp\\(TB\\/([\\d\\w\\.\\-]+)\\)"), new Browser("UCBrowser", "UC?Browser", "UC?Browser\\/([\\d\\w\\.\\-]+)"), new Browser("MiuiBrowser", "MiuiBrowser|mibrowser", "MiuiBrowser\\/([\\d\\w\\.\\-]+)"), new Browser("Quark", "Quark", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Lenovo", "SLBrowser", "SLBrowser/([\\d\\w\\.\\-]+)"), new Browser("MSEdge", "Edge|Edg", "(?:edge|Edg|EdgA)\\/([\\d\\w\\.\\-]+)"), new Browser("Chrome", "chrome", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Firefox", "firefox", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("IEMobile", "iemobile", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Android Browser", "android", "version\\/([\\d\\w\\.\\-]+)"), new Browser("Safari", "safari", "version\\/([\\d\\w\\.\\-]+)"), new Browser("Opera", "opera", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Konqueror", "konqueror", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("PS3", "playstation 3", "([\\d\\w\\.\\-]+)\\)\\s*$"), new Browser("PSP", "playstation portable", "([\\d\\w\\.\\-]+)\\)?\\s*$"), new Browser("Lotus", "lotus.notes", "Lotus-Notes\\/([\\w.]+)"), new Browser("Thunderbird", "thunderbird", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Netscape", "netscape", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Seamonkey", "seamonkey", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Outlook", "microsoft.outlook", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Evolution", "evolution", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("MSIE", "msie", "msie ([\\d\\w\\.\\-]+)"), new Browser("MSIE11", "rv:11", "rv:([\\d\\w\\.\\-]+)"), new Browser("Gabble", "Gabble", "[\\/ ]([\\d\\w\\.\\-]+)"), new Browser("Yammer Desktop", "AdobeAir", "([\\d\\w\\.\\-]+)\\/Yammer"), new Browser("Yammer Mobile", "Yammer[\\s]+([\\d\\w\\.\\-]+)", "Yammer[\\s]+([\\d\\w\\.\\-]+)"), new Browser("Apache HTTP Client", "Apache\\\\-HttpClient", "Apache\\-HttpClient\\/([\\d\\w\\.\\-]+)"), new Browser("BlackBerry", "BlackBerry", "BlackBerry[\\d]+\\/([\\d\\w\\.\\-]+)") });
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
/*     */ 
/*     */ 
/*     */   
/*     */   private Pattern versionPattern;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void addCustomBrowser(String name, String regex, String versionRegex) {
/*  88 */     browers.add(new Browser(name, regex, versionRegex));
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
/*     */   public Browser(String name, String regex, String versionRegex) {
/* 101 */     super(name, regex);
/* 102 */     if ("[\\/ ]([\\d\\w\\.\\-]+)".equals(versionRegex)) {
/* 103 */       versionRegex = name + versionRegex;
/*     */     }
/* 105 */     if (null != versionRegex) {
/* 106 */       this.versionPattern = Pattern.compile(versionRegex, 2);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getVersion(String userAgentString) {
/* 117 */     if (isUnknown()) {
/* 118 */       return null;
/*     */     }
/* 120 */     return ReUtil.getGroup1(this.versionPattern, userAgentString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMobile() {
/* 129 */     String name = getName();
/* 130 */     return ("PSP".equals(name) || "Yammer Mobile"
/* 131 */       .equals(name) || "Android Browser"
/* 132 */       .equals(name) || "IEMobile"
/* 133 */       .equals(name) || "MicroMessenger"
/* 134 */       .equals(name) || "miniProgram"
/* 135 */       .equals(name) || "DingTalk"
/* 136 */       .equals(name));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\htt\\useragent\Browser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */