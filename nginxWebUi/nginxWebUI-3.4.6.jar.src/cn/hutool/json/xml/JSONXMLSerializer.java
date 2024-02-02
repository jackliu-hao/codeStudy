/*     */ package cn.hutool.json.xml;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.EscapeUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSONArray;
/*     */ import cn.hutool.json.JSONException;
/*     */ import cn.hutool.json.JSONObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSONXMLSerializer
/*     */ {
/*     */   public static String toXml(Object object) throws JSONException {
/*  27 */     return toXml(object, null);
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
/*     */   public static String toXml(Object object, String tagName) throws JSONException {
/*  39 */     return toXml(object, tagName, new String[] { "content" });
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
/*     */   public static String toXml(Object object, String tagName, String... contentKeys) throws JSONException {
/*  52 */     if (null == object) {
/*  53 */       return null;
/*     */     }
/*     */     
/*  56 */     StringBuilder sb = new StringBuilder();
/*  57 */     if (object instanceof JSONObject) {
/*     */ 
/*     */       
/*  60 */       appendTag(sb, tagName, false);
/*     */ 
/*     */       
/*  63 */       ((JSONObject)object).forEach((key, value) -> {
/*     */             if (ArrayUtil.isArray(value)) {
/*     */               value = new JSONArray(value);
/*     */             }
/*     */             
/*     */             if (ArrayUtil.contains((Object[])contentKeys, key)) {
/*     */               if (value instanceof JSONArray) {
/*     */                 int i = 0;
/*     */                 
/*     */                 for (Object val : value) {
/*     */                   if (i > 0) {
/*     */                     sb.append('\n');
/*     */                   }
/*     */                   
/*     */                   sb.append(EscapeUtil.escapeXml(val.toString()));
/*     */                   
/*     */                   i++;
/*     */                 } 
/*     */               } else {
/*     */                 sb.append(EscapeUtil.escapeXml(value.toString()));
/*     */               } 
/*     */             } else if (StrUtil.isEmptyIfStr(value)) {
/*     */               sb.append(wrapWithTag(null, key));
/*     */             } else if (value instanceof JSONArray) {
/*     */               for (Object val : value) {
/*     */                 if (val instanceof JSONArray) {
/*     */                   sb.append(wrapWithTag(toXml(val), key));
/*     */                   
/*     */                   continue;
/*     */                 } 
/*     */                 
/*     */                 sb.append(toXml(val, key));
/*     */               } 
/*     */             } else {
/*     */               sb.append(toXml(value, key));
/*     */             } 
/*     */           });
/*     */       
/* 101 */       appendTag(sb, tagName, true);
/* 102 */       return sb.toString();
/*     */     } 
/*     */     
/* 105 */     if (ArrayUtil.isArray(object)) {
/* 106 */       object = new JSONArray(object);
/*     */     }
/*     */     
/* 109 */     if (object instanceof JSONArray) {
/* 110 */       for (Object val : object)
/*     */       {
/*     */ 
/*     */         
/* 114 */         sb.append(toXml(val, (tagName == null) ? "array" : tagName));
/*     */       }
/* 116 */       return sb.toString();
/*     */     } 
/*     */     
/* 119 */     return wrapWithTag(EscapeUtil.escapeXml(object.toString()), tagName);
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
/*     */   private static void appendTag(StringBuilder sb, String tagName, boolean isEndTag) {
/* 131 */     if (StrUtil.isNotBlank(tagName)) {
/* 132 */       sb.append('<');
/* 133 */       if (isEndTag) {
/* 134 */         sb.append('/');
/*     */       }
/* 136 */       sb.append(tagName).append('>');
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
/*     */   private static String wrapWithTag(String content, String tagName) {
/* 149 */     if (StrUtil.isBlank(tagName)) {
/* 150 */       return StrUtil.wrap(content, "\"");
/*     */     }
/*     */     
/* 153 */     if (StrUtil.isEmpty(content)) {
/* 154 */       return "<" + tagName + "/>";
/*     */     }
/* 156 */     return "<" + tagName + ">" + content + "</" + tagName + ">";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\xml\JSONXMLSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */