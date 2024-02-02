/*     */ package javax.servlet.http;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.ResourceBundle;
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
/*     */ 
/*     */ 
/*     */ public class Cookie
/*     */   implements Cloneable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -6454587001725327448L;
/*     */   private static final String TSPECIALS;
/*     */   private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
/*  71 */   private static ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
/*     */   
/*     */   static {
/*  74 */     if (Boolean.valueOf(System.getProperty("org.glassfish.web.rfc2109_cookie_names_enforced", "true")).booleanValue()) {
/*  75 */       TSPECIALS = "/()<>@,;:\\\"[]?={} \t";
/*     */     } else {
/*  77 */       TSPECIALS = ",; ";
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String name;
/*     */ 
/*     */   
/*     */   private String value;
/*     */ 
/*     */   
/*     */   private String comment;
/*     */ 
/*     */   
/*     */   private String domain;
/*     */ 
/*     */   
/*  95 */   private int maxAge = -1;
/*     */   private String path;
/*     */   private boolean secure;
/*  98 */   private int version = 0;
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
/*     */   private boolean isHttpOnly = false;
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
/*     */   public Cookie(String name, String value) {
/* 133 */     if (name == null || name.length() == 0) {
/* 134 */       throw new IllegalArgumentException(lStrings
/* 135 */           .getString("err.cookie_name_blank"));
/*     */     }
/* 137 */     if (!isToken(name) || name
/* 138 */       .equalsIgnoreCase("Comment") || name
/* 139 */       .equalsIgnoreCase("Discard") || name
/* 140 */       .equalsIgnoreCase("Domain") || name
/* 141 */       .equalsIgnoreCase("Expires") || name
/* 142 */       .equalsIgnoreCase("Max-Age") || name
/* 143 */       .equalsIgnoreCase("Path") || name
/* 144 */       .equalsIgnoreCase("Secure") || name
/* 145 */       .equalsIgnoreCase("Version") || name
/* 146 */       .startsWith("$")) {
/* 147 */       String errMsg = lStrings.getString("err.cookie_name_is_token");
/* 148 */       Object[] errArgs = new Object[1];
/* 149 */       errArgs[0] = name;
/* 150 */       errMsg = MessageFormat.format(errMsg, errArgs);
/* 151 */       throw new IllegalArgumentException(errMsg);
/*     */     } 
/*     */     
/* 154 */     this.name = name;
/* 155 */     this.value = value;
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
/*     */   public void setComment(String purpose) {
/* 170 */     this.comment = purpose;
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
/*     */   public String getComment() {
/* 182 */     return this.comment;
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
/*     */   public void setDomain(String domain) {
/* 202 */     this.domain = domain.toLowerCase(Locale.ENGLISH);
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
/*     */   public String getDomain() {
/* 215 */     return this.domain;
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
/*     */ 
/*     */   
/*     */   public void setMaxAge(int expiry) {
/* 239 */     this.maxAge = expiry;
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
/*     */   public int getMaxAge() {
/* 255 */     return this.maxAge;
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
/*     */   public void setPath(String uri) {
/* 277 */     this.path = uri;
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
/*     */   public String getPath() {
/* 291 */     return this.path;
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
/*     */   public void setSecure(boolean flag) {
/* 307 */     this.secure = flag;
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
/*     */   public boolean getSecure() {
/* 321 */     return this.secure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 331 */     return this.name;
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
/*     */   public void setValue(String newValue) {
/* 350 */     this.value = newValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValue() {
/* 361 */     return this.value;
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
/*     */   public int getVersion() {
/* 378 */     return this.version;
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
/*     */   public void setVersion(int v) {
/* 397 */     this.version = v;
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
/*     */   private boolean isToken(String value) {
/* 410 */     int len = value.length();
/* 411 */     for (int i = 0; i < len; i++) {
/* 412 */       char c = value.charAt(i);
/* 413 */       if (c < ' ' || c >= '' || TSPECIALS.indexOf(c) != -1) {
/* 414 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 418 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 427 */       return super.clone();
/* 428 */     } catch (CloneNotSupportedException e) {
/* 429 */       throw new RuntimeException(e.getMessage());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHttpOnly(boolean isHttpOnly) {
/* 450 */     this.isHttpOnly = isHttpOnly;
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
/*     */   public boolean isHttpOnly() {
/* 462 */     return this.isHttpOnly;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\Cookie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */