/*    */ package cn.hutool.core.io;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InvalidClassException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectStreamClass;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ValidateObjectInputStream
/*    */   extends ObjectInputStream
/*    */ {
/*    */   private Set<String> whiteClassSet;
/*    */   private Set<String> blackClassSet;
/*    */   
/*    */   public ValidateObjectInputStream(InputStream inputStream, Class<?>... acceptClasses) throws IOException {
/* 33 */     super(inputStream);
/* 34 */     accept(acceptClasses);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void refuse(Class<?>... refuseClasses) {
/* 44 */     if (null == this.blackClassSet) {
/* 45 */       this.blackClassSet = new HashSet<>();
/*    */     }
/* 47 */     for (Class<?> acceptClass : refuseClasses) {
/* 48 */       this.blackClassSet.add(acceptClass.getName());
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void accept(Class<?>... acceptClasses) {
/* 58 */     if (null == this.whiteClassSet) {
/* 59 */       this.whiteClassSet = new HashSet<>();
/*    */     }
/* 61 */     for (Class<?> acceptClass : acceptClasses) {
/* 62 */       this.whiteClassSet.add(acceptClass.getName());
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
/* 71 */     validateClassName(desc.getName());
/* 72 */     return super.resolveClass(desc);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void validateClassName(String className) throws InvalidClassException {
/* 82 */     if (CollUtil.isNotEmpty(this.blackClassSet) && 
/* 83 */       this.blackClassSet.contains(className)) {
/* 84 */       throw new InvalidClassException("Unauthorized deserialization attempt by black list", className);
/*    */     }
/*    */ 
/*    */     
/* 88 */     if (CollUtil.isEmpty(this.whiteClassSet)) {
/*    */       return;
/*    */     }
/* 91 */     if (className.startsWith("java.")) {
/*    */       return;
/*    */     }
/*    */     
/* 95 */     if (this.whiteClassSet.contains(className)) {
/*    */       return;
/*    */     }
/*    */     
/* 99 */     throw new InvalidClassException("Unauthorized deserialization attempt", className);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\ValidateObjectInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */