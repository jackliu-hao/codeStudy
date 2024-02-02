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
/*     */ public class OS
/*     */   extends UserAgentInfo
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  21 */   public static final OS Unknown = new OS("Unknown", null);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   public static final List<OS> oses = CollUtil.newArrayList((Object[])new OS[] { new OS("Windows 10 or Windows Server 2016", "windows nt 10\\.0", "windows nt (10\\.0)"), new OS("Windows 8.1 or Windows Server 2012R2", "windows nt 6\\.3", "windows nt (6\\.3)"), new OS("Windows 8 or Windows Server 2012", "windows nt 6\\.2", "windows nt (6\\.2)"), new OS("Windows Vista", "windows nt 6\\.0", "windows nt (6\\.0)"), new OS("Windows 7 or Windows Server 2008R2", "windows nt 6\\.1", "windows nt (6\\.1)"), new OS("Windows 2003", "windows nt 5\\.2", "windows nt (5\\.2)"), new OS("Windows XP", "windows nt 5\\.1", "windows nt (5\\.1)"), new OS("Windows 2000", "windows nt 5\\.0", "windows nt (5\\.0)"), new OS("Windows Phone", "windows (ce|phone|mobile)( os)?", "windows (?:ce|phone|mobile) (\\d+([._]\\d+)*)"), new OS("Windows", "windows"), new OS("OSX", "os x (\\d+)[._](\\d+)", "os x (\\d+([._]\\d+)*)"), new OS("Android", "Android", "Android (\\d+([._]\\d+)*)"), new OS("Android", "XiaoMi|MI\\s+", "\\(X(\\d+([._]\\d+)*)"), new OS("Linux", "linux"), new OS("Wii", "wii", "wii libnup/(\\d+([._]\\d+)*)"), new OS("PS3", "playstation 3", "playstation 3; (\\d+([._]\\d+)*)"), new OS("PSP", "playstation portable", "Portable\\); (\\d+([._]\\d+)*)"), new OS("iPad", "\\(iPad.*os (\\d+)[._](\\d+)", "\\(iPad.*os (\\d+([._]\\d+)*)"), new OS("iPhone", "\\(iPhone.*os (\\d+)[._](\\d+)", "\\(iPhone.*os (\\d+([._]\\d+)*)"), new OS("YPod", "iPod touch[\\s\\;]+iPhone.*os (\\d+)[._](\\d+)", "iPod touch[\\s\\;]+iPhone.*os (\\d+([._]\\d+)*)"), new OS("YPad", "iPad[\\s\\;]+iPhone.*os (\\d+)[._](\\d+)", "iPad[\\s\\;]+iPhone.*os (\\d+([._]\\d+)*)"), new OS("YPhone", "iPhone[\\s\\;]+iPhone.*os (\\d+)[._](\\d+)", "iPhone[\\s\\;]+iPhone.*os (\\d+([._]\\d+)*)"), new OS("Symbian", "symbian(os)?"), new OS("Darwin", "Darwin\\/([\\d\\w\\.\\-]+)", "Darwin\\/([\\d\\w\\.\\-]+)"), new OS("Adobe Air", "AdobeAir\\/([\\d\\w\\.\\-]+)", "AdobeAir\\/([\\d\\w\\.\\-]+)"), new OS("Java", "Java[\\s]+([\\d\\w\\.\\-]+)", "Java[\\s]+([\\d\\w\\.\\-]+)") });
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
/*     */   public static synchronized void addCustomOs(String name, String regex, String versionRegex) {
/*  64 */     oses.add(new OS(name, regex, versionRegex));
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
/*     */   public OS(String name, String regex) {
/*  76 */     this(name, regex, null);
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
/*     */   public OS(String name, String regex, String versionRegex) {
/*  88 */     super(name, regex);
/*  89 */     if (null != versionRegex) {
/*  90 */       this.versionPattern = Pattern.compile(versionRegex, 2);
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
/* 101 */     if (isUnknown() || null == this.versionPattern)
/*     */     {
/* 103 */       return null;
/*     */     }
/* 105 */     return ReUtil.getGroup1(this.versionPattern, userAgentString);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\htt\\useragent\OS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */