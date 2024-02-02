/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.collection.ArrayIter;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.lang.mutable.Mutable;
/*     */ import cn.hutool.core.lang.mutable.MutablePair;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.TypeUtil;
/*     */ import cn.hutool.json.serialize.GlobalSerializeMapping;
/*     */ import cn.hutool.json.serialize.JSONSerializer;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ObjectMapper
/*     */ {
/*     */   private final Object source;
/*     */   
/*     */   public static ObjectMapper of(Object source) {
/*  49 */     return new ObjectMapper(source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectMapper(Object source) {
/*  60 */     this.source = source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void map(JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
/*  71 */     Object source = this.source;
/*  72 */     if (null == source) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  77 */     JSONSerializer serializer = GlobalSerializeMapping.getSerializer(source.getClass());
/*  78 */     if (serializer instanceof cn.hutool.json.serialize.JSONObjectSerializer) {
/*  79 */       serializer.serialize(jsonObject, source);
/*     */       
/*     */       return;
/*     */     } 
/*  83 */     if (source instanceof JSONArray)
/*     */     {
/*  85 */       throw new JSONException("Unsupported type [{}] to JSONObject!", new Object[] { source.getClass() });
/*     */     }
/*     */     
/*  88 */     if (source instanceof Map) {
/*     */       
/*  90 */       for (Map.Entry<?, ?> e : (Iterable<Map.Entry<?, ?>>)((Map)source).entrySet()) {
/*  91 */         jsonObject.set(Convert.toStr(e.getKey()), e.getValue(), filter, false);
/*     */       }
/*  93 */     } else if (source instanceof Map.Entry) {
/*  94 */       Map.Entry entry = (Map.Entry)source;
/*  95 */       jsonObject.set(Convert.toStr(entry.getKey()), entry.getValue(), filter, false);
/*  96 */     } else if (source instanceof CharSequence) {
/*     */       
/*  98 */       mapFromStr((CharSequence)source, jsonObject, filter);
/*  99 */     } else if (source instanceof Reader) {
/* 100 */       mapFromTokener(new JSONTokener((Reader)source, jsonObject.getConfig()), jsonObject, filter);
/* 101 */     } else if (source instanceof InputStream) {
/* 102 */       mapFromTokener(new JSONTokener((InputStream)source, jsonObject.getConfig()), jsonObject, filter);
/* 103 */     } else if (source instanceof byte[]) {
/* 104 */       mapFromTokener(new JSONTokener(IoUtil.toStream((byte[])source), jsonObject.getConfig()), jsonObject, filter);
/* 105 */     } else if (source instanceof JSONTokener) {
/*     */       
/* 107 */       mapFromTokener((JSONTokener)source, jsonObject, filter);
/* 108 */     } else if (source instanceof ResourceBundle) {
/*     */       
/* 110 */       mapFromResourceBundle((ResourceBundle)source, jsonObject, filter);
/* 111 */     } else if (BeanUtil.isReadableBean(source.getClass())) {
/*     */ 
/*     */       
/* 114 */       mapFromBean(source, jsonObject);
/*     */     } else {
/*     */       
/* 117 */       throw new JSONException("Unsupported type [{}] to JSONObject!", new Object[] { source.getClass() });
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
/*     */   public void map(JSONArray jsonArray, Filter<Mutable<Object>> filter) throws JSONException {
/* 130 */     Object source = this.source;
/* 131 */     if (null == source) {
/*     */       return;
/*     */     }
/*     */     
/* 135 */     JSONSerializer serializer = GlobalSerializeMapping.getSerializer(source.getClass());
/* 136 */     if (null != serializer && JSONArray.class.equals(TypeUtil.getTypeArgument(serializer.getClass()))) {
/*     */       
/* 138 */       serializer.serialize(jsonArray, source);
/* 139 */     } else if (source instanceof CharSequence) {
/*     */       
/* 141 */       mapFromStr((CharSequence)source, jsonArray, filter);
/* 142 */     } else if (source instanceof Reader) {
/* 143 */       mapFromTokener(new JSONTokener((Reader)source, jsonArray.getConfig()), jsonArray, filter);
/* 144 */     } else if (source instanceof InputStream) {
/* 145 */       mapFromTokener(new JSONTokener((InputStream)source, jsonArray.getConfig()), jsonArray, filter);
/* 146 */     } else if (source instanceof byte[]) {
/*     */       
/*     */       try {
/* 149 */         mapFromTokener(new JSONTokener(IoUtil.toStream((byte[])source), jsonArray.getConfig()), jsonArray, filter);
/* 150 */       } catch (JSONException ignore) {
/*     */ 
/*     */         
/* 153 */         for (byte b : (byte[])source) {
/* 154 */           jsonArray.add(Byte.valueOf(b));
/*     */         }
/*     */       } 
/* 157 */     } else if (source instanceof JSONTokener) {
/* 158 */       mapFromTokener((JSONTokener)source, jsonArray, filter);
/*     */     } else {
/*     */       Iterator<?> iter;
/* 161 */       if (ArrayUtil.isArray(source)) {
/* 162 */         ArrayIter arrayIter = new ArrayIter(source);
/* 163 */       } else if (source instanceof Iterator) {
/* 164 */         iter = (Iterator)source;
/* 165 */       } else if (source instanceof Iterable) {
/* 166 */         iter = ((Iterable)source).iterator();
/*     */       } else {
/* 168 */         throw new JSONException("JSONArray initial value should be a string or collection or array.");
/*     */       } 
/*     */       
/* 171 */       JSONConfig config = jsonArray.getConfig();
/*     */       
/* 173 */       while (iter.hasNext()) {
/* 174 */         Object next = iter.next();
/*     */         
/* 176 */         if (next != source) {
/* 177 */           jsonArray.addRaw(JSONUtil.wrap(next, config), filter);
/*     */         }
/*     */       } 
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
/*     */   private static void mapFromResourceBundle(ResourceBundle bundle, JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
/* 192 */     Enumeration<String> keys = bundle.getKeys();
/* 193 */     while (keys.hasMoreElements()) {
/* 194 */       String key = keys.nextElement();
/* 195 */       if (key != null) {
/* 196 */         InternalJSONUtil.propertyPut(jsonObject, key, bundle.getString(key), filter);
/*     */       }
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
/*     */   private static void mapFromStr(CharSequence source, JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
/* 209 */     String jsonStr = StrUtil.trim(source);
/* 210 */     if (StrUtil.startWith(jsonStr, '<')) {
/*     */       
/* 212 */       XML.toJSONObject(jsonObject, jsonStr, false);
/*     */       return;
/*     */     } 
/* 215 */     mapFromTokener(new JSONTokener(StrUtil.trim(source), jsonObject.getConfig()), jsonObject, filter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void mapFromStr(CharSequence source, JSONArray jsonArray, Filter<Mutable<Object>> filter) {
/* 226 */     if (null != source) {
/* 227 */       mapFromTokener(new JSONTokener(StrUtil.trim(source), jsonArray.getConfig()), jsonArray, filter);
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
/*     */   private static void mapFromTokener(JSONTokener x, JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
/* 239 */     JSONParser.of(x).parseTo(jsonObject, filter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void mapFromTokener(JSONTokener x, JSONArray jsonArray, Filter<Mutable<Object>> filter) {
/* 250 */     JSONParser.of(x).parseTo(jsonArray, filter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void mapFromBean(Object bean, JSONObject jsonObject) {
/* 260 */     BeanUtil.beanToMap(bean, (Map)jsonObject, InternalJSONUtil.toCopyOptions(jsonObject.getConfig()));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\ObjectMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */