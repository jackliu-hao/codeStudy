/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeiderMorseEncoder
/*     */   implements StringEncoder
/*     */ {
/*  83 */   private PhoneticEngine engine = new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, true);
/*     */ 
/*     */   
/*     */   public Object encode(Object source) throws EncoderException {
/*  87 */     if (!(source instanceof String)) {
/*  88 */       throw new EncoderException("BeiderMorseEncoder encode parameter is not of type String");
/*     */     }
/*  90 */     return encode((String)source);
/*     */   }
/*     */ 
/*     */   
/*     */   public String encode(String source) throws EncoderException {
/*  95 */     if (source == null) {
/*  96 */       return null;
/*     */     }
/*  98 */     return this.engine.encode(source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NameType getNameType() {
/* 107 */     return this.engine.getNameType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RuleType getRuleType() {
/* 116 */     return this.engine.getRuleType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConcat() {
/* 125 */     return this.engine.isConcat();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConcat(boolean concat) {
/* 136 */     this
/*     */ 
/*     */       
/* 139 */       .engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), concat, this.engine.getMaxPhonemes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNameType(NameType nameType) {
/* 150 */     this
/*     */ 
/*     */       
/* 153 */       .engine = new PhoneticEngine(nameType, this.engine.getRuleType(), this.engine.isConcat(), this.engine.getMaxPhonemes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRuleType(RuleType ruleType) {
/* 163 */     this
/*     */ 
/*     */       
/* 166 */       .engine = new PhoneticEngine(this.engine.getNameType(), ruleType, this.engine.isConcat(), this.engine.getMaxPhonemes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxPhonemes(int maxPhonemes) {
/* 177 */     this
/*     */       
/* 179 */       .engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), this.engine.isConcat(), maxPhonemes);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\bm\BeiderMorseEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */