/*     */ package org.noear.snack.from;
/*     */ import java.io.File;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.TimeZone;
/*     */ import org.noear.snack.ONode;
/*     */ import org.noear.snack.core.Context;
/*     */ import org.noear.snack.core.DEFAULTS;
/*     */ import org.noear.snack.core.Feature;
/*     */ import org.noear.snack.core.NodeEncoderEntity;
/*     */ import org.noear.snack.core.Options;
/*     */ import org.noear.snack.core.exts.ClassWrap;
/*     */ import org.noear.snack.core.exts.FieldWrap;
/*     */ import org.noear.snack.core.utils.DateUtil;
/*     */ 
/*     */ public class ObjectFromer implements Fromer {
/*     */   public void handle(Context ctx) {
/*  33 */     ctx.target = analyse(ctx.options, ctx.source);
/*     */   }
/*     */ 
/*     */   
/*     */   private ONode analyse(Options opt, Object source) {
/*  38 */     ONode rst = new ONode(opt);
/*     */     
/*  40 */     if (source == null) {
/*  41 */       return rst;
/*     */     }
/*     */     
/*  44 */     Class<?> clz = source.getClass();
/*     */ 
/*     */     
/*  47 */     for (NodeEncoderEntity encoder : opt.encoders()) {
/*  48 */       if (encoder.isEncodable(clz)) {
/*  49 */         encoder.encode(source, rst);
/*  50 */         return rst;
/*     */       } 
/*     */     } 
/*     */     
/*  54 */     if (source instanceof ONode) {
/*  55 */       rst.val(source);
/*  56 */     } else if (source instanceof String) {
/*  57 */       rst.val().setString((String)source);
/*  58 */     } else if (source instanceof Date) {
/*  59 */       rst.val().setDate((Date)source);
/*  60 */     } else if (source instanceof LocalDateTime) {
/*  61 */       Instant instant = ((LocalDateTime)source).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
/*  62 */       rst.val().setDate(new Date(instant.getEpochSecond() * 1000L + (instant.getNano() / 1000000)));
/*  63 */     } else if (source instanceof LocalDate) {
/*  64 */       Instant instant = ((LocalDate)source).atTime(LocalTime.MIN).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
/*  65 */       rst.val().setDate(new Date(instant.getEpochSecond() * 1000L));
/*  66 */     } else if (source instanceof LocalTime) {
/*  67 */       Instant instant = ((LocalTime)source).atDate(LocalDate.of(1970, 1, 1)).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
/*  68 */       rst.val().setDate(new Date(instant.getEpochSecond() * 1000L));
/*  69 */     } else if (source instanceof Boolean) {
/*  70 */       rst.val().setBool(((Boolean)source).booleanValue());
/*  71 */     } else if (source instanceof Number) {
/*  72 */       rst.val().setNumber((Number)source);
/*  73 */     } else if (source instanceof Throwable) {
/*  74 */       analyseBean(opt, rst, clz, source);
/*  75 */     } else if (source instanceof Properties) {
/*  76 */       analyseProps(opt, rst, clz, source);
/*  77 */     } else if (!analyseArray(opt, rst, clz, source)) {
/*     */       
/*  79 */       if (clz.isEnum()) {
/*  80 */         Enum em = (Enum)source;
/*     */         
/*  82 */         if (opt.hasFeature(Feature.EnumUsingName)) {
/*  83 */           rst.val().setString(em.name());
/*     */         } else {
/*  85 */           rst.val().setNumber(Integer.valueOf(em.ordinal()));
/*     */         } 
/*  87 */       } else if (source instanceof Map) {
/*     */         
/*  89 */         if (opt.hasFeature(Feature.WriteClassName)) {
/*  90 */           typeSet(opt, rst, clz);
/*     */         }
/*     */         
/*  93 */         rst.asObject();
/*  94 */         Map map = (Map)source;
/*  95 */         for (Object k : map.keySet()) {
/*  96 */           if (k != null) {
/*  97 */             rst.setNode(k.toString(), analyse(opt, map.get(k)));
/*     */           }
/*     */         } 
/* 100 */       } else if (source instanceof Iterable) {
/* 101 */         rst.asArray();
/* 102 */         ONode ary = rst;
/*     */         
/* 104 */         if (opt.hasFeature(Feature.WriteArrayClassName)) {
/* 105 */           rst.add(typeSet(opt, new ONode(opt), clz));
/* 106 */           ary = rst.addNew().asArray();
/*     */         } 
/*     */         
/* 109 */         for (Object o : source) {
/* 110 */           ary.addNode(analyse(opt, o));
/*     */         }
/* 112 */       } else if (source instanceof Enumeration) {
/* 113 */         rst.asArray();
/* 114 */         Enumeration o = (Enumeration)source;
/* 115 */         while (o.hasMoreElements()) {
/* 116 */           rst.addNode(analyse(opt, o.nextElement()));
/*     */         }
/*     */       } else {
/* 119 */         String clzName = clz.getName();
/*     */         
/* 121 */         if (clzName.endsWith(".Undefined")) {
/* 122 */           rst.val().setNull();
/*     */         }
/* 124 */         else if (!analyseOther(opt, rst, clz, source) && 
/* 125 */           !clzName.startsWith("jdk.")) {
/* 126 */           analyseBean(opt, rst, clz, source);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 132 */     return rst;
/*     */   }
/*     */   
/*     */   private ONode typeSet(Options cfg, ONode o, Class<?> clz) {
/* 136 */     return o.set(cfg.getTypePropertyName(), clz.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean analyseArray(Options cfg, ONode rst, Class<?> clz, Object obj) {
/* 141 */     if (obj instanceof Object[]) {
/* 142 */       rst.asArray();
/* 143 */       for (Object o : (Object[])obj) {
/* 144 */         rst.addNode(analyse(cfg, o));
/*     */       }
/* 146 */     } else if (obj instanceof byte[]) {
/* 147 */       rst.asArray();
/* 148 */       for (byte o : (byte[])obj) {
/* 149 */         rst.addNode(analyse(cfg, Byte.valueOf(o)));
/*     */       }
/* 151 */     } else if (obj instanceof short[]) {
/* 152 */       rst.asArray();
/* 153 */       for (short o : (short[])obj) {
/* 154 */         rst.addNode(analyse(cfg, Short.valueOf(o)));
/*     */       }
/* 156 */     } else if (obj instanceof int[]) {
/* 157 */       rst.asArray();
/* 158 */       for (int o : (int[])obj) {
/* 159 */         rst.addNode(analyse(cfg, Integer.valueOf(o)));
/*     */       }
/* 161 */     } else if (obj instanceof long[]) {
/* 162 */       rst.asArray();
/* 163 */       for (long o : (long[])obj) {
/* 164 */         rst.addNode(analyse(cfg, Long.valueOf(o)));
/*     */       }
/* 166 */     } else if (obj instanceof float[]) {
/* 167 */       rst.asArray();
/* 168 */       for (float o : (float[])obj) {
/* 169 */         rst.addNode(analyse(cfg, Float.valueOf(o)));
/*     */       }
/* 171 */     } else if (obj instanceof double[]) {
/* 172 */       rst.asArray();
/* 173 */       for (double o : (double[])obj) {
/* 174 */         rst.addNode(analyse(cfg, Double.valueOf(o)));
/*     */       }
/* 176 */     } else if (obj instanceof boolean[]) {
/* 177 */       rst.asArray();
/* 178 */       for (boolean o : (boolean[])obj) {
/* 179 */         rst.addNode(analyse(cfg, Boolean.valueOf(o)));
/*     */       }
/* 181 */     } else if (obj instanceof char[]) {
/* 182 */       rst.asArray();
/* 183 */       for (char o : (char[])obj) {
/* 184 */         rst.addNode(analyse(cfg, Character.valueOf(o)));
/*     */       }
/*     */     } else {
/* 187 */       return false;
/*     */     } 
/*     */     
/* 190 */     return true;
/*     */   }
/*     */   
/*     */   private boolean analyseProps(Options cfg, ONode rst, Class<?> clz, Object obj) {
/* 194 */     Properties props = (Properties)obj;
/*     */     
/* 196 */     if (props.size() == 0) {
/* 197 */       rst.asNull();
/* 198 */       return true;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 203 */     List<String> keyVector = new ArrayList<>();
/* 204 */     props.keySet().forEach(k -> {
/*     */           if (k instanceof String) {
/*     */             keyVector.add((String)k);
/*     */           }
/*     */         });
/* 209 */     Collections.sort(keyVector);
/*     */ 
/*     */     
/* 212 */     if (((String)keyVector.get(0)).startsWith("[")) {
/* 213 */       rst.asArray();
/*     */     } else {
/* 215 */       rst.asObject();
/*     */     } 
/*     */     
/* 218 */     for (String key : keyVector) {
/* 219 */       String val = props.getProperty(key);
/*     */       
/* 221 */       String[] keySegments = key.split("\\.");
/* 222 */       ONode n1 = rst;
/*     */       
/* 224 */       for (int i = 0; i < keySegments.length; i++) {
/* 225 */         String p1 = keySegments[i];
/*     */         
/* 227 */         if (p1.endsWith("]")) {
/* 228 */           int idx = Integer.parseInt(p1.substring(p1.lastIndexOf('[') + 1, p1.length() - 1));
/* 229 */           p1 = p1.substring(0, p1.lastIndexOf('['));
/*     */           
/* 231 */           if (p1.length() > 0) {
/* 232 */             n1 = n1.getOrNew(p1).getOrNew(idx);
/*     */           } else {
/* 234 */             n1 = n1.getOrNew(idx);
/*     */           } 
/*     */         } else {
/* 237 */           n1 = n1.getOrNew(p1);
/*     */         } 
/*     */       } 
/*     */       
/* 241 */       n1.val(val);
/*     */     } 
/*     */     
/* 244 */     return true;
/*     */   }
/*     */   
/*     */   private boolean analyseBean(Options cfg, ONode rst, Class<?> clz, Object obj) {
/* 248 */     rst.asObject();
/*     */ 
/*     */     
/* 251 */     if (cfg.hasFeature(Feature.WriteClassName)) {
/* 252 */       rst.set(cfg.getTypePropertyName(), clz.getName());
/*     */     }
/*     */ 
/*     */     
/* 256 */     Collection<FieldWrap> list = ClassWrap.get(clz).fieldAllWraps();
/*     */     
/* 258 */     for (FieldWrap f : list) {
/* 259 */       if (!f.isSerialize()) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 264 */       Object val = f.getValue(obj);
/*     */ 
/*     */       
/* 267 */       if (val == null) {
/* 268 */         if (f.isIncNull()) {
/*     */           
/* 270 */           if (cfg.hasFeature(Feature.StringFieldInitEmpty) && f.genericType == String.class) {
/* 271 */             rst.setNode(f.getName(), analyse(cfg, ""));
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 276 */           if (cfg.hasFeature(Feature.SerializeNulls)) {
/* 277 */             rst.setNode(f.getName(), analyse(cfg, null));
/*     */           }
/*     */         } 
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */ 
/*     */       
/* 286 */       if (val.equals(obj)) {
/*     */         continue;
/*     */       }
/*     */       
/* 290 */       if (!StringUtil.isEmpty(f.getFormat())) {
/* 291 */         if (val instanceof Date) {
/* 292 */           String val2 = DateUtil.format((Date)val, f.getFormat());
/* 293 */           rst.set(f.getName(), val2);
/*     */           
/*     */           continue;
/*     */         } 
/* 297 */         if (val instanceof Number) {
/* 298 */           NumberFormat format = new DecimalFormat(f.getFormat());
/* 299 */           String val2 = format.format(val);
/* 300 */           rst.set(f.getName(), val2);
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } 
/* 305 */       rst.setNode(f.getName(), analyse(cfg, val));
/*     */     } 
/*     */     
/* 308 */     return true;
/*     */   }
/*     */   
/*     */   private boolean analyseOther(Options cfg, ONode rst, Class<?> clz, Object obj) {
/* 312 */     if (obj instanceof SimpleDateFormat) {
/* 313 */       rst.set(cfg.getTypePropertyName(), clz.getName());
/* 314 */       rst.set("val", ((SimpleDateFormat)obj).toPattern());
/* 315 */     } else if (clz == Class.class) {
/* 316 */       rst.val().setString(clz.getName());
/* 317 */     } else if (obj instanceof InetSocketAddress) {
/* 318 */       InetSocketAddress address = (InetSocketAddress)obj;
/* 319 */       InetAddress inetAddress = address.getAddress();
/* 320 */       rst.set("address", inetAddress.getHostAddress());
/* 321 */       rst.set("port", Integer.valueOf(address.getPort()));
/* 322 */     } else if (obj instanceof File) {
/* 323 */       rst.val().setString(((File)obj).getPath());
/* 324 */     } else if (obj instanceof InetAddress) {
/* 325 */       rst.val().setString(((InetAddress)obj).getHostAddress());
/* 326 */     } else if (obj instanceof TimeZone) {
/* 327 */       rst.val().setString(((TimeZone)obj).getID());
/* 328 */     } else if (obj instanceof Currency) {
/* 329 */       rst.val().setString(((Currency)obj).getCurrencyCode());
/* 330 */     } else if (obj instanceof Iterator) {
/* 331 */       rst.asArray();
/* 332 */       ((Iterator)obj).forEachRemaining(v -> rst.add(analyse(cfg, v)));
/*     */     
/*     */     }
/* 335 */     else if (obj instanceof Map.Entry) {
/* 336 */       Map.Entry kv = (Map.Entry)obj;
/* 337 */       Object k = kv.getKey();
/* 338 */       Object v = kv.getValue();
/* 339 */       rst.asObject();
/* 340 */       if (k != null) {
/* 341 */         rst.set(k.toString(), analyse(cfg, v));
/*     */       }
/* 343 */     } else if (obj instanceof Calendar) {
/* 344 */       rst.val().setDate(((Calendar)obj).getTime());
/* 345 */     } else if (obj instanceof Clob) {
/* 346 */       rst.val().setString(BeanUtil.clobToString((Clob)obj));
/* 347 */     } else if (obj instanceof Appendable) {
/* 348 */       rst.val().setString(obj.toString());
/*     */     } else {
/* 350 */       return false;
/*     */     } 
/*     */     
/* 353 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\from\ObjectFromer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */