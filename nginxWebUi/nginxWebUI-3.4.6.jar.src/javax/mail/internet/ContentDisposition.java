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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContentDisposition
/*     */ {
/*     */   private String disposition;
/*     */   private ParameterList list;
/*     */   
/*     */   public ContentDisposition() {}
/*     */   
/*     */   public ContentDisposition(String disposition, ParameterList list) {
/*  73 */     this.disposition = disposition;
/*  74 */     this.list = list;
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
/*     */   public ContentDisposition(String s) throws ParseException {
/*  87 */     HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
/*     */ 
/*     */ 
/*     */     
/*  91 */     HeaderTokenizer.Token tk = h.next();
/*  92 */     if (tk.getType() != -1) {
/*  93 */       throw new ParseException("Expected disposition, got " + tk.getValue());
/*     */     }
/*  95 */     this.disposition = tk.getValue();
/*     */ 
/*     */     
/*  98 */     String rem = h.getRemainder();
/*  99 */     if (rem != null) {
/* 100 */       this.list = new ParameterList(rem);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDisposition() {
/* 109 */     return this.disposition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParameter(String name) {
/* 119 */     if (this.list == null) {
/* 120 */       return null;
/*     */     }
/* 122 */     return this.list.get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParameterList getParameterList() {
/* 133 */     return this.list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDisposition(String disposition) {
/* 142 */     this.disposition = disposition;
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
/*     */   public void setParameter(String name, String value) {
/* 154 */     if (this.list == null) {
/* 155 */       this.list = new ParameterList();
/*     */     }
/* 157 */     this.list.set(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParameterList(ParameterList list) {
/* 166 */     this.list = list;
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
/*     */   public String toString() {
/* 178 */     if (this.disposition == null) {
/* 179 */       return null;
/*     */     }
/* 181 */     if (this.list == null) {
/* 182 */       return this.disposition;
/*     */     }
/* 184 */     StringBuffer sb = new StringBuffer(this.disposition);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 189 */     sb.append(this.list.toString(sb.length() + 21));
/* 190 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\ContentDisposition.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */