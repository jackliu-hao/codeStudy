/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Function;
/*     */ import org.noear.solon.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Props
/*     */   extends Properties
/*     */ {
/*     */   public Props(Properties defaults) {
/*  28 */     super(defaults);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int size() {
/*  33 */     if (this.defaults == null) {
/*  34 */       return super.size();
/*     */     }
/*  36 */     return super.size() + this.defaults.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(String key) {
/*  44 */     return getProperty(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getByExpr(String expr) {
/*  51 */     String name = expr;
/*  52 */     if (name.startsWith("${") && name.endsWith("}")) {
/*  53 */       name = expr.substring(2, name.length() - 1);
/*     */     }
/*     */     
/*  56 */     return get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getByParse(String expr) {
/*  63 */     if (Utils.isEmpty(expr)) {
/*  64 */       return expr;
/*     */     }
/*     */     
/*  67 */     int start = expr.indexOf("${");
/*  68 */     if (start < 0) {
/*  69 */       return expr;
/*     */     }
/*  71 */     int end = expr.indexOf("}");
/*  72 */     String name = expr.substring(start + 2, end);
/*  73 */     String value = get(name);
/*  74 */     return expr.substring(0, start) + value + expr.substring(end + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(String key, String def) {
/*  84 */     return getProperty(key, def);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getBool(String key, boolean def) {
/*  93 */     return ((Boolean)getOrDef(key, Boolean.valueOf(def), Boolean::parseBoolean)).booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getInt(String key, int def) {
/* 102 */     return ((Integer)getOrDef(key, Integer.valueOf(def), Integer::parseInt)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLong(String key, long def) {
/* 111 */     return ((Long)getOrDef(key, Long.valueOf(def), Long::parseLong)).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Double getDouble(String key, double def) {
/* 120 */     return getOrDef(key, Double.valueOf(def), Double::parseDouble);
/*     */   }
/*     */   
/*     */   private <T> T getOrDef(String key, T def, Function<String, T> convert) {
/* 124 */     String temp = get(key);
/* 125 */     if (Utils.isEmpty(temp)) {
/* 126 */       return def;
/*     */     }
/* 128 */     return convert.apply(temp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getBean(String keyStarts, Class<T> clz) {
/* 139 */     Properties props = getProp(keyStarts);
/* 140 */     return PropsConverter.global().convert(props, null, clz, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props getProp(String keyStarts) {
/* 149 */     Props prop = new Props();
/* 150 */     doFind(keyStarts + ".", prop::put);
/* 151 */     if (prop.size() == 0) {
/* 152 */       doFind(keyStarts + "[", (k, v) -> prop.put("[" + k, v));
/*     */     }
/*     */ 
/*     */     
/* 156 */     return prop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Props getPropByExpr(String expr) {
/* 163 */     String name = expr;
/* 164 */     if (name.startsWith("${") && name.endsWith("}")) {
/* 165 */       name = expr.substring(2, name.length() - 1);
/*     */     }
/*     */     
/* 168 */     return getProp(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NvMap getXmap(String keyStarts) {
/* 177 */     NvMap map = new NvMap();
/* 178 */     doFind(keyStarts + ".", map::put);
/* 179 */     return map;
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
/*     */   private void doFind(String keyStarts, BiConsumer<String, String> setFun) {
/* 191 */     String key2 = keyStarts;
/* 192 */     int idx2 = key2.length();
/*     */     
/* 194 */     forEach((k, v) -> {
/*     */           if (k instanceof String && v instanceof String) {
/*     */             String keyStr = (String)k;
/*     */             if (keyStr.startsWith(key2)) {
/*     */               String key = keyStr.substring(idx2);
/*     */               setFun.accept(key, (String)v);
/*     */               if (key.contains("-")) {
/*     */                 String[] ss = key.split("-");
/*     */                 StringBuilder sb = new StringBuilder(key.length());
/*     */                 sb.append(ss[0]);
/*     */                 for (int i = 1; i < ss.length; i++) {
/*     */                   if (ss[i].length() > 1) {
/*     */                     sb.append(ss[i].substring(0, 1).toUpperCase()).append(ss[i].substring(1));
/*     */                   } else {
/*     */                     sb.append(ss[i].toUpperCase());
/*     */                   } 
/*     */                 } 
/*     */                 setFun.accept(sb.toString(), (String)v);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
/* 225 */     if (this.defaults == null) {
/* 226 */       super.forEach(action);
/*     */     } else {
/* 228 */       this.defaults.forEach(action);
/* 229 */       super.forEach((k, v) -> {
/*     */             if (!this.defaults.containsKey(k)) {
/*     */               action.accept(k, v);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 239 */   private Set<BiConsumer<String, String>> _changeEvent = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onChange(BiConsumer<String, String> event) {
/* 245 */     this._changeEvent.add(event);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Object put(Object key, Object value) {
/* 253 */     Object obj = super.put(key, value);
/*     */     
/* 255 */     if (key instanceof String && value instanceof String) {
/* 256 */       this._changeEvent.forEach(event -> event.accept((String)key, (String)value));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 261 */     return obj;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void loadAdd(URL url) {
/* 272 */     if (url != null) {
/* 273 */       Properties props = Utils.loadProperties(url);
/* 274 */       loadAdd(props);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void loadAdd(Properties props) {
/* 279 */     loadAddDo(props, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void loadAddDo(Properties props, boolean toSystem) {
/* 288 */     if (props != null)
/* 289 */       for (Map.Entry<Object, Object> kv : props.entrySet()) {
/* 290 */         Object k1 = kv.getKey();
/* 291 */         Object v1 = kv.getValue();
/*     */         
/* 293 */         if (k1 instanceof String) {
/* 294 */           String key = (String)k1;
/*     */           
/* 296 */           if (Utils.isEmpty(key)) {
/*     */             continue;
/*     */           }
/*     */           
/* 300 */           if (v1 instanceof String) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 305 */             String v1Str = (String)v1;
/* 306 */             int symStart = 0;
/*     */             
/*     */             while (true) {
/* 309 */               symStart = v1Str.indexOf("${", symStart);
/* 310 */               if (symStart >= 0) {
/* 311 */                 int symEnd = v1Str.indexOf("}", symStart + 1);
/* 312 */                 if (symEnd > symStart) {
/* 313 */                   String tmpK = v1Str.substring(symStart + 2, symEnd);
/*     */                   
/* 315 */                   String tmpV2 = props.getProperty(tmpK);
/* 316 */                   if (tmpV2 == null) {
/* 317 */                     tmpV2 = getProperty(tmpK);
/*     */                   }
/*     */                   
/* 320 */                   if (tmpV2 == null) {
/* 321 */                     symStart = symEnd; continue;
/*     */                   } 
/* 323 */                   if (symStart > 0)
/*     */                   {
/* 325 */                     tmpV2 = v1Str.substring(0, symStart) + tmpV2;
/*     */                   }
/* 327 */                   symStart = tmpV2.length();
/* 328 */                   v1Str = tmpV2 + v1Str.substring(symEnd + 1);
/*     */ 
/*     */                   
/*     */                   continue;
/*     */                 } 
/*     */               } 
/*     */ 
/*     */               
/*     */               break;
/*     */             } 
/*     */ 
/*     */             
/* 340 */             v1 = v1Str;
/*     */           } 
/*     */           
/* 343 */           if (v1 != null) {
/* 344 */             if (toSystem) {
/* 345 */               System.getProperties().put(k1, v1);
/*     */             }
/*     */             
/* 348 */             put(k1, v1);
/*     */           } 
/*     */         } 
/*     */       }  
/*     */   }
/*     */   
/*     */   public Props() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\Props.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */