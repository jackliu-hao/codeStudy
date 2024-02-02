/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.nio.ByteBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class Utf8
/*      */ {
/*   84 */   private static final Processor processor = (UnsafeProcessor.isAvailable() && !Android.isOnAndroidDevice()) ? new UnsafeProcessor() : new SafeProcessor();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final long ASCII_MASK_LONG = -9187201950435737472L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int MAX_BYTES_PER_CHAR = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPLETE = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int MALFORMED = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int UNSAFE_COUNT_ASCII_THRESHOLD = 16;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValidUtf8(byte[] bytes) {
/*  147 */     return processor.isValidUtf8(bytes, 0, bytes.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValidUtf8(byte[] bytes, int index, int limit) {
/*  159 */     return processor.isValidUtf8(bytes, index, limit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
/*  176 */     return processor.partialIsValidUtf8(state, bytes, index, limit);
/*      */   }
/*      */   
/*      */   private static int incompleteStateFor(int byte1) {
/*  180 */     return (byte1 > -12) ? -1 : byte1;
/*      */   }
/*      */   
/*      */   private static int incompleteStateFor(int byte1, int byte2) {
/*  184 */     return (byte1 > -12 || byte2 > -65) ? -1 : (byte1 ^ byte2 << 8);
/*      */   }
/*      */   
/*      */   private static int incompleteStateFor(int byte1, int byte2, int byte3) {
/*  188 */     return (byte1 > -12 || byte2 > -65 || byte3 > -65) ? -1 : (byte1 ^ byte2 << 8 ^ byte3 << 16);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static int incompleteStateFor(byte[] bytes, int index, int limit) {
/*  194 */     int byte1 = bytes[index - 1];
/*  195 */     switch (limit - index) {
/*      */       case 0:
/*  197 */         return incompleteStateFor(byte1);
/*      */       case 1:
/*  199 */         return incompleteStateFor(byte1, bytes[index]);
/*      */       case 2:
/*  201 */         return incompleteStateFor(byte1, bytes[index], bytes[index + 1]);
/*      */     } 
/*  203 */     throw new AssertionError();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static int incompleteStateFor(ByteBuffer buffer, int byte1, int index, int remaining) {
/*  209 */     switch (remaining) {
/*      */       case 0:
/*  211 */         return incompleteStateFor(byte1);
/*      */       case 1:
/*  213 */         return incompleteStateFor(byte1, buffer.get(index));
/*      */       case 2:
/*  215 */         return incompleteStateFor(byte1, buffer.get(index), buffer.get(index + 1));
/*      */     } 
/*  217 */     throw new AssertionError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class UnpairedSurrogateException
/*      */     extends IllegalArgumentException
/*      */   {
/*      */     UnpairedSurrogateException(int index, int length) {
/*  227 */       super("Unpaired surrogate at index " + index + " of " + length);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int encodedLength(CharSequence sequence) {
/*  241 */     int utf16Length = sequence.length();
/*  242 */     int utf8Length = utf16Length;
/*  243 */     int i = 0;
/*      */ 
/*      */     
/*  246 */     while (i < utf16Length && sequence.charAt(i) < '') {
/*  247 */       i++;
/*      */     }
/*      */ 
/*      */     
/*  251 */     for (; i < utf16Length; i++) {
/*  252 */       char c = sequence.charAt(i);
/*  253 */       if (c < 'ࠀ') {
/*  254 */         utf8Length += 127 - c >>> 31;
/*      */       } else {
/*  256 */         utf8Length += encodedLengthGeneral(sequence, i);
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  261 */     if (utf8Length < utf16Length)
/*      */     {
/*  263 */       throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (utf8Length + 4294967296L));
/*      */     }
/*      */     
/*  266 */     return utf8Length;
/*      */   }
/*      */   
/*      */   private static int encodedLengthGeneral(CharSequence sequence, int start) {
/*  270 */     int utf16Length = sequence.length();
/*  271 */     int utf8Length = 0;
/*  272 */     for (int i = start; i < utf16Length; i++) {
/*  273 */       char c = sequence.charAt(i);
/*  274 */       if (c < 'ࠀ') {
/*  275 */         utf8Length += 127 - c >>> 31;
/*      */       } else {
/*  277 */         utf8Length += 2;
/*      */         
/*  279 */         if ('?' <= c && c <= '?') {
/*      */           
/*  281 */           int cp = Character.codePointAt(sequence, i);
/*  282 */           if (cp < 65536) {
/*  283 */             throw new UnpairedSurrogateException(i, utf16Length);
/*      */           }
/*  285 */           i++;
/*      */         } 
/*      */       } 
/*      */     } 
/*  289 */     return utf8Length;
/*      */   }
/*      */   
/*      */   static int encode(CharSequence in, byte[] out, int offset, int length) {
/*  293 */     return processor.encodeUtf8(in, out, offset, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static boolean isValidUtf8(ByteBuffer buffer) {
/*  307 */     return processor.isValidUtf8(buffer, buffer.position(), buffer.remaining());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
/*  320 */     return processor.partialIsValidUtf8(state, buffer, index, limit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String decodeUtf8(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
/*  330 */     return processor.decodeUtf8(buffer, index, size);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
/*  340 */     return processor.decodeUtf8(bytes, index, size);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void encodeUtf8(CharSequence in, ByteBuffer out) {
/*  354 */     processor.encodeUtf8(in, out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int estimateConsecutiveAscii(ByteBuffer buffer, int index, int limit) {
/*  369 */     int i = index;
/*  370 */     int lim = limit - 7;
/*      */ 
/*      */ 
/*      */     
/*  374 */     for (; i < lim && (buffer.getLong(i) & 0x8080808080808080L) == 0L; i += 8);
/*  375 */     return i - index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static abstract class Processor
/*      */   {
/*      */     final boolean isValidUtf8(byte[] bytes, int index, int limit) {
/*  390 */       return (partialIsValidUtf8(0, bytes, index, limit) == 0);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract int partialIsValidUtf8(int param1Int1, byte[] param1ArrayOfbyte, int param1Int2, int param1Int3);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final boolean isValidUtf8(ByteBuffer buffer, int index, int limit) {
/*  417 */       return (partialIsValidUtf8(0, buffer, index, limit) == 0);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
/*  428 */       if (buffer.hasArray()) {
/*  429 */         int offset = buffer.arrayOffset();
/*  430 */         return partialIsValidUtf8(state, buffer.array(), offset + index, offset + limit);
/*  431 */       }  if (buffer.isDirect()) {
/*  432 */         return partialIsValidUtf8Direct(state, buffer, index, limit);
/*      */       }
/*  434 */       return partialIsValidUtf8Default(state, buffer, index, limit);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract int partialIsValidUtf8Direct(int param1Int1, ByteBuffer param1ByteBuffer, int param1Int2, int param1Int3);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final int partialIsValidUtf8Default(int state, ByteBuffer buffer, int index, int limit) {
/*  448 */       if (state != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  456 */         if (index >= limit) {
/*  457 */           return state;
/*      */         }
/*      */         
/*  460 */         byte byte1 = (byte)state;
/*      */         
/*  462 */         if (byte1 < -32) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  467 */           if (byte1 < -62 || buffer
/*      */             
/*  469 */             .get(index++) > -65) {
/*  470 */             return -1;
/*      */           }
/*  472 */         } else if (byte1 < -16) {
/*      */ 
/*      */ 
/*      */           
/*  476 */           byte byte2 = (byte)(state >> 8 ^ 0xFFFFFFFF);
/*  477 */           if (byte2 == 0) {
/*  478 */             byte2 = buffer.get(index++);
/*  479 */             if (index >= limit) {
/*  480 */               return Utf8.incompleteStateFor(byte1, byte2);
/*      */             }
/*      */           } 
/*  483 */           if (byte2 > -65 || (byte1 == -32 && byte2 < -96) || (byte1 == -19 && byte2 >= -96) || buffer
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  489 */             .get(index++) > -65) {
/*  490 */             return -1;
/*      */           
/*      */           }
/*      */         }
/*      */         else {
/*      */           
/*  496 */           byte byte2 = (byte)(state >> 8 ^ 0xFFFFFFFF);
/*  497 */           byte byte3 = 0;
/*  498 */           if (byte2 == 0) {
/*  499 */             byte2 = buffer.get(index++);
/*  500 */             if (index >= limit) {
/*  501 */               return Utf8.incompleteStateFor(byte1, byte2);
/*      */             }
/*      */           } else {
/*  504 */             byte3 = (byte)(state >> 16);
/*      */           } 
/*  506 */           if (byte3 == 0) {
/*  507 */             byte3 = buffer.get(index++);
/*  508 */             if (index >= limit) {
/*  509 */               return Utf8.incompleteStateFor(byte1, byte2, byte3);
/*      */             }
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  517 */           if (byte2 > -65 || (byte1 << 28) + byte2 - -112 >> 30 != 0 || byte3 > -65 || buffer
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  526 */             .get(index++) > -65) {
/*  527 */             return -1;
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  533 */       return partialIsValidUtf8(buffer, index, limit);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static int partialIsValidUtf8(ByteBuffer buffer, int index, int limit) {
/*  541 */       index += Utf8.estimateConsecutiveAscii(buffer, index, limit);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       while (true)
/*  549 */       { if (index >= limit)
/*  550 */           return 0; 
/*      */         int byte1;
/*  552 */         if ((byte1 = buffer.get(index++)) < 0)
/*      */         
/*      */         { 
/*  555 */           if (byte1 < -32) {
/*      */             
/*  557 */             if (index >= limit)
/*      */             {
/*  559 */               return byte1;
/*      */             }
/*      */ 
/*      */ 
/*      */             
/*  564 */             if (byte1 < -62 || buffer.get(index) > -65) {
/*  565 */               return -1;
/*      */             }
/*  567 */             index++; continue;
/*  568 */           }  if (byte1 < -16) {
/*      */             
/*  570 */             if (index >= limit - 1)
/*      */             {
/*  572 */               return Utf8.incompleteStateFor(buffer, byte1, index, limit - index);
/*      */             }
/*      */             
/*  575 */             byte b = buffer.get(index++);
/*  576 */             if (b > -65 || (byte1 == -32 && b < -96) || (byte1 == -19 && b >= -96) || buffer
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  582 */               .get(index) > -65) {
/*  583 */               return -1;
/*      */             }
/*  585 */             index++;
/*      */             continue;
/*      */           } 
/*  588 */           if (index >= limit - 2)
/*      */           {
/*  590 */             return Utf8.incompleteStateFor(buffer, byte1, index, limit - index);
/*      */           }
/*      */ 
/*      */           
/*  594 */           int byte2 = buffer.get(index++);
/*  595 */           if (byte2 > -65 || (byte1 << 28) + byte2 - -112 >> 30 != 0 || buffer
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  602 */             .get(index++) > -65 || buffer
/*      */             
/*  604 */             .get(index++) > -65)
/*  605 */             break;  }  }  return -1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract String decodeUtf8(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws InvalidProtocolBufferException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final String decodeUtf8(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
/*  626 */       if (buffer.hasArray()) {
/*  627 */         int offset = buffer.arrayOffset();
/*  628 */         return decodeUtf8(buffer.array(), offset + index, size);
/*  629 */       }  if (buffer.isDirect()) {
/*  630 */         return decodeUtf8Direct(buffer, index, size);
/*      */       }
/*  632 */       return decodeUtf8Default(buffer, index, size);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract String decodeUtf8Direct(ByteBuffer param1ByteBuffer, int param1Int1, int param1Int2) throws InvalidProtocolBufferException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final String decodeUtf8Default(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
/*  646 */       if ((index | size | buffer.limit() - index - size) < 0) {
/*  647 */         throw new ArrayIndexOutOfBoundsException(
/*  648 */             String.format("buffer limit=%d, index=%d, limit=%d", new Object[] { Integer.valueOf(buffer.limit()), Integer.valueOf(index), Integer.valueOf(size) }));
/*      */       }
/*      */       
/*  651 */       int offset = index;
/*  652 */       int limit = offset + size;
/*      */ 
/*      */ 
/*      */       
/*  656 */       char[] resultArr = new char[size];
/*  657 */       int resultPos = 0;
/*      */ 
/*      */ 
/*      */       
/*  661 */       while (offset < limit) {
/*  662 */         byte b = buffer.get(offset);
/*  663 */         if (!Utf8.DecodeUtil.isOneByte(b)) {
/*      */           break;
/*      */         }
/*  666 */         offset++;
/*  667 */         Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
/*      */       } 
/*      */       
/*  670 */       while (offset < limit) {
/*  671 */         byte byte1 = buffer.get(offset++);
/*  672 */         if (Utf8.DecodeUtil.isOneByte(byte1)) {
/*  673 */           Utf8.DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);
/*      */ 
/*      */           
/*  676 */           while (offset < limit) {
/*  677 */             byte b = buffer.get(offset);
/*  678 */             if (!Utf8.DecodeUtil.isOneByte(b)) {
/*      */               break;
/*      */             }
/*  681 */             offset++;
/*  682 */             Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
/*      */           }  continue;
/*  684 */         }  if (Utf8.DecodeUtil.isTwoBytes(byte1)) {
/*  685 */           if (offset >= limit) {
/*  686 */             throw InvalidProtocolBufferException.invalidUtf8();
/*      */           }
/*  688 */           Utf8.DecodeUtil.handleTwoBytes(byte1, buffer
/*  689 */               .get(offset++), resultArr, resultPos++); continue;
/*  690 */         }  if (Utf8.DecodeUtil.isThreeBytes(byte1)) {
/*  691 */           if (offset >= limit - 1) {
/*  692 */             throw InvalidProtocolBufferException.invalidUtf8();
/*      */           }
/*  694 */           Utf8.DecodeUtil.handleThreeBytes(byte1, buffer
/*      */               
/*  696 */               .get(offset++), buffer
/*  697 */               .get(offset++), resultArr, resultPos++);
/*      */           
/*      */           continue;
/*      */         } 
/*  701 */         if (offset >= limit - 2) {
/*  702 */           throw InvalidProtocolBufferException.invalidUtf8();
/*      */         }
/*  704 */         Utf8.DecodeUtil.handleFourBytes(byte1, buffer
/*      */             
/*  706 */             .get(offset++), buffer
/*  707 */             .get(offset++), buffer
/*  708 */             .get(offset++), resultArr, resultPos++);
/*      */ 
/*      */ 
/*      */         
/*  712 */         resultPos++;
/*      */       } 
/*      */ 
/*      */       
/*  716 */       return new String(resultArr, 0, resultPos);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract int encodeUtf8(CharSequence param1CharSequence, byte[] param1ArrayOfbyte, int param1Int1, int param1Int2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void encodeUtf8(CharSequence in, ByteBuffer out) {
/*  769 */       if (out.hasArray()) {
/*  770 */         int offset = out.arrayOffset();
/*  771 */         int endIndex = Utf8.encode(in, out.array(), offset + out.position(), out.remaining());
/*  772 */         out.position(endIndex - offset);
/*  773 */       } else if (out.isDirect()) {
/*  774 */         encodeUtf8Direct(in, out);
/*      */       } else {
/*  776 */         encodeUtf8Default(in, out);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract void encodeUtf8Direct(CharSequence param1CharSequence, ByteBuffer param1ByteBuffer);
/*      */ 
/*      */ 
/*      */     
/*      */     final void encodeUtf8Default(CharSequence in, ByteBuffer out) {
/*  788 */       int inLength = in.length();
/*  789 */       int outIx = out.position();
/*  790 */       int inIx = 0;
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*      */         char c;
/*      */ 
/*      */         
/*  798 */         for (; inIx < inLength && (c = in.charAt(inIx)) < ''; inIx++) {
/*  799 */           out.put(outIx + inIx, (byte)c);
/*      */         }
/*  801 */         if (inIx == inLength) {
/*      */           
/*  803 */           out.position(outIx + inIx);
/*      */           
/*      */           return;
/*      */         } 
/*  807 */         outIx += inIx;
/*  808 */         for (; inIx < inLength; inIx++, outIx++) {
/*  809 */           c = in.charAt(inIx);
/*  810 */           if (c < '') {
/*      */             
/*  812 */             out.put(outIx, (byte)c);
/*  813 */           } else if (c < 'ࠀ') {
/*      */ 
/*      */ 
/*      */             
/*  817 */             out.put(outIx++, (byte)(0xC0 | c >>> 6));
/*  818 */             out.put(outIx, (byte)(0x80 | 0x3F & c));
/*  819 */           } else if (c < '?' || '?' < c) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  824 */             out.put(outIx++, (byte)(0xE0 | c >>> 12));
/*  825 */             out.put(outIx++, (byte)(0x80 | 0x3F & c >>> 6));
/*  826 */             out.put(outIx, (byte)(0x80 | 0x3F & c));
/*      */           } else {
/*      */             char low;
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  833 */             if (inIx + 1 == inLength || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
/*  834 */               throw new Utf8.UnpairedSurrogateException(inIx, inLength);
/*      */             }
/*      */             
/*  837 */             int codePoint = Character.toCodePoint(c, low);
/*  838 */             out.put(outIx++, (byte)(0xF0 | codePoint >>> 18));
/*  839 */             out.put(outIx++, (byte)(0x80 | 0x3F & codePoint >>> 12));
/*  840 */             out.put(outIx++, (byte)(0x80 | 0x3F & codePoint >>> 6));
/*  841 */             out.put(outIx, (byte)(0x80 | 0x3F & codePoint));
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/*  846 */         out.position(outIx);
/*  847 */       } catch (IndexOutOfBoundsException e) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  852 */         int badWriteIndex = out.position() + Math.max(inIx, outIx - out.position() + 1);
/*  853 */         throw new ArrayIndexOutOfBoundsException("Failed writing " + in
/*  854 */             .charAt(inIx) + " at index " + badWriteIndex);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static final class SafeProcessor
/*      */     extends Processor
/*      */   {
/*      */     int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
/*  863 */       if (state != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  871 */         if (index >= limit) {
/*  872 */           return state;
/*      */         }
/*  874 */         int byte1 = (byte)state;
/*      */         
/*  876 */         if (byte1 < -32) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  881 */           if (byte1 < -62 || bytes[index++] > -65)
/*      */           {
/*      */             
/*  884 */             return -1;
/*      */           }
/*  886 */         } else if (byte1 < -16) {
/*      */ 
/*      */ 
/*      */           
/*  890 */           int byte2 = (byte)(state >> 8 ^ 0xFFFFFFFF);
/*  891 */           if (byte2 == 0) {
/*  892 */             byte2 = bytes[index++];
/*  893 */             if (index >= limit) {
/*  894 */               return Utf8.incompleteStateFor(byte1, byte2);
/*      */             }
/*      */           } 
/*  897 */           if (byte2 > -65 || (byte1 == -32 && byte2 < -96) || (byte1 == -19 && byte2 >= -96) || bytes[index++] > -65)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  904 */             return -1;
/*      */           
/*      */           }
/*      */         }
/*      */         else {
/*      */           
/*  910 */           int byte2 = (byte)(state >> 8 ^ 0xFFFFFFFF);
/*  911 */           int byte3 = 0;
/*  912 */           if (byte2 == 0) {
/*  913 */             byte2 = bytes[index++];
/*  914 */             if (index >= limit) {
/*  915 */               return Utf8.incompleteStateFor(byte1, byte2);
/*      */             }
/*      */           } else {
/*  918 */             byte3 = (byte)(state >> 16);
/*      */           } 
/*  920 */           if (byte3 == 0) {
/*  921 */             byte3 = bytes[index++];
/*  922 */             if (index >= limit) {
/*  923 */               return Utf8.incompleteStateFor(byte1, byte2, byte3);
/*      */             }
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  931 */           if (byte2 > -65 || (byte1 << 28) + byte2 - -112 >> 30 != 0 || byte3 > -65 || bytes[index++] > -65)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  941 */             return -1;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/*  946 */       return partialIsValidUtf8(bytes, index, limit);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
/*  952 */       return partialIsValidUtf8Default(state, buffer, index, limit);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
/*  958 */       if ((index | size | bytes.length - index - size) < 0) {
/*  959 */         throw new ArrayIndexOutOfBoundsException(
/*  960 */             String.format("buffer length=%d, index=%d, size=%d", new Object[] { Integer.valueOf(bytes.length), Integer.valueOf(index), Integer.valueOf(size) }));
/*      */       }
/*      */       
/*  963 */       int offset = index;
/*  964 */       int limit = offset + size;
/*      */ 
/*      */ 
/*      */       
/*  968 */       char[] resultArr = new char[size];
/*  969 */       int resultPos = 0;
/*      */ 
/*      */ 
/*      */       
/*  973 */       while (offset < limit) {
/*  974 */         byte b = bytes[offset];
/*  975 */         if (!Utf8.DecodeUtil.isOneByte(b)) {
/*      */           break;
/*      */         }
/*  978 */         offset++;
/*  979 */         Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
/*      */       } 
/*      */       
/*  982 */       while (offset < limit) {
/*  983 */         byte byte1 = bytes[offset++];
/*  984 */         if (Utf8.DecodeUtil.isOneByte(byte1)) {
/*  985 */           Utf8.DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);
/*      */ 
/*      */           
/*  988 */           while (offset < limit) {
/*  989 */             byte b = bytes[offset];
/*  990 */             if (!Utf8.DecodeUtil.isOneByte(b)) {
/*      */               break;
/*      */             }
/*  993 */             offset++;
/*  994 */             Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
/*      */           }  continue;
/*  996 */         }  if (Utf8.DecodeUtil.isTwoBytes(byte1)) {
/*  997 */           if (offset >= limit) {
/*  998 */             throw InvalidProtocolBufferException.invalidUtf8();
/*      */           }
/* 1000 */           Utf8.DecodeUtil.handleTwoBytes(byte1, bytes[offset++], resultArr, resultPos++); continue;
/* 1001 */         }  if (Utf8.DecodeUtil.isThreeBytes(byte1)) {
/* 1002 */           if (offset >= limit - 1) {
/* 1003 */             throw InvalidProtocolBufferException.invalidUtf8();
/*      */           }
/* 1005 */           Utf8.DecodeUtil.handleThreeBytes(byte1, bytes[offset++], bytes[offset++], resultArr, resultPos++);
/*      */ 
/*      */           
/*      */           continue;
/*      */         } 
/*      */ 
/*      */         
/* 1012 */         if (offset >= limit - 2) {
/* 1013 */           throw InvalidProtocolBufferException.invalidUtf8();
/*      */         }
/* 1015 */         Utf8.DecodeUtil.handleFourBytes(byte1, bytes[offset++], bytes[offset++], bytes[offset++], resultArr, resultPos++);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1023 */         resultPos++;
/*      */       } 
/*      */ 
/*      */       
/* 1027 */       return new String(resultArr, 0, resultPos);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String decodeUtf8Direct(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
/* 1034 */       return decodeUtf8Default(buffer, index, size);
/*      */     }
/*      */ 
/*      */     
/*      */     int encodeUtf8(CharSequence in, byte[] out, int offset, int length) {
/* 1039 */       int utf16Length = in.length();
/* 1040 */       int j = offset;
/* 1041 */       int i = 0;
/* 1042 */       int limit = offset + length;
/*      */       
/*      */       char c;
/* 1045 */       for (; i < utf16Length && i + j < limit && (c = in.charAt(i)) < ''; i++) {
/* 1046 */         out[j + i] = (byte)c;
/*      */       }
/* 1048 */       if (i == utf16Length) {
/* 1049 */         return j + utf16Length;
/*      */       }
/* 1051 */       j += i;
/* 1052 */       for (; i < utf16Length; i++) {
/* 1053 */         c = in.charAt(i);
/* 1054 */         if (c < '' && j < limit) {
/* 1055 */           out[j++] = (byte)c;
/* 1056 */         } else if (c < 'ࠀ' && j <= limit - 2) {
/* 1057 */           out[j++] = (byte)(0x3C0 | c >>> 6);
/* 1058 */           out[j++] = (byte)(0x80 | 0x3F & c);
/* 1059 */         } else if ((c < '?' || '?' < c) && j <= limit - 3) {
/*      */           
/* 1061 */           out[j++] = (byte)(0x1E0 | c >>> 12);
/* 1062 */           out[j++] = (byte)(0x80 | 0x3F & c >>> 6);
/* 1063 */           out[j++] = (byte)(0x80 | 0x3F & c);
/* 1064 */         } else if (j <= limit - 4) {
/*      */           char low;
/*      */ 
/*      */           
/* 1068 */           if (i + 1 == in.length() || !Character.isSurrogatePair(c, low = in.charAt(++i))) {
/* 1069 */             throw new Utf8.UnpairedSurrogateException(i - 1, utf16Length);
/*      */           }
/* 1071 */           int codePoint = Character.toCodePoint(c, low);
/* 1072 */           out[j++] = (byte)(0xF0 | codePoint >>> 18);
/* 1073 */           out[j++] = (byte)(0x80 | 0x3F & codePoint >>> 12);
/* 1074 */           out[j++] = (byte)(0x80 | 0x3F & codePoint >>> 6);
/* 1075 */           out[j++] = (byte)(0x80 | 0x3F & codePoint);
/*      */         }
/*      */         else {
/*      */           
/* 1079 */           if ('?' <= c && c <= '?' && (i + 1 == in
/* 1080 */             .length() || !Character.isSurrogatePair(c, in.charAt(i + 1)))) {
/* 1081 */             throw new Utf8.UnpairedSurrogateException(i, utf16Length);
/*      */           }
/* 1083 */           throw new ArrayIndexOutOfBoundsException("Failed writing " + c + " at index " + j);
/*      */         } 
/*      */       } 
/* 1086 */       return j;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void encodeUtf8Direct(CharSequence in, ByteBuffer out) {
/* 1092 */       encodeUtf8Default(in, out);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private static int partialIsValidUtf8(byte[] bytes, int index, int limit) {
/* 1098 */       while (index < limit && bytes[index] >= 0) {
/* 1099 */         index++;
/*      */       }
/*      */       
/* 1102 */       return (index >= limit) ? 0 : partialIsValidUtf8NonAscii(bytes, index, limit);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static int partialIsValidUtf8NonAscii(byte[] bytes, int index, int limit) {
/*      */       while (true) {
/* 1112 */         if (index >= limit)
/* 1113 */           return 0; 
/*      */         int byte1;
/* 1115 */         if ((byte1 = bytes[index++]) < 0) {
/*      */           
/* 1117 */           if (byte1 < -32) {
/*      */ 
/*      */             
/* 1120 */             if (index >= limit)
/*      */             {
/* 1122 */               return byte1;
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 1127 */             if (byte1 < -62 || bytes[index++] > -65)
/* 1128 */               return -1;  continue;
/*      */           } 
/* 1130 */           if (byte1 < -16) {
/*      */ 
/*      */             
/* 1133 */             if (index >= limit - 1)
/* 1134 */               return Utf8.incompleteStateFor(bytes, index, limit); 
/*      */             int i;
/* 1136 */             if ((i = bytes[index++]) > -65 || (byte1 == -32 && i < -96) || (byte1 == -19 && i >= -96) || bytes[index++] > -65)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1143 */               return -1;
/*      */             }
/*      */             
/*      */             continue;
/*      */           } 
/* 1148 */           if (index >= limit - 2)
/* 1149 */             return Utf8.incompleteStateFor(bytes, index, limit); 
/*      */           int byte2;
/* 1151 */           if ((byte2 = bytes[index++]) > -65 || (byte1 << 28) + byte2 - -112 >> 30 != 0 || bytes[index++] > -65 || bytes[index++] > -65) {
/*      */             break;
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1161 */       return -1;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final class UnsafeProcessor
/*      */     extends Processor
/*      */   {
/*      */     static boolean isAvailable() {
/* 1172 */       return (UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
/* 1178 */       if ((index | limit | bytes.length - limit) < 0) {
/* 1179 */         throw new ArrayIndexOutOfBoundsException(
/* 1180 */             String.format("Array length=%d, index=%d, limit=%d", new Object[] { Integer.valueOf(bytes.length), Integer.valueOf(index), Integer.valueOf(limit) }));
/*      */       }
/* 1182 */       long offset = index;
/* 1183 */       long offsetLimit = limit;
/* 1184 */       if (state != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1192 */         if (offset >= offsetLimit) {
/* 1193 */           return state;
/*      */         }
/* 1195 */         int byte1 = (byte)state;
/*      */         
/* 1197 */         if (byte1 < -32) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1202 */           if (byte1 < -62 || 
/*      */             
/* 1204 */             UnsafeUtil.getByte(bytes, offset++) > -65) {
/* 1205 */             return -1;
/*      */           }
/* 1207 */         } else if (byte1 < -16) {
/*      */ 
/*      */ 
/*      */           
/* 1211 */           int byte2 = (byte)(state >> 8 ^ 0xFFFFFFFF);
/*      */           
/* 1213 */           byte2 = UnsafeUtil.getByte(bytes, offset++);
/* 1214 */           if (byte2 == 0 && offset >= offsetLimit) {
/* 1215 */             return Utf8.incompleteStateFor(byte1, byte2);
/*      */           }
/*      */           
/* 1218 */           if (byte2 > -65 || (byte1 == -32 && byte2 < -96) || (byte1 == -19 && byte2 >= -96) || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1224 */             UnsafeUtil.getByte(bytes, offset++) > -65) {
/* 1225 */             return -1;
/*      */           
/*      */           }
/*      */         }
/*      */         else {
/*      */           
/* 1231 */           int byte2 = (byte)(state >> 8 ^ 0xFFFFFFFF);
/* 1232 */           int byte3 = 0;
/* 1233 */           if (byte2 == 0) {
/* 1234 */             byte2 = UnsafeUtil.getByte(bytes, offset++);
/* 1235 */             if (offset >= offsetLimit) {
/* 1236 */               return Utf8.incompleteStateFor(byte1, byte2);
/*      */             }
/*      */           } else {
/* 1239 */             byte3 = (byte)(state >> 16);
/*      */           } 
/*      */           
/* 1242 */           byte3 = UnsafeUtil.getByte(bytes, offset++);
/* 1243 */           if (byte3 == 0 && offset >= offsetLimit) {
/* 1244 */             return Utf8.incompleteStateFor(byte1, byte2, byte3);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1252 */           if (byte2 > -65 || (byte1 << 28) + byte2 - -112 >> 30 != 0 || byte3 > -65 || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1261 */             UnsafeUtil.getByte(bytes, offset++) > -65) {
/* 1262 */             return -1;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1267 */       return partialIsValidUtf8(bytes, offset, (int)(offsetLimit - offset));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
/* 1274 */       if ((index | limit | buffer.limit() - limit) < 0) {
/* 1275 */         throw new ArrayIndexOutOfBoundsException(
/* 1276 */             String.format("buffer limit=%d, index=%d, limit=%d", new Object[] { Integer.valueOf(buffer.limit()), Integer.valueOf(index), Integer.valueOf(limit) }));
/*      */       }
/* 1278 */       long address = UnsafeUtil.addressOffset(buffer) + index;
/* 1279 */       long addressLimit = address + (limit - index);
/* 1280 */       if (state != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1288 */         if (address >= addressLimit) {
/* 1289 */           return state;
/*      */         }
/*      */         
/* 1292 */         int byte1 = (byte)state;
/*      */         
/* 1294 */         if (byte1 < -32) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1299 */           if (byte1 < -62 || 
/*      */             
/* 1301 */             UnsafeUtil.getByte(address++) > -65) {
/* 1302 */             return -1;
/*      */           }
/* 1304 */         } else if (byte1 < -16) {
/*      */ 
/*      */ 
/*      */           
/* 1308 */           int byte2 = (byte)(state >> 8 ^ 0xFFFFFFFF);
/*      */           
/* 1310 */           byte2 = UnsafeUtil.getByte(address++);
/* 1311 */           if (byte2 == 0 && address >= addressLimit) {
/* 1312 */             return Utf8.incompleteStateFor(byte1, byte2);
/*      */           }
/*      */           
/* 1315 */           if (byte2 > -65 || (byte1 == -32 && byte2 < -96) || (byte1 == -19 && byte2 >= -96) || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1321 */             UnsafeUtil.getByte(address++) > -65) {
/* 1322 */             return -1;
/*      */           
/*      */           }
/*      */         }
/*      */         else {
/*      */           
/* 1328 */           int byte2 = (byte)(state >> 8 ^ 0xFFFFFFFF);
/* 1329 */           int byte3 = 0;
/* 1330 */           if (byte2 == 0) {
/* 1331 */             byte2 = UnsafeUtil.getByte(address++);
/* 1332 */             if (address >= addressLimit) {
/* 1333 */               return Utf8.incompleteStateFor(byte1, byte2);
/*      */             }
/*      */           } else {
/* 1336 */             byte3 = (byte)(state >> 16);
/*      */           } 
/*      */           
/* 1339 */           byte3 = UnsafeUtil.getByte(address++);
/* 1340 */           if (byte3 == 0 && address >= addressLimit) {
/* 1341 */             return Utf8.incompleteStateFor(byte1, byte2, byte3);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1349 */           if (byte2 > -65 || (byte1 << 28) + byte2 - -112 >> 30 != 0 || byte3 > -65 || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1358 */             UnsafeUtil.getByte(address++) > -65) {
/* 1359 */             return -1;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1364 */       return partialIsValidUtf8(address, (int)(addressLimit - address));
/*      */     }
/*      */ 
/*      */     
/*      */     String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
/* 1369 */       if ((index | size | bytes.length - index - size) < 0) {
/* 1370 */         throw new ArrayIndexOutOfBoundsException(
/* 1371 */             String.format("buffer length=%d, index=%d, size=%d", new Object[] { Integer.valueOf(bytes.length), Integer.valueOf(index), Integer.valueOf(size) }));
/*      */       }
/*      */       
/* 1374 */       int offset = index;
/* 1375 */       int limit = offset + size;
/*      */ 
/*      */ 
/*      */       
/* 1379 */       char[] resultArr = new char[size];
/* 1380 */       int resultPos = 0;
/*      */ 
/*      */ 
/*      */       
/* 1384 */       while (offset < limit) {
/* 1385 */         byte b = UnsafeUtil.getByte(bytes, offset);
/* 1386 */         if (!Utf8.DecodeUtil.isOneByte(b)) {
/*      */           break;
/*      */         }
/* 1389 */         offset++;
/* 1390 */         Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
/*      */       } 
/*      */       
/* 1393 */       while (offset < limit) {
/* 1394 */         byte byte1 = UnsafeUtil.getByte(bytes, offset++);
/* 1395 */         if (Utf8.DecodeUtil.isOneByte(byte1)) {
/* 1396 */           Utf8.DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);
/*      */ 
/*      */           
/* 1399 */           while (offset < limit) {
/* 1400 */             byte b = UnsafeUtil.getByte(bytes, offset);
/* 1401 */             if (!Utf8.DecodeUtil.isOneByte(b)) {
/*      */               break;
/*      */             }
/* 1404 */             offset++;
/* 1405 */             Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
/*      */           }  continue;
/* 1407 */         }  if (Utf8.DecodeUtil.isTwoBytes(byte1)) {
/* 1408 */           if (offset >= limit) {
/* 1409 */             throw InvalidProtocolBufferException.invalidUtf8();
/*      */           }
/* 1411 */           Utf8.DecodeUtil.handleTwoBytes(byte1, 
/* 1412 */               UnsafeUtil.getByte(bytes, offset++), resultArr, resultPos++); continue;
/* 1413 */         }  if (Utf8.DecodeUtil.isThreeBytes(byte1)) {
/* 1414 */           if (offset >= limit - 1) {
/* 1415 */             throw InvalidProtocolBufferException.invalidUtf8();
/*      */           }
/* 1417 */           Utf8.DecodeUtil.handleThreeBytes(byte1, 
/*      */               
/* 1419 */               UnsafeUtil.getByte(bytes, offset++), 
/* 1420 */               UnsafeUtil.getByte(bytes, offset++), resultArr, resultPos++);
/*      */           
/*      */           continue;
/*      */         } 
/* 1424 */         if (offset >= limit - 2) {
/* 1425 */           throw InvalidProtocolBufferException.invalidUtf8();
/*      */         }
/* 1427 */         Utf8.DecodeUtil.handleFourBytes(byte1, 
/*      */             
/* 1429 */             UnsafeUtil.getByte(bytes, offset++), 
/* 1430 */             UnsafeUtil.getByte(bytes, offset++), 
/* 1431 */             UnsafeUtil.getByte(bytes, offset++), resultArr, resultPos++);
/*      */ 
/*      */ 
/*      */         
/* 1435 */         resultPos++;
/*      */       } 
/*      */ 
/*      */       
/* 1439 */       return new String(resultArr, 0, resultPos);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String decodeUtf8Direct(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
/* 1446 */       if ((index | size | buffer.limit() - index - size) < 0) {
/* 1447 */         throw new ArrayIndexOutOfBoundsException(
/* 1448 */             String.format("buffer limit=%d, index=%d, limit=%d", new Object[] { Integer.valueOf(buffer.limit()), Integer.valueOf(index), Integer.valueOf(size) }));
/*      */       }
/* 1450 */       long address = UnsafeUtil.addressOffset(buffer) + index;
/* 1451 */       long addressLimit = address + size;
/*      */ 
/*      */ 
/*      */       
/* 1455 */       char[] resultArr = new char[size];
/* 1456 */       int resultPos = 0;
/*      */ 
/*      */ 
/*      */       
/* 1460 */       while (address < addressLimit) {
/* 1461 */         byte b = UnsafeUtil.getByte(address);
/* 1462 */         if (!Utf8.DecodeUtil.isOneByte(b)) {
/*      */           break;
/*      */         }
/* 1465 */         address++;
/* 1466 */         Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
/*      */       } 
/*      */       
/* 1469 */       while (address < addressLimit) {
/* 1470 */         byte byte1 = UnsafeUtil.getByte(address++);
/* 1471 */         if (Utf8.DecodeUtil.isOneByte(byte1)) {
/* 1472 */           Utf8.DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);
/*      */ 
/*      */           
/* 1475 */           while (address < addressLimit) {
/* 1476 */             byte b = UnsafeUtil.getByte(address);
/* 1477 */             if (!Utf8.DecodeUtil.isOneByte(b)) {
/*      */               break;
/*      */             }
/* 1480 */             address++;
/* 1481 */             Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
/*      */           }  continue;
/* 1483 */         }  if (Utf8.DecodeUtil.isTwoBytes(byte1)) {
/* 1484 */           if (address >= addressLimit) {
/* 1485 */             throw InvalidProtocolBufferException.invalidUtf8();
/*      */           }
/* 1487 */           Utf8.DecodeUtil.handleTwoBytes(byte1, 
/* 1488 */               UnsafeUtil.getByte(address++), resultArr, resultPos++); continue;
/* 1489 */         }  if (Utf8.DecodeUtil.isThreeBytes(byte1)) {
/* 1490 */           if (address >= addressLimit - 1L) {
/* 1491 */             throw InvalidProtocolBufferException.invalidUtf8();
/*      */           }
/* 1493 */           Utf8.DecodeUtil.handleThreeBytes(byte1, 
/*      */               
/* 1495 */               UnsafeUtil.getByte(address++), 
/* 1496 */               UnsafeUtil.getByte(address++), resultArr, resultPos++);
/*      */           
/*      */           continue;
/*      */         } 
/* 1500 */         if (address >= addressLimit - 2L) {
/* 1501 */           throw InvalidProtocolBufferException.invalidUtf8();
/*      */         }
/* 1503 */         Utf8.DecodeUtil.handleFourBytes(byte1, 
/*      */             
/* 1505 */             UnsafeUtil.getByte(address++), 
/* 1506 */             UnsafeUtil.getByte(address++), 
/* 1507 */             UnsafeUtil.getByte(address++), resultArr, resultPos++);
/*      */ 
/*      */ 
/*      */         
/* 1511 */         resultPos++;
/*      */       } 
/*      */ 
/*      */       
/* 1515 */       return new String(resultArr, 0, resultPos);
/*      */     }
/*      */ 
/*      */     
/*      */     int encodeUtf8(CharSequence in, byte[] out, int offset, int length) {
/* 1520 */       long outIx = offset;
/* 1521 */       long outLimit = outIx + length;
/* 1522 */       int inLimit = in.length();
/* 1523 */       if (inLimit > length || out.length - length < offset)
/*      */       {
/* 1525 */         throw new ArrayIndexOutOfBoundsException("Failed writing " + in
/* 1526 */             .charAt(inLimit - 1) + " at index " + (offset + length));
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1531 */       int inIx = 0; char c;
/* 1532 */       for (; inIx < inLimit && (c = in.charAt(inIx)) < ''; inIx++) {
/* 1533 */         UnsafeUtil.putByte(out, outIx++, (byte)c);
/*      */       }
/* 1535 */       if (inIx == inLimit)
/*      */       {
/* 1537 */         return (int)outIx;
/*      */       }
/*      */       
/* 1540 */       for (; inIx < inLimit; inIx++) {
/* 1541 */         c = in.charAt(inIx);
/*      */         
/* 1543 */         UnsafeUtil.putByte(out, outIx++, (byte)c);
/*      */         
/* 1545 */         UnsafeUtil.putByte(out, outIx++, (byte)(0x3C0 | c >>> 6));
/* 1546 */         UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & c));
/*      */ 
/*      */         
/* 1549 */         UnsafeUtil.putByte(out, outIx++, (byte)(0x1E0 | c >>> 12));
/* 1550 */         UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & c >>> 6));
/* 1551 */         UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & c));
/* 1552 */         if (outIx <= outLimit - 4L) {
/*      */           char low;
/*      */ 
/*      */           
/* 1556 */           if (inIx + 1 == inLimit || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
/* 1557 */             throw new Utf8.UnpairedSurrogateException(inIx - 1, inLimit);
/*      */           }
/* 1559 */           int codePoint = Character.toCodePoint(c, low);
/* 1560 */           UnsafeUtil.putByte(out, outIx++, (byte)(0xF0 | codePoint >>> 18));
/* 1561 */           UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & codePoint >>> 12));
/* 1562 */           UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & codePoint >>> 6));
/* 1563 */           UnsafeUtil.putByte(out, outIx++, (byte)(0x80 | 0x3F & codePoint));
/*      */         } else {
/* 1565 */           if ('?' <= c && c <= '?' && (inIx + 1 == inLimit || 
/* 1566 */             !Character.isSurrogatePair(c, in.charAt(inIx + 1))))
/*      */           {
/* 1568 */             throw new Utf8.UnpairedSurrogateException(inIx, inLimit);
/*      */           }
/*      */           
/* 1571 */           throw new ArrayIndexOutOfBoundsException("Failed writing " + c + " at index " + outIx);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1576 */       return (int)outIx;
/*      */     }
/*      */ 
/*      */     
/*      */     void encodeUtf8Direct(CharSequence in, ByteBuffer out) {
/* 1581 */       long address = UnsafeUtil.addressOffset(out);
/* 1582 */       long outIx = address + out.position();
/* 1583 */       long outLimit = address + out.limit();
/* 1584 */       int inLimit = in.length();
/* 1585 */       if (inLimit > outLimit - outIx)
/*      */       {
/* 1587 */         throw new ArrayIndexOutOfBoundsException("Failed writing " + in
/* 1588 */             .charAt(inLimit - 1) + " at index " + out.limit());
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1593 */       int inIx = 0; char c;
/* 1594 */       for (; inIx < inLimit && (c = in.charAt(inIx)) < ''; inIx++) {
/* 1595 */         UnsafeUtil.putByte(outIx++, (byte)c);
/*      */       }
/* 1597 */       if (inIx == inLimit) {
/*      */         
/* 1599 */         out.position((int)(outIx - address));
/*      */         
/*      */         return;
/*      */       } 
/* 1603 */       for (; inIx < inLimit; inIx++) {
/* 1604 */         c = in.charAt(inIx);
/*      */         
/* 1606 */         UnsafeUtil.putByte(outIx++, (byte)c);
/*      */         
/* 1608 */         UnsafeUtil.putByte(outIx++, (byte)(0x3C0 | c >>> 6));
/* 1609 */         UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & c));
/*      */ 
/*      */         
/* 1612 */         UnsafeUtil.putByte(outIx++, (byte)(0x1E0 | c >>> 12));
/* 1613 */         UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & c >>> 6));
/* 1614 */         UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & c));
/* 1615 */         if (outIx <= outLimit - 4L) {
/*      */           char low;
/*      */ 
/*      */           
/* 1619 */           if (inIx + 1 == inLimit || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
/* 1620 */             throw new Utf8.UnpairedSurrogateException(inIx - 1, inLimit);
/*      */           }
/* 1622 */           int codePoint = Character.toCodePoint(c, low);
/* 1623 */           UnsafeUtil.putByte(outIx++, (byte)(0xF0 | codePoint >>> 18));
/* 1624 */           UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & codePoint >>> 12));
/* 1625 */           UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & codePoint >>> 6));
/* 1626 */           UnsafeUtil.putByte(outIx++, (byte)(0x80 | 0x3F & codePoint));
/*      */         } else {
/* 1628 */           if ('?' <= c && c <= '?' && (inIx + 1 == inLimit || 
/* 1629 */             !Character.isSurrogatePair(c, in.charAt(inIx + 1))))
/*      */           {
/* 1631 */             throw new Utf8.UnpairedSurrogateException(inIx, inLimit);
/*      */           }
/*      */           
/* 1634 */           throw new ArrayIndexOutOfBoundsException("Failed writing " + c + " at index " + outIx);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1639 */       out.position((int)(outIx - address));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static int unsafeEstimateConsecutiveAscii(byte[] bytes, long offset, int maxChars) {
/* 1654 */       if (maxChars < 16)
/*      */       {
/* 1656 */         return 0;
/*      */       }
/*      */       
/* 1659 */       for (int i = 0; i < maxChars; i++) {
/* 1660 */         if (UnsafeUtil.getByte(bytes, offset++) < 0) {
/* 1661 */           return i;
/*      */         }
/*      */       } 
/* 1664 */       return maxChars;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static int unsafeEstimateConsecutiveAscii(long address, int maxChars) {
/* 1672 */       int remaining = maxChars;
/* 1673 */       if (remaining < 16)
/*      */       {
/* 1675 */         return 0;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1681 */       int unaligned = 8 - ((int)address & 0x7);
/* 1682 */       for (int j = unaligned; j > 0; j--) {
/* 1683 */         if (UnsafeUtil.getByte(address++) < 0) {
/* 1684 */           return unaligned - j;
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1691 */       remaining -= unaligned;
/*      */       
/* 1693 */       while (remaining >= 8 && (UnsafeUtil.getLong(address) & 0x8080808080808080L) == 0L) {
/* 1694 */         address += 8L; remaining -= 8;
/* 1695 */       }  return maxChars - remaining;
/*      */     }
/*      */ 
/*      */     
/*      */     private static int partialIsValidUtf8(byte[] bytes, long offset, int remaining) {
/* 1700 */       int skipped = unsafeEstimateConsecutiveAscii(bytes, offset, remaining);
/* 1701 */       remaining -= skipped;
/* 1702 */       offset += skipped;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       while (true)
/* 1708 */       { int byte1 = 0;
/* 1709 */         for (; remaining > 0 && (byte1 = UnsafeUtil.getByte(bytes, offset++)) >= 0; remaining--);
/* 1710 */         if (remaining == 0) {
/* 1711 */           return 0;
/*      */         }
/* 1713 */         remaining--;
/*      */ 
/*      */         
/* 1716 */         if (byte1 < -32) {
/*      */           
/* 1718 */           if (remaining == 0)
/*      */           {
/* 1720 */             return byte1;
/*      */           }
/* 1722 */           remaining--;
/*      */ 
/*      */ 
/*      */           
/* 1726 */           if (byte1 < -62 || UnsafeUtil.getByte(bytes, offset++) > -65)
/* 1727 */             return -1;  continue;
/*      */         } 
/* 1729 */         if (byte1 < -16) {
/*      */           
/* 1731 */           if (remaining < 2)
/*      */           {
/* 1733 */             return unsafeIncompleteStateFor(bytes, byte1, offset, remaining);
/*      */           }
/* 1735 */           remaining -= 2;
/*      */           
/*      */           int i;
/* 1738 */           if ((i = UnsafeUtil.getByte(bytes, offset++)) > -65 || (byte1 == -32 && i < -96) || (byte1 == -19 && i >= -96) || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1744 */             UnsafeUtil.getByte(bytes, offset++) > -65) {
/* 1745 */             return -1;
/*      */           }
/*      */           continue;
/*      */         } 
/* 1749 */         if (remaining < 3)
/*      */         {
/* 1751 */           return unsafeIncompleteStateFor(bytes, byte1, offset, remaining);
/*      */         }
/* 1753 */         remaining -= 3;
/*      */         
/*      */         int byte2;
/* 1756 */         if ((byte2 = UnsafeUtil.getByte(bytes, offset++)) > -65 || (byte1 << 28) + byte2 - -112 >> 30 != 0 || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1763 */           UnsafeUtil.getByte(bytes, offset++) > -65 || 
/*      */           
/* 1765 */           UnsafeUtil.getByte(bytes, offset++) > -65)
/* 1766 */           break;  }  return -1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static int partialIsValidUtf8(long address, int remaining) {
/* 1774 */       int skipped = unsafeEstimateConsecutiveAscii(address, remaining);
/* 1775 */       address += skipped;
/* 1776 */       remaining -= skipped;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       while (true)
/* 1782 */       { int byte1 = 0;
/* 1783 */         for (; remaining > 0 && (byte1 = UnsafeUtil.getByte(address++)) >= 0; remaining--);
/* 1784 */         if (remaining == 0) {
/* 1785 */           return 0;
/*      */         }
/* 1787 */         remaining--;
/*      */         
/* 1789 */         if (byte1 < -32) {
/*      */ 
/*      */           
/* 1792 */           if (remaining == 0)
/*      */           {
/* 1794 */             return byte1;
/*      */           }
/* 1796 */           remaining--;
/*      */ 
/*      */ 
/*      */           
/* 1800 */           if (byte1 < -62 || UnsafeUtil.getByte(address++) > -65)
/* 1801 */             return -1;  continue;
/*      */         } 
/* 1803 */         if (byte1 < -16) {
/*      */ 
/*      */           
/* 1806 */           if (remaining < 2)
/*      */           {
/* 1808 */             return unsafeIncompleteStateFor(address, byte1, remaining);
/*      */           }
/* 1810 */           remaining -= 2;
/*      */           
/* 1812 */           byte b = UnsafeUtil.getByte(address++);
/* 1813 */           if (b > -65 || (byte1 == -32 && b < -96) || (byte1 == -19 && b >= -96) || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1819 */             UnsafeUtil.getByte(address++) > -65) {
/* 1820 */             return -1;
/*      */           }
/*      */           
/*      */           continue;
/*      */         } 
/* 1825 */         if (remaining < 3)
/*      */         {
/* 1827 */           return unsafeIncompleteStateFor(address, byte1, remaining);
/*      */         }
/* 1829 */         remaining -= 3;
/*      */         
/* 1831 */         byte byte2 = UnsafeUtil.getByte(address++);
/* 1832 */         if (byte2 > -65 || (byte1 << 28) + byte2 - -112 >> 30 != 0 || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1839 */           UnsafeUtil.getByte(address++) > -65 || 
/*      */           
/* 1841 */           UnsafeUtil.getByte(address++) > -65)
/* 1842 */           break;  }  return -1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static int unsafeIncompleteStateFor(byte[] bytes, int byte1, long offset, int remaining) {
/* 1850 */       switch (remaining) {
/*      */         case 0:
/* 1852 */           return Utf8.incompleteStateFor(byte1);
/*      */         case 1:
/* 1854 */           return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(bytes, offset));
/*      */         case 2:
/* 1856 */           return Utf8.incompleteStateFor(byte1, 
/* 1857 */               UnsafeUtil.getByte(bytes, offset), UnsafeUtil.getByte(bytes, offset + 1L));
/*      */       } 
/* 1859 */       throw new AssertionError();
/*      */     }
/*      */ 
/*      */     
/*      */     private static int unsafeIncompleteStateFor(long address, int byte1, int remaining) {
/* 1864 */       switch (remaining) {
/*      */         case 0:
/* 1866 */           return Utf8.incompleteStateFor(byte1);
/*      */         case 1:
/* 1868 */           return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(address));
/*      */         case 2:
/* 1870 */           return Utf8.incompleteStateFor(byte1, 
/* 1871 */               UnsafeUtil.getByte(address), UnsafeUtil.getByte(address + 1L));
/*      */       } 
/* 1873 */       throw new AssertionError();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class DecodeUtil
/*      */   {
/*      */     private static boolean isOneByte(byte b) {
/* 1887 */       return (b >= 0);
/*      */     }
/*      */ 
/*      */     
/*      */     private static boolean isTwoBytes(byte b) {
/* 1892 */       return (b < -32);
/*      */     }
/*      */ 
/*      */     
/*      */     private static boolean isThreeBytes(byte b) {
/* 1897 */       return (b < -16);
/*      */     }
/*      */     
/*      */     private static void handleOneByte(byte byte1, char[] resultArr, int resultPos) {
/* 1901 */       resultArr[resultPos] = (char)byte1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static void handleTwoBytes(byte byte1, byte byte2, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
/* 1908 */       if (byte1 < -62 || isNotTrailingByte(byte2)) {
/* 1909 */         throw InvalidProtocolBufferException.invalidUtf8();
/*      */       }
/* 1911 */       resultArr[resultPos] = (char)((byte1 & 0x1F) << 6 | trailingByteValue(byte2));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private static void handleThreeBytes(byte byte1, byte byte2, byte byte3, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
/* 1917 */       if (isNotTrailingByte(byte2) || (byte1 == -32 && byte2 < -96) || (byte1 == -19 && byte2 >= -96) || 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1922 */         isNotTrailingByte(byte3)) {
/* 1923 */         throw InvalidProtocolBufferException.invalidUtf8();
/*      */       }
/* 1925 */       resultArr[resultPos] = 
/*      */         
/* 1927 */         (char)((byte1 & 0xF) << 12 | trailingByteValue(byte2) << 6 | trailingByteValue(byte3));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private static void handleFourBytes(byte byte1, byte byte2, byte byte3, byte byte4, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
/* 1933 */       if (isNotTrailingByte(byte2) || (byte1 << 28) + byte2 - -112 >> 30 != 0 || 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1942 */         isNotTrailingByte(byte3) || 
/* 1943 */         isNotTrailingByte(byte4)) {
/* 1944 */         throw InvalidProtocolBufferException.invalidUtf8();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1950 */       int codepoint = (byte1 & 0x7) << 18 | trailingByteValue(byte2) << 12 | trailingByteValue(byte3) << 6 | trailingByteValue(byte4);
/* 1951 */       resultArr[resultPos] = highSurrogate(codepoint);
/* 1952 */       resultArr[resultPos + 1] = lowSurrogate(codepoint);
/*      */     }
/*      */ 
/*      */     
/*      */     private static boolean isNotTrailingByte(byte b) {
/* 1957 */       return (b > -65);
/*      */     }
/*      */ 
/*      */     
/*      */     private static int trailingByteValue(byte b) {
/* 1962 */       return b & 0x3F;
/*      */     }
/*      */     
/*      */     private static char highSurrogate(int codePoint) {
/* 1966 */       return (char)(55232 + (codePoint >>> 10));
/*      */     }
/*      */ 
/*      */     
/*      */     private static char lowSurrogate(int codePoint) {
/* 1971 */       return (char)(56320 + (codePoint & 0x3FF));
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Utf8.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */