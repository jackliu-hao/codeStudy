/*     */ package org.noear.snack.to;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Parameter;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.math.BigInteger;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.sql.Date;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.atomic.DoubleAdder;
/*     */ import java.util.concurrent.atomic.LongAdder;
/*     */ import org.noear.snack.ONode;
/*     */ import org.noear.snack.ONodeData;
/*     */ import org.noear.snack.ONodeType;
/*     */ import org.noear.snack.OValue;
/*     */ import org.noear.snack.OValueType;
/*     */ import org.noear.snack.core.Context;
/*     */ import org.noear.snack.core.DEFAULTS;
/*     */ import org.noear.snack.core.NodeDecoderEntity;
/*     */ import org.noear.snack.core.exts.ClassWrap;
/*     */ import org.noear.snack.core.exts.EnumWrap;
/*     */ import org.noear.snack.core.exts.FieldWrap;
/*     */ import org.noear.snack.core.utils.BeanUtil;
/*     */ import org.noear.snack.core.utils.ParameterizedTypeImpl;
/*     */ import org.noear.snack.core.utils.TypeUtil;
/*     */ import org.noear.snack.exception.SnackException;
/*     */ 
/*     */ public class ObjectToer implements Toer {
/*     */   public void handle(Context ctx) throws Exception {
/*  37 */     ONode o = (ONode)ctx.source;
/*     */     
/*  39 */     if (null != o) {
/*  40 */       ctx.target = analyse(ctx, o, ctx.target, ctx.target_clz, ctx.target_type, null);
/*     */     }
/*     */   }
/*     */   
/*     */   private Object analyse(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
/*  45 */     if (o == null) {
/*  46 */       return null;
/*     */     }
/*     */     
/*  49 */     if (clz != null && 
/*  50 */       ONode.class.isAssignableFrom(clz)) {
/*  51 */       return o;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  56 */     if (o.isObject() || o.isArray()) {
/*  57 */       clz = getTypeByNode(ctx, o, clz);
/*     */     }
/*     */     
/*  60 */     if (clz != null)
/*     */     {
/*  62 */       for (NodeDecoderEntity decoder : ctx.options.decoders()) {
/*  63 */         if (decoder.isDecodable(clz)) {
/*  64 */           return decoder.decode(o, clz);
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  70 */     if (String.class == clz) {
/*  71 */       return o.getString();
/*     */     }
/*     */ 
/*     */     
/*  75 */     switch (o.nodeType()) {
/*     */       case Value:
/*  77 */         return analyseVal(ctx, o.nodeData(), clz);
/*     */       
/*     */       case Object:
/*  80 */         o.remove(ctx.options.getTypePropertyName());
/*     */         
/*  82 */         if (Properties.class.isAssignableFrom(clz))
/*  83 */           return analyseProps(ctx, o, (Properties)rst, clz, type, genericInfo); 
/*  84 */         if (Map.class.isAssignableFrom(clz))
/*  85 */           return analyseMap(ctx, o, clz, type, genericInfo); 
/*  86 */         if (StackTraceElement.class.isAssignableFrom(clz)) {
/*  87 */           String declaringClass = o.get("declaringClass").getString();
/*  88 */           if (declaringClass == null)
/*     */           {
/*  90 */             declaringClass = o.get("className").getString();
/*     */           }
/*     */           
/*  93 */           return new StackTraceElement(declaringClass, o
/*     */               
/*  95 */               .get("methodName").getString(), o
/*  96 */               .get("fileName").getString(), o
/*  97 */               .get("lineNumber").getInt());
/*     */         } 
/*  99 */         if (type instanceof ParameterizedType) {
/* 100 */           genericInfo = TypeUtil.getGenericInfo(type);
/*     */         }
/*     */         
/* 103 */         return analyseBean(ctx, o, rst, clz, type, genericInfo);
/*     */       
/*     */       case Array:
/* 106 */         if (clz.isArray()) {
/* 107 */           return analyseArray(ctx, o.nodeData(), clz);
/*     */         }
/* 109 */         return analyseCollection(ctx, o, clz, type, genericInfo);
/*     */     } 
/*     */     
/* 112 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean is(Class<?> s, Class<?> t) {
/* 117 */     return s.isAssignableFrom(t);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object analyseVal(Context ctx, ONodeData d, Class<?> clz) throws Exception {
/* 122 */     OValue v = d.value;
/*     */     
/* 124 */     if (v.type() == OValueType.Null) {
/* 125 */       return null;
/*     */     }
/*     */     
/* 128 */     if (clz == null) {
/* 129 */       return v.getRaw();
/*     */     }
/*     */     
/* 132 */     if (is(Byte.class, clz) || clz == byte.class)
/* 133 */       return Byte.valueOf((byte)(int)v.getLong()); 
/* 134 */     if (is(Short.class, clz) || clz == short.class)
/* 135 */       return Short.valueOf(v.getShort()); 
/* 136 */     if (is(Integer.class, clz) || clz == int.class)
/* 137 */       return Integer.valueOf(v.getInt()); 
/* 138 */     if (is(Long.class, clz) || clz == long.class)
/* 139 */       return Long.valueOf(v.getLong()); 
/* 140 */     if (is(LongAdder.class, clz)) {
/* 141 */       LongAdder tmp = new LongAdder();
/* 142 */       tmp.add(v.getLong());
/* 143 */       return tmp;
/* 144 */     }  if (is(Float.class, clz) || clz == float.class)
/* 145 */       return Float.valueOf(v.getFloat()); 
/* 146 */     if (is(Double.class, clz) || clz == double.class)
/* 147 */       return Double.valueOf(v.getDouble()); 
/* 148 */     if (is(DoubleAdder.class, clz)) {
/* 149 */       DoubleAdder tmp = new DoubleAdder();
/* 150 */       tmp.add(v.getDouble());
/* 151 */       return tmp;
/* 152 */     }  if (is(Boolean.class, clz) || clz == boolean.class)
/* 153 */       return Boolean.valueOf(v.getBoolean()); 
/* 154 */     if (is(Character.class, clz) || clz == char.class)
/* 155 */       return Character.valueOf(v.getChar()); 
/* 156 */     if (is(String.class, clz))
/* 157 */       return v.getString(); 
/* 158 */     if (is(Timestamp.class, clz))
/* 159 */       return new Timestamp(v.getLong()); 
/* 160 */     if (is(Date.class, clz))
/* 161 */       return new Date(v.getLong()); 
/* 162 */     if (is(Time.class, clz))
/* 163 */       return new Time(v.getLong()); 
/* 164 */     if (is(Date.class, clz))
/* 165 */       return v.getDate(); 
/* 166 */     if (is(LocalDateTime.class, clz))
/* 167 */       return v.getDate().toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalDateTime(); 
/* 168 */     if (is(LocalDate.class, clz))
/* 169 */       return v.getDate().toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalDate(); 
/* 170 */     if (is(LocalTime.class, clz))
/* 171 */       return v.getDate().toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalTime(); 
/* 172 */     if (is(BigDecimal.class, clz)) {
/* 173 */       if (v.type() == OValueType.Number && 
/* 174 */         v.getRawNumber() instanceof BigDecimal) {
/* 175 */         return v.getRawNumber();
/*     */       }
/*     */ 
/*     */       
/* 179 */       return new BigDecimal(v.getString());
/* 180 */     }  if (is(BigInteger.class, clz)) {
/* 181 */       if (v.type() == OValueType.Number && 
/* 182 */         v.getRawNumber() instanceof BigInteger) {
/* 183 */         return v.getRawNumber();
/*     */       }
/*     */ 
/*     */       
/* 187 */       return new BigInteger(v.getString());
/* 188 */     }  if (clz.isEnum())
/* 189 */       return analyseEnum(ctx, d, clz); 
/* 190 */     if (is(Class.class, clz))
/* 191 */       return BeanUtil.loadClass(v.getString()); 
/* 192 */     if (is(Object.class, clz)) {
/* 193 */       Object val = v.getRaw();
/*     */       
/* 195 */       if (val instanceof String && clz.isInterface()) {
/*     */         
/* 197 */         Class<?> valClz = BeanUtil.loadClass((String)val);
/* 198 */         return BeanUtil.newInstance(valClz);
/*     */       } 
/* 200 */       return val;
/*     */     } 
/*     */     
/* 203 */     throw new SnackException("unsupport type " + clz.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public Object analyseEnum(Context ctx, ONodeData d, Class<?> target) {
/* 208 */     EnumWrap ew = TypeUtil.createEnum(target);
/* 209 */     if (d.value.type() == OValueType.String) {
/* 210 */       return ew.get(d.value.getString());
/*     */     }
/* 212 */     return ew.get(d.value.getInt());
/*     */   }
/*     */ 
/*     */   
/*     */   public Object analyseArray(Context ctx, ONodeData d, Class<?> target) throws Exception {
/* 217 */     int len = d.array.size();
/*     */     
/* 219 */     if (is(byte[].class, target)) {
/* 220 */       byte[] val = new byte[len];
/* 221 */       for (int i = 0; i < len; i++) {
/* 222 */         val[i] = (byte)(int)((ONode)d.array.get(i)).getLong();
/*     */       }
/* 224 */       return val;
/* 225 */     }  if (is(short[].class, target)) {
/* 226 */       short[] val = new short[len];
/* 227 */       for (int i = 0; i < len; i++) {
/* 228 */         val[i] = ((ONode)d.array.get(i)).getShort();
/*     */       }
/* 230 */       return val;
/* 231 */     }  if (is(int[].class, target)) {
/* 232 */       int[] val = new int[len];
/* 233 */       for (int i = 0; i < len; i++) {
/* 234 */         val[i] = ((ONode)d.array.get(i)).getInt();
/*     */       }
/* 236 */       return val;
/* 237 */     }  if (is(long[].class, target)) {
/* 238 */       long[] val = new long[len];
/* 239 */       for (int i = 0; i < len; i++) {
/* 240 */         val[i] = ((ONode)d.array.get(i)).getLong();
/*     */       }
/* 242 */       return val;
/* 243 */     }  if (is(float[].class, target)) {
/* 244 */       float[] val = new float[len];
/* 245 */       for (int i = 0; i < len; i++) {
/* 246 */         val[i] = ((ONode)d.array.get(i)).getFloat();
/*     */       }
/* 248 */       return val;
/* 249 */     }  if (is(double[].class, target)) {
/* 250 */       double[] val = new double[len];
/* 251 */       for (int i = 0; i < len; i++) {
/* 252 */         val[i] = ((ONode)d.array.get(i)).getDouble();
/*     */       }
/* 254 */       return val;
/* 255 */     }  if (is(boolean[].class, target)) {
/* 256 */       boolean[] val = new boolean[len];
/* 257 */       for (int i = 0; i < len; i++) {
/* 258 */         val[i] = ((ONode)d.array.get(i)).getBoolean();
/*     */       }
/* 260 */       return val;
/* 261 */     }  if (is(char[].class, target)) {
/* 262 */       char[] val = new char[len];
/* 263 */       for (int i = 0; i < len; i++) {
/* 264 */         val[i] = ((ONode)d.array.get(i)).getChar();
/*     */       }
/* 266 */       return val;
/* 267 */     }  if (is(String[].class, target)) {
/* 268 */       String[] val = new String[len];
/* 269 */       for (int i = 0; i < len; i++) {
/* 270 */         val[i] = ((ONode)d.array.get(i)).getString();
/*     */       }
/* 272 */       return val;
/* 273 */     }  if (is(Object[].class, target)) {
/* 274 */       Class<?> c = target.getComponentType();
/* 275 */       Object[] val = (Object[])Array.newInstance(c, len);
/* 276 */       for (int i = 0; i < len; i++) {
/* 277 */         val[i] = analyse(ctx, d.array.get(i), null, c, c, null);
/*     */       }
/* 279 */       return val;
/*     */     } 
/* 281 */     throw new SnackException("unsupport type " + target.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object analyseCollection(Context ctx, ONode o, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
/* 287 */     Collection<Object> list = TypeUtil.createCollection(clz, false);
/*     */     
/* 289 */     if (list == null) {
/* 290 */       return null;
/*     */     }
/*     */     
/* 293 */     Type itemType = null;
/* 294 */     if (ctx.target_type != null) {
/* 295 */       itemType = TypeUtil.getCollectionItemType(type);
/*     */     }
/*     */ 
/*     */     
/* 299 */     if (itemType != null && itemType instanceof TypeVariable) {
/* 300 */       itemType = null;
/*     */     }
/*     */     
/* 303 */     for (ONode o1 : (o.nodeData()).array) {
/* 304 */       list.add(analyse(ctx, o1, null, (Class)itemType, itemType, genericInfo));
/*     */     }
/*     */     
/* 307 */     return list;
/*     */   }
/*     */   
/*     */   public Object analyseProps(Context ctx, ONode o, Properties rst, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
/* 311 */     if (rst == null) {
/* 312 */       rst = new Properties();
/*     */     }
/*     */     
/* 315 */     String prefix = "";
/* 316 */     propsLoad0(rst, prefix, o);
/*     */     
/* 318 */     return rst;
/*     */   }
/*     */   
/*     */   private void propsLoad0(Properties props, String prefix, ONode tmp) {
/* 322 */     if (tmp.isObject()) {
/* 323 */       tmp.forEach((k, v) -> {
/*     */             String prefix2 = prefix + "." + k;
/*     */             
/*     */             propsLoad0(props, prefix2, v);
/*     */           });
/*     */       return;
/*     */     } 
/* 330 */     if (tmp.isArray()) {
/* 331 */       int index = 0;
/* 332 */       for (ONode v : tmp.ary()) {
/* 333 */         String prefix2 = prefix + "[" + index + "]";
/* 334 */         propsLoad0(props, prefix2, v);
/* 335 */         index++;
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 340 */     if (tmp.isNull()) {
/* 341 */       propsPut0(props, prefix, "");
/*     */     } else {
/* 343 */       propsPut0(props, prefix, tmp.getString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void propsPut0(Properties props, String key, Object val) {
/* 348 */     if (key.startsWith(".")) {
/* 349 */       props.put(key.substring(1), val);
/*     */     } else {
/* 351 */       props.put(key, val);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object analyseMap(Context ctx, ONode o, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
/* 357 */     Map<Object, Object> map = TypeUtil.createMap(clz);
/*     */     
/* 359 */     if (type instanceof ParameterizedType) {
/* 360 */       ParameterizedType ptt = (ParameterizedType)type;
/* 361 */       Type kType = ptt.getActualTypeArguments()[0];
/* 362 */       Type vType = ptt.getActualTypeArguments()[1];
/*     */       
/* 364 */       if (kType instanceof ParameterizedType) {
/* 365 */         kType = ((ParameterizedType)kType).getRawType();
/*     */       }
/*     */       
/* 368 */       if (vType instanceof ParameterizedType) {
/* 369 */         vType = ((ParameterizedType)vType).getRawType();
/*     */       }
/*     */       
/* 372 */       if (kType == String.class) {
/* 373 */         for (Map.Entry<String, ONode> kv : (Iterable<Map.Entry<String, ONode>>)(o.nodeData()).object.entrySet()) {
/* 374 */           map.put(kv.getKey(), analyse(ctx, kv.getValue(), null, (Class)vType, vType, genericInfo));
/*     */         }
/*     */       } else {
/* 377 */         for (Map.Entry<String, ONode> kv : (Iterable<Map.Entry<String, ONode>>)(o.nodeData()).object.entrySet()) {
/* 378 */           map.put(TypeUtil.strTo(kv.getKey(), (Class)kType), analyse(ctx, kv.getValue(), null, (Class)vType, vType, genericInfo));
/*     */         }
/*     */       } 
/*     */     } else {
/* 382 */       for (Map.Entry<String, ONode> kv : (Iterable<Map.Entry<String, ONode>>)(o.nodeData()).object.entrySet()) {
/* 383 */         map.put(kv.getKey(), analyse(ctx, kv.getValue(), null, null, null, genericInfo));
/*     */       }
/*     */     } 
/*     */     
/* 387 */     return map;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object analyseBean(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<TypeVariable, Type> genericInfo) throws Exception {
/* 392 */     if (is(SimpleDateFormat.class, clz)) {
/* 393 */       return new SimpleDateFormat(o.get("val").getString());
/*     */     }
/*     */     
/* 396 */     if (is(InetSocketAddress.class, clz)) {
/* 397 */       return new InetSocketAddress(o.get("address").getString(), o.get("port").getInt());
/*     */     }
/*     */     
/* 400 */     if (is(Throwable.class, clz)) {
/*     */       
/* 402 */       String message = o.get("message").getString();
/* 403 */       if (!StringUtil.isEmpty(message)) {
/*     */         try {
/* 405 */           Constructor<?> fun = clz.getConstructor(new Class[] { String.class });
/* 406 */           rst = fun.newInstance(new Object[] { message });
/* 407 */         } catch (Throwable throwable) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 413 */     ClassWrap clzWrap = ClassWrap.get(clz);
/*     */     
/* 415 */     if (clzWrap.recordable()) {
/*     */       
/* 417 */       Parameter[] argsP = clzWrap.recordParams();
/* 418 */       Object[] argsV = new Object[argsP.length];
/*     */       
/* 420 */       for (int j = 0; j < argsP.length; j++) {
/* 421 */         Parameter f = argsP[j];
/* 422 */         String fieldK = f.getName();
/* 423 */         if (o.contains(fieldK)) {
/* 424 */           Class<?> fieldT = f.getType();
/* 425 */           Type fieldGt = f.getParameterizedType();
/*     */           
/* 427 */           Object val = analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, null, genericInfo);
/* 428 */           argsV[j] = val;
/*     */         } 
/*     */       } 
/*     */       
/* 432 */       rst = clzWrap.recordConstructor().newInstance(argsV);
/*     */     } else {
/* 434 */       if (rst == null) {
/* 435 */         rst = BeanUtil.newInstance(clz);
/*     */       }
/*     */       
/* 438 */       if (rst == null) {
/* 439 */         return null;
/*     */       }
/*     */       
/* 442 */       boolean disSetter = !ctx.options.hasFeature(Feature.UseSetter);
/*     */       
/* 444 */       for (FieldWrap f : clzWrap.fieldAllWraps()) {
/* 445 */         if (!f.isDeserialize()) {
/*     */           continue;
/*     */         }
/*     */         
/* 449 */         String fieldK = f.getName();
/* 450 */         if (o.contains(fieldK)) {
/* 451 */           Class fieldT = f.type;
/* 452 */           Type fieldGt = f.genericType;
/* 453 */           if (f.readonly) {
/* 454 */             analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, f.getValue(rst), genericInfo); continue;
/*     */           } 
/* 456 */           Object val = analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, null, genericInfo);
/*     */           
/* 458 */           f.setValue(rst, val, disSetter);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 464 */     return rst;
/*     */   }
/*     */   
/*     */   private Object analyseBeanOfValue(String fieldK, Class<?> fieldT, Type fieldGt, Context ctx, ONode o, Object rst, Map<TypeVariable, Type> genericInfo) throws Exception {
/*     */     ParameterizedTypeImpl parameterizedTypeImpl;
/* 469 */     if (genericInfo != null) {
/* 470 */       if (fieldGt instanceof TypeVariable) {
/* 471 */         Type tmp = genericInfo.get(fieldGt);
/* 472 */         if (tmp != null) {
/* 473 */           fieldGt = tmp;
/* 474 */           if (tmp instanceof Class) {
/* 475 */             fieldT = (Class)tmp;
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 482 */       if (fieldGt instanceof ParameterizedType) {
/* 483 */         ParameterizedType fieldGt2 = (ParameterizedType)fieldGt;
/* 484 */         Type[] actualTypes = fieldGt2.getActualTypeArguments();
/* 485 */         boolean actualTypesChanged = false;
/*     */         
/* 487 */         fieldT = (Class)fieldGt2.getRawType();
/*     */         
/* 489 */         for (int i = 0, len = actualTypes.length; i < len; i++) {
/* 490 */           Type tmp = actualTypes[i];
/* 491 */           if (tmp instanceof TypeVariable) {
/* 492 */             tmp = genericInfo.get(tmp);
/*     */             
/* 494 */             if (tmp != null) {
/* 495 */               actualTypes[i] = tmp;
/* 496 */               actualTypesChanged = true;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 501 */         if (actualTypesChanged) {
/* 502 */           parameterizedTypeImpl = new ParameterizedTypeImpl((Class)fieldGt2.getRawType(), actualTypes, fieldGt2.getOwnerType());
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 507 */     return analyse(ctx, o.get(fieldK), rst, fieldT, (Type)parameterizedTypeImpl, genericInfo);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Class<?> getTypeByNode(Context ctx, ONode o, Class<?> def) {
/* 515 */     if (ctx.target_type == null) {
/* 516 */       if (o.isObject()) {
/* 517 */         return LinkedHashMap.class;
/*     */       }
/*     */       
/* 520 */       if (o.isArray()) {
/* 521 */         return ArrayList.class;
/*     */       }
/*     */     } 
/*     */     
/* 525 */     String typeStr = null;
/* 526 */     if (o.isArray() && o.ary().size() == 2) {
/* 527 */       ONode o1 = o.ary().get(0);
/* 528 */       if (o1.isObject() && o1.obj().size() == 1) {
/*     */ 
/*     */ 
/*     */         
/* 532 */         ONode n1 = (ONode)o1.obj().get(ctx.options.getTypePropertyName());
/* 533 */         if (n1 != null) {
/* 534 */           typeStr = n1.val().getString();
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 539 */     if (o.isObject()) {
/* 540 */       ONode n1 = (ONode)o.obj().get(ctx.options.getTypePropertyName());
/* 541 */       if (n1 != null) {
/* 542 */         typeStr = n1.val().getString();
/*     */       }
/*     */     } 
/*     */     
/* 546 */     if (!StringUtil.isEmpty(typeStr)) {
/* 547 */       Class<?> clz = BeanUtil.loadClass(typeStr);
/* 548 */       if (clz == null) {
/* 549 */         throw new SnackException("unsupport type " + typeStr);
/*     */       }
/* 551 */       return clz;
/*     */     } 
/*     */     
/* 554 */     if (def == null || def == Object.class) {
/* 555 */       if (o.isObject()) {
/* 556 */         return LinkedHashMap.class;
/*     */       }
/*     */       
/* 559 */       if (o.isArray()) {
/* 560 */         return ArrayList.class;
/*     */       }
/*     */     } 
/*     */     
/* 564 */     return def;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\to\ObjectToer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */