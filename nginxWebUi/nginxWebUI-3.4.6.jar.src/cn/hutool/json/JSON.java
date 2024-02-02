/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.lang.TypeReference;
/*     */ import java.io.Serializable;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface JSON
/*     */   extends Cloneable, Serializable
/*     */ {
/*     */   Object getByPath(String paramString);
/*     */   
/*     */   void putByPath(String paramString, Object paramObject);
/*     */   
/*     */   <T> T getByPath(String paramString, Class<T> paramClass);
/*     */   
/*     */   default String toStringPretty() throws JSONException {
/*  99 */     return toJSONString(4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String toJSONString(int indentFactor) throws JSONException {
/* 110 */     StringWriter sw = new StringWriter();
/* 111 */     synchronized (sw.getBuffer()) {
/* 112 */       return write(sw, indentFactor, 0).toString();
/*     */     } 
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
/*     */   default Writer write(Writer writer) throws JSONException {
/* 125 */     return write(writer, 0, 0);
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
/*     */   Writer write(Writer paramWriter, int paramInt1, int paramInt2) throws JSONException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default <T> T toBean(Class<T> clazz) {
/* 148 */     return toBean(clazz);
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
/*     */   default <T> T toBean(TypeReference<T> reference) {
/* 160 */     return toBean(reference.getType());
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
/*     */   default <T> T toBean(Type type) {
/* 172 */     return toBean(type, false);
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
/*     */   default <T> T toBean(Type type, boolean ignoreError) {
/* 185 */     return JSONConverter.jsonConvert(type, this, ignoreError);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSON.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */