/*     */ package javax.mail.internet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AddressException
/*     */   extends ParseException
/*     */ {
/*  56 */   protected String ref = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   protected int pos = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long serialVersionUID = 9134583443539323120L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AddressException() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AddressException(String s) {
/*  79 */     super(s);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AddressException(String s, String ref) {
/*  90 */     super(s);
/*  91 */     this.ref = ref;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AddressException(String s, String ref, int pos) {
/* 100 */     super(s);
/* 101 */     this.ref = ref;
/* 102 */     this.pos = pos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRef() {
/* 110 */     return this.ref;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPos() {
/* 118 */     return this.pos;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 122 */     String s = super.toString();
/* 123 */     if (this.ref == null)
/* 124 */       return s; 
/* 125 */     s = s + " in string ``" + this.ref + "''";
/* 126 */     if (this.pos < 0)
/* 127 */       return s; 
/* 128 */     return s + " at position " + this.pos;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\AddressException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */