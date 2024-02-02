/*     */ package cn.hutool.core.bean.copier;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.convert.TypeConverter;
/*     */ import cn.hutool.core.lang.Editor;
/*     */ import cn.hutool.core.lang.func.Func1;
/*     */ import cn.hutool.core.lang.func.LambdaUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.BiPredicate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CopyOptions
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected Class<?> editable;
/*     */   protected boolean ignoreNullValue;
/*     */   private BiPredicate<Field, Object> propertiesFilter;
/*     */   protected boolean ignoreError;
/*     */   protected boolean ignoreCase;
/*     */   private Editor<String> fieldNameEditor;
/*     */   protected BiFunction<String, Object, Object> fieldValueEditor;
/*     */   protected boolean transientSupport = true;
/*     */   protected boolean override = true;
/*     */   protected TypeConverter converter;
/*     */   
/*     */   public CopyOptions() {
/*  73 */     this.converter = ((type, value) -> Convert.convertWithCheck(type, value, null, this.ignoreError)); } public CopyOptions(Class<?> editable, boolean ignoreNullValue, String... ignoreProperties) { this.converter = ((type, value) -> Convert.convertWithCheck(type, value, null, this.ignoreError));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     this.propertiesFilter = ((f, v) -> true);
/* 115 */     this.editable = editable;
/* 116 */     this.ignoreNullValue = ignoreNullValue;
/* 117 */     setIgnoreProperties(ignoreProperties); }
/*     */   
/*     */   public static CopyOptions create() {
/*     */     return new CopyOptions();
/*     */   }
/*     */   public static CopyOptions create(Class<?> editable, boolean ignoreNullValue, String... ignoreProperties) {
/*     */     return new CopyOptions(editable, ignoreNullValue, ignoreProperties);
/*     */   }
/*     */   
/*     */   public CopyOptions setEditable(Class<?> editable) {
/* 127 */     this.editable = editable;
/* 128 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setIgnoreNullValue(boolean ignoreNullVall) {
/* 138 */     this.ignoreNullValue = ignoreNullVall;
/* 139 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions ignoreNullValue() {
/* 149 */     return setIgnoreNullValue(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setPropertiesFilter(BiPredicate<Field, Object> propertiesFilter) {
/* 160 */     this.propertiesFilter = propertiesFilter;
/* 161 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setIgnoreProperties(String... ignoreProperties) {
/* 171 */     return setPropertiesFilter((field, o) -> (false == ArrayUtil.contains((Object[])ignoreProperties, field.getName())));
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
/*     */   public <P, R> CopyOptions setIgnoreProperties(Func1<P, R>... funcs) {
/* 185 */     Set<String> ignoreProperties = ArrayUtil.mapToSet((Object[])funcs, LambdaUtil::getFieldName);
/* 186 */     return setPropertiesFilter((field, o) -> (false == ignoreProperties.contains(field.getName())));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setIgnoreError(boolean ignoreError) {
/* 196 */     this.ignoreError = ignoreError;
/* 197 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions ignoreError() {
/* 207 */     return setIgnoreError(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setIgnoreCase(boolean ignoreCase) {
/* 217 */     this.ignoreCase = ignoreCase;
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions ignoreCase() {
/* 228 */     return setIgnoreCase(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setFieldMapping(Map<String, String> fieldMapping) {
/* 239 */     return setFieldNameEditor(key -> (String)fieldMapping.getOrDefault(key, key));
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
/*     */   public CopyOptions setFieldNameEditor(Editor<String> fieldNameEditor) {
/* 253 */     this.fieldNameEditor = fieldNameEditor;
/* 254 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setFieldValueEditor(BiFunction<String, Object, Object> fieldValueEditor) {
/* 265 */     this.fieldValueEditor = fieldValueEditor;
/* 266 */     return this;
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
/*     */   protected Object editFieldValue(String fieldName, Object fieldValue) {
/* 278 */     return (null != this.fieldValueEditor) ? this.fieldValueEditor
/* 279 */       .apply(fieldName, fieldValue) : fieldValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setTransientSupport(boolean transientSupport) {
/* 290 */     this.transientSupport = transientSupport;
/* 291 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setOverride(boolean override) {
/* 302 */     this.override = override;
/* 303 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyOptions setConverter(TypeConverter converter) {
/* 314 */     this.converter = converter;
/* 315 */     return this;
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
/*     */   protected Object convertField(Type targetType, Object fieldValue) {
/* 328 */     return (null != this.converter) ? this.converter
/* 329 */       .convert(targetType, fieldValue) : fieldValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String editFieldName(String fieldName) {
/* 340 */     return (null != this.fieldNameEditor) ? (String)this.fieldNameEditor.edit(fieldName) : fieldName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean testPropertyFilter(Field field, Object value) {
/* 351 */     return (null == this.propertiesFilter || this.propertiesFilter.test(field, value));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\CopyOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */