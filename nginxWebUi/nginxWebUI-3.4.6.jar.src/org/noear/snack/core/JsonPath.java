/*     */ package org.noear.snack.core;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.regex.Pattern;
/*     */ import org.noear.snack.ONode;
/*     */ import org.noear.snack.OValue;
/*     */ import org.noear.snack.OValueType;
/*     */ import org.noear.snack.core.exts.CharBuffer;
/*     */ import org.noear.snack.core.exts.CharReader;
/*     */ import org.noear.snack.core.exts.ThData;
/*     */ import org.noear.snack.core.exts.TmpCache;
/*     */ import org.noear.snack.core.utils.StringUtil;
/*     */ 
/*     */ 
/*     */ public class JsonPath
/*     */ {
/*  22 */   private static int _cacheSize = 1024;
/*  23 */   private static Map<String, JsonPath> _jpathCache = new HashMap<>(128);
/*     */   
/*     */   public static ONode eval(ONode source, String jpath, boolean useStandard, boolean cacheJpath) {
/*  26 */     ((TmpCache)tlCache.get()).clear();
/*  27 */     return do_get(source, jpath, cacheJpath, useStandard);
/*     */   }
/*     */ 
/*     */   
/*     */   private static ONode do_get(ONode source, String jpath, boolean cacheJpath, boolean useStandard) {
/*  32 */     JsonPath jsonPath = null;
/*  33 */     if (cacheJpath) {
/*  34 */       jsonPath = _jpathCache.get(jpath);
/*  35 */       if (jsonPath == null) {
/*  36 */         synchronized (jpath.intern()) {
/*  37 */           jsonPath = _jpathCache.get(jpath);
/*  38 */           if (jsonPath == null) {
/*  39 */             jsonPath = compile(jpath);
/*  40 */             if (_jpathCache.size() < _cacheSize) {
/*  41 */               _jpathCache.put(jpath, jsonPath);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } else {
/*  47 */       jsonPath = compile(jpath);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  52 */     return exec(jsonPath, source, useStandard);
/*     */   }
/*     */   
/*  55 */   private static final ThData<CharBuffer> tlBuilder = new ThData(() -> new CharBuffer());
/*  56 */   private static final ThData<TmpCache> tlCache = new ThData(() -> new TmpCache());
/*     */ 
/*     */   
/*     */   private List<Segment> segments;
/*     */ 
/*     */   
/*     */   private JsonPath() {
/*  63 */     this.segments = new ArrayList<>();
/*     */   }
/*     */ 
/*     */   
/*     */   private static JsonPath compile(String jpath) {
/*  68 */     String jpath2 = jpath.replace("..", "._");
/*  69 */     JsonPath jsonPath = new JsonPath();
/*     */     
/*  71 */     char token = Character.MIN_VALUE;
/*  72 */     char c = Character.MIN_VALUE;
/*  73 */     CharBuffer buffer = (CharBuffer)tlBuilder.get();
/*  74 */     buffer.setLength(0);
/*  75 */     CharReader reader = new CharReader(jpath2);
/*     */     while (true) {
/*  77 */       c = reader.next();
/*     */       
/*  79 */       if (c == '\000') {
/*  80 */         if (buffer.length() > 0) {
/*  81 */           jsonPath.segments.add(new Segment(buffer.toString()));
/*  82 */           buffer.clear();
/*     */         } 
/*     */         
/*     */         break;
/*     */       } 
/*  87 */       switch (c) {
/*     */         case '.':
/*  89 */           if (token > '\000') {
/*  90 */             buffer.append(c); continue;
/*     */           } 
/*  92 */           if (buffer.length() > 0) {
/*  93 */             jsonPath.segments.add(new Segment(buffer.toString()));
/*  94 */             buffer.clear();
/*     */           } 
/*     */           continue;
/*     */         
/*     */         case '(':
/*  99 */           if (token == '[') {
/* 100 */             token = c;
/*     */           }
/* 102 */           buffer.append(c);
/*     */           continue;
/*     */         case ')':
/* 105 */           if (token == '(') {
/* 106 */             token = c;
/*     */           }
/* 108 */           buffer.append(c);
/*     */           continue;
/*     */         case '[':
/* 111 */           if (token == '\000') {
/* 112 */             token = c;
/* 113 */             if (buffer.length() > 0) {
/* 114 */               jsonPath.segments.add(new Segment(buffer.toString()));
/* 115 */               buffer.clear();
/*     */             }  continue;
/*     */           } 
/* 118 */           buffer.append(c);
/*     */           continue;
/*     */         
/*     */         case ']':
/* 122 */           if (token == '[' || token == ')') {
/* 123 */             token = Character.MIN_VALUE;
/* 124 */             buffer.append(c);
/* 125 */             if (buffer.length() > 0) {
/* 126 */               jsonPath.segments.add(new Segment(buffer.toString()));
/* 127 */               buffer.clear();
/*     */             }  continue;
/*     */           } 
/* 130 */           buffer.append(c);
/*     */           continue;
/*     */       } 
/*     */       
/* 134 */       buffer.append(c);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 139 */     return jsonPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ONode exec(JsonPath jsonPath, ONode source, boolean useStandard) {
/* 146 */     ONode tmp = source;
/* 147 */     boolean branch_do = false;
/* 148 */     for (Iterator<Segment> iterator = jsonPath.segments.iterator(); iterator.hasNext(); ) { Segment s = iterator.next();
/* 149 */       if (tmp == null) {
/*     */         break;
/*     */       }
/*     */       
/* 153 */       if (branch_do && (useStandard || s.cmdAry != null)) {
/* 154 */         ONode tmp2 = (new ONode(source.options())).asArray();
/*     */         
/* 156 */         Consumer<ONode> act1 = n1 -> {
/*     */             ONode n2 = s.handler.run(s, source, n1, Boolean.valueOf(useStandard));
/*     */             
/*     */             if (n2 != null) {
/*     */               if (s.cmdAry != null) {
/*     */                 if (n2.isArray()) {
/*     */                   tmp2.addAll(n2.ary());
/*     */                 } else {
/*     */                   tmp2.addNode(n2);
/*     */                 } 
/*     */               } else {
/*     */                 tmp2.addNode(n2);
/*     */               } 
/*     */             }
/*     */           };
/* 171 */         tmp.ary().forEach(act1);
/*     */         
/* 173 */         tmp = tmp2;
/*     */         
/* 175 */         if (!useStandard)
/* 176 */           branch_do = false; 
/*     */         continue;
/*     */       } 
/* 179 */       tmp = s.handler.run(s, source, tmp, Boolean.valueOf(useStandard));
/* 180 */       branch_do = s.cmdHasUnline; }
/*     */ 
/*     */ 
/*     */     
/* 184 */     if (tmp == null) {
/* 185 */       return new ONode(source.options());
/*     */     }
/* 187 */     return tmp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void scanByName(String name, ONode source, List<ONode> target) {
/* 195 */     if (source.isObject()) {
/* 196 */       for (Map.Entry<String, ONode> kv : (Iterable<Map.Entry<String, ONode>>)source.obj().entrySet()) {
/* 197 */         if (name.equals(kv.getKey())) {
/* 198 */           target.add(kv.getValue());
/*     */         }
/*     */         
/* 201 */         scanByName(name, kv.getValue(), target);
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 206 */     if (source.isArray()) {
/* 207 */       for (ONode n1 : source.ary()) {
/* 208 */         scanByName(name, n1, target);
/*     */       }
/*     */       return;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void scanByAll(String name, ONode source, boolean isRoot, List<ONode> target) {
/* 215 */     if (!isRoot) {
/* 216 */       target.add(source);
/*     */     }
/*     */     
/* 219 */     if (source.isObject()) {
/* 220 */       for (Map.Entry<String, ONode> kv : (Iterable<Map.Entry<String, ONode>>)source.obj().entrySet()) {
/* 221 */         scanByAll(name, kv.getValue(), false, target);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/* 226 */     if (source.isArray()) {
/* 227 */       for (ONode n1 : source.ary()) {
/* 228 */         scanByAll(name, n1, false, target);
/*     */       }
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean compare(ONode root, ONode parent, ONode leftO, String op, String right, boolean useStandard) {
/*     */     int end;
/*     */     String exp;
/* 238 */     if (leftO == null) {
/* 239 */       return false;
/*     */     }
/*     */     
/* 242 */     if (!leftO.isValue() || leftO.val().isNull()) {
/* 243 */       return false;
/*     */     }
/*     */     
/* 246 */     OValue left = leftO.val();
/* 247 */     ONode rightO = null;
/*     */     
/* 249 */     if (right.startsWith("$")) {
/*     */       
/* 251 */       rightO = (ONode)((TmpCache)tlCache.get()).get(right);
/* 252 */       if (rightO == null) {
/* 253 */         rightO = do_get(root, right, true, useStandard);
/* 254 */         ((TmpCache)tlCache.get()).put(right, rightO);
/*     */       } 
/*     */     } 
/*     */     
/* 258 */     if (right.startsWith("@")) {
/* 259 */       rightO = do_get(parent, right, true, useStandard);
/*     */     }
/*     */     
/* 262 */     if (rightO != null) {
/* 263 */       if (rightO.isValue()) {
/* 264 */         if (rightO.val().type() == OValueType.String) {
/* 265 */           right = "'" + rightO.getString() + "'";
/*     */         } else {
/* 267 */           right = rightO.getDouble() + "";
/*     */         } 
/*     */       } else {
/* 270 */         right = null;
/*     */       } 
/*     */     }
/*     */     
/* 274 */     switch (op) {
/*     */       case "==":
/* 276 */         if (right == null) {
/* 277 */           return false;
/*     */         }
/*     */         
/* 280 */         if (right.startsWith("'")) {
/* 281 */           return left.getString().equals(right.substring(1, right.length() - 1));
/*     */         }
/* 283 */         return (left.getDouble() == Double.parseDouble(right));
/*     */ 
/*     */       
/*     */       case "!=":
/* 287 */         if (right == null) {
/* 288 */           return false;
/*     */         }
/*     */         
/* 291 */         if (right.startsWith("'")) {
/* 292 */           return !left.getString().equals(right.substring(1, right.length() - 1));
/*     */         }
/* 294 */         return (left.getDouble() != Double.parseDouble(right));
/*     */ 
/*     */       
/*     */       case "<":
/* 298 */         if (right == null) {
/* 299 */           return false;
/*     */         }
/*     */         
/* 302 */         return (left.getDouble() < Double.parseDouble(right));
/*     */       
/*     */       case "<=":
/* 305 */         if (right == null) {
/* 306 */           return false;
/*     */         }
/* 308 */         return (left.getDouble() <= Double.parseDouble(right));
/*     */       
/*     */       case ">":
/* 311 */         if (right == null) {
/* 312 */           return false;
/*     */         }
/* 314 */         return (left.getDouble() > Double.parseDouble(right));
/*     */       
/*     */       case ">=":
/* 317 */         if (right == null) {
/* 318 */           return false;
/*     */         }
/* 320 */         return (left.getDouble() >= Double.parseDouble(right));
/*     */       
/*     */       case "=~":
/* 323 */         if (right == null) {
/* 324 */           return false;
/*     */         }
/* 326 */         end = right.lastIndexOf('/');
/* 327 */         exp = right.substring(1, end);
/* 328 */         return regex(right, exp).matcher(left.getString()).find();
/*     */       
/*     */       case "in":
/* 331 */         if (right == null) {
/* 332 */           Object val = left.getRaw();
/* 333 */           for (ONode n1 : rightO.ary()) {
/* 334 */             if (n1.val().getRaw().equals(val)) {
/* 335 */               return true;
/*     */             }
/*     */           } 
/* 338 */           return false;
/*     */         } 
/* 340 */         if (right.indexOf("'") > 0) {
/* 341 */           return getStringAry(right).contains(left.getString());
/*     */         }
/* 343 */         return getDoubleAry(right).contains(Double.valueOf(left.getDouble()));
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case "nin":
/* 349 */         if (right == null) {
/* 350 */           Object val = left.getRaw();
/* 351 */           for (ONode n1 : rightO.ary()) {
/* 352 */             if (n1.val().getRaw().equals(val)) {
/* 353 */               return false;
/*     */             }
/*     */           } 
/* 356 */           return true;
/*     */         } 
/* 358 */         if (right.indexOf("'") > 0) {
/* 359 */           return !getStringAry(right).contains(left.getString());
/*     */         }
/* 361 */         return !getDoubleAry(right).contains(Double.valueOf(left.getDouble()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 367 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<String> getStringAry(String text) {
/* 374 */     List<String> ary = new ArrayList<>();
/* 375 */     String test2 = text.substring(1, text.length() - 1);
/* 376 */     String[] ss = test2.split(",");
/* 377 */     for (String s : ss) {
/* 378 */       ary.add(s.substring(1, s.length() - 1));
/*     */     }
/*     */     
/* 381 */     return ary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<Double> getDoubleAry(String text) {
/* 388 */     List<Double> ary = new ArrayList<>();
/* 389 */     String test2 = text.substring(1, text.length() - 1);
/* 390 */     String[] ss = test2.split(",");
/* 391 */     for (String s : ss) {
/* 392 */       ary.add(Double.valueOf(Double.parseDouble(s)));
/*     */     }
/*     */     
/* 395 */     return ary;
/*     */   }
/*     */   private static Resolver handler_$; private static Resolver handler_xx; private static Resolver handler_x; private static Resolver handler_prop; private static Resolver handler_fun; private static Resolver handler_ary_x;
/* 398 */   private static Map<String, Pattern> _regexLib = new HashMap<>(); private static Resolver handler_ary_exp; private static Resolver handler_ary_ref; private static Resolver handler_ary_multi; private static Resolver handler_ary_range; private static Resolver handler_ary_prop;
/*     */   private static Pattern regex(String exprFull, String expr) {
/* 400 */     Pattern p = _regexLib.get(exprFull);
/* 401 */     if (p == null) {
/* 402 */       synchronized (exprFull.intern()) {
/* 403 */         if (p == null) {
/* 404 */           if (exprFull.endsWith("i")) {
/* 405 */             p = Pattern.compile(expr, 2);
/*     */           } else {
/* 407 */             p = Pattern.compile(expr);
/*     */           } 
/* 409 */           _regexLib.put(exprFull, p);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 414 */     return p;
/*     */   }
/*     */   static {
/* 417 */     handler_$ = ((s, root, tmp, usd) -> tmp);
/* 418 */     handler_xx = ((s, root, tmp, usd) -> {
/*     */         if (s.name.length() > 0) {
/*     */           ONode tmp2 = (new ONode(root.options())).asArray();
/*     */           
/*     */           if ("*".equals(s.name)) {
/*     */             scanByAll(s.name, tmp, true, tmp2.ary());
/*     */           } else {
/*     */             scanByName(s.name, tmp, tmp2.ary());
/*     */           } 
/*     */           
/*     */           if (tmp2.count() > 0) {
/*     */             return tmp2;
/*     */           }
/*     */         } 
/*     */         
/*     */         return null;
/*     */       });
/*     */     
/* 436 */     handler_x = ((s, root, tmp, usd) -> {
/*     */         ONode tmp2 = null;
/*     */         
/*     */         if (tmp.count() > 0) {
/*     */           tmp2 = (new ONode(tmp.options())).asArray();
/*     */           
/*     */           if (tmp.isObject()) {
/*     */             tmp2.addAll(tmp.obj().values());
/*     */           } else {
/*     */             tmp2.addAll(tmp.ary());
/*     */           } 
/*     */         } 
/*     */         
/*     */         return tmp2;
/*     */       });
/* 451 */     handler_prop = ((s, root, tmp, usd) -> {
/*     */         if (tmp.isObject()) {
/*     */           return tmp.getOrNull(s.cmd);
/*     */         }
/*     */         
/*     */         if (tmp.isArray()) {
/*     */           ONode tmp2 = (new ONode(tmp.options())).asArray();
/*     */           
/*     */           for (ONode n1 : tmp.ary()) {
/*     */             if (n1.isObject()) {
/*     */               ONode n2 = (ONode)(n1.nodeData()).object.get(s.cmd);
/*     */               
/*     */               if (n2 != null) {
/*     */                 tmp2.add(n2);
/*     */               }
/*     */             } 
/*     */           } 
/*     */           
/*     */           return tmp2;
/*     */         } 
/*     */         
/*     */         return null;
/*     */       });
/*     */     
/* 475 */     handler_fun = ((s, root, tmp, usd) -> {
/*     */         switch (s.cmd) {
/*     */           case "size()":
/*     */             return (new ONode(tmp.options())).val(Integer.valueOf(tmp.count()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case "length()":
/*     */             return tmp.isValue() ? (new ONode(tmp.options())).val(Integer.valueOf(tmp.getString().length())) : (new ONode(tmp.options())).val(Integer.valueOf(tmp.count()));
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case "min()":
/*     */             if (tmp.isArray()) {
/*     */               ONode min_n = null;
/*     */               for (ONode n1 : tmp.ary()) {
/*     */                 if (n1.isValue()) {
/*     */                   if (min_n == null) {
/*     */                     min_n = n1;
/*     */                     continue;
/*     */                   } 
/*     */                   if (n1.getDouble() < min_n.getDouble()) {
/*     */                     min_n = n1;
/*     */                   }
/*     */                 } 
/*     */               } 
/*     */               return min_n;
/*     */             } 
/*     */             return null;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case "max()":
/*     */             if (tmp.isArray()) {
/*     */               ONode max_n = null;
/*     */               for (ONode n1 : tmp.ary()) {
/*     */                 if (n1.isValue()) {
/*     */                   if (max_n == null) {
/*     */                     max_n = n1;
/*     */                     continue;
/*     */                   } 
/*     */                   if (n1.getDouble() > max_n.getDouble()) {
/*     */                     max_n = n1;
/*     */                   }
/*     */                 } 
/*     */               } 
/*     */               return max_n;
/*     */             } 
/*     */             return null;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case "avg()":
/*     */             if (tmp.isArray()) {
/*     */               double sum = 0.0D;
/*     */               int num = 0;
/*     */               for (ONode n1 : tmp.ary()) {
/*     */                 if (n1.isValue()) {
/*     */                   sum += n1.getDouble();
/*     */                   num++;
/*     */                 } 
/*     */               } 
/*     */               if (num > 0) {
/*     */                 return (new ONode(tmp.options())).val(Double.valueOf(sum / num));
/*     */               }
/*     */             } 
/*     */             return null;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case "sum()":
/*     */             if (tmp.isArray()) {
/*     */               double sum = 0.0D;
/*     */               for (ONode n1 : tmp.ary()) {
/*     */                 sum += n1.getDouble();
/*     */               }
/*     */               return (new ONode(tmp.options())).val(Double.valueOf(sum));
/*     */             } 
/*     */             return null;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         return null;
/*     */       });
/* 567 */     handler_ary_x = ((s, root, tmp, usd) -> {
/*     */         ONode tmp2 = null;
/*     */         
/*     */         if (tmp.isArray()) {
/*     */           tmp2 = tmp;
/*     */         }
/*     */         
/*     */         if (tmp.isObject()) {
/*     */           tmp2 = (new ONode(tmp.options())).asArray();
/*     */           
/*     */           tmp2.addAll(tmp.obj().values());
/*     */         } 
/*     */         return tmp2;
/*     */       });
/* 581 */     handler_ary_exp = ((s, root, tmp, usd) -> {
/*     */         ONode tmp2 = tmp;
/*     */ 
/*     */         
/*     */         if (s.op == null) {
/*     */           if (tmp.isObject()) {
/*     */             if (do_get(tmp, s.left, true, usd.booleanValue()).isNull()) {
/*     */               return null;
/*     */             }
/*     */           } else if (tmp.isArray()) {
/*     */             tmp2 = (new ONode(tmp.options())).asArray();
/*     */ 
/*     */             
/*     */             for (ONode n1 : tmp.ary()) {
/*     */               if (!do_get(n1, s.left, true, usd.booleanValue()).isNull()) {
/*     */                 (tmp2.nodeData()).array.add(n1);
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } else if (tmp.isObject()) {
/*     */           if ("@".equals(s.left)) {
/*     */             return null;
/*     */           }
/*     */           
/*     */           ONode leftO = do_get(tmp, s.left, true, usd.booleanValue());
/*     */           
/*     */           if (!compare(root, tmp, leftO, s.op, s.right, usd.booleanValue())) {
/*     */             return null;
/*     */           }
/*     */         } else if (tmp.isArray()) {
/*     */           tmp2 = (new ONode(tmp.options())).asArray();
/*     */           
/*     */           if ("@".equals(s.left)) {
/*     */             for (ONode n1 : tmp.ary()) {
/*     */               if (compare(root, n1, n1, s.op, s.right, usd.booleanValue())) {
/*     */                 tmp2.addNode(n1);
/*     */               }
/*     */             } 
/*     */           } else {
/*     */             for (ONode n1 : tmp.ary()) {
/*     */               ONode leftO = do_get(n1, s.left, true, usd.booleanValue());
/*     */               
/*     */               if (compare(root, n1, leftO, s.op, s.right, usd.booleanValue())) {
/*     */                 tmp2.addNode(n1);
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } else if (tmp.isValue() && "@".equals(s.left) && !compare(root, tmp, tmp, s.op, s.right, usd.booleanValue())) {
/*     */           return null;
/*     */         } 
/*     */         
/*     */         return tmp2;
/*     */       });
/*     */     
/* 635 */     handler_ary_ref = ((s, root, tmp, usd) -> {
/*     */         ONode tmp2 = null;
/*     */         
/*     */         if (tmp.isObject()) {
/*     */           if (s.cmdAry.startsWith("$")) {
/*     */             tmp2 = do_get(root, s.cmdAry, true, usd.booleanValue());
/*     */           } else {
/*     */             tmp2 = do_get(tmp, s.cmdAry, true, usd.booleanValue());
/*     */           } 
/*     */           
/*     */           if (tmp2.isValue()) {
/*     */             tmp2 = tmp.get(tmp2.getString());
/*     */           } else {
/*     */             tmp2 = null;
/*     */           } 
/*     */         } 
/*     */         
/*     */         return tmp2;
/*     */       });
/*     */     
/* 655 */     handler_ary_multi = ((s, root, tmp, usd) -> {
/*     */         ONode tmp2 = null;
/*     */         
/*     */         if (s.cmdAry.indexOf("'") >= 0) {
/*     */           if (tmp.isObject()) {
/*     */             for (String k : s.nameS) {
/*     */               ONode n1 = (ONode)tmp.obj().get(k);
/*     */               
/*     */               if (n1 != null) {
/*     */                 if (tmp2 == null) {
/*     */                   tmp2 = (new ONode(tmp.options())).asArray();
/*     */                 }
/*     */                 
/*     */                 tmp2.addNode(n1);
/*     */               } 
/*     */             } 
/*     */           }
/*     */           
/*     */           if (tmp.isArray()) {
/*     */             tmp2 = (new ONode(tmp.options())).asArray();
/*     */             
/*     */             for (ONode tmp1 : tmp.ary()) {
/*     */               if (tmp1.isObject()) {
/*     */                 for (String k : s.nameS) {
/*     */                   ONode n1 = (ONode)tmp1.obj().get(k);
/*     */                   
/*     */                   if (n1 != null) {
/*     */                     tmp2.addNode(n1);
/*     */                   }
/*     */                 } 
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } else if (tmp.isArray()) {
/*     */           List<ONode> list2 = (tmp.nodeData()).array;
/*     */           
/*     */           int len2 = list2.size();
/*     */           
/*     */           Iterator<Integer> iterator = s.indexS.iterator();
/*     */           
/*     */           while (iterator.hasNext()) {
/*     */             int idx = ((Integer)iterator.next()).intValue();
/*     */             if (idx >= 0 && idx < len2) {
/*     */               if (tmp2 == null) {
/*     */                 tmp2 = (new ONode(tmp.options())).asArray();
/*     */               }
/*     */               tmp2.addNode(list2.get(idx));
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         return tmp2;
/*     */       });
/* 707 */     handler_ary_range = ((s, root, tmp, usd) -> {
/*     */         if (tmp.isArray()) {
/*     */           int count = tmp.count();
/*     */           
/*     */           int start = s.start;
/*     */           
/*     */           int end = s.end;
/*     */           
/*     */           if (start < 0) {
/*     */             start = count + start;
/*     */           }
/*     */           
/*     */           if (end == 0) {
/*     */             end = count;
/*     */           }
/*     */           
/*     */           if (end < 0) {
/*     */             end = count + end;
/*     */           }
/*     */           
/*     */           if (start < 0) {
/*     */             start = 0;
/*     */           }
/*     */           
/*     */           if (end > count) {
/*     */             end = count;
/*     */           }
/*     */           return (new ONode(tmp.options())).addAll(tmp.ary().subList(start, end));
/*     */         } 
/*     */         return null;
/*     */       });
/* 738 */     handler_ary_prop = ((s, root, tmp, usd) -> {
/*     */         if (s.cmdHasQuote) {
/*     */           if (tmp.isObject()) {
/*     */             return tmp.getOrNull(s.name);
/*     */           }
/*     */           if (tmp.isArray()) {
/*     */             ONode tmp2 = (new ONode(tmp.options())).asArray();
/*     */             for (ONode n1 : tmp.ary()) {
/*     */               if (n1.isObject()) {
/*     */                 ONode n2 = (ONode)(n1.nodeData()).object.get(s.name);
/*     */                 if (n2 != null) {
/*     */                   tmp2.add(n2);
/*     */                 }
/*     */               } 
/*     */             } 
/*     */             return tmp2;
/*     */           } 
/*     */           return null;
/*     */         } 
/*     */         return (s.start < 0) ? tmp.getOrNull(tmp.count() + s.start) : tmp.getOrNull(s.start);
/*     */       });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Segment
/*     */   {
/*     */     public String cmd;
/*     */ 
/*     */     
/*     */     public String cmdAry;
/*     */ 
/*     */     
/*     */     public boolean cmdHasQuote;
/*     */ 
/*     */     
/*     */     public boolean cmdHasUnline;
/*     */ 
/*     */     
/*     */     public List<Integer> indexS;
/*     */ 
/*     */     
/*     */     public List<String> nameS;
/*     */     
/*     */     public String name;
/*     */     
/* 784 */     public int start = 0;
/* 785 */     public int end = 0;
/*     */     
/*     */     public String left;
/*     */     
/*     */     public String op;
/*     */     public String right;
/*     */     public JsonPath.Resolver handler;
/*     */     
/*     */     public Segment(String test) {
/* 794 */       this.cmd = test.trim();
/* 795 */       this.cmdHasQuote = (this.cmd.indexOf("'") >= 0);
/* 796 */       this.cmdHasUnline = this.cmd.startsWith("_");
/*     */       
/* 798 */       if (this.cmdHasUnline) {
/* 799 */         this.name = this.cmd.substring(1);
/*     */       }
/*     */       
/* 802 */       if (this.cmd.endsWith("]")) {
/* 803 */         this.cmdAry = this.cmd.substring(0, this.cmd.length() - 1).trim();
/*     */         
/* 805 */         if (this.cmdAry.startsWith("?")) {
/* 806 */           String s2 = this.cmdAry.substring(2, this.cmdAry.length() - 1);
/* 807 */           String[] ss2 = s2.split(" ");
/* 808 */           this.left = ss2[0];
/*     */           
/* 810 */           if (ss2.length == 3) {
/* 811 */             this.op = ss2[1];
/* 812 */             this.right = ss2[2];
/*     */           } 
/* 814 */         } else if (this.cmdAry.indexOf(":") >= 0) {
/* 815 */           String[] iAry = this.cmdAry.split(":", -1);
/* 816 */           this.start = 0;
/* 817 */           if (iAry[0].length() > 0) {
/* 818 */             this.start = Integer.parseInt(iAry[0]);
/*     */           }
/* 820 */           this.end = 0;
/* 821 */           if (iAry[1].length() > 0) {
/* 822 */             this.end = Integer.parseInt(iAry[1]);
/*     */           }
/* 824 */         } else if (this.cmdAry.indexOf(",") > 0) {
/* 825 */           if (this.cmdAry.indexOf("'") >= 0) {
/* 826 */             this.nameS = new ArrayList<>();
/* 827 */             String[] iAry = this.cmdAry.split(",");
/* 828 */             for (String i1 : iAry) {
/* 829 */               i1 = i1.trim();
/* 830 */               this.nameS.add(i1.substring(1, i1.length() - 1));
/*     */             } 
/*     */           } else {
/*     */             
/* 834 */             this.indexS = new ArrayList<>();
/* 835 */             String[] iAry = this.cmdAry.split(",");
/* 836 */             for (String i1 : iAry) {
/* 837 */               i1 = i1.trim();
/* 838 */               this.indexS.add(Integer.valueOf(Integer.parseInt(i1)));
/*     */             }
/*     */           
/*     */           } 
/* 842 */         } else if (this.cmdAry.indexOf("'") >= 0) {
/* 843 */           this.name = this.cmdAry.substring(1, this.cmdAry.length() - 1);
/* 844 */         } else if (StringUtil.isInteger(this.cmdAry)) {
/* 845 */           this.start = Integer.parseInt(this.cmdAry);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 852 */       if ("$".equals(this.cmd) || "@".equals(this.cmd)) {
/* 853 */         this.handler = JsonPath.handler_$;
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 858 */       if (this.cmd.startsWith("_")) {
/* 859 */         this.handler = JsonPath.handler_xx;
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 864 */       if ("*".equals(this.cmd)) {
/* 865 */         this.handler = JsonPath.handler_x;
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 870 */       if (this.cmd.endsWith("]")) {
/* 871 */         if ("*".equals(this.cmdAry)) {
/*     */ 
/*     */           
/* 874 */           this.handler = JsonPath.handler_ary_x; return;
/*     */         } 
/* 876 */         if (this.cmd.startsWith("?")) {
/*     */ 
/*     */           
/* 879 */           this.handler = JsonPath.handler_ary_exp;
/* 880 */         } else if (this.cmdAry.indexOf(",") > 0) {
/*     */ 
/*     */ 
/*     */           
/* 884 */           this.handler = JsonPath.handler_ary_multi;
/*     */         }
/* 886 */         else if (this.cmdAry.indexOf(":") >= 0) {
/*     */ 
/*     */ 
/*     */           
/* 890 */           this.handler = JsonPath.handler_ary_range;
/*     */         }
/* 892 */         else if (this.cmdAry.startsWith("$.") || this.cmdAry.startsWith("@.")) {
/*     */           
/* 894 */           this.handler = JsonPath.handler_ary_ref;
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 900 */           this.handler = JsonPath.handler_ary_prop;
/*     */         }
/*     */       
/* 903 */       } else if (this.cmd.endsWith(")")) {
/*     */         
/* 905 */         this.handler = JsonPath.handler_fun;
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 910 */         this.handler = JsonPath.handler_prop;
/*     */       } 
/*     */     }
/*     */     
/*     */     public int length() {
/* 915 */       return this.cmd.length();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 920 */       return this.cmd;
/*     */     }
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   private static interface Resolver {
/*     */     ONode run(JsonPath.Segment param1Segment, ONode param1ONode1, ONode param1ONode2, Boolean param1Boolean);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\JsonPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */