/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class AnnotationDefaultAttribute
/*    */   extends AnnotationsAttribute
/*    */ {
/*    */   private final AnnotationsAttribute.ElementValue element_value;
/*    */   private static CPUTF8 attributeName;
/*    */   
/*    */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/* 34 */     attributeName = cpUTF8Value;
/*    */   }
/*    */   
/*    */   public AnnotationDefaultAttribute(AnnotationsAttribute.ElementValue element_value) {
/* 38 */     super(attributeName);
/* 39 */     this.element_value = element_value;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getLength() {
/* 44 */     return this.element_value.getLength();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 49 */     this.element_value.writeBody(dos);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void resolve(ClassConstantPool pool) {
/* 54 */     super.resolve(pool);
/* 55 */     this.element_value.resolve(pool);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 60 */     return "AnnotationDefault: " + this.element_value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 65 */     return (this == obj);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 70 */     List<CPUTF8> nested = new ArrayList();
/* 71 */     nested.add(attributeName);
/* 72 */     nested.addAll(this.element_value.getClassFileEntries());
/* 73 */     ClassFileEntry[] nestedEntries = new ClassFileEntry[nested.size()];
/* 74 */     for (int i = 0; i < nestedEntries.length; i++) {
/* 75 */       nestedEntries[i] = nested.get(i);
/*    */     }
/* 77 */     return nestedEntries;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\AnnotationDefaultAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */