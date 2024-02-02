/*     */ package javax.mail;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Provider
/*     */ {
/*     */   private Type type;
/*     */   private String protocol;
/*     */   private String className;
/*     */   private String vendor;
/*     */   private String version;
/*     */   
/*     */   public static class Type
/*     */   {
/*  63 */     public static final Type STORE = new Type("STORE");
/*  64 */     public static final Type TRANSPORT = new Type("TRANSPORT");
/*     */     
/*     */     private String type;
/*     */     
/*     */     private Type(String type) {
/*  69 */       this.type = type;
/*     */     }
/*     */     
/*     */     public String toString() {
/*  73 */       return this.type;
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
/*     */   public Provider(Type type, String protocol, String classname, String vendor, String version) {
/*  93 */     this.type = type;
/*  94 */     this.protocol = protocol;
/*  95 */     this.className = classname;
/*  96 */     this.vendor = vendor;
/*  97 */     this.version = version;
/*     */   }
/*     */ 
/*     */   
/*     */   public Type getType() {
/* 102 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProtocol() {
/* 107 */     return this.protocol;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getClassName() {
/* 112 */     return this.className;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVendor() {
/* 117 */     return this.vendor;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVersion() {
/* 122 */     return this.version;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 127 */     String s = "javax.mail.Provider[" + this.type + "," + this.protocol + "," + this.className;
/*     */ 
/*     */     
/* 130 */     if (this.vendor != null) {
/* 131 */       s = s + "," + this.vendor;
/*     */     }
/* 133 */     if (this.version != null) {
/* 134 */       s = s + "," + this.version;
/*     */     }
/* 136 */     s = s + "]";
/* 137 */     return s;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Provider.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */