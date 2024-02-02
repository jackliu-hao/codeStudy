/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ final class TaglibMethodUtil
/*     */ {
/*  36 */   private static final Pattern FUNCTION_SIGNATURE_PATTERN = Pattern.compile("^([\\w\\.]+(\\s*\\[\\s*\\])?)\\s+(\\w+)\\s*\\((.*)\\)$", 32);
/*     */   
/*  38 */   private static final Pattern FUNCTION_PARAMETER_PATTERN = Pattern.compile("^([\\w\\.]+)(\\s*\\[\\s*\\])?$");
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
/*     */   static Method getMethodByFunctionSignature(Class clazz, String signature) throws SecurityException, NoSuchMethodException, ClassNotFoundException {
/*  51 */     Matcher m1 = FUNCTION_SIGNATURE_PATTERN.matcher(signature);
/*     */     
/*  53 */     if (!m1.matches()) {
/*  54 */       throw new IllegalArgumentException("Invalid function signature (doesn't match this pattern: " + FUNCTION_SIGNATURE_PATTERN + ")");
/*     */     }
/*     */ 
/*     */     
/*  58 */     String methodName = m1.group(3);
/*  59 */     String params = m1.group(4).trim();
/*  60 */     Class[] paramTypes = null;
/*     */     
/*  62 */     if ("".equals(params)) {
/*  63 */       paramTypes = new Class[0];
/*     */     } else {
/*  65 */       String[] paramsArray = StringUtil.split(params, ',');
/*  66 */       paramTypes = new Class[paramsArray.length];
/*  67 */       String token = null;
/*  68 */       String paramType = null;
/*  69 */       boolean isPrimitive = false;
/*  70 */       boolean isArrayType = false;
/*  71 */       Matcher m2 = null;
/*     */       
/*  73 */       for (int i = 0; i < paramsArray.length; i++) {
/*  74 */         token = paramsArray[i].trim();
/*  75 */         m2 = FUNCTION_PARAMETER_PATTERN.matcher(token);
/*  76 */         if (!m2.matches()) {
/*  77 */           throw new IllegalArgumentException("Invalid argument signature (doesn't match pattern " + FUNCTION_PARAMETER_PATTERN + "): " + token);
/*     */         }
/*     */ 
/*     */         
/*  81 */         paramType = m2.group(1);
/*  82 */         isPrimitive = (paramType.indexOf('.') == -1);
/*  83 */         isArrayType = (m2.group(2) != null);
/*     */         
/*  85 */         if (isPrimitive) {
/*  86 */           if ("byte".equals(paramType)) {
/*  87 */             paramTypes[i] = isArrayType ? byte[].class : byte.class;
/*  88 */           } else if ("short".equals(paramType)) {
/*  89 */             paramTypes[i] = isArrayType ? short[].class : short.class;
/*  90 */           } else if ("int".equals(paramType)) {
/*  91 */             paramTypes[i] = isArrayType ? int[].class : int.class;
/*  92 */           } else if ("long".equals(paramType)) {
/*  93 */             paramTypes[i] = isArrayType ? long[].class : long.class;
/*  94 */           } else if ("float".equals(paramType)) {
/*  95 */             paramTypes[i] = isArrayType ? float[].class : float.class;
/*  96 */           } else if ("double".equals(paramType)) {
/*  97 */             paramTypes[i] = isArrayType ? double[].class : double.class;
/*  98 */           } else if ("boolean".equals(paramType)) {
/*  99 */             paramTypes[i] = isArrayType ? boolean[].class : boolean.class;
/* 100 */           } else if ("char".equals(paramType)) {
/* 101 */             paramTypes[i] = isArrayType ? char[].class : char.class;
/*     */           } else {
/* 103 */             throw new IllegalArgumentException("Invalid primitive type: '" + paramType + "'.");
/*     */           }
/*     */         
/* 106 */         } else if (isArrayType) {
/* 107 */           paramTypes[i] = ClassUtil.forName("[L" + paramType + ";");
/*     */         } else {
/* 109 */           paramTypes[i] = ClassUtil.forName(paramType);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 115 */     return clazz.getMethod(methodName, paramTypes);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\TaglibMethodUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */