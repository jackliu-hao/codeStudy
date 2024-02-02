/*     */ package javax.activation;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ActivationDataFlavor
/*     */   extends DataFlavor
/*     */ {
/*  57 */   private String mimeType = null;
/*  58 */   private MimeType mimeObject = null;
/*  59 */   private String humanPresentableName = null;
/*  60 */   private Class representationClass = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActivationDataFlavor(Class representationClass, String mimeType, String humanPresentableName) {
/*  81 */     super(mimeType, humanPresentableName);
/*     */ 
/*     */     
/*  84 */     this.mimeType = mimeType;
/*  85 */     this.humanPresentableName = humanPresentableName;
/*  86 */     this.representationClass = representationClass;
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
/*     */   public ActivationDataFlavor(Class representationClass, String humanPresentableName) {
/* 108 */     super(representationClass, humanPresentableName);
/* 109 */     this.mimeType = super.getMimeType();
/* 110 */     this.representationClass = representationClass;
/* 111 */     this.humanPresentableName = humanPresentableName;
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
/*     */   public ActivationDataFlavor(String mimeType, String humanPresentableName) {
/* 130 */     super(mimeType, humanPresentableName);
/* 131 */     this.mimeType = mimeType;
/*     */     try {
/* 133 */       this.representationClass = Class.forName("java.io.InputStream");
/* 134 */     } catch (ClassNotFoundException ex) {}
/*     */ 
/*     */     
/* 137 */     this.humanPresentableName = humanPresentableName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMimeType() {
/* 146 */     return this.mimeType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class getRepresentationClass() {
/* 155 */     return this.representationClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHumanPresentableName() {
/* 164 */     return this.humanPresentableName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHumanPresentableName(String humanPresentableName) {
/* 173 */     this.humanPresentableName = humanPresentableName;
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
/*     */   public boolean equals(DataFlavor dataFlavor) {
/* 185 */     return (isMimeTypeEqual(dataFlavor) && dataFlavor.getRepresentationClass() == this.representationClass);
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
/*     */   public boolean isMimeTypeEqual(String mimeType) {
/* 202 */     MimeType mt = null;
/*     */     try {
/* 204 */       if (this.mimeObject == null)
/* 205 */         this.mimeObject = new MimeType(this.mimeType); 
/* 206 */       mt = new MimeType(mimeType);
/* 207 */     } catch (MimeTypeParseException e) {}
/*     */     
/* 209 */     return this.mimeObject.match(mt);
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
/*     */   protected String normalizeMimeTypeParameter(String parameterName, String parameterValue) {
/* 229 */     return parameterValue;
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
/*     */   protected String normalizeMimeType(String mimeType) {
/* 245 */     return mimeType;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\ActivationDataFlavor.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */