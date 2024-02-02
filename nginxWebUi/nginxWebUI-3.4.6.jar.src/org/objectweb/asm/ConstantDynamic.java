/*     */ package org.objectweb.asm;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ConstantDynamic
/*     */ {
/*     */   private final String name;
/*     */   private final String descriptor;
/*     */   private final Handle bootstrapMethod;
/*     */   private final Object[] bootstrapMethodArguments;
/*     */   
/*     */   public ConstantDynamic(String name, String descriptor, Handle bootstrapMethod, Object... bootstrapMethodArguments) {
/*  68 */     this.name = name;
/*  69 */     this.descriptor = descriptor;
/*  70 */     this.bootstrapMethod = bootstrapMethod;
/*  71 */     this.bootstrapMethodArguments = bootstrapMethodArguments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  80 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDescriptor() {
/*  89 */     return this.descriptor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Handle getBootstrapMethod() {
/*  98 */     return this.bootstrapMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBootstrapMethodArgumentCount() {
/* 109 */     return this.bootstrapMethodArguments.length;
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
/*     */   public Object getBootstrapMethodArgument(int index) {
/* 121 */     return this.bootstrapMethodArguments[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object[] getBootstrapMethodArgumentsUnsafe() {
/* 132 */     return this.bootstrapMethodArguments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() {
/* 141 */     char firstCharOfDescriptor = this.descriptor.charAt(0);
/* 142 */     return (firstCharOfDescriptor == 'J' || firstCharOfDescriptor == 'D') ? 2 : 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 147 */     if (object == this) {
/* 148 */       return true;
/*     */     }
/* 150 */     if (!(object instanceof ConstantDynamic)) {
/* 151 */       return false;
/*     */     }
/* 153 */     ConstantDynamic constantDynamic = (ConstantDynamic)object;
/* 154 */     return (this.name.equals(constantDynamic.name) && this.descriptor
/* 155 */       .equals(constantDynamic.descriptor) && this.bootstrapMethod
/* 156 */       .equals(constantDynamic.bootstrapMethod) && 
/* 157 */       Arrays.equals(this.bootstrapMethodArguments, constantDynamic.bootstrapMethodArguments));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 162 */     return this.name.hashCode() ^ 
/* 163 */       Integer.rotateLeft(this.descriptor.hashCode(), 8) ^ 
/* 164 */       Integer.rotateLeft(this.bootstrapMethod.hashCode(), 16) ^ 
/* 165 */       Integer.rotateLeft(Arrays.hashCode(this.bootstrapMethodArguments), 24);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 170 */     return this.name + " : " + this.descriptor + ' ' + this.bootstrapMethod + ' ' + 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 176 */       Arrays.toString(this.bootstrapMethodArguments);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\ConstantDynamic.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */