/*     */ package cn.hutool.http.useragent;
/*     */ 
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UserAgentInfo
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final String NameUnknown = "Unknown";
/*     */   private final String name;
/*     */   private final Pattern pattern;
/*     */   
/*     */   public UserAgentInfo(String name, String regex) {
/*  31 */     this(name, (null == regex) ? null : Pattern.compile(regex, 2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UserAgentInfo(String name, Pattern pattern) {
/*  41 */     this.name = name;
/*  42 */     this.pattern = pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  51 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Pattern getPattern() {
/*  60 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMatch(String content) {
/*  70 */     return ReUtil.contains(this.pattern, content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUnknown() {
/*  79 */     return "Unknown".equals(this.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  84 */     int prime = 31;
/*  85 */     int result = 1;
/*  86 */     result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
/*  87 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  92 */     if (this == obj) {
/*  93 */       return true;
/*     */     }
/*  95 */     if (obj == null) {
/*  96 */       return false;
/*     */     }
/*  98 */     if (getClass() != obj.getClass()) {
/*  99 */       return false;
/*     */     }
/* 101 */     UserAgentInfo other = (UserAgentInfo)obj;
/* 102 */     if (this.name == null)
/* 103 */       return (other.name == null); 
/* 104 */     return this.name.equals(other.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 109 */     return this.name;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\htt\\useragent\UserAgentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */