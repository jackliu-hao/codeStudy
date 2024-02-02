/*     */ package cn.hutool.http.useragent;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class Platform
/*     */   extends UserAgentInfo
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  20 */   public static final Platform Unknown = new Platform("Unknown", null);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  25 */   public static final Platform IPHONE = new Platform("iPhone", "iphone");
/*     */ 
/*     */ 
/*     */   
/*  29 */   public static final Platform IPOD = new Platform("iPod", "ipod");
/*     */ 
/*     */ 
/*     */   
/*  33 */   public static final Platform IPAD = new Platform("iPad", "ipad");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  38 */   public static final Platform ANDROID = new Platform("Android", "android");
/*     */ 
/*     */ 
/*     */   
/*  42 */   public static final Platform GOOGLE_TV = new Platform("GoogleTV", "googletv");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   public static final Platform WINDOWS_PHONE = new Platform("Windows Phone", "windows (ce|phone|mobile)( os)?");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   public static final List<Platform> mobilePlatforms = CollUtil.newArrayList((Object[])new Platform[] { WINDOWS_PHONE, IPAD, IPOD, IPHONE, new Platform("Android", "XiaoMi|MI\\s+"), ANDROID, GOOGLE_TV, new Platform("htcFlyer", "htc_flyer"), new Platform("Symbian", "symbian(os)?"), new Platform("Blackberry", "blackberry") });
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
/*  68 */   public static final List<Platform> desktopPlatforms = CollUtil.newArrayList((Object[])new Platform[] { new Platform("Windows", "windows"), new Platform("Mac", "(macintosh|darwin)"), new Platform("Linux", "linux"), new Platform("Wii", "wii"), new Platform("Playstation", "playstation"), new Platform("Java", "java") });
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
/*  83 */   public static final List<Platform> platforms = new ArrayList<>(13); static {
/*  84 */     platforms.addAll(mobilePlatforms);
/*  85 */     platforms.addAll(desktopPlatforms);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Platform(String name, String regex) {
/*  95 */     super(name, regex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMobile() {
/* 104 */     return mobilePlatforms.contains(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIPhoneOrIPod() {
/* 114 */     return (equals(IPHONE) || equals(IPOD));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIPad() {
/* 124 */     return equals(IPAD);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIos() {
/* 134 */     return (isIPhoneOrIPod() || isIPad());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAndroid() {
/* 144 */     return (equals(ANDROID) || equals(GOOGLE_TV));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\htt\\useragent\Platform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */