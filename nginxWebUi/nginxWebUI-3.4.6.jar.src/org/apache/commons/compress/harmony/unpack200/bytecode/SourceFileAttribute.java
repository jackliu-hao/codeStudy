/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
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
/*     */ public class SourceFileAttribute
/*     */   extends Attribute
/*     */ {
/*     */   private final CPUTF8 name;
/*     */   private int nameIndex;
/*     */   private static CPUTF8 attributeName;
/*     */   
/*     */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/*  32 */     attributeName = cpUTF8Value;
/*     */   }
/*     */   
/*     */   public SourceFileAttribute(CPUTF8 name) {
/*  36 */     super(attributeName);
/*  37 */     this.name = name;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  42 */     if (this == obj) {
/*  43 */       return true;
/*     */     }
/*  45 */     if (!super.equals(obj)) {
/*  46 */       return false;
/*     */     }
/*  48 */     if (getClass() != obj.getClass()) {
/*  49 */       return false;
/*     */     }
/*  51 */     SourceFileAttribute other = (SourceFileAttribute)obj;
/*  52 */     if (this.name == null) {
/*  53 */       if (other.name != null) {
/*  54 */         return false;
/*     */       }
/*  56 */     } else if (!this.name.equals(other.name)) {
/*  57 */       return false;
/*     */     } 
/*  59 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSourceFileAttribute() {
/*  69 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getLength() {
/*  74 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  79 */     return new ClassFileEntry[] { getAttributeName(), this.name };
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  84 */     int PRIME = 31;
/*  85 */     int result = super.hashCode();
/*  86 */     result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
/*  87 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  92 */     super.resolve(pool);
/*  93 */     this.nameIndex = pool.indexOf(this.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  98 */     return "SourceFile: " + this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 103 */     dos.writeShort(this.nameIndex);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\SourceFileAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */