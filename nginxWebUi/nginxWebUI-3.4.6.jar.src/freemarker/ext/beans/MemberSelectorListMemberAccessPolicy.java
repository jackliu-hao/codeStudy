/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class MemberSelectorListMemberAccessPolicy
/*     */   implements MemberAccessPolicy
/*     */ {
/*     */   private final ListType listType;
/*     */   private final MethodMatcher methodMatcher;
/*     */   private final ConstructorMatcher constructorMatcher;
/*     */   private final FieldMatcher fieldMatcher;
/*     */   private final Class<? extends Annotation> matchAnnotation;
/*     */   
/*     */   enum ListType
/*     */   {
/*  72 */     WHITELIST,
/*     */     
/*  74 */     BLACKLIST;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class MemberSelector
/*     */   {
/*     */     private final Class<?> upperBoundType;
/*     */ 
/*     */ 
/*     */     
/*     */     private final Method method;
/*     */ 
/*     */ 
/*     */     
/*     */     private final Constructor<?> constructor;
/*     */ 
/*     */ 
/*     */     
/*     */     private final Field field;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public MemberSelector(Class<?> upperBoundType, Method method) {
/* 101 */       NullArgumentException.check("upperBoundType", upperBoundType);
/* 102 */       NullArgumentException.check("method", method);
/* 103 */       this.upperBoundType = upperBoundType;
/* 104 */       this.method = method;
/* 105 */       this.constructor = null;
/* 106 */       this.field = null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public MemberSelector(Class<?> upperBoundType, Constructor<?> constructor) {
/* 114 */       NullArgumentException.check("upperBoundType", upperBoundType);
/* 115 */       NullArgumentException.check("constructor", constructor);
/* 116 */       this.upperBoundType = upperBoundType;
/* 117 */       this.method = null;
/* 118 */       this.constructor = constructor;
/* 119 */       this.field = null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public MemberSelector(Class<?> upperBoundType, Field field) {
/* 127 */       NullArgumentException.check("upperBoundType", upperBoundType);
/* 128 */       NullArgumentException.check("field", field);
/* 129 */       this.upperBoundType = upperBoundType;
/* 130 */       this.method = null;
/* 131 */       this.constructor = null;
/* 132 */       this.field = field;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Class<?> getUpperBoundType() {
/* 139 */       return this.upperBoundType;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Method getMethod() {
/* 147 */       return this.method;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Constructor<?> getConstructor() {
/* 155 */       return this.constructor;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Field getField() {
/* 163 */       return this.field;
/*     */     }
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
/*     */     public static MemberSelector parse(String memberSelectorString, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
/* 190 */       if (memberSelectorString.contains("<") || memberSelectorString.contains(">") || memberSelectorString
/* 191 */         .contains("...") || memberSelectorString.contains(";")) {
/* 192 */         throw new IllegalArgumentException("Malformed whitelist entry (shouldn't contain \"<\", \">\", \"...\", or \";\"): " + memberSelectorString);
/*     */       }
/*     */ 
/*     */       
/* 196 */       String cleanedStr = memberSelectorString.trim().replaceAll("\\s*([\\.,\\(\\)\\[\\]])\\s*", "$1");
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 201 */       int openParenIdx = cleanedStr.indexOf('(');
/* 202 */       boolean hasArgList = (openParenIdx != -1);
/* 203 */       int postMemberNameIdx = hasArgList ? openParenIdx : cleanedStr.length();
/*     */ 
/*     */       
/* 206 */       int postClassDotIdx = cleanedStr.lastIndexOf('.', postMemberNameIdx);
/* 207 */       if (postClassDotIdx == -1) {
/* 208 */         throw new IllegalArgumentException("Malformed whitelist entry (missing dot): " + memberSelectorString);
/*     */       }
/*     */ 
/*     */       
/* 212 */       String upperBoundClassStr = cleanedStr.substring(0, postClassDotIdx);
/* 213 */       if (!MemberSelectorListMemberAccessPolicy.isWellFormedClassName(upperBoundClassStr)) {
/* 214 */         throw new IllegalArgumentException("Malformed whitelist entry (malformed upper bound class name): " + memberSelectorString);
/*     */       }
/*     */       
/* 217 */       Class<?> upperBoundClass = classLoader.loadClass(upperBoundClassStr);
/*     */       
/* 219 */       String memberName = cleanedStr.substring(postClassDotIdx + 1, postMemberNameIdx);
/* 220 */       if (!MemberSelectorListMemberAccessPolicy.isWellFormedJavaIdentifier(memberName)) {
/* 221 */         throw new IllegalArgumentException("Malformed whitelist entry (malformed member name): " + memberSelectorString);
/*     */       }
/*     */ 
/*     */       
/* 225 */       if (hasArgList) {
/* 226 */         if (cleanedStr.charAt(cleanedStr.length() - 1) != ')') {
/* 227 */           throw new IllegalArgumentException("Malformed whitelist entry (should end with ')'): " + memberSelectorString);
/*     */         }
/*     */         
/* 230 */         String argsSpec = cleanedStr.substring(postMemberNameIdx + 1, cleanedStr.length() - 1);
/* 231 */         StringTokenizer tok = new StringTokenizer(argsSpec, ",");
/* 232 */         int argCount = tok.countTokens();
/* 233 */         Class<?>[] argTypes = new Class[argCount];
/* 234 */         for (int i = 0; i < argCount; i++) {
/* 235 */           Class<?> argClass; String argClassName = tok.nextToken();
/* 236 */           int arrayDimensions = 0;
/* 237 */           while (argClassName.endsWith("[]")) {
/* 238 */             arrayDimensions++;
/* 239 */             argClassName = argClassName.substring(0, argClassName.length() - 2);
/*     */           } 
/*     */           
/* 242 */           Class<?> primArgClass = ClassUtil.resolveIfPrimitiveTypeName(argClassName);
/* 243 */           if (primArgClass != null) {
/* 244 */             argClass = primArgClass;
/*     */           } else {
/* 246 */             if (!MemberSelectorListMemberAccessPolicy.isWellFormedClassName(argClassName)) {
/* 247 */               throw new IllegalArgumentException("Malformed whitelist entry (malformed argument class name): " + memberSelectorString);
/*     */             }
/*     */             
/* 250 */             argClass = classLoader.loadClass(argClassName);
/*     */           } 
/* 252 */           argTypes[i] = ClassUtil.getArrayClass(argClass, arrayDimensions);
/*     */         } 
/* 254 */         return memberName.equals(upperBoundClass.getSimpleName()) ? new MemberSelector(upperBoundClass, upperBoundClass
/* 255 */             .getConstructor(argTypes)) : new MemberSelector(upperBoundClass, upperBoundClass
/* 256 */             .getMethod(memberName, argTypes));
/*     */       } 
/* 258 */       return new MemberSelector(upperBoundClass, upperBoundClass.getField(memberName));
/*     */     }
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
/*     */     public static List<MemberSelector> parse(Collection<String> memberSelectors, boolean ignoreMissingClassOrMember, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
/* 274 */       List<MemberSelector> parsedMemberSelectors = new ArrayList<>(memberSelectors.size());
/* 275 */       for (String memberSelector : memberSelectors) {
/* 276 */         if (!isIgnoredLine(memberSelector)) {
/*     */           try {
/* 278 */             parsedMemberSelectors.add(parse(memberSelector, classLoader));
/* 279 */           } catch (ClassNotFoundException|NoSuchFieldException|NoSuchMethodException e) {
/* 280 */             if (!ignoreMissingClassOrMember) {
/* 281 */               throw e;
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/* 286 */       return parsedMemberSelectors;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static boolean isIgnoredLine(String line) {
/* 295 */       String trimmedLine = line.trim();
/* 296 */       return (trimmedLine.length() == 0 || trimmedLine.startsWith("#") || trimmedLine.startsWith("//"));
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
/*     */ 
/*     */   
/*     */   MemberSelectorListMemberAccessPolicy(Collection<? extends MemberSelector> memberSelectors, ListType listType, Class<? extends Annotation> matchAnnotation) {
/* 311 */     this.listType = listType;
/* 312 */     this.matchAnnotation = matchAnnotation;
/*     */     
/* 314 */     this.methodMatcher = new MethodMatcher();
/* 315 */     this.constructorMatcher = new ConstructorMatcher();
/* 316 */     this.fieldMatcher = new FieldMatcher();
/* 317 */     for (MemberSelector memberSelector : memberSelectors) {
/* 318 */       Class<?> upperBoundClass = memberSelector.upperBoundType;
/* 319 */       if (memberSelector.constructor != null) {
/* 320 */         this.constructorMatcher.addMatching(upperBoundClass, memberSelector.constructor); continue;
/* 321 */       }  if (memberSelector.method != null) {
/* 322 */         this.methodMatcher.addMatching(upperBoundClass, memberSelector.method); continue;
/* 323 */       }  if (memberSelector.field != null) {
/* 324 */         this.fieldMatcher.addMatching(upperBoundClass, memberSelector.field); continue;
/*     */       } 
/* 326 */       throw new AssertionError();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final ClassMemberAccessPolicy forClass(final Class<?> contextClass) {
/* 333 */     return new ClassMemberAccessPolicy()
/*     */       {
/*     */         public boolean isMethodExposed(Method method) {
/* 336 */           return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult((MemberSelectorListMemberAccessPolicy.this
/* 337 */               .methodMatcher.matches(contextClass, method) || (MemberSelectorListMemberAccessPolicy.this
/* 338 */               .matchAnnotation != null && 
/* 339 */               _MethodUtil.getInheritableAnnotation(contextClass, method, (Class)MemberSelectorListMemberAccessPolicy.this.matchAnnotation) != null)));
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public boolean isConstructorExposed(Constructor<?> constructor) {
/* 345 */           return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult((MemberSelectorListMemberAccessPolicy.this
/* 346 */               .constructorMatcher.matches(contextClass, constructor) || (MemberSelectorListMemberAccessPolicy.this
/* 347 */               .matchAnnotation != null && 
/* 348 */               _MethodUtil.getInheritableAnnotation(contextClass, constructor, (Class)MemberSelectorListMemberAccessPolicy.this.matchAnnotation) != null)));
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public boolean isFieldExposed(Field field) {
/* 354 */           return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult((MemberSelectorListMemberAccessPolicy.this
/* 355 */               .fieldMatcher.matches(contextClass, field) || (MemberSelectorListMemberAccessPolicy.this
/* 356 */               .matchAnnotation != null && 
/* 357 */               _MethodUtil.getInheritableAnnotation(contextClass, field, (Class)MemberSelectorListMemberAccessPolicy.this.matchAnnotation) != null)));
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean matchResultToIsExposedResult(boolean matches) {
/* 364 */     if (this.listType == ListType.WHITELIST) {
/* 365 */       return matches;
/*     */     }
/* 367 */     if (this.listType == ListType.BLACKLIST) {
/* 368 */       return !matches;
/*     */     }
/* 370 */     throw new AssertionError();
/*     */   }
/*     */   
/*     */   private static boolean isWellFormedClassName(String s) {
/* 374 */     if (s.length() == 0) {
/* 375 */       return false;
/*     */     }
/* 377 */     int identifierStartIdx = 0;
/* 378 */     for (int i = 0; i < s.length(); i++) {
/* 379 */       char c = s.charAt(i);
/* 380 */       if (i == identifierStartIdx) {
/* 381 */         if (!Character.isJavaIdentifierStart(c)) {
/* 382 */           return false;
/*     */         }
/* 384 */       } else if (c == '.' && i != s.length() - 1) {
/* 385 */         identifierStartIdx = i + 1;
/*     */       }
/* 387 */       else if (!Character.isJavaIdentifierPart(c)) {
/* 388 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 392 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean isWellFormedJavaIdentifier(String s) {
/* 396 */     if (s.length() == 0) {
/* 397 */       return false;
/*     */     }
/* 399 */     if (!Character.isJavaIdentifierStart(s.charAt(0))) {
/* 400 */       return false;
/*     */     }
/* 402 */     for (int i = 1; i < s.length(); i++) {
/* 403 */       if (!Character.isJavaIdentifierPart(s.charAt(i))) {
/* 404 */         return false;
/*     */       }
/*     */     } 
/* 407 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\MemberSelectorListMemberAccessPolicy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */