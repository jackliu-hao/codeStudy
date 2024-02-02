/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.bean.copier.CopyOptions;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.lang.mutable.MutablePair;
/*     */ import cn.hutool.core.map.CaseInsensitiveLinkedMap;
/*     */ import cn.hutool.core.map.CaseInsensitiveTreeMap;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class InternalJSONUtil
/*     */ {
/*     */   static Object testValidity(Object obj) throws JSONException {
/*  40 */     if (false == ObjectUtil.isValidIfNumber(obj)) {
/*  41 */       throw new JSONException("JSON does not allow non-finite numbers.");
/*     */     }
/*  43 */     return obj;
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
/*     */   static String valueToString(Object value) throws JSONException {
/*  62 */     if (value == null || value instanceof JSONNull) {
/*  63 */       return JSONNull.NULL.toString();
/*     */     }
/*  65 */     if (value instanceof JSONString)
/*     */       try {
/*  67 */         return ((JSONString)value).toJSONString();
/*  68 */       } catch (Exception e) {
/*  69 */         throw new JSONException(e);
/*     */       }  
/*  71 */     if (value instanceof Number)
/*  72 */       return NumberUtil.toStr((Number)value); 
/*  73 */     if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray)
/*  74 */       return value.toString(); 
/*  75 */     if (value instanceof Map) {
/*  76 */       Map<?, ?> map = (Map<?, ?>)value;
/*  77 */       return (new JSONObject(map)).toString();
/*  78 */     }  if (value instanceof Collection) {
/*  79 */       Collection<?> coll = (Collection)value;
/*  80 */       return (new JSONArray(coll)).toString();
/*  81 */     }  if (ArrayUtil.isArray(value)) {
/*  82 */       return (new JSONArray(value)).toString();
/*     */     }
/*  84 */     return JSONUtil.quote(value.toString());
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
/*     */   public static Object stringToValue(String string) {
/*  96 */     if (StrUtil.isEmpty(string) || "null".equalsIgnoreCase(string)) {
/*  97 */       return JSONNull.NULL;
/*     */     }
/*     */ 
/*     */     
/* 101 */     if ("true".equalsIgnoreCase(string)) {
/* 102 */       return Boolean.TRUE;
/*     */     }
/* 104 */     if ("false".equalsIgnoreCase(string)) {
/* 105 */       return Boolean.FALSE;
/*     */     }
/*     */ 
/*     */     
/* 109 */     char b = string.charAt(0);
/* 110 */     if ((b >= '0' && b <= '9') || b == '-') {
/*     */       try {
/* 112 */         if (StrUtil.containsAnyIgnoreCase(string, new CharSequence[] { ".", "e" }))
/*     */         {
/* 114 */           return new BigDecimal(string);
/*     */         }
/* 116 */         long myLong = Long.parseLong(string);
/* 117 */         if (string.equals(Long.toString(myLong))) {
/* 118 */           if (myLong == (int)myLong) {
/* 119 */             return Integer.valueOf((int)myLong);
/*     */           }
/* 121 */           return Long.valueOf(myLong);
/*     */         }
/*     */       
/*     */       }
/* 125 */       catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 130 */     return string;
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
/*     */   static JSONObject propertyPut(JSONObject jsonObject, Object key, Object value, Filter<MutablePair<String, Object>> filter) {
/* 144 */     String[] path = StrUtil.splitToArray(Convert.toStr(key), '.');
/* 145 */     int last = path.length - 1;
/* 146 */     JSONObject target = jsonObject;
/* 147 */     for (int i = 0; i < last; i++) {
/* 148 */       String segment = path[i];
/* 149 */       JSONObject nextTarget = target.getJSONObject(segment);
/* 150 */       if (nextTarget == null) {
/* 151 */         nextTarget = new JSONObject(target.getConfig());
/* 152 */         target.setOnce(segment, nextTarget, filter);
/*     */       } 
/* 154 */       target = nextTarget;
/*     */     } 
/* 156 */     target.setOnce(path[last], value, filter);
/* 157 */     return jsonObject;
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
/*     */   static boolean defaultIgnoreNullValue(Object obj) {
/* 174 */     return (false == obj instanceof CharSequence && false == obj instanceof JSONTokener && false == obj instanceof Map);
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
/*     */   static CopyOptions toCopyOptions(JSONConfig config) {
/* 187 */     return CopyOptions.create()
/* 188 */       .setIgnoreCase(config.isIgnoreCase())
/* 189 */       .setIgnoreError(config.isIgnoreError())
/* 190 */       .setIgnoreNullValue(config.isIgnoreNullValue())
/* 191 */       .setTransientSupport(config.isTransientSupport());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Map<String, Object> createRawMap(int capacity, JSONConfig config) {
/*     */     Map<String, Object> rawHashMap;
/* 203 */     if (null == config) {
/* 204 */       config = JSONConfig.create();
/*     */     }
/* 206 */     Comparator<String> keyComparator = config.getKeyComparator();
/* 207 */     if (config.isIgnoreCase()) {
/* 208 */       if (null != keyComparator) {
/* 209 */         CaseInsensitiveTreeMap caseInsensitiveTreeMap = new CaseInsensitiveTreeMap(keyComparator);
/*     */       } else {
/* 211 */         CaseInsensitiveLinkedMap caseInsensitiveLinkedMap = new CaseInsensitiveLinkedMap(capacity);
/*     */       }
/*     */     
/* 214 */     } else if (null != keyComparator) {
/* 215 */       rawHashMap = new TreeMap<>(keyComparator);
/*     */     } else {
/* 217 */       rawHashMap = new LinkedHashMap<>(capacity);
/*     */     } 
/*     */     
/* 220 */     return rawHashMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\InternalJSONUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */