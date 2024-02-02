/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Version
/*     */   implements Serializable
/*     */ {
/*     */   private final int major;
/*     */   private final int minor;
/*     */   private final int micro;
/*     */   private final String extraInfo;
/*     */   private final String originalStringValue;
/*     */   private final Boolean gaeCompliant;
/*     */   private final Date buildDate;
/*     */   private final int intValue;
/*     */   private volatile String calculatedStringValue;
/*     */   private int hashCode;
/*     */   
/*     */   public Version(String stringValue) {
/*  55 */     this(stringValue, (Boolean)null, (Date)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Version(String stringValue, Boolean gaeCompliant, Date buildDate) {
/*  62 */     stringValue = stringValue.trim();
/*  63 */     this.originalStringValue = stringValue;
/*     */     
/*  65 */     int[] parts = new int[3];
/*  66 */     String extraInfoTmp = null;
/*     */     
/*  68 */     int partIdx = 0;
/*  69 */     for (int i = 0; i < stringValue.length(); i++) {
/*  70 */       char c = stringValue.charAt(i);
/*  71 */       if (isNumber(c)) {
/*  72 */         parts[partIdx] = parts[partIdx] * 10 + c - 48;
/*     */       } else {
/*  74 */         if (i == 0) {
/*  75 */           throw new IllegalArgumentException("The version number string " + 
/*  76 */               StringUtil.jQuote(stringValue) + " doesn't start with a number.");
/*     */         }
/*     */         
/*  79 */         if (c == '.') {
/*  80 */           char nextC = (i + 1 >= stringValue.length()) ? Character.MIN_VALUE : stringValue.charAt(i + 1);
/*  81 */           if (nextC == '.') {
/*  82 */             throw new IllegalArgumentException("The version number string " + 
/*  83 */                 StringUtil.jQuote(stringValue) + " contains multiple dots after a number.");
/*     */           }
/*     */           
/*  86 */           if (partIdx == 2 || !isNumber(nextC)) {
/*  87 */             extraInfoTmp = stringValue.substring(i);
/*     */             break;
/*     */           } 
/*  90 */           partIdx++;
/*     */         } else {
/*     */           
/*  93 */           extraInfoTmp = stringValue.substring(i);
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*  99 */     if (extraInfoTmp != null) {
/* 100 */       char firstChar = extraInfoTmp.charAt(0);
/* 101 */       if (firstChar == '.' || firstChar == '-' || firstChar == '_') {
/* 102 */         extraInfoTmp = extraInfoTmp.substring(1);
/* 103 */         if (extraInfoTmp.length() == 0) {
/* 104 */           throw new IllegalArgumentException("The version number string " + 
/* 105 */               StringUtil.jQuote(stringValue) + " has an extra info section opened with \"" + firstChar + "\", but it's empty.");
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 111 */     this.extraInfo = extraInfoTmp;
/*     */     
/* 113 */     this.major = parts[0];
/* 114 */     this.minor = parts[1];
/* 115 */     this.micro = parts[2];
/* 116 */     this.intValue = calculateIntValue();
/*     */     
/* 118 */     this.gaeCompliant = gaeCompliant;
/* 119 */     this.buildDate = buildDate;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isNumber(char c) {
/* 124 */     return (c >= '0' && c <= '9');
/*     */   }
/*     */   
/*     */   public Version(int major, int minor, int micro) {
/* 128 */     this(major, minor, micro, null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Version(int intValue) {
/* 137 */     this.intValue = intValue;
/*     */     
/* 139 */     this.micro = intValue % 1000;
/* 140 */     this.minor = intValue / 1000 % 1000;
/* 141 */     this.major = intValue / 1000000;
/*     */     
/* 143 */     this.extraInfo = null;
/* 144 */     this.gaeCompliant = null;
/* 145 */     this.buildDate = null;
/* 146 */     this.originalStringValue = null;
/*     */   }
/*     */   
/*     */   public Version(int major, int minor, int micro, String extraInfo, Boolean gaeCompatible, Date buildDate) {
/* 150 */     this.major = major;
/* 151 */     this.minor = minor;
/* 152 */     this.micro = micro;
/* 153 */     this.extraInfo = extraInfo;
/* 154 */     this.gaeCompliant = gaeCompatible;
/* 155 */     this.buildDate = buildDate;
/* 156 */     this.intValue = calculateIntValue();
/* 157 */     this.originalStringValue = null;
/*     */   }
/*     */   
/*     */   private int calculateIntValue() {
/* 161 */     return intValueFor(this.major, this.minor, this.micro);
/*     */   }
/*     */   
/*     */   public static int intValueFor(int major, int minor, int micro) {
/* 165 */     return major * 1000000 + minor * 1000 + micro;
/*     */   }
/*     */   
/*     */   private String getStringValue() {
/* 169 */     if (this.originalStringValue != null) return this.originalStringValue;
/*     */     
/* 171 */     String calculatedStringValue = this.calculatedStringValue;
/* 172 */     if (calculatedStringValue == null) {
/* 173 */       synchronized (this) {
/* 174 */         calculatedStringValue = this.calculatedStringValue;
/* 175 */         if (calculatedStringValue == null) {
/* 176 */           calculatedStringValue = this.major + "." + this.minor + "." + this.micro;
/* 177 */           if (this.extraInfo != null) calculatedStringValue = calculatedStringValue + "-" + this.extraInfo; 
/* 178 */           this.calculatedStringValue = calculatedStringValue;
/*     */         } 
/*     */       } 
/*     */     }
/* 182 */     return calculatedStringValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 190 */     return getStringValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMajor() {
/* 197 */     return this.major;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinor() {
/* 204 */     return this.minor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMicro() {
/* 211 */     return this.micro;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getExtraInfo() {
/* 221 */     return this.extraInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isGAECompliant() {
/* 228 */     return this.gaeCompliant;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getBuildDate() {
/* 235 */     return this.buildDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int intValue() {
/* 242 */     return this.intValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 247 */     int r = this.hashCode;
/* 248 */     if (r != 0) return r; 
/* 249 */     synchronized (this) {
/* 250 */       if (this.hashCode == 0) {
/* 251 */         int prime = 31;
/* 252 */         int result = 1;
/* 253 */         result = 31 * result + ((this.buildDate == null) ? 0 : this.buildDate.hashCode());
/* 254 */         result = 31 * result + ((this.extraInfo == null) ? 0 : this.extraInfo.hashCode());
/* 255 */         result = 31 * result + ((this.gaeCompliant == null) ? 0 : this.gaeCompliant.hashCode());
/* 256 */         result = 31 * result + this.intValue;
/* 257 */         if (result == 0) result = -1; 
/* 258 */         this.hashCode = result;
/*     */       } 
/* 260 */       return this.hashCode;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 266 */     if (this == obj) return true; 
/* 267 */     if (obj == null) return false; 
/* 268 */     if (getClass() != obj.getClass()) return false;
/*     */     
/* 270 */     Version other = (Version)obj;
/*     */     
/* 272 */     if (this.intValue != other.intValue) return false;
/*     */     
/* 274 */     if (other.hashCode() != hashCode()) return false;
/*     */     
/* 276 */     if (this.buildDate == null) {
/* 277 */       if (other.buildDate != null) return false; 
/* 278 */     } else if (!this.buildDate.equals(other.buildDate)) {
/* 279 */       return false;
/*     */     } 
/*     */     
/* 282 */     if (this.extraInfo == null) {
/* 283 */       if (other.extraInfo != null) return false; 
/* 284 */     } else if (!this.extraInfo.equals(other.extraInfo)) {
/* 285 */       return false;
/*     */     } 
/*     */     
/* 288 */     if (this.gaeCompliant == null) {
/* 289 */       if (other.gaeCompliant != null) return false; 
/* 290 */     } else if (!this.gaeCompliant.equals(other.gaeCompliant)) {
/* 291 */       return false;
/*     */     } 
/*     */     
/* 294 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\Version.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */