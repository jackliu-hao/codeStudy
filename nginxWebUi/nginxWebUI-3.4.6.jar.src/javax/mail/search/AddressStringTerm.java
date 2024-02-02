/*     */ package javax.mail.search;
/*     */ 
/*     */ import javax.mail.Address;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AddressStringTerm
/*     */   extends StringTerm
/*     */ {
/*     */   private static final long serialVersionUID = 3086821234204980368L;
/*     */   
/*     */   protected AddressStringTerm(String pattern) {
/*  68 */     super(pattern, true);
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
/*     */   protected boolean match(Address a) {
/*  84 */     if (a instanceof InternetAddress) {
/*  85 */       InternetAddress ia = (InternetAddress)a;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  90 */       return match(ia.toUnicodeString());
/*     */     } 
/*  92 */     return match(a.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  99 */     if (!(obj instanceof AddressStringTerm))
/* 100 */       return false; 
/* 101 */     return super.equals(obj);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\AddressStringTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */