/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import org.apache.commons.compress.harmony.pack200.Codec;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
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
/*     */ public class AttributeLayout
/*     */   implements IMatcher
/*     */ {
/*     */   public static final String ACC_ABSTRACT = "ACC_ABSTRACT";
/*     */   public static final String ACC_ANNOTATION = "ACC_ANNOTATION";
/*     */   public static final String ACC_ENUM = "ACC_ENUM";
/*     */   public static final String ACC_FINAL = "ACC_FINAL";
/*     */   public static final String ACC_INTERFACE = "ACC_INTERFACE";
/*     */   public static final String ACC_NATIVE = "ACC_NATIVE";
/*     */   public static final String ACC_PRIVATE = "ACC_PRIVATE";
/*     */   public static final String ACC_PROTECTED = "ACC_PROTECTED";
/*     */   public static final String ACC_PUBLIC = "ACC_PUBLIC";
/*     */   public static final String ACC_STATIC = "ACC_STATIC";
/*     */   public static final String ACC_STRICT = "ACC_STRICT";
/*     */   public static final String ACC_SYNCHRONIZED = "ACC_SYNCHRONIZED";
/*     */   public static final String ACC_SYNTHETIC = "ACC_SYNTHETIC";
/*     */   public static final String ACC_TRANSIENT = "ACC_TRANSIENT";
/*     */   public static final String ACC_VOLATILE = "ACC_VOLATILE";
/*     */   public static final String ATTRIBUTE_ANNOTATION_DEFAULT = "AnnotationDefault";
/*     */   public static final String ATTRIBUTE_CLASS_FILE_VERSION = "class-file version";
/*     */   public static final String ATTRIBUTE_CODE = "Code";
/*     */   public static final String ATTRIBUTE_CONSTANT_VALUE = "ConstantValue";
/*     */   public static final String ATTRIBUTE_DEPRECATED = "Deprecated";
/*     */   public static final String ATTRIBUTE_ENCLOSING_METHOD = "EnclosingMethod";
/*     */   public static final String ATTRIBUTE_EXCEPTIONS = "Exceptions";
/*     */   public static final String ATTRIBUTE_INNER_CLASSES = "InnerClasses";
/*     */   public static final String ATTRIBUTE_LINE_NUMBER_TABLE = "LineNumberTable";
/*     */   public static final String ATTRIBUTE_LOCAL_VARIABLE_TABLE = "LocalVariableTable";
/*     */   public static final String ATTRIBUTE_LOCAL_VARIABLE_TYPE_TABLE = "LocalVariableTypeTable";
/*     */   public static final String ATTRIBUTE_RUNTIME_INVISIBLE_ANNOTATIONS = "RuntimeInvisibleAnnotations";
/*     */   public static final String ATTRIBUTE_RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS = "RuntimeInvisibleParameterAnnotations";
/*     */   public static final String ATTRIBUTE_RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";
/*     */   public static final String ATTRIBUTE_RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS = "RuntimeVisibleParameterAnnotations";
/*     */   public static final String ATTRIBUTE_SIGNATURE = "Signature";
/*     */   public static final String ATTRIBUTE_SOURCE_FILE = "SourceFile";
/*     */   public static final int CONTEXT_CLASS = 0;
/*     */   public static final int CONTEXT_CODE = 3;
/*     */   public static final int CONTEXT_FIELD = 1;
/*     */   public static final int CONTEXT_METHOD = 2;
/*  64 */   public static final String[] contextNames = new String[] { "Class", "Field", "Method", "Code" };
/*     */   
/*     */   private final int context;
/*     */   
/*     */   private static ClassFileEntry getValue(String layout, long value, SegmentConstantPool pool) throws Pack200Exception {
/*  69 */     if (layout.startsWith("R")) {
/*     */       
/*  71 */       if (layout.indexOf('N') != -1) {
/*  72 */         value--;
/*     */       }
/*  74 */       if (layout.startsWith("RU")) {
/*  75 */         return pool.getValue(1, value);
/*     */       }
/*  77 */       if (layout.startsWith("RS")) {
/*  78 */         return pool.getValue(8, value);
/*     */       }
/*  80 */     } else if (layout.startsWith("K")) {
/*  81 */       char type = layout.charAt(1);
/*  82 */       switch (type) {
/*     */         case 'S':
/*  84 */           return pool.getValue(6, value);
/*     */         case 'C':
/*     */         case 'I':
/*  87 */           return pool.getValue(2, value);
/*     */         case 'F':
/*  89 */           return pool.getValue(3, value);
/*     */         case 'J':
/*  91 */           return pool.getValue(4, value);
/*     */         case 'D':
/*  93 */           return pool.getValue(5, value);
/*     */       } 
/*     */     } 
/*  96 */     throw new Pack200Exception("Unknown layout encoding: " + layout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int index;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String layout;
/*     */ 
/*     */ 
/*     */   
/*     */   private long mask;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String name;
/*     */ 
/*     */   
/*     */   private final boolean isDefault;
/*     */ 
/*     */   
/*     */   private int backwardsCallCount;
/*     */ 
/*     */ 
/*     */   
/*     */   public AttributeLayout(String name, int context, String layout, int index) throws Pack200Exception {
/* 125 */     this(name, context, layout, index, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AttributeLayout(String name, int context, String layout, int index, boolean isDefault) throws Pack200Exception {
/* 131 */     this.index = index;
/* 132 */     this.context = context;
/* 133 */     if (index >= 0) {
/* 134 */       this.mask = 1L << index;
/*     */     } else {
/* 136 */       this.mask = 0L;
/*     */     } 
/* 138 */     if (context != 0 && context != 3 && context != 1 && context != 2)
/*     */     {
/* 140 */       throw new Pack200Exception("Attribute context out of range: " + context);
/*     */     }
/* 142 */     if (layout == null) {
/* 143 */       throw new Pack200Exception("Cannot have a null layout");
/*     */     }
/* 145 */     if (name == null || name.length() == 0) {
/* 146 */       throw new Pack200Exception("Cannot have an unnamed layout");
/*     */     }
/* 148 */     this.name = name;
/* 149 */     this.layout = layout;
/* 150 */     this.isDefault = isDefault;
/*     */   }
/*     */   
/*     */   public Codec getCodec() {
/* 154 */     if (this.layout.indexOf('O') >= 0) {
/* 155 */       return (Codec)Codec.BRANCH5;
/*     */     }
/* 157 */     if (this.layout.indexOf('P') >= 0) {
/* 158 */       return (Codec)Codec.BCI5;
/*     */     }
/* 160 */     if (this.layout.indexOf('S') >= 0 && this.layout.indexOf("KS") < 0 && this.layout
/* 161 */       .indexOf("RS") < 0) {
/* 162 */       return (Codec)Codec.SIGNED5;
/*     */     }
/* 164 */     if (this.layout.indexOf('B') >= 0) {
/* 165 */       return (Codec)Codec.BYTE1;
/*     */     }
/* 167 */     return (Codec)Codec.UNSIGNED5;
/*     */   }
/*     */   
/*     */   public String getLayout() {
/* 171 */     return this.layout;
/*     */   }
/*     */   
/*     */   public ClassFileEntry getValue(long value, SegmentConstantPool pool) throws Pack200Exception {
/* 175 */     return getValue(this.layout, value, pool);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassFileEntry getValue(long value, String type, SegmentConstantPool pool) throws Pack200Exception {
/* 183 */     if (!this.layout.startsWith("KQ")) {
/* 184 */       return getValue(this.layout, value, pool);
/*     */     }
/* 186 */     if (type.equals("Ljava/lang/String;")) {
/* 187 */       ClassFileEntry value2 = getValue("KS", value, pool);
/* 188 */       return value2;
/*     */     } 
/* 190 */     return getValue("K" + type + this.layout.substring(2), value, pool);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 196 */     int PRIME = 31;
/* 197 */     int r = 1;
/* 198 */     if (this.name != null) {
/* 199 */       r = r * 31 + this.name.hashCode();
/*     */     }
/* 201 */     if (this.layout != null) {
/* 202 */       r = r * 31 + this.layout.hashCode();
/*     */     }
/* 204 */     r = r * 31 + this.index;
/* 205 */     r = r * 31 + this.context;
/* 206 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(long value) {
/* 216 */     return ((value & this.mask) != 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 221 */     return contextNames[this.context] + ": " + this.name;
/*     */   }
/*     */   
/*     */   public int getContext() {
/* 225 */     return this.context;
/*     */   }
/*     */   
/*     */   public int getIndex() {
/* 229 */     return this.index;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 233 */     return this.name;
/*     */   }
/*     */   
/*     */   public int numBackwardsCallables() {
/* 237 */     if (this.layout == "*") {
/* 238 */       return 1;
/*     */     }
/* 240 */     return this.backwardsCallCount;
/*     */   }
/*     */   
/*     */   public boolean isDefaultLayout() {
/* 244 */     return this.isDefault;
/*     */   }
/*     */   
/*     */   public void setBackwardsCallCount(int backwardsCallCount) {
/* 248 */     this.backwardsCallCount = backwardsCallCount;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\AttributeLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */