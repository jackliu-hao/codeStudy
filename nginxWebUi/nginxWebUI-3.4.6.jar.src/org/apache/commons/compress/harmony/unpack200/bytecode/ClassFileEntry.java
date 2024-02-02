/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ClassFileEntry
/*    */ {
/* 27 */   protected static final ClassFileEntry[] NONE = new ClassFileEntry[0];
/*    */   
/*    */   private boolean resolved;
/*    */   
/*    */   protected abstract void doWrite(DataOutputStream paramDataOutputStream) throws IOException;
/*    */   
/*    */   public abstract boolean equals(Object paramObject);
/*    */   
/*    */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 36 */     return NONE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract int hashCode();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void resolve(ClassConstantPool pool) {
/* 48 */     this.resolved = true;
/*    */   }
/*    */   
/*    */   protected int objectHashCode() {
/* 52 */     return super.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract String toString();
/*    */   
/*    */   public final void write(DataOutputStream dos) throws IOException {
/* 59 */     if (!this.resolved) {
/* 60 */       throw new IllegalStateException("Entry has not been resolved");
/*    */     }
/* 62 */     doWrite(dos);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\ClassFileEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */