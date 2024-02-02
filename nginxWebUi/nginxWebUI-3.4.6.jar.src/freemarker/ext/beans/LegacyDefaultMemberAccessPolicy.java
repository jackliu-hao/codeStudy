/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashSet;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
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
/*     */ public final class LegacyDefaultMemberAccessPolicy
/*     */   implements MemberAccessPolicy
/*     */ {
/*  40 */   public static final LegacyDefaultMemberAccessPolicy INSTANCE = new LegacyDefaultMemberAccessPolicy();
/*     */   
/*     */   private static final String UNSAFE_METHODS_PROPERTIES = "unsafeMethods.properties";
/*  43 */   private static final Set<Method> UNSAFE_METHODS = createUnsafeMethodsSet();
/*     */   
/*     */   private static Set<Method> createUnsafeMethodsSet() {
/*     */     try {
/*  47 */       Properties props = ClassUtil.loadProperties(BeansWrapper.class, "unsafeMethods.properties");
/*  48 */       Set<Method> set = new HashSet<>(props.size() * 4 / 3, 1.0F);
/*  49 */       for (Object key : props.keySet()) {
/*     */         try {
/*  51 */           set.add(parseMethodSpec((String)key));
/*  52 */         } catch (ClassNotFoundException|NoSuchMethodException e) {
/*  53 */           if (ClassIntrospector.DEVELOPMENT_MODE) {
/*  54 */             throw e;
/*     */           }
/*     */         } 
/*     */       } 
/*  58 */       return set;
/*  59 */     } catch (Exception e) {
/*  60 */       throw new RuntimeException("Could not load unsafe method set", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Method parseMethodSpec(String methodSpec) throws ClassNotFoundException, NoSuchMethodException {
/*  67 */     int brace = methodSpec.indexOf('(');
/*  68 */     int dot = methodSpec.lastIndexOf('.', brace);
/*  69 */     Class<?> clazz = ClassUtil.forName(methodSpec.substring(0, dot));
/*  70 */     String methodName = methodSpec.substring(dot + 1, brace);
/*  71 */     String argSpec = methodSpec.substring(brace + 1, methodSpec.length() - 1);
/*  72 */     StringTokenizer tok = new StringTokenizer(argSpec, ",");
/*  73 */     int argcount = tok.countTokens();
/*  74 */     Class<?>[] argTypes = new Class[argcount];
/*  75 */     for (int i = 0; i < argcount; i++) {
/*  76 */       String argClassName = tok.nextToken();
/*  77 */       argTypes[i] = ClassUtil.resolveIfPrimitiveTypeName(argClassName);
/*  78 */       if (argTypes[i] == null) {
/*  79 */         argTypes[i] = ClassUtil.forName(argClassName);
/*     */       }
/*     */     } 
/*  82 */     return clazz.getMethod(methodName, argTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassMemberAccessPolicy forClass(Class<?> containingClass) {
/*  90 */     return CLASS_MEMBER_ACCESS_POLICY_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isToStringAlwaysExposed() {
/*  95 */     return true;
/*     */   }
/*     */   
/*  98 */   private static final BlacklistClassMemberAccessPolicy CLASS_MEMBER_ACCESS_POLICY_INSTANCE = new BlacklistClassMemberAccessPolicy();
/*     */   
/*     */   private static class BlacklistClassMemberAccessPolicy implements ClassMemberAccessPolicy {
/*     */     private BlacklistClassMemberAccessPolicy() {}
/*     */     
/*     */     public boolean isMethodExposed(Method method) {
/* 104 */       return !LegacyDefaultMemberAccessPolicy.UNSAFE_METHODS.contains(method);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isConstructorExposed(Constructor<?> constructor) {
/* 109 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFieldExposed(Field field) {
/* 114 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\LegacyDefaultMemberAccessPolicy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */