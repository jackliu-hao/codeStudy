/*     */ package com.google.protobuf;
/*     */ 
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
/*     */ public final class WireFormat
/*     */ {
/*     */   static final int FIXED32_SIZE = 4;
/*     */   static final int FIXED64_SIZE = 8;
/*     */   static final int MAX_VARINT32_SIZE = 5;
/*     */   static final int MAX_VARINT64_SIZE = 10;
/*     */   static final int MAX_VARINT_SIZE = 10;
/*     */   public static final int WIRETYPE_VARINT = 0;
/*     */   public static final int WIRETYPE_FIXED64 = 1;
/*     */   public static final int WIRETYPE_LENGTH_DELIMITED = 2;
/*     */   public static final int WIRETYPE_START_GROUP = 3;
/*     */   public static final int WIRETYPE_END_GROUP = 4;
/*     */   public static final int WIRETYPE_FIXED32 = 5;
/*     */   static final int TAG_TYPE_BITS = 3;
/*     */   static final int TAG_TYPE_MASK = 7;
/*     */   static final int MESSAGE_SET_ITEM = 1;
/*     */   static final int MESSAGE_SET_TYPE_ID = 2;
/*     */   static final int MESSAGE_SET_MESSAGE = 3;
/*     */   
/*     */   public static int getTagWireType(int tag) {
/*  67 */     return tag & 0x7;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getTagFieldNumber(int tag) {
/*  72 */     return tag >>> 3;
/*     */   }
/*     */ 
/*     */   
/*     */   static int makeTag(int fieldNumber, int wireType) {
/*  77 */     return fieldNumber << 3 | wireType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum JavaType
/*     */   {
/*  85 */     INT((String)Integer.valueOf(0)),
/*  86 */     LONG((String)Long.valueOf(0L)),
/*  87 */     FLOAT((String)Float.valueOf(0.0F)),
/*  88 */     DOUBLE((String)Double.valueOf(0.0D)),
/*  89 */     BOOLEAN((String)Boolean.valueOf(false)),
/*  90 */     STRING(""),
/*  91 */     BYTE_STRING((String)ByteString.EMPTY),
/*  92 */     ENUM(null),
/*  93 */     MESSAGE(null);
/*     */     
/*     */     JavaType(Object defaultDefault) {
/*  96 */       this.defaultDefault = defaultDefault;
/*     */     }
/*     */     private final Object defaultDefault;
/*     */     
/*     */     Object getDefaultDefault() {
/* 101 */       return this.defaultDefault;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum FieldType
/*     */   {
/* 112 */     DOUBLE((String)WireFormat.JavaType.DOUBLE, 1),
/* 113 */     FLOAT((String)WireFormat.JavaType.FLOAT, 5),
/* 114 */     INT64((String)WireFormat.JavaType.LONG, 0),
/* 115 */     UINT64((String)WireFormat.JavaType.LONG, 0),
/* 116 */     INT32((String)WireFormat.JavaType.INT, 0),
/* 117 */     FIXED64((String)WireFormat.JavaType.LONG, 1),
/* 118 */     FIXED32((String)WireFormat.JavaType.INT, 5),
/* 119 */     BOOL((String)WireFormat.JavaType.BOOLEAN, 0),
/* 120 */     STRING((String)WireFormat.JavaType.STRING, 2)
/*     */     {
/*     */       public boolean isPackable() {
/* 123 */         return false;
/*     */       }
/*     */     },
/* 126 */     GROUP((String)WireFormat.JavaType.MESSAGE, 3)
/*     */     {
/*     */       public boolean isPackable() {
/* 129 */         return false;
/*     */       }
/*     */     },
/* 132 */     MESSAGE((String)WireFormat.JavaType.MESSAGE, 2)
/*     */     {
/*     */       public boolean isPackable() {
/* 135 */         return false;
/*     */       }
/*     */     },
/* 138 */     BYTES((String)WireFormat.JavaType.BYTE_STRING, 2)
/*     */     {
/*     */       public boolean isPackable() {
/* 141 */         return false;
/*     */       }
/*     */     },
/* 144 */     UINT32((String)WireFormat.JavaType.INT, 0),
/* 145 */     ENUM((String)WireFormat.JavaType.ENUM, 0),
/* 146 */     SFIXED32((String)WireFormat.JavaType.INT, 5),
/* 147 */     SFIXED64((String)WireFormat.JavaType.LONG, 1),
/* 148 */     SINT32((String)WireFormat.JavaType.INT, 0),
/* 149 */     SINT64((String)WireFormat.JavaType.LONG, 0);
/*     */     
/*     */     FieldType(WireFormat.JavaType javaType, int wireType) {
/* 152 */       this.javaType = javaType;
/* 153 */       this.wireType = wireType;
/*     */     }
/*     */     
/*     */     private final WireFormat.JavaType javaType;
/*     */     private final int wireType;
/*     */     
/*     */     public WireFormat.JavaType getJavaType() {
/* 160 */       return this.javaType;
/*     */     }
/*     */     
/*     */     public int getWireType() {
/* 164 */       return this.wireType;
/*     */     }
/*     */     
/*     */     public boolean isPackable() {
/* 168 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 178 */   static final int MESSAGE_SET_ITEM_TAG = makeTag(1, 3);
/* 179 */   static final int MESSAGE_SET_ITEM_END_TAG = makeTag(1, 4);
/* 180 */   static final int MESSAGE_SET_TYPE_ID_TAG = makeTag(2, 0);
/*     */   
/* 182 */   static final int MESSAGE_SET_MESSAGE_TAG = makeTag(3, 2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   enum Utf8Validation
/*     */   {
/* 190 */     LOOSE
/*     */     {
/*     */       Object readString(CodedInputStream input) throws IOException {
/* 193 */         return input.readString();
/*     */       }
/*     */     },
/*     */     
/* 197 */     STRICT
/*     */     {
/*     */       Object readString(CodedInputStream input) throws IOException {
/* 200 */         return input.readStringRequireUtf8();
/*     */       }
/*     */     },
/*     */     
/* 204 */     LAZY
/*     */     {
/*     */       Object readString(CodedInputStream input) throws IOException {
/* 207 */         return input.readBytes();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     abstract Object readString(CodedInputStream param1CodedInputStream) throws IOException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Object readPrimitiveField(CodedInputStream input, FieldType type, Utf8Validation utf8Validation) throws IOException {
/* 227 */     switch (type) {
/*     */       case DOUBLE:
/* 229 */         return Double.valueOf(input.readDouble());
/*     */       case FLOAT:
/* 231 */         return Float.valueOf(input.readFloat());
/*     */       case INT64:
/* 233 */         return Long.valueOf(input.readInt64());
/*     */       case UINT64:
/* 235 */         return Long.valueOf(input.readUInt64());
/*     */       case INT32:
/* 237 */         return Integer.valueOf(input.readInt32());
/*     */       case FIXED64:
/* 239 */         return Long.valueOf(input.readFixed64());
/*     */       case FIXED32:
/* 241 */         return Integer.valueOf(input.readFixed32());
/*     */       case BOOL:
/* 243 */         return Boolean.valueOf(input.readBool());
/*     */       case BYTES:
/* 245 */         return input.readBytes();
/*     */       case UINT32:
/* 247 */         return Integer.valueOf(input.readUInt32());
/*     */       case SFIXED32:
/* 249 */         return Integer.valueOf(input.readSFixed32());
/*     */       case SFIXED64:
/* 251 */         return Long.valueOf(input.readSFixed64());
/*     */       case SINT32:
/* 253 */         return Integer.valueOf(input.readSInt32());
/*     */       case SINT64:
/* 255 */         return Long.valueOf(input.readSInt64());
/*     */       
/*     */       case STRING:
/* 258 */         return utf8Validation.readString(input);
/*     */       case GROUP:
/* 260 */         throw new IllegalArgumentException("readPrimitiveField() cannot handle nested groups.");
/*     */       case MESSAGE:
/* 262 */         throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
/*     */ 
/*     */       
/*     */       case ENUM:
/* 266 */         throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
/*     */     } 
/*     */     
/* 269 */     throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\WireFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */