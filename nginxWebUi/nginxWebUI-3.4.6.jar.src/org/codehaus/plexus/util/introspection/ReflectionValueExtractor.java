/*     */ package org.codehaus.plexus.util.introspection;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.codehaus.plexus.util.StringUtils;
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
/*     */ public class ReflectionValueExtractor
/*     */ {
/*  44 */   private static final Class[] CLASS_ARGS = new Class[0];
/*     */   
/*  46 */   private static final Object[] OBJECT_ARGS = new Object[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private static final Map classMaps = new WeakHashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final Pattern INDEXED_PROPS = Pattern.compile("(\\w+)\\[(\\d+)\\]");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   private static final Pattern MAPPED_PROPS = Pattern.compile("(\\w+)\\((.+)\\)");
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
/*     */   public static Object evaluate(String expression, Object root) throws Exception {
/*  87 */     return evaluate(expression, root, true);
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
/*     */ 
/*     */   
/*     */   public static Object evaluate(String expression, Object root, boolean trimRootToken) throws Exception {
/* 110 */     if (trimRootToken)
/*     */     {
/* 112 */       expression = expression.substring(expression.indexOf('.') + 1);
/*     */     }
/*     */     
/* 115 */     Object value = root;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 122 */     StringTokenizer parser = new StringTokenizer(expression, ".");
/*     */     
/* 124 */     while (parser.hasMoreTokens()) {
/*     */       Method method;
/*     */       
/* 127 */       if (value == null)
/*     */       {
/* 129 */         return null;
/*     */       }
/*     */       
/* 132 */       String token = parser.nextToken();
/*     */       
/* 134 */       ClassMap classMap = getClassMap(value.getClass());
/*     */ 
/*     */       
/* 137 */       Object[] localParams = OBJECT_ARGS;
/*     */ 
/*     */       
/* 140 */       Matcher matcher = INDEXED_PROPS.matcher(token);
/* 141 */       if (matcher.find()) {
/*     */         
/* 143 */         String methodBase = StringUtils.capitalizeFirstLetter(matcher.group(1));
/* 144 */         String methodName = "get" + methodBase;
/* 145 */         method = classMap.findMethod(methodName, (Object[])CLASS_ARGS);
/* 146 */         value = method.invoke(value, OBJECT_ARGS);
/* 147 */         classMap = getClassMap(value.getClass());
/*     */         
/* 149 */         if (classMap.getCachedClass().isArray()) {
/*     */           
/* 151 */           value = Arrays.asList((Object[])value);
/* 152 */           classMap = getClassMap(value.getClass());
/*     */         } 
/*     */         
/* 155 */         if (value instanceof java.util.List)
/*     */         {
/*     */           
/* 158 */           localParams = new Object[1];
/* 159 */           localParams[0] = Integer.valueOf(matcher.group(2));
/* 160 */           method = classMap.findMethod("get", localParams);
/*     */         }
/*     */         else
/*     */         {
/* 164 */           throw new Exception("The token '" + token + "' refers to a java.util.List or an array, but the value seems is an instance of '" + value.getClass() + "'.");
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 172 */         matcher = MAPPED_PROPS.matcher(token);
/* 173 */         if (matcher.find()) {
/*     */           
/* 175 */           String methodBase = StringUtils.capitalizeFirstLetter(matcher.group(1));
/* 176 */           String methodName = "get" + methodBase;
/* 177 */           method = classMap.findMethod(methodName, (Object[])CLASS_ARGS);
/* 178 */           value = method.invoke(value, OBJECT_ARGS);
/* 179 */           classMap = getClassMap(value.getClass());
/*     */           
/* 181 */           if (value instanceof Map)
/*     */           {
/*     */             
/* 184 */             localParams = new Object[1];
/* 185 */             localParams[0] = matcher.group(2);
/* 186 */             method = classMap.findMethod("get", localParams);
/*     */           }
/*     */           else
/*     */           {
/* 190 */             throw new Exception("The token '" + token + "' refers to a java.util.Map, but the value seems is an instance of '" + value.getClass() + "'.");
/*     */           
/*     */           }
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 197 */           String methodBase = StringUtils.capitalizeFirstLetter(token);
/* 198 */           String methodName = "get" + methodBase;
/* 199 */           method = classMap.findMethod(methodName, (Object[])CLASS_ARGS);
/*     */           
/* 201 */           if (method == null) {
/*     */ 
/*     */             
/* 204 */             methodName = "is" + methodBase;
/*     */             
/* 206 */             method = classMap.findMethod(methodName, (Object[])CLASS_ARGS);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 211 */       if (method == null)
/*     */       {
/* 213 */         return null;
/*     */       }
/*     */ 
/*     */       
/*     */       try {
/* 218 */         value = method.invoke(value, localParams);
/*     */       }
/* 220 */       catch (InvocationTargetException e) {
/*     */ 
/*     */         
/* 223 */         if (e.getCause() instanceof IndexOutOfBoundsException)
/*     */         {
/* 225 */           return null;
/*     */         }
/*     */         
/* 228 */         throw e;
/*     */       } 
/*     */     } 
/*     */     
/* 232 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   private static ClassMap getClassMap(Class clazz) {
/* 237 */     ClassMap classMap = (ClassMap)classMaps.get(clazz);
/*     */     
/* 239 */     if (classMap == null) {
/*     */       
/* 241 */       classMap = new ClassMap(clazz);
/*     */       
/* 243 */       classMaps.put(clazz, classMap);
/*     */     } 
/*     */     
/* 246 */     return classMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\introspection\ReflectionValueExtractor.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */