/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import java.lang.reflect.Member;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
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
/*     */ abstract class MemberMatcher<M extends Member, S>
/*     */ {
/*     */   protected abstract S toMemberSignature(M paramM);
/*     */   
/*     */   protected abstract boolean matchInUpperBoundTypeSubtypes();
/*     */   
/*  36 */   private final Map<S, Types> signaturesToUpperBoundTypes = new HashMap<>();
/*     */   
/*     */   private static class Types {
/*  39 */     private final Set<Class<?>> set = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Types() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean containsInterfaces;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void addMatching(Class<?> upperBoundType, M member) {
/*  60 */     Class<?> declaringClass = member.getDeclaringClass();
/*  61 */     if (!declaringClass.isAssignableFrom(upperBoundType)) {
/*  62 */       throw new IllegalArgumentException("Upper bound class " + upperBoundType.getName() + " is not the same type or a subtype of the declaring type of member " + member + ".");
/*     */     }
/*     */ 
/*     */     
/*  66 */     S memberSignature = toMemberSignature(member);
/*  67 */     Types upperBoundTypes = this.signaturesToUpperBoundTypes.get(memberSignature);
/*  68 */     if (upperBoundTypes == null) {
/*  69 */       upperBoundTypes = new Types();
/*  70 */       this.signaturesToUpperBoundTypes.put(memberSignature, upperBoundTypes);
/*     */     } 
/*  72 */     upperBoundTypes.set.add(upperBoundType);
/*  73 */     if (upperBoundType.isInterface()) {
/*  74 */       upperBoundTypes.containsInterfaces = true;
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
/*     */   boolean matches(Class<?> contextClass, M member) {
/*  87 */     S memberSignature = toMemberSignature(member);
/*  88 */     Types upperBoundTypes = this.signaturesToUpperBoundTypes.get(memberSignature);
/*     */     
/*  90 */     return (upperBoundTypes != null && (
/*  91 */       matchInUpperBoundTypeSubtypes() ? 
/*  92 */       containsTypeOrSuperType(upperBoundTypes, contextClass) : 
/*  93 */       containsExactType(upperBoundTypes, contextClass)));
/*     */   }
/*     */   
/*     */   private static boolean containsExactType(Types types, Class<?> c) {
/*  97 */     if (c == null) {
/*  98 */       return false;
/*     */     }
/* 100 */     return types.set.contains(c);
/*     */   }
/*     */   
/*     */   private static boolean containsTypeOrSuperType(Types types, Class<?> c) {
/* 104 */     if (c == null) {
/* 105 */       return false;
/*     */     }
/*     */     
/* 108 */     if (types.set.contains(c)) {
/* 109 */       return true;
/*     */     }
/* 111 */     if (containsTypeOrSuperType(types, c.getSuperclass())) {
/* 112 */       return true;
/*     */     }
/* 114 */     if (types.containsInterfaces) {
/* 115 */       for (Class<?> anInterface : c.getInterfaces()) {
/* 116 */         if (containsTypeOrSuperType(types, anInterface)) {
/* 117 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 121 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\MemberMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */