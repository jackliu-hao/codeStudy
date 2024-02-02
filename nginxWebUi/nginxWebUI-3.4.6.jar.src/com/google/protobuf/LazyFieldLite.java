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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LazyFieldLite
/*     */ {
/*  58 */   private static final ExtensionRegistryLite EMPTY_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();
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
/*     */   private ByteString delayedBytes;
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
/*     */   private ExtensionRegistryLite extensionRegistry;
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
/*     */   protected volatile MessageLite value;
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
/*     */   private volatile ByteString memoizedBytes;
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
/*     */   public LazyFieldLite(ExtensionRegistryLite extensionRegistry, ByteString bytes) {
/* 119 */     checkArguments(extensionRegistry, bytes);
/* 120 */     this.extensionRegistry = extensionRegistry;
/* 121 */     this.delayedBytes = bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LazyFieldLite() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public static LazyFieldLite fromValue(MessageLite value) {
/* 132 */     LazyFieldLite lf = new LazyFieldLite();
/* 133 */     lf.setValue(value);
/* 134 */     return lf;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 139 */     if (this == o) {
/* 140 */       return true;
/*     */     }
/*     */     
/* 143 */     if (!(o instanceof LazyFieldLite)) {
/* 144 */       return false;
/*     */     }
/*     */     
/* 147 */     LazyFieldLite other = (LazyFieldLite)o;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 153 */     MessageLite value1 = this.value;
/* 154 */     MessageLite value2 = other.value;
/* 155 */     if (value1 == null && value2 == null)
/* 156 */       return toByteString().equals(other.toByteString()); 
/* 157 */     if (value1 != null && value2 != null)
/* 158 */       return value1.equals(value2); 
/* 159 */     if (value1 != null) {
/* 160 */       return value1.equals(other.getValue(value1.getDefaultInstanceForType()));
/*     */     }
/* 162 */     return getValue(value2.getDefaultInstanceForType()).equals(value2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 171 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsDefaultInstance() {
/* 178 */     return (this.memoizedBytes == ByteString.EMPTY || (this.value == null && (this.delayedBytes == null || this.delayedBytes == ByteString.EMPTY)));
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
/*     */ 
/*     */   
/*     */   public void clear() {
/* 192 */     this.delayedBytes = null;
/* 193 */     this.value = null;
/* 194 */     this.memoizedBytes = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(LazyFieldLite other) {
/* 204 */     this.delayedBytes = other.delayedBytes;
/* 205 */     this.value = other.value;
/* 206 */     this.memoizedBytes = other.memoizedBytes;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 211 */     if (other.extensionRegistry != null) {
/* 212 */       this.extensionRegistry = other.extensionRegistry;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageLite getValue(MessageLite defaultInstance) {
/* 223 */     ensureInitialized(defaultInstance);
/* 224 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageLite setValue(MessageLite value) {
/* 234 */     MessageLite originalValue = this.value;
/* 235 */     this.delayedBytes = null;
/* 236 */     this.memoizedBytes = null;
/* 237 */     this.value = value;
/* 238 */     return originalValue;
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
/*     */   public void merge(LazyFieldLite other) {
/* 250 */     if (other.containsDefaultInstance()) {
/*     */       return;
/*     */     }
/*     */     
/* 254 */     if (containsDefaultInstance()) {
/* 255 */       set(other);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 261 */     if (this.extensionRegistry == null) {
/* 262 */       this.extensionRegistry = other.extensionRegistry;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 270 */     if (this.delayedBytes != null && other.delayedBytes != null) {
/* 271 */       this.delayedBytes = this.delayedBytes.concat(other.delayedBytes);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 278 */     if (this.value == null && other.value != null) {
/* 279 */       setValue(mergeValueAndBytes(other.value, this.delayedBytes, this.extensionRegistry)); return;
/*     */     } 
/* 281 */     if (this.value != null && other.value == null) {
/* 282 */       setValue(mergeValueAndBytes(this.value, other.delayedBytes, other.extensionRegistry));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 287 */     setValue(this.value.toBuilder().mergeFrom(other.value).build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 298 */     if (containsDefaultInstance()) {
/* 299 */       setByteString(input.readBytes(), extensionRegistry);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 305 */     if (this.extensionRegistry == null) {
/* 306 */       this.extensionRegistry = extensionRegistry;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 314 */     if (this.delayedBytes != null) {
/* 315 */       setByteString(this.delayedBytes.concat(input.readBytes()), this.extensionRegistry);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 323 */       setValue(this.value.toBuilder().mergeFrom(input, extensionRegistry).build());
/* 324 */     } catch (InvalidProtocolBufferException invalidProtocolBufferException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static MessageLite mergeValueAndBytes(MessageLite value, ByteString otherBytes, ExtensionRegistryLite extensionRegistry) {
/*     */     try {
/* 333 */       return value.toBuilder().mergeFrom(otherBytes, extensionRegistry).build();
/* 334 */     } catch (InvalidProtocolBufferException e) {
/*     */ 
/*     */       
/* 337 */       return value;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setByteString(ByteString bytes, ExtensionRegistryLite extensionRegistry) {
/* 343 */     checkArguments(extensionRegistry, bytes);
/* 344 */     this.delayedBytes = bytes;
/* 345 */     this.extensionRegistry = extensionRegistry;
/* 346 */     this.value = null;
/* 347 */     this.memoizedBytes = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 357 */     if (this.memoizedBytes != null)
/* 358 */       return this.memoizedBytes.size(); 
/* 359 */     if (this.delayedBytes != null)
/* 360 */       return this.delayedBytes.size(); 
/* 361 */     if (this.value != null) {
/* 362 */       return this.value.getSerializedSize();
/*     */     }
/* 364 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteString toByteString() {
/* 370 */     if (this.memoizedBytes != null) {
/* 371 */       return this.memoizedBytes;
/*     */     }
/*     */ 
/*     */     
/* 375 */     if (this.delayedBytes != null) {
/* 376 */       return this.delayedBytes;
/*     */     }
/* 378 */     synchronized (this) {
/* 379 */       if (this.memoizedBytes != null) {
/* 380 */         return this.memoizedBytes;
/*     */       }
/* 382 */       if (this.value == null) {
/* 383 */         this.memoizedBytes = ByteString.EMPTY;
/*     */       } else {
/* 385 */         this.memoizedBytes = this.value.toByteString();
/*     */       } 
/* 387 */       return this.memoizedBytes;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void writeTo(Writer writer, int fieldNumber) throws IOException {
/* 393 */     if (this.memoizedBytes != null) {
/* 394 */       writer.writeBytes(fieldNumber, this.memoizedBytes);
/* 395 */     } else if (this.delayedBytes != null) {
/* 396 */       writer.writeBytes(fieldNumber, this.delayedBytes);
/* 397 */     } else if (this.value != null) {
/* 398 */       writer.writeMessage(fieldNumber, this.value);
/*     */     } else {
/* 400 */       writer.writeBytes(fieldNumber, ByteString.EMPTY);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void ensureInitialized(MessageLite defaultInstance) {
/* 406 */     if (this.value != null) {
/*     */       return;
/*     */     }
/* 409 */     synchronized (this) {
/* 410 */       if (this.value != null) {
/*     */         return;
/*     */       }
/*     */       try {
/* 414 */         if (this.delayedBytes != null) {
/*     */ 
/*     */           
/* 417 */           MessageLite parsedValue = defaultInstance.getParserForType().parseFrom(this.delayedBytes, this.extensionRegistry);
/* 418 */           this.value = parsedValue;
/* 419 */           this.memoizedBytes = this.delayedBytes;
/*     */         } else {
/* 421 */           this.value = defaultInstance;
/* 422 */           this.memoizedBytes = ByteString.EMPTY;
/*     */         } 
/* 424 */       } catch (InvalidProtocolBufferException e) {
/*     */ 
/*     */         
/* 427 */         this.value = defaultInstance;
/* 428 */         this.memoizedBytes = ByteString.EMPTY;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void checkArguments(ExtensionRegistryLite extensionRegistry, ByteString bytes) {
/* 434 */     if (extensionRegistry == null) {
/* 435 */       throw new NullPointerException("found null ExtensionRegistry");
/*     */     }
/* 437 */     if (bytes == null)
/* 438 */       throw new NullPointerException("found null ByteString"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\LazyFieldLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */