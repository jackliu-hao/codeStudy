/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.Entity;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Wrapper
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private Character preWrapQuote;
/*     */   private Character sufWrapQuote;
/*     */   
/*     */   public Wrapper() {}
/*     */   
/*     */   public Wrapper(Character wrapQuote) {
/*  42 */     this.preWrapQuote = wrapQuote;
/*  43 */     this.sufWrapQuote = wrapQuote;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wrapper(Character preWrapQuote, Character sufWrapQuote) {
/*  53 */     this.preWrapQuote = preWrapQuote;
/*  54 */     this.sufWrapQuote = sufWrapQuote;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getPreWrapQuote() {
/*  63 */     return this.preWrapQuote.charValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPreWrapQuote(Character preWrapQuote) {
/*  72 */     this.preWrapQuote = preWrapQuote;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getSufWrapQuote() {
/*  79 */     return this.sufWrapQuote.charValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSufWrapQuote(Character sufWrapQuote) {
/*  88 */     this.sufWrapQuote = sufWrapQuote;
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
/*     */   public String wrap(String field) {
/* 100 */     if (this.preWrapQuote == null || this.sufWrapQuote == null || StrUtil.isBlank(field)) {
/* 101 */       return field;
/*     */     }
/*     */ 
/*     */     
/* 105 */     if (StrUtil.isSurround(field, this.preWrapQuote.charValue(), this.sufWrapQuote.charValue())) {
/* 106 */       return field;
/*     */     }
/*     */ 
/*     */     
/* 110 */     if (StrUtil.containsAnyIgnoreCase(field, new CharSequence[] { "*", "(", " ", " as " })) {
/* 111 */       return field;
/*     */     }
/*     */ 
/*     */     
/* 115 */     if (field.contains(".")) {
/* 116 */       Collection<String> target = CollUtil.edit(StrUtil.split(field, '.', 2), t -> StrUtil.format("{}{}{}", new Object[] { this.preWrapQuote, t, this.sufWrapQuote }));
/* 117 */       return CollectionUtil.join(target, ".");
/*     */     } 
/*     */     
/* 120 */     return StrUtil.format("{}{}{}", new Object[] { this.preWrapQuote, field, this.sufWrapQuote });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] wrap(String... fields) {
/* 131 */     if (ArrayUtil.isEmpty((Object[])fields)) {
/* 132 */       return fields;
/*     */     }
/*     */     
/* 135 */     String[] wrappedFields = new String[fields.length];
/* 136 */     for (int i = 0; i < fields.length; i++) {
/* 137 */       wrappedFields[i] = wrap(fields[i]);
/*     */     }
/*     */     
/* 140 */     return wrappedFields;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> wrap(Collection<String> fields) {
/* 151 */     if (CollectionUtil.isEmpty(fields)) {
/* 152 */       return fields;
/*     */     }
/*     */     
/* 155 */     return Arrays.asList(wrap(fields.<String>toArray(new String[0])));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity wrap(Entity entity) {
/* 166 */     if (null == entity) {
/* 167 */       return null;
/*     */     }
/*     */     
/* 170 */     Entity wrapedEntity = new Entity();
/*     */ 
/*     */     
/* 173 */     wrapedEntity.setTableName(wrap(entity.getTableName()));
/*     */ 
/*     */     
/* 176 */     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)entity.entrySet()) {
/* 177 */       wrapedEntity.set(wrap(entry.getKey()), entry.getValue());
/*     */     }
/*     */     
/* 180 */     return wrapedEntity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition[] wrap(Condition... conditions) {
/* 191 */     Condition[] clonedConditions = new Condition[conditions.length];
/* 192 */     if (ArrayUtil.isNotEmpty((Object[])conditions))
/*     */     {
/* 194 */       for (int i = 0; i < conditions.length; i++) {
/* 195 */         Condition clonedCondition = (Condition)conditions[i].clone();
/* 196 */         clonedCondition.setField(wrap(clonedCondition.getField()));
/* 197 */         clonedConditions[i] = clonedCondition;
/*     */       } 
/*     */     }
/*     */     
/* 201 */     return clonedConditions;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\Wrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */