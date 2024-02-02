/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
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
/*     */ public abstract class AbstractMessageLite<MessageType extends AbstractMessageLite<MessageType, BuilderType>, BuilderType extends AbstractMessageLite.Builder<MessageType, BuilderType>>
/*     */   implements MessageLite
/*     */ {
/*  53 */   protected int memoizedHashCode = 0;
/*     */ 
/*     */   
/*     */   public ByteString toByteString() {
/*     */     try {
/*  58 */       ByteString.CodedBuilder out = ByteString.newCodedBuilder(getSerializedSize());
/*  59 */       writeTo(out.getCodedOutput());
/*  60 */       return out.build();
/*  61 */     } catch (IOException e) {
/*  62 */       throw new RuntimeException(getSerializingExceptionMessage("ByteString"), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] toByteArray() {
/*     */     try {
/*  69 */       byte[] result = new byte[getSerializedSize()];
/*  70 */       CodedOutputStream output = CodedOutputStream.newInstance(result);
/*  71 */       writeTo(output);
/*  72 */       output.checkNoSpaceLeft();
/*  73 */       return result;
/*  74 */     } catch (IOException e) {
/*  75 */       throw new RuntimeException(getSerializingExceptionMessage("byte array"), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream output) throws IOException {
/*  81 */     int bufferSize = CodedOutputStream.computePreferredBufferSize(getSerializedSize());
/*  82 */     CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, bufferSize);
/*  83 */     writeTo(codedOutput);
/*  84 */     codedOutput.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeDelimitedTo(OutputStream output) throws IOException {
/*  89 */     int serialized = getSerializedSize();
/*     */     
/*  91 */     int bufferSize = CodedOutputStream.computePreferredBufferSize(
/*  92 */         CodedOutputStream.computeRawVarint32Size(serialized) + serialized);
/*  93 */     CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, bufferSize);
/*  94 */     codedOutput.writeRawVarint32(serialized);
/*  95 */     writeTo(codedOutput);
/*  96 */     codedOutput.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getMemoizedSerializedSize() {
/* 102 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   void setMemoizedSerializedSize(int size) {
/* 106 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   int getSerializedSize(Schema<AbstractMessageLite<MessageType, BuilderType>> schema) {
/* 111 */     int memoizedSerializedSize = getMemoizedSerializedSize();
/* 112 */     if (memoizedSerializedSize == -1) {
/* 113 */       memoizedSerializedSize = schema.getSerializedSize(this);
/* 114 */       setMemoizedSerializedSize(memoizedSerializedSize);
/*     */     } 
/* 116 */     return memoizedSerializedSize;
/*     */   }
/*     */ 
/*     */   
/*     */   UninitializedMessageException newUninitializedMessageException() {
/* 121 */     return new UninitializedMessageException(this);
/*     */   }
/*     */   
/*     */   private String getSerializingExceptionMessage(String target) {
/* 125 */     return "Serializing " + 
/* 126 */       getClass().getName() + " to a " + target + " threw an IOException (should never happen).";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void checkByteStringIsUtf8(ByteString byteString) throws IllegalArgumentException {
/* 134 */     if (!byteString.isValidUtf8()) {
/* 135 */       throw new IllegalArgumentException("Byte string is not UTF-8.");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected static <T> void addAll(Iterable<T> values, Collection<? super T> list) {
/* 142 */     Builder.addAll(values, (List<? super T>)list);
/*     */   }
/*     */   
/*     */   protected static <T> void addAll(Iterable<T> values, List<? super T> list) {
/* 146 */     Builder.addAll(values, list);
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
/*     */   public static abstract class Builder<MessageType extends AbstractMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>>
/*     */     implements MessageLite.Builder
/*     */   {
/*     */     public BuilderType mergeFrom(CodedInputStream input) throws IOException {
/* 173 */       return mergeFrom(input, ExtensionRegistryLite.getEmptyRegistry());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
/*     */       try {
/* 185 */         CodedInputStream input = data.newCodedInput();
/* 186 */         mergeFrom(input);
/* 187 */         input.checkLastTagWas(0);
/* 188 */         return (BuilderType)this;
/* 189 */       } catch (InvalidProtocolBufferException e) {
/* 190 */         throw e;
/* 191 */       } catch (IOException e) {
/* 192 */         throw new RuntimeException(getReadingExceptionMessage("ByteString"), e);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*     */       try {
/* 201 */         CodedInputStream input = data.newCodedInput();
/* 202 */         mergeFrom(input, extensionRegistry);
/* 203 */         input.checkLastTagWas(0);
/* 204 */         return (BuilderType)this;
/* 205 */       } catch (InvalidProtocolBufferException e) {
/* 206 */         throw e;
/* 207 */       } catch (IOException e) {
/* 208 */         throw new RuntimeException(getReadingExceptionMessage("ByteString"), e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
/* 214 */       return mergeFrom(data, 0, data.length);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
/*     */       try {
/* 221 */         CodedInputStream input = CodedInputStream.newInstance(data, off, len);
/* 222 */         mergeFrom(input);
/* 223 */         input.checkLastTagWas(0);
/* 224 */         return (BuilderType)this;
/* 225 */       } catch (InvalidProtocolBufferException e) {
/* 226 */         throw e;
/* 227 */       } catch (IOException e) {
/* 228 */         throw new RuntimeException(getReadingExceptionMessage("byte array"), e);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 235 */       return mergeFrom(data, 0, data.length, extensionRegistry);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*     */       try {
/* 246 */         CodedInputStream input = CodedInputStream.newInstance(data, off, len);
/* 247 */         mergeFrom(input, extensionRegistry);
/* 248 */         input.checkLastTagWas(0);
/* 249 */         return (BuilderType)this;
/* 250 */       } catch (InvalidProtocolBufferException e) {
/* 251 */         throw e;
/* 252 */       } catch (IOException e) {
/* 253 */         throw new RuntimeException(getReadingExceptionMessage("byte array"), e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(InputStream input) throws IOException {
/* 259 */       CodedInputStream codedInput = CodedInputStream.newInstance(input);
/* 260 */       mergeFrom(codedInput);
/* 261 */       codedInput.checkLastTagWas(0);
/* 262 */       return (BuilderType)this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 268 */       CodedInputStream codedInput = CodedInputStream.newInstance(input);
/* 269 */       mergeFrom(codedInput, extensionRegistry);
/* 270 */       codedInput.checkLastTagWas(0);
/* 271 */       return (BuilderType)this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     static final class LimitedInputStream
/*     */       extends FilterInputStream
/*     */     {
/*     */       private int limit;
/*     */ 
/*     */       
/*     */       LimitedInputStream(InputStream in, int limit) {
/* 283 */         super(in);
/* 284 */         this.limit = limit;
/*     */       }
/*     */ 
/*     */       
/*     */       public int available() throws IOException {
/* 289 */         return Math.min(super.available(), this.limit);
/*     */       }
/*     */ 
/*     */       
/*     */       public int read() throws IOException {
/* 294 */         if (this.limit <= 0) {
/* 295 */           return -1;
/*     */         }
/* 297 */         int result = super.read();
/* 298 */         if (result >= 0) {
/* 299 */           this.limit--;
/*     */         }
/* 301 */         return result;
/*     */       }
/*     */ 
/*     */       
/*     */       public int read(byte[] b, int off, int len) throws IOException {
/* 306 */         if (this.limit <= 0) {
/* 307 */           return -1;
/*     */         }
/* 309 */         len = Math.min(len, this.limit);
/* 310 */         int result = super.read(b, off, len);
/* 311 */         if (result >= 0) {
/* 312 */           this.limit -= result;
/*     */         }
/* 314 */         return result;
/*     */       }
/*     */ 
/*     */       
/*     */       public long skip(long n) throws IOException {
/* 319 */         long result = super.skip(Math.min(n, this.limit));
/* 320 */         if (result >= 0L) {
/* 321 */           this.limit = (int)(this.limit - result);
/*     */         }
/* 323 */         return result;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 330 */       int firstByte = input.read();
/* 331 */       if (firstByte == -1) {
/* 332 */         return false;
/*     */       }
/* 334 */       int size = CodedInputStream.readRawVarint32(firstByte, input);
/* 335 */       InputStream limitedInput = new LimitedInputStream(input, size);
/* 336 */       mergeFrom(limitedInput, extensionRegistry);
/* 337 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean mergeDelimitedFrom(InputStream input) throws IOException {
/* 342 */       return mergeDelimitedFrom(input, ExtensionRegistryLite.getEmptyRegistry());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(MessageLite other) {
/* 348 */       if (!getDefaultInstanceForType().getClass().isInstance(other)) {
/* 349 */         throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
/*     */       }
/*     */ 
/*     */       
/* 353 */       return internalMergeFrom((MessageType)other);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private String getReadingExceptionMessage(String target) {
/* 359 */       return "Reading " + 
/* 360 */         getClass().getName() + " from a " + target + " threw an IOException (should never happen).";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static <T> void addAllCheckingNulls(Iterable<T> values, List<? super T> list) {
/* 368 */       if (list instanceof ArrayList && values instanceof Collection) {
/* 369 */         ((ArrayList)list).ensureCapacity(list.size() + ((Collection)values).size());
/*     */       }
/* 371 */       int begin = list.size();
/* 372 */       for (T value : values) {
/* 373 */         if (value == null) {
/*     */           
/* 375 */           String message = "Element at index " + (list.size() - begin) + " is null.";
/* 376 */           for (int i = list.size() - 1; i >= begin; i--) {
/* 377 */             list.remove(i);
/*     */           }
/* 379 */           throw new NullPointerException(message);
/*     */         } 
/* 381 */         list.add(value);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected static UninitializedMessageException newUninitializedMessageException(MessageLite message) {
/* 388 */       return new UninitializedMessageException(message);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     protected static <T> void addAll(Iterable<T> values, Collection<? super T> list) {
/* 394 */       addAll(values, (List<? super T>)list);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected static <T> void addAll(Iterable<T> values, List<? super T> list) {
/* 405 */       Internal.checkNotNull(values);
/* 406 */       if (values instanceof LazyStringList) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 412 */         List<?> lazyValues = ((LazyStringList)values).getUnderlyingElements();
/* 413 */         LazyStringList lazyList = (LazyStringList)list;
/* 414 */         int begin = list.size();
/* 415 */         for (Object value : lazyValues) {
/* 416 */           if (value == null) {
/*     */             
/* 418 */             String message = "Element at index " + (lazyList.size() - begin) + " is null.";
/* 419 */             for (int i = lazyList.size() - 1; i >= begin; i--) {
/* 420 */               lazyList.remove(i);
/*     */             }
/* 422 */             throw new NullPointerException(message);
/*     */           } 
/* 424 */           if (value instanceof ByteString) {
/* 425 */             lazyList.add((ByteString)value); continue;
/*     */           } 
/* 427 */           lazyList.add((String)value);
/*     */         }
/*     */       
/*     */       }
/* 431 */       else if (values instanceof PrimitiveNonBoxingCollection) {
/* 432 */         list.addAll((Collection<? extends T>)values);
/*     */       } else {
/* 434 */         addAllCheckingNulls(values, list);
/*     */       } 
/*     */     }
/*     */     
/*     */     public abstract BuilderType clone();
/*     */     
/*     */     public abstract BuilderType mergeFrom(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite) throws IOException;
/*     */     
/*     */     protected abstract BuilderType internalMergeFrom(MessageType param1MessageType);
/*     */   }
/*     */   
/*     */   protected static interface InternalOneOfEnum {
/*     */     int getNumber();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\AbstractMessageLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */