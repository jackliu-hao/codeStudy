/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.core.Macro;
/*     */ import freemarker.core.TemplateMarkupOutputModel;
/*     */ import freemarker.core._CoreAPI;
/*     */ import freemarker.ext.beans.BeanModel;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateCollectionModelEx;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateDirectiveModel;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import freemarker.template.TemplateNodeModelEx;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Array;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
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
/*     */ public class ClassUtil
/*     */ {
/*     */   public static Class forName(String className) throws ClassNotFoundException {
/*     */     try {
/*  82 */       ClassLoader ctcl = Thread.currentThread().getContextClassLoader();
/*  83 */       if (ctcl != null) {
/*  84 */         return Class.forName(className, true, ctcl);
/*     */       }
/*  86 */     } catch (ClassNotFoundException classNotFoundException) {
/*     */     
/*  88 */     } catch (SecurityException securityException) {}
/*     */ 
/*     */ 
/*     */     
/*  92 */     return Class.forName(className);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  97 */   private static final Map<String, Class<?>> PRIMITIVE_CLASSES_BY_NAME = new HashMap<>(); static {
/*  98 */     PRIMITIVE_CLASSES_BY_NAME.put("boolean", boolean.class);
/*  99 */     PRIMITIVE_CLASSES_BY_NAME.put("byte", byte.class);
/* 100 */     PRIMITIVE_CLASSES_BY_NAME.put("char", char.class);
/* 101 */     PRIMITIVE_CLASSES_BY_NAME.put("short", short.class);
/* 102 */     PRIMITIVE_CLASSES_BY_NAME.put("int", int.class);
/* 103 */     PRIMITIVE_CLASSES_BY_NAME.put("long", long.class);
/* 104 */     PRIMITIVE_CLASSES_BY_NAME.put("float", float.class);
/* 105 */     PRIMITIVE_CLASSES_BY_NAME.put("double", double.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> resolveIfPrimitiveTypeName(String typeName) {
/* 114 */     return PRIMITIVE_CLASSES_BY_NAME.get(typeName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> getArrayClass(Class<?> elementType, int dimensions) {
/* 124 */     return (dimensions == 0) ? elementType : Array.newInstance(elementType, new int[dimensions]).getClass();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getShortClassName(Class pClass) {
/* 133 */     return getShortClassName(pClass, false);
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
/*     */   public static String getShortClassName(Class pClass, boolean shortenFreeMarkerClasses) {
/* 148 */     if (pClass == null)
/* 149 */       return null; 
/* 150 */     if (pClass.isArray()) {
/* 151 */       return getShortClassName(pClass.getComponentType()) + "[]";
/*     */     }
/* 153 */     String cn = pClass.getName();
/* 154 */     if (cn.startsWith("java.lang.") || cn.startsWith("java.util.")) {
/* 155 */       return cn.substring(10);
/*     */     }
/* 157 */     if (shortenFreeMarkerClasses) {
/* 158 */       if (cn.startsWith("freemarker.template."))
/* 159 */         return "f.t" + cn.substring(19); 
/* 160 */       if (cn.startsWith("freemarker.ext.beans."))
/* 161 */         return "f.e.b" + cn.substring(20); 
/* 162 */       if (cn.startsWith("freemarker.core."))
/* 163 */         return "f.c" + cn.substring(15); 
/* 164 */       if (cn.startsWith("freemarker.ext."))
/* 165 */         return "f.e" + cn.substring(14); 
/* 166 */       if (cn.startsWith("freemarker.")) {
/* 167 */         return "f" + cn.substring(10);
/*     */       }
/*     */     } 
/*     */     
/* 171 */     return cn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getShortClassNameOfObject(Object obj) {
/* 182 */     return getShortClassNameOfObject(obj, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getShortClassNameOfObject(Object obj, boolean shortenFreeMarkerClasses) {
/* 192 */     if (obj == null) {
/* 193 */       return "Null";
/*     */     }
/* 195 */     return getShortClassName(obj.getClass(), shortenFreeMarkerClasses);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Class getPrimaryTemplateModelInterface(TemplateModel tm) {
/* 203 */     if (tm instanceof BeanModel) {
/* 204 */       if (tm instanceof freemarker.ext.beans.CollectionModel)
/* 205 */         return TemplateSequenceModel.class; 
/* 206 */       if (tm instanceof freemarker.ext.beans.IteratorModel || tm instanceof freemarker.ext.beans.EnumerationModel)
/* 207 */         return TemplateCollectionModel.class; 
/* 208 */       if (tm instanceof freemarker.ext.beans.MapModel)
/* 209 */         return TemplateHashModelEx.class; 
/* 210 */       if (tm instanceof freemarker.ext.beans.NumberModel)
/* 211 */         return TemplateNumberModel.class; 
/* 212 */       if (tm instanceof freemarker.ext.beans.BooleanModel)
/* 213 */         return TemplateBooleanModel.class; 
/* 214 */       if (tm instanceof freemarker.ext.beans.DateModel)
/* 215 */         return TemplateDateModel.class; 
/* 216 */       if (tm instanceof freemarker.ext.beans.StringModel) {
/* 217 */         Object wrapped = ((BeanModel)tm).getWrappedObject();
/* 218 */         return (wrapped instanceof String) ? TemplateScalarModel.class : ((tm instanceof TemplateHashModelEx) ? TemplateHashModelEx.class : null);
/*     */       } 
/*     */ 
/*     */       
/* 222 */       return null;
/*     */     } 
/* 224 */     if (tm instanceof freemarker.ext.beans.SimpleMethodModel || tm instanceof freemarker.ext.beans.OverloadedMethodsModel)
/* 225 */       return TemplateMethodModelEx.class; 
/* 226 */     if (tm instanceof TemplateCollectionModel && 
/* 227 */       _CoreAPI.isLazilyGeneratedSequenceModel((TemplateCollectionModel)tm)) {
/* 228 */       return TemplateSequenceModel.class;
/*     */     }
/* 230 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void appendTemplateModelTypeName(StringBuilder sb, Set typeNamesAppended, Class<?> cl) {
/* 235 */     int initalLength = sb.length();
/*     */     
/* 237 */     if (TemplateNodeModelEx.class.isAssignableFrom(cl)) {
/* 238 */       appendTypeName(sb, typeNamesAppended, "extended node");
/* 239 */     } else if (TemplateNodeModel.class.isAssignableFrom(cl)) {
/* 240 */       appendTypeName(sb, typeNamesAppended, "node");
/*     */     } 
/*     */     
/* 243 */     if (TemplateDirectiveModel.class.isAssignableFrom(cl)) {
/* 244 */       appendTypeName(sb, typeNamesAppended, "directive");
/* 245 */     } else if (TemplateTransformModel.class.isAssignableFrom(cl)) {
/* 246 */       appendTypeName(sb, typeNamesAppended, "transform");
/*     */     } 
/*     */     
/* 249 */     if (TemplateSequenceModel.class.isAssignableFrom(cl)) {
/* 250 */       appendTypeName(sb, typeNamesAppended, "sequence");
/* 251 */     } else if (TemplateCollectionModel.class.isAssignableFrom(cl)) {
/* 252 */       appendTypeName(sb, typeNamesAppended, 
/* 253 */           TemplateCollectionModelEx.class.isAssignableFrom(cl) ? "extended_collection" : "collection");
/* 254 */     } else if (TemplateModelIterator.class.isAssignableFrom(cl)) {
/* 255 */       appendTypeName(sb, typeNamesAppended, "iterator");
/*     */     } 
/*     */     
/* 258 */     if (TemplateMethodModel.class.isAssignableFrom(cl)) {
/* 259 */       appendTypeName(sb, typeNamesAppended, "method");
/*     */     }
/*     */     
/* 262 */     if (Environment.Namespace.class.isAssignableFrom(cl)) {
/* 263 */       appendTypeName(sb, typeNamesAppended, "namespace");
/* 264 */     } else if (TemplateHashModelEx.class.isAssignableFrom(cl)) {
/* 265 */       appendTypeName(sb, typeNamesAppended, "extended_hash");
/* 266 */     } else if (TemplateHashModel.class.isAssignableFrom(cl)) {
/* 267 */       appendTypeName(sb, typeNamesAppended, "hash");
/*     */     } 
/*     */     
/* 270 */     if (TemplateNumberModel.class.isAssignableFrom(cl)) {
/* 271 */       appendTypeName(sb, typeNamesAppended, "number");
/*     */     }
/*     */     
/* 274 */     if (TemplateDateModel.class.isAssignableFrom(cl)) {
/* 275 */       appendTypeName(sb, typeNamesAppended, "date_or_time_or_datetime");
/*     */     }
/*     */     
/* 278 */     if (TemplateBooleanModel.class.isAssignableFrom(cl)) {
/* 279 */       appendTypeName(sb, typeNamesAppended, "boolean");
/*     */     }
/*     */     
/* 282 */     if (TemplateScalarModel.class.isAssignableFrom(cl)) {
/* 283 */       appendTypeName(sb, typeNamesAppended, "string");
/*     */     }
/*     */     
/* 286 */     if (TemplateMarkupOutputModel.class.isAssignableFrom(cl)) {
/* 287 */       appendTypeName(sb, typeNamesAppended, "markup_output");
/*     */     }
/*     */     
/* 290 */     if (sb.length() == initalLength) {
/* 291 */       appendTypeName(sb, typeNamesAppended, "misc_template_model");
/*     */     }
/*     */   }
/*     */   
/*     */   private static Class getUnwrappedClass(TemplateModel tm) {
/*     */     Object unwrapped;
/*     */     try {
/* 298 */       if (tm instanceof WrapperTemplateModel) {
/* 299 */         unwrapped = ((WrapperTemplateModel)tm).getWrappedObject();
/* 300 */       } else if (tm instanceof AdapterTemplateModel) {
/* 301 */         unwrapped = ((AdapterTemplateModel)tm).getAdaptedObject(Object.class);
/*     */       } else {
/* 303 */         unwrapped = null;
/*     */       } 
/* 305 */     } catch (Throwable e) {
/* 306 */       unwrapped = null;
/*     */     } 
/* 308 */     return (unwrapped != null) ? unwrapped.getClass() : null;
/*     */   }
/*     */   
/*     */   private static void appendTypeName(StringBuilder sb, Set<String> typeNamesAppended, String name) {
/* 312 */     if (!typeNamesAppended.contains(name)) {
/* 313 */       if (sb.length() != 0) sb.append("+"); 
/* 314 */       sb.append(name);
/* 315 */       typeNamesAppended.add(name);
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
/*     */   public static String getFTLTypeDescription(TemplateModel tm) {
/*     */     String javaClassName;
/* 328 */     if (tm == null) {
/* 329 */       return "Null";
/*     */     }
/* 331 */     Set typeNamesAppended = new HashSet();
/*     */     
/* 333 */     StringBuilder sb = new StringBuilder();
/*     */     
/* 335 */     Class primaryInterface = getPrimaryTemplateModelInterface(tm);
/* 336 */     if (primaryInterface != null) {
/* 337 */       appendTemplateModelTypeName(sb, typeNamesAppended, primaryInterface);
/*     */     }
/*     */     
/* 340 */     if (tm instanceof Macro) {
/* 341 */       appendTypeName(sb, typeNamesAppended, ((Macro)tm).isFunction() ? "function" : "macro");
/*     */     }
/*     */     
/* 344 */     appendTemplateModelTypeName(sb, typeNamesAppended, tm.getClass());
/*     */ 
/*     */     
/* 347 */     Class unwrappedClass = getUnwrappedClass(tm);
/* 348 */     if (unwrappedClass != null) {
/* 349 */       javaClassName = getShortClassName(unwrappedClass, true);
/*     */     } else {
/* 351 */       javaClassName = null;
/*     */     } 
/*     */     
/* 354 */     sb.append(" (");
/* 355 */     String modelClassName = getShortClassName(tm.getClass(), true);
/* 356 */     if (javaClassName == null) {
/* 357 */       sb.append("wrapper: ");
/* 358 */       sb.append(modelClassName);
/*     */     } else {
/* 360 */       sb.append(javaClassName);
/* 361 */       sb.append(" wrapped into ");
/* 362 */       sb.append(modelClassName);
/*     */     } 
/* 364 */     sb.append(")");
/*     */     
/* 366 */     return sb.toString();
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
/*     */   public static Class primitiveClassToBoxingClass(Class<int> primitiveClass) {
/* 382 */     if (primitiveClass == int.class) return Integer.class; 
/* 383 */     if (primitiveClass == boolean.class) return Boolean.class; 
/* 384 */     if (primitiveClass == long.class) return Long.class; 
/* 385 */     if (primitiveClass == double.class) return Double.class; 
/* 386 */     if (primitiveClass == char.class) return Character.class; 
/* 387 */     if (primitiveClass == float.class) return Float.class; 
/* 388 */     if (primitiveClass == byte.class) return Byte.class; 
/* 389 */     if (primitiveClass == short.class) return Short.class; 
/* 390 */     if (primitiveClass == void.class) return Void.class; 
/* 391 */     return primitiveClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class boxingClassToPrimitiveClass(Class<Integer> boxingClass) {
/* 401 */     if (boxingClass == Integer.class) return int.class; 
/* 402 */     if (boxingClass == Boolean.class) return boolean.class; 
/* 403 */     if (boxingClass == Long.class) return long.class; 
/* 404 */     if (boxingClass == Double.class) return double.class; 
/* 405 */     if (boxingClass == Character.class) return char.class; 
/* 406 */     if (boxingClass == Float.class) return float.class; 
/* 407 */     if (boxingClass == Byte.class) return byte.class; 
/* 408 */     if (boxingClass == Short.class) return short.class; 
/* 409 */     if (boxingClass == Void.class) return void.class; 
/* 410 */     return boxingClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNumerical(Class<?> type) {
/* 421 */     return (Number.class.isAssignableFrom(type) || (type
/* 422 */       .isPrimitive() && type != boolean.class && type != char.class && type != void.class));
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
/*     */ 
/*     */   
/*     */   public static InputStream getReasourceAsStream(Class<?> baseClass, String resource, boolean optional) throws IOException {
/*     */     InputStream ins;
/*     */     try {
/* 445 */       ins = baseClass.getResourceAsStream(resource);
/* 446 */     } catch (Exception e) {
/*     */ 
/*     */       
/* 449 */       URL url = baseClass.getResource(resource);
/* 450 */       ins = (url != null) ? url.openStream() : null;
/*     */     } 
/* 452 */     if (!optional) {
/* 453 */       checkInputStreamNotNull(ins, baseClass, resource);
/*     */     }
/* 455 */     return ins;
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
/*     */   public static InputStream getReasourceAsStream(ClassLoader classLoader, String resource, boolean optional) throws IOException {
/*     */     InputStream ins;
/*     */     try {
/* 469 */       ins = classLoader.getResourceAsStream(resource);
/* 470 */     } catch (Exception e) {
/* 471 */       URL url = classLoader.getResource(resource);
/* 472 */       ins = (url != null) ? url.openStream() : null;
/*     */     } 
/* 474 */     if (ins == null && !optional) {
/* 475 */       throw new IOException("Class-loader resource not found (shown quoted): " + 
/* 476 */           StringUtil.jQuote(resource) + ". The base ClassLoader was: " + classLoader);
/*     */     }
/* 478 */     return ins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Properties loadProperties(Class<?> baseClass, String resource) throws IOException {
/* 488 */     Properties props = new Properties();
/*     */     
/* 490 */     InputStream ins = null;
/*     */     
/*     */     try {
/*     */       try {
/* 494 */         ins = baseClass.getResourceAsStream(resource);
/* 495 */       } catch (Exception e) {
/* 496 */         throw new MaybeZipFileClosedException();
/*     */       } 
/* 498 */       checkInputStreamNotNull(ins, baseClass, resource);
/*     */       try {
/* 500 */         props.load(ins);
/* 501 */       } catch (Exception e) {
/* 502 */         throw new MaybeZipFileClosedException();
/*     */       } finally {
/*     */         try {
/* 505 */           ins.close();
/* 506 */         } catch (Exception exception) {}
/*     */ 
/*     */         
/* 509 */         ins = null;
/*     */       } 
/* 511 */     } catch (MaybeZipFileClosedException e) {
/*     */ 
/*     */       
/* 514 */       URL url = baseClass.getResource(resource);
/* 515 */       ins = (url != null) ? url.openStream() : null;
/* 516 */       checkInputStreamNotNull(ins, baseClass, resource);
/* 517 */       props.load(ins);
/*     */     } finally {
/* 519 */       if (ins != null) {
/*     */         try {
/* 521 */           ins.close();
/* 522 */         } catch (Exception exception) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 527 */     return props;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void checkInputStreamNotNull(InputStream ins, Class<?> baseClass, String resource) throws IOException {
/* 532 */     if (ins == null)
/* 533 */       throw new IOException("Class-loader resource not found (shown quoted): " + 
/* 534 */           StringUtil.jQuote(resource) + ". The base class was " + baseClass.getName() + "."); 
/*     */   }
/*     */   
/*     */   private static class MaybeZipFileClosedException extends Exception {
/*     */     private MaybeZipFileClosedException() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\ClassUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */