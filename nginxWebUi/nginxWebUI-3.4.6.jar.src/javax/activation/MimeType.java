/*     */ package javax.activation;
/*     */ 
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MimeType
/*     */   implements Externalizable
/*     */ {
/*     */   private String primaryType;
/*     */   private String subType;
/*     */   private MimeTypeParameterList parameters;
/*     */   private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
/*     */   
/*     */   public MimeType() {
/*  54 */     this.primaryType = "application";
/*  55 */     this.subType = "*";
/*  56 */     this.parameters = new MimeTypeParameterList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MimeType(String rawdata) throws MimeTypeParseException {
/*  65 */     parse(rawdata);
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
/*     */   public MimeType(String primary, String sub) throws MimeTypeParseException {
/*  79 */     if (isValidToken(primary)) {
/*  80 */       this.primaryType = primary.toLowerCase();
/*     */     } else {
/*  82 */       throw new MimeTypeParseException("Primary type is invalid.");
/*     */     } 
/*     */ 
/*     */     
/*  86 */     if (isValidToken(sub)) {
/*  87 */       this.subType = sub.toLowerCase();
/*     */     } else {
/*  89 */       throw new MimeTypeParseException("Sub type is invalid.");
/*     */     } 
/*     */     
/*  92 */     this.parameters = new MimeTypeParameterList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parse(String rawdata) throws MimeTypeParseException {
/*  99 */     int slashIndex = rawdata.indexOf('/');
/* 100 */     int semIndex = rawdata.indexOf(';');
/* 101 */     if (slashIndex < 0 && semIndex < 0)
/*     */     {
/*     */       
/* 104 */       throw new MimeTypeParseException("Unable to find a sub type."); } 
/* 105 */     if (slashIndex < 0 && semIndex >= 0)
/*     */     {
/*     */       
/* 108 */       throw new MimeTypeParseException("Unable to find a sub type."); } 
/* 109 */     if (slashIndex >= 0 && semIndex < 0) {
/*     */       
/* 111 */       this.primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase();
/* 112 */       this.subType = rawdata.substring(slashIndex + 1).trim().toLowerCase();
/* 113 */       this.parameters = new MimeTypeParameterList();
/* 114 */     } else if (slashIndex < semIndex) {
/*     */       
/* 116 */       this.primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase();
/* 117 */       this.subType = rawdata.substring(slashIndex + 1, semIndex).trim().toLowerCase();
/*     */       
/* 119 */       this.parameters = new MimeTypeParameterList(rawdata.substring(semIndex));
/*     */     }
/*     */     else {
/*     */       
/* 123 */       throw new MimeTypeParseException("Unable to find a sub type.");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 129 */     if (!isValidToken(this.primaryType)) {
/* 130 */       throw new MimeTypeParseException("Primary type is invalid.");
/*     */     }
/*     */     
/* 133 */     if (!isValidToken(this.subType)) {
/* 134 */       throw new MimeTypeParseException("Sub type is invalid.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPrimaryType() {
/* 143 */     return this.primaryType;
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
/*     */   public void setPrimaryType(String primary) throws MimeTypeParseException {
/* 155 */     if (!isValidToken(this.primaryType))
/* 156 */       throw new MimeTypeParseException("Primary type is invalid."); 
/* 157 */     this.primaryType = primary.toLowerCase();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSubType() {
/* 166 */     return this.subType;
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
/*     */   public void setSubType(String sub) throws MimeTypeParseException {
/* 178 */     if (!isValidToken(this.subType))
/* 179 */       throw new MimeTypeParseException("Sub type is invalid."); 
/* 180 */     this.subType = sub.toLowerCase();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MimeTypeParameterList getParameters() {
/* 189 */     return this.parameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParameter(String name) {
/* 200 */     return this.parameters.get(name);
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
/* 211 */     this.parameters.set(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeParameter(String name) {
/* 220 */     this.parameters.remove(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 227 */     return getBaseType() + this.parameters.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBaseType() {
/* 237 */     return this.primaryType + "/" + this.subType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(MimeType type) {
/* 248 */     return (this.primaryType.equals(type.getPrimaryType()) && (this.subType.equals("*") || type.getSubType().equals("*") || this.subType.equals(type.getSubType())));
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
/*     */   public boolean match(String rawdata) throws MimeTypeParseException {
/* 262 */     return match(new MimeType(rawdata));
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
/*     */   public void writeExternal(ObjectOutput out) throws IOException {
/* 275 */     out.writeUTF(toString());
/* 276 */     out.flush();
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
/*     */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
/*     */     try {
/* 293 */       parse(in.readUTF());
/* 294 */     } catch (MimeTypeParseException e) {
/* 295 */       throw new IOException(e.toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isTokenChar(char c) {
/* 305 */     return (c > ' ' && c < '' && "()<>@,;:/[]?=\\\"".indexOf(c) < 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isValidToken(String s) {
/* 312 */     int len = s.length();
/* 313 */     if (len > 0) {
/* 314 */       for (int i = 0; i < len; i++) {
/* 315 */         char c = s.charAt(i);
/* 316 */         if (!isTokenChar(c)) {
/* 317 */           return false;
/*     */         }
/*     */       } 
/* 320 */       return true;
/*     */     } 
/* 322 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\MimeType.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */