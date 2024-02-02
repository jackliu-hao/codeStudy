/*     */ package javax.mail.internet;
/*     */ 
/*     */ import javax.mail.Session;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class UniqueValue
/*     */ {
/*  62 */   private static int id = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getUniqueBoundaryValue() {
/*  72 */     StringBuffer s = new StringBuffer();
/*     */ 
/*     */     
/*  75 */     s.append("----=_Part_").append(getUniqueId()).append("_").append(s.hashCode()).append('.').append(System.currentTimeMillis());
/*     */ 
/*     */     
/*  78 */     return s.toString();
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
/*     */   public static String getUniqueMessageIDValue(Session ssn) {
/*  97 */     String suffix = null;
/*     */     
/*  99 */     InternetAddress addr = InternetAddress.getLocalAddress(ssn);
/* 100 */     if (addr != null) {
/* 101 */       suffix = addr.getAddress();
/*     */     } else {
/* 103 */       suffix = "javamailuser@localhost";
/*     */     } 
/*     */     
/* 106 */     StringBuffer s = new StringBuffer();
/*     */ 
/*     */     
/* 109 */     s.append(s.hashCode()).append('.').append(getUniqueId()).append('.').append(System.currentTimeMillis()).append('.').append("JavaMail.").append(suffix);
/*     */ 
/*     */ 
/*     */     
/* 113 */     return s.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static synchronized int getUniqueId() {
/* 121 */     return id++;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\UniqueValue.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */