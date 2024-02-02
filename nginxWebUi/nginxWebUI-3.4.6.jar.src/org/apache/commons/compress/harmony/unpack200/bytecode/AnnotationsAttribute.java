/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AnnotationsAttribute
/*     */   extends Attribute
/*     */ {
/*     */   public static class Annotation
/*     */   {
/*     */     private final int num_pairs;
/*     */     private final CPUTF8[] element_names;
/*     */     private final AnnotationsAttribute.ElementValue[] element_values;
/*     */     private final CPUTF8 type;
/*     */     private int type_index;
/*     */     private int[] name_indexes;
/*     */     
/*     */     public Annotation(int num_pairs, CPUTF8 type, CPUTF8[] element_names, AnnotationsAttribute.ElementValue[] element_values) {
/*  45 */       this.num_pairs = num_pairs;
/*  46 */       this.type = type;
/*  47 */       this.element_names = element_names;
/*  48 */       this.element_values = element_values;
/*     */     }
/*     */     
/*     */     public int getLength() {
/*  52 */       int length = 4;
/*  53 */       for (int i = 0; i < this.num_pairs; i++) {
/*  54 */         length += 2;
/*  55 */         length += this.element_values[i].getLength();
/*     */       } 
/*  57 */       return length;
/*     */     }
/*     */     
/*     */     public void resolve(ClassConstantPool pool) {
/*  61 */       this.type.resolve(pool);
/*  62 */       this.type_index = pool.indexOf(this.type);
/*  63 */       this.name_indexes = new int[this.num_pairs];
/*  64 */       for (int i = 0; i < this.element_names.length; i++) {
/*  65 */         this.element_names[i].resolve(pool);
/*  66 */         this.name_indexes[i] = pool.indexOf(this.element_names[i]);
/*  67 */         this.element_values[i].resolve(pool);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void writeBody(DataOutputStream dos) throws IOException {
/*  72 */       dos.writeShort(this.type_index);
/*  73 */       dos.writeShort(this.num_pairs);
/*  74 */       for (int i = 0; i < this.num_pairs; i++) {
/*  75 */         dos.writeShort(this.name_indexes[i]);
/*  76 */         this.element_values[i].writeBody(dos);
/*     */       } 
/*     */     }
/*     */     
/*     */     public List getClassFileEntries() {
/*  81 */       List<CPUTF8> entries = new ArrayList();
/*  82 */       for (int i = 0; i < this.element_names.length; i++) {
/*  83 */         entries.add(this.element_names[i]);
/*  84 */         entries.addAll(this.element_values[i].getClassFileEntries());
/*     */       } 
/*  86 */       entries.add(this.type);
/*  87 */       return entries;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class ElementValue
/*     */   {
/*     */     private final Object value;
/*     */     
/*     */     private final int tag;
/*  97 */     private int constant_value_index = -1;
/*     */     
/*     */     public ElementValue(int tag, Object value) {
/* 100 */       this.tag = tag;
/* 101 */       this.value = value;
/*     */     }
/*     */     
/*     */     public List getClassFileEntries() {
/* 105 */       List<CPUTF8> entries = new ArrayList(1);
/* 106 */       if (this.value instanceof CPNameAndType) {
/*     */         
/* 108 */         entries.add(((CPNameAndType)this.value).name);
/* 109 */         entries.add(((CPNameAndType)this.value).descriptor);
/* 110 */       } else if (this.value instanceof ClassFileEntry) {
/* 111 */         entries.add(this.value);
/* 112 */       } else if (this.value instanceof ElementValue[]) {
/* 113 */         ElementValue[] values = (ElementValue[])this.value;
/* 114 */         for (int i = 0; i < values.length; i++) {
/* 115 */           entries.addAll(values[i].getClassFileEntries());
/*     */         }
/* 117 */       } else if (this.value instanceof AnnotationsAttribute.Annotation) {
/* 118 */         entries.addAll(((AnnotationsAttribute.Annotation)this.value).getClassFileEntries());
/*     */       } 
/* 120 */       return entries;
/*     */     }
/*     */     
/*     */     public void resolve(ClassConstantPool pool) {
/* 124 */       if (this.value instanceof CPConstant) {
/* 125 */         ((CPConstant)this.value).resolve(pool);
/* 126 */         this.constant_value_index = pool.indexOf((CPConstant)this.value);
/* 127 */       } else if (this.value instanceof CPClass) {
/* 128 */         ((CPClass)this.value).resolve(pool);
/* 129 */         this.constant_value_index = pool.indexOf((CPClass)this.value);
/* 130 */       } else if (this.value instanceof CPUTF8) {
/* 131 */         ((CPUTF8)this.value).resolve(pool);
/* 132 */         this.constant_value_index = pool.indexOf((CPUTF8)this.value);
/* 133 */       } else if (this.value instanceof CPNameAndType) {
/* 134 */         ((CPNameAndType)this.value).resolve(pool);
/* 135 */       } else if (this.value instanceof AnnotationsAttribute.Annotation) {
/* 136 */         ((AnnotationsAttribute.Annotation)this.value).resolve(pool);
/* 137 */       } else if (this.value instanceof ElementValue[]) {
/* 138 */         ElementValue[] nestedValues = (ElementValue[])this.value;
/* 139 */         for (int i = 0; i < nestedValues.length; i++) {
/* 140 */           nestedValues[i].resolve(pool);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public void writeBody(DataOutputStream dos) throws IOException {
/* 146 */       dos.writeByte(this.tag);
/* 147 */       if (this.constant_value_index != -1) {
/* 148 */         dos.writeShort(this.constant_value_index);
/* 149 */       } else if (this.value instanceof CPNameAndType) {
/* 150 */         ((CPNameAndType)this.value).writeBody(dos);
/* 151 */       } else if (this.value instanceof AnnotationsAttribute.Annotation) {
/* 152 */         ((AnnotationsAttribute.Annotation)this.value).writeBody(dos);
/* 153 */       } else if (this.value instanceof ElementValue[]) {
/* 154 */         ElementValue[] nestedValues = (ElementValue[])this.value;
/* 155 */         dos.writeShort(nestedValues.length);
/* 156 */         for (int i = 0; i < nestedValues.length; i++) {
/* 157 */           nestedValues[i].writeBody(dos);
/*     */         }
/*     */       } else {
/* 160 */         throw new Error("");
/*     */       }  } public int getLength() {
/*     */       int length;
/*     */       ElementValue[] nestedValues;
/*     */       int i;
/* 165 */       switch (this.tag) {
/*     */         case 66:
/*     */         case 67:
/*     */         case 68:
/*     */         case 70:
/*     */         case 73:
/*     */         case 74:
/*     */         case 83:
/*     */         case 90:
/*     */         case 99:
/*     */         case 115:
/* 176 */           return 3;
/*     */         case 101:
/* 178 */           return 5;
/*     */         case 91:
/* 180 */           length = 3;
/* 181 */           nestedValues = (ElementValue[])this.value;
/* 182 */           for (i = 0; i < nestedValues.length; i++) {
/* 183 */             length += nestedValues[i].getLength();
/*     */           }
/* 185 */           return length;
/*     */         case 64:
/* 187 */           return 1 + ((AnnotationsAttribute.Annotation)this.value).getLength();
/*     */       } 
/* 189 */       return 0;
/*     */     }
/*     */   }
/*     */   
/*     */   public AnnotationsAttribute(CPUTF8 attributeName) {
/* 194 */     super(attributeName);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\AnnotationsAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */