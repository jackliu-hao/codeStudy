/*     */ package cn.hutool.core.text.csv;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CsvConfig<T extends CsvConfig<T>>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -8069578249066158459L;
/*  23 */   protected char fieldSeparator = ',';
/*     */ 
/*     */ 
/*     */   
/*  27 */   protected char textDelimiter = '"';
/*     */ 
/*     */ 
/*     */   
/*  31 */   protected Character commentCharacter = Character.valueOf('#');
/*     */ 
/*     */ 
/*     */   
/*  35 */   protected Map<String, String> headerAlias = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T setFieldSeparator(char fieldSeparator) {
/*  44 */     this.fieldSeparator = fieldSeparator;
/*  45 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T setTextDelimiter(char textDelimiter) {
/*  55 */     this.textDelimiter = textDelimiter;
/*  56 */     return (T)this;
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
/*     */   public T disableComment() {
/*  68 */     return setCommentCharacter(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T setCommentCharacter(Character commentCharacter) {
/*  79 */     this.commentCharacter = commentCharacter;
/*  80 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T setHeaderAlias(Map<String, String> headerAlias) {
/*  91 */     this.headerAlias = headerAlias;
/*  92 */     return (T)this;
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
/*     */   public T addHeaderAlias(String header, String alias) {
/* 104 */     this.headerAlias.put(header, alias);
/* 105 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T removeHeaderAlias(String header) {
/* 116 */     this.headerAlias.remove(header);
/* 117 */     return (T)this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */