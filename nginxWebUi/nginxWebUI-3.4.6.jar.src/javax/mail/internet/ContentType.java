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
/*     */ 
/*     */ public class ContentType
/*     */ {
/*     */   private String primaryType;
/*     */   private String subType;
/*     */   private ParameterList list;
/*     */   
/*     */   public ContentType() {}
/*     */   
/*     */   public ContentType(String primaryType, String subType, ParameterList list) {
/*  75 */     this.primaryType = primaryType;
/*  76 */     this.subType = subType;
/*  77 */     this.list = list;
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
/*     */   public ContentType(String s) throws ParseException {
/*  89 */     HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
/*     */ 
/*     */ 
/*     */     
/*  93 */     HeaderTokenizer.Token tk = h.next();
/*  94 */     if (tk.getType() != -1) {
/*  95 */       throw new ParseException("Expected MIME type, got " + tk.getValue());
/*     */     }
/*  97 */     this.primaryType = tk.getValue();
/*     */ 
/*     */     
/* 100 */     tk = h.next();
/* 101 */     if ((char)tk.getType() != '/') {
/* 102 */       throw new ParseException("Expected '/', got " + tk.getValue());
/*     */     }
/*     */     
/* 105 */     tk = h.next();
/* 106 */     if (tk.getType() != -1) {
/* 107 */       throw new ParseException("Expected MIME subtype, got " + tk.getValue());
/*     */     }
/* 109 */     this.subType = tk.getValue();
/*     */ 
/*     */     
/* 112 */     String rem = h.getRemainder();
/* 113 */     if (rem != null) {
/* 114 */       this.list = new ParameterList(rem);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPrimaryType() {
/* 122 */     return this.primaryType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSubType() {
/* 130 */     return this.subType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBaseType() {
/* 141 */     return this.primaryType + '/' + this.subType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParameter(String name) {
/* 150 */     if (this.list == null) {
/* 151 */       return null;
/*     */     }
/* 153 */     return this.list.get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParameterList getParameterList() {
/* 163 */     return this.list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPrimaryType(String primaryType) {
/* 171 */     this.primaryType = primaryType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSubType(String subType) {
/* 179 */     this.subType = subType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParameter(String name, String value) {
/* 190 */     if (this.list == null) {
/* 191 */       this.list = new ParameterList();
/*     */     }
/* 193 */     this.list.set(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParameterList(ParameterList list) {
/* 201 */     this.list = list;
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
/* 212 */     if (this.primaryType == null || this.subType == null) {
/* 213 */       return null;
/*     */     }
/* 215 */     StringBuffer sb = new StringBuffer();
/* 216 */     sb.append(this.primaryType).append('/').append(this.subType);
/* 217 */     if (this.list != null)
/*     */     {
/*     */ 
/*     */       
/* 221 */       sb.append(this.list.toString(sb.length() + 14));
/*     */     }
/* 223 */     return sb.toString();
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
/*     */   public boolean match(ContentType cType) {
/* 246 */     if (!this.primaryType.equalsIgnoreCase(cType.getPrimaryType())) {
/* 247 */       return false;
/*     */     }
/* 249 */     String sType = cType.getSubType();
/*     */ 
/*     */     
/* 252 */     if (this.subType.charAt(0) == '*' || sType.charAt(0) == '*') {
/* 253 */       return true;
/*     */     }
/*     */     
/* 256 */     if (!this.subType.equalsIgnoreCase(sType)) {
/* 257 */       return false;
/*     */     }
/* 259 */     return true;
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
/*     */   public boolean match(String s) {
/*     */     try {
/* 280 */       return match(new ContentType(s));
/* 281 */     } catch (ParseException pex) {
/* 282 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\ContentType.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */