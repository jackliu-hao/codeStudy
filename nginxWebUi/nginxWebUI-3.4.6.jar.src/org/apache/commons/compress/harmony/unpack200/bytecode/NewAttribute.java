/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class NewAttribute
/*     */   extends BCIRenumberedAttribute
/*     */ {
/*  30 */   private final List lengths = new ArrayList();
/*  31 */   private final List body = new ArrayList();
/*     */   private ClassConstantPool pool;
/*     */   private final int layoutIndex;
/*     */   
/*     */   public NewAttribute(CPUTF8 attributeName, int layoutIndex) {
/*  36 */     super(attributeName);
/*  37 */     this.layoutIndex = layoutIndex;
/*     */   }
/*     */   
/*     */   public int getLayoutIndex() {
/*  41 */     return this.layoutIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getLength() {
/*  51 */     int length = 0;
/*  52 */     for (int iter = 0; iter < this.lengths.size(); iter++) {
/*  53 */       length += ((Integer)this.lengths.get(iter)).intValue();
/*     */     }
/*  55 */     return length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/*  65 */     for (int i = 0; i < this.lengths.size(); i++) {
/*  66 */       int length = ((Integer)this.lengths.get(i)).intValue();
/*  67 */       Object obj = this.body.get(i);
/*  68 */       long value = 0L;
/*  69 */       if (obj instanceof Long) {
/*  70 */         value = ((Long)obj).longValue();
/*  71 */       } else if (obj instanceof ClassFileEntry) {
/*  72 */         value = this.pool.indexOf((ClassFileEntry)obj);
/*  73 */       } else if (obj instanceof BCValue) {
/*  74 */         value = ((BCValue)obj).actualValue;
/*     */       } 
/*     */       
/*  77 */       if (length == 1) {
/*  78 */         dos.writeByte((int)value);
/*  79 */       } else if (length == 2) {
/*  80 */         dos.writeShort((int)value);
/*  81 */       } else if (length == 4) {
/*  82 */         dos.writeInt((int)value);
/*  83 */       } else if (length == 8) {
/*  84 */         dos.writeLong(value);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  96 */     return this.attributeName.underlyingString();
/*     */   }
/*     */   
/*     */   public void addInteger(int length, long value) {
/* 100 */     this.lengths.add(Integer.valueOf(length));
/* 101 */     this.body.add(Long.valueOf(value));
/*     */   }
/*     */   
/*     */   public void addBCOffset(int length, int value) {
/* 105 */     this.lengths.add(Integer.valueOf(length));
/* 106 */     this.body.add(new BCOffset(value));
/*     */   }
/*     */   
/*     */   public void addBCIndex(int length, int value) {
/* 110 */     this.lengths.add(Integer.valueOf(length));
/* 111 */     this.body.add(new BCIndex(value));
/*     */   }
/*     */   
/*     */   public void addBCLength(int length, int value) {
/* 115 */     this.lengths.add(Integer.valueOf(length));
/* 116 */     this.body.add(new BCLength(value));
/*     */   }
/*     */   
/*     */   public void addToBody(int length, Object value) {
/* 120 */     this.lengths.add(Integer.valueOf(length));
/* 121 */     this.body.add(value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/* 126 */     super.resolve(pool);
/* 127 */     for (int iter = 0; iter < this.body.size(); iter++) {
/* 128 */       Object element = this.body.get(iter);
/* 129 */       if (element instanceof ClassFileEntry) {
/* 130 */         ((ClassFileEntry)element).resolve(pool);
/*     */       }
/*     */     } 
/* 133 */     this.pool = pool;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 138 */     int total = 1;
/* 139 */     for (int iter = 0; iter < this.body.size(); iter++) {
/* 140 */       Object element = this.body.get(iter);
/* 141 */       if (element instanceof ClassFileEntry) {
/* 142 */         total++;
/*     */       }
/*     */     } 
/* 145 */     ClassFileEntry[] nested = new ClassFileEntry[total];
/* 146 */     nested[0] = getAttributeName();
/* 147 */     int i = 1;
/* 148 */     for (int j = 0; j < this.body.size(); j++) {
/* 149 */       Object element = this.body.get(j);
/* 150 */       if (element instanceof ClassFileEntry) {
/* 151 */         nested[i] = (ClassFileEntry)element;
/* 152 */         i++;
/*     */       } 
/*     */     } 
/* 155 */     return nested;
/*     */   }
/*     */   
/*     */   private static class BCOffset
/*     */     extends BCValue {
/*     */     private final int offset;
/*     */     private int index;
/*     */     
/*     */     public BCOffset(int offset) {
/* 164 */       this.offset = offset;
/*     */     }
/*     */     
/*     */     public void setIndex(int index) {
/* 168 */       this.index = index;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BCIndex
/*     */     extends BCValue
/*     */   {
/*     */     private final int index;
/*     */     
/*     */     public BCIndex(int index) {
/* 178 */       this.index = index;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BCLength
/*     */     extends BCValue {
/*     */     private final int length;
/*     */     
/*     */     public BCLength(int length) {
/* 187 */       this.length = length;
/*     */     }
/*     */   }
/*     */   
/*     */   private static abstract class BCValue {
/*     */     int actualValue;
/*     */     
/*     */     private BCValue() {}
/*     */     
/*     */     public void setActualValue(int value) {
/* 197 */       this.actualValue = value;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] getStartPCs() {
/* 205 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void renumber(List<Integer> byteCodeOffsets) {
/* 210 */     if (!this.renumbered) {
/* 211 */       Object previous = null;
/* 212 */       for (Iterator iter = this.body.iterator(); iter.hasNext(); ) {
/* 213 */         Object obj = iter.next();
/* 214 */         if (obj instanceof BCIndex) {
/* 215 */           BCIndex bcIndex = (BCIndex)obj;
/* 216 */           bcIndex.setActualValue(((Integer)byteCodeOffsets.get(bcIndex.index)).intValue());
/* 217 */         } else if (obj instanceof BCOffset) {
/* 218 */           BCOffset bcOffset = (BCOffset)obj;
/* 219 */           if (previous instanceof BCIndex) {
/* 220 */             int index = ((BCIndex)previous).index + bcOffset.offset;
/* 221 */             bcOffset.setIndex(index);
/* 222 */             bcOffset.setActualValue(((Integer)byteCodeOffsets.get(index)).intValue());
/* 223 */           } else if (previous instanceof BCOffset) {
/* 224 */             int index = ((BCOffset)previous).index + bcOffset.offset;
/* 225 */             bcOffset.setIndex(index);
/* 226 */             bcOffset.setActualValue(((Integer)byteCodeOffsets.get(index)).intValue());
/*     */           } else {
/*     */             
/* 229 */             bcOffset.setActualValue(((Integer)byteCodeOffsets.get(bcOffset.offset)).intValue());
/*     */           } 
/* 231 */         } else if (obj instanceof BCLength) {
/*     */           
/* 233 */           BCLength bcLength = (BCLength)obj;
/* 234 */           BCIndex prevIndex = (BCIndex)previous;
/* 235 */           int index = prevIndex.index + bcLength.length;
/* 236 */           int actualLength = ((Integer)byteCodeOffsets.get(index)).intValue() - prevIndex.actualValue;
/* 237 */           bcLength.setActualValue(actualLength);
/*     */         } 
/* 239 */         previous = obj;
/*     */       } 
/* 241 */       this.renumbered = true;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\NewAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */