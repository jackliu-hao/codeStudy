/*     */ package javax.mail.search;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import javax.mail.Message;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HeaderTerm
/*     */   extends StringTerm
/*     */ {
/*     */   protected String headerName;
/*     */   private static final long serialVersionUID = 8342514650333389122L;
/*     */   
/*     */   public HeaderTerm(String headerName, String pattern) {
/*  70 */     super(pattern);
/*  71 */     this.headerName = headerName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHeaderName() {
/*  78 */     return this.headerName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Message msg) {
/*     */     String[] headers;
/*     */     try {
/*  91 */       headers = msg.getHeader(this.headerName);
/*  92 */     } catch (Exception e) {
/*  93 */       return false;
/*     */     } 
/*     */     
/*  96 */     if (headers == null) {
/*  97 */       return false;
/*     */     }
/*  99 */     for (int i = 0; i < headers.length; i++) {
/* 100 */       if (match(headers[i]))
/* 101 */         return true; 
/* 102 */     }  return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 109 */     if (!(obj instanceof HeaderTerm))
/* 110 */       return false; 
/* 111 */     HeaderTerm ht = (HeaderTerm)obj;
/*     */     
/* 113 */     return (ht.headerName.equalsIgnoreCase(this.headerName) && super.equals(ht));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 121 */     return this.headerName.toLowerCase(Locale.ENGLISH).hashCode() + super.hashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\HeaderTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */