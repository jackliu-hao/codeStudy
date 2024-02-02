/*     */ package com.mysql.cj;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerVersion
/*     */   implements Comparable<ServerVersion>
/*     */ {
/*     */   private String completeVersion;
/*     */   private Integer major;
/*     */   private Integer minor;
/*     */   private Integer subminor;
/*     */   
/*     */   public ServerVersion(String completeVersion, int major, int minor, int subminor) {
/*  42 */     this.completeVersion = completeVersion;
/*  43 */     this.major = Integer.valueOf(major);
/*  44 */     this.minor = Integer.valueOf(minor);
/*  45 */     this.subminor = Integer.valueOf(subminor);
/*     */   }
/*     */   
/*     */   public ServerVersion(int major, int minor, int subminor) {
/*  49 */     this(null, major, minor, subminor);
/*     */   }
/*     */   
/*     */   public int getMajor() {
/*  53 */     return this.major.intValue();
/*     */   }
/*     */   
/*     */   public int getMinor() {
/*  57 */     return this.minor.intValue();
/*     */   }
/*     */   
/*     */   public int getSubminor() {
/*  61 */     return this.subminor.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  72 */     if (this.completeVersion != null) {
/*  73 */       return this.completeVersion;
/*     */     }
/*  75 */     return String.format("%d.%d.%d", new Object[] { this.major, this.minor, this.subminor });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  80 */     if (this == obj) {
/*  81 */       return true;
/*     */     }
/*  83 */     if (obj == null || !ServerVersion.class.isAssignableFrom(obj.getClass())) {
/*  84 */       return false;
/*     */     }
/*  86 */     ServerVersion another = (ServerVersion)obj;
/*  87 */     if (getMajor() != another.getMajor() || getMinor() != another.getMinor() || getSubminor() != another.getSubminor()) {
/*  88 */       return false;
/*     */     }
/*  90 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  95 */     int hash = 23;
/*  96 */     hash += 19 * hash + this.major.intValue();
/*  97 */     hash += 19 * hash + this.minor.intValue();
/*  98 */     hash += 19 * hash + this.subminor.intValue();
/*  99 */     return hash;
/*     */   }
/*     */   
/*     */   public int compareTo(ServerVersion other) {
/*     */     int c;
/* 104 */     if ((c = this.major.compareTo(Integer.valueOf(other.getMajor()))) != 0)
/* 105 */       return c; 
/* 106 */     if ((c = this.minor.compareTo(Integer.valueOf(other.getMinor()))) != 0) {
/* 107 */       return c;
/*     */     }
/* 109 */     return this.subminor.compareTo(Integer.valueOf(other.getSubminor()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean meetsMinimum(ServerVersion min) {
/* 120 */     return (compareTo(min) >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServerVersion parseVersion(String versionString) {
/* 131 */     int point = versionString.indexOf('.');
/*     */     
/* 133 */     if (point != -1) {
/*     */       try {
/* 135 */         int serverMajorVersion = Integer.parseInt(versionString.substring(0, point));
/*     */         
/* 137 */         String remaining = versionString.substring(point + 1, versionString.length());
/* 138 */         point = remaining.indexOf('.');
/*     */         
/* 140 */         if (point != -1) {
/* 141 */           int serverMinorVersion = Integer.parseInt(remaining.substring(0, point));
/*     */           
/* 143 */           remaining = remaining.substring(point + 1, remaining.length());
/*     */           
/* 145 */           int pos = 0;
/*     */           
/* 147 */           while (pos < remaining.length() && 
/* 148 */             remaining.charAt(pos) >= '0' && remaining.charAt(pos) <= '9')
/*     */           {
/*     */ 
/*     */             
/* 152 */             pos++;
/*     */           }
/*     */           
/* 155 */           int serverSubminorVersion = Integer.parseInt(remaining.substring(0, pos));
/*     */           
/* 157 */           return new ServerVersion(versionString, serverMajorVersion, serverMinorVersion, serverSubminorVersion);
/*     */         } 
/* 159 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 164 */     return new ServerVersion(0, 0, 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ServerVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */