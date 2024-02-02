/*     */ package org.apache.commons.compress.compressors.deflate64;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.utils.BitInputStream;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
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
/*     */ class HuffmanDecoder
/*     */   implements Closeable
/*     */ {
/*  56 */   private static final short[] RUN_LENGTH_TABLE = new short[] { 96, 128, 160, 192, 224, 256, 288, 320, 353, 417, 481, 545, 610, 738, 866, 994, 1123, 1379, 1635, 1891, 2148, 2660, 3172, 3684, 4197, 5221, 6245, 7269, 112 };
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
/*  82 */   private static final int[] DISTANCE_TABLE = new int[] { 16, 32, 48, 64, 81, 113, 146, 210, 275, 403, 532, 788, 1045, 1557, 2070, 3094, 4119, 6167, 8216, 12312, 16409, 24601, 32794, 49178, 65563, 98331, 131100, 196636, 262173, 393245, 524318, 786462 };
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
/*  93 */   private static final int[] CODE_LENGTHS_ORDER = new int[] { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   private static final int[] FIXED_LITERALS = new int[288]; static {
/* 104 */     Arrays.fill(FIXED_LITERALS, 0, 144, 8);
/* 105 */     Arrays.fill(FIXED_LITERALS, 144, 256, 9);
/* 106 */     Arrays.fill(FIXED_LITERALS, 256, 280, 7);
/* 107 */     Arrays.fill(FIXED_LITERALS, 280, 288, 8);
/*     */   }
/* 109 */   private static final int[] FIXED_DISTANCE = new int[32]; static {
/* 110 */     Arrays.fill(FIXED_DISTANCE, 5);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean finalBlock;
/*     */   private DecoderState state;
/*     */   private BitInputStream reader;
/*     */   private final InputStream in;
/* 118 */   private final DecodingMemory memory = new DecodingMemory();
/*     */   
/*     */   HuffmanDecoder(InputStream in) {
/* 121 */     this.reader = new BitInputStream(in, ByteOrder.LITTLE_ENDIAN);
/* 122 */     this.in = in;
/* 123 */     this.state = new InitialState();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 128 */     this.state = new InitialState();
/* 129 */     this.reader = null;
/*     */   }
/*     */   
/*     */   public int decode(byte[] b) throws IOException {
/* 133 */     return decode(b, 0, b.length);
/*     */   }
/*     */   
/*     */   public int decode(byte[] b, int off, int len) throws IOException {
/* 137 */     while (!this.finalBlock || this.state.hasData()) {
/* 138 */       if (this.state.state() == HuffmanState.INITIAL) {
/* 139 */         int[][] tables; this.finalBlock = (readBits(1) == 1L);
/* 140 */         int mode = (int)readBits(2);
/* 141 */         switch (mode) {
/*     */           case 0:
/* 143 */             switchToUncompressedState();
/*     */             continue;
/*     */           case 1:
/* 146 */             this.state = new HuffmanCodes(HuffmanState.FIXED_CODES, FIXED_LITERALS, FIXED_DISTANCE);
/*     */             continue;
/*     */           case 2:
/* 149 */             tables = readDynamicTables();
/* 150 */             this.state = new HuffmanCodes(HuffmanState.DYNAMIC_CODES, tables[0], tables[1]);
/*     */             continue;
/*     */         } 
/* 153 */         throw new IllegalStateException("Unsupported compression: " + mode);
/*     */       } 
/*     */       
/* 156 */       int r = this.state.read(b, off, len);
/* 157 */       if (r != 0) {
/* 158 */         return r;
/*     */       }
/*     */     } 
/*     */     
/* 162 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getBytesRead() {
/* 169 */     return this.reader.getBytesRead();
/*     */   }
/*     */   
/*     */   private void switchToUncompressedState() throws IOException {
/* 173 */     this.reader.alignWithByteBoundary();
/* 174 */     long bLen = readBits(16);
/* 175 */     long bNLen = readBits(16);
/* 176 */     if (((bLen ^ 0xFFFFL) & 0xFFFFL) != bNLen)
/*     */     {
/* 178 */       throw new IllegalStateException("Illegal LEN / NLEN values");
/*     */     }
/* 180 */     this.state = new UncompressedState(bLen);
/*     */   }
/*     */   
/*     */   private int[][] readDynamicTables() throws IOException {
/* 184 */     int[][] result = new int[2][];
/* 185 */     int literals = (int)(readBits(5) + 257L);
/* 186 */     result[0] = new int[literals];
/*     */     
/* 188 */     int distances = (int)(readBits(5) + 1L);
/* 189 */     result[1] = new int[distances];
/*     */     
/* 191 */     populateDynamicTables(this.reader, result[0], result[1]);
/* 192 */     return result;
/*     */   }
/*     */   
/*     */   int available() throws IOException {
/* 196 */     return this.state.available();
/*     */   }
/*     */   
/*     */   private static abstract class DecoderState {
/*     */     private DecoderState() {}
/*     */     
/*     */     abstract HuffmanState state();
/*     */     
/*     */     abstract int read(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException;
/*     */     
/*     */     abstract boolean hasData();
/*     */     
/*     */     abstract int available() throws IOException; }
/*     */   
/*     */   private class UncompressedState extends DecoderState { private final long blockLength;
/*     */     private long read;
/*     */     
/*     */     private UncompressedState(long blockLength) {
/* 214 */       this.blockLength = blockLength;
/*     */     }
/*     */ 
/*     */     
/*     */     HuffmanState state() {
/* 219 */       return (this.read < this.blockLength) ? HuffmanState.STORED : HuffmanState.INITIAL;
/*     */     }
/*     */ 
/*     */     
/*     */     int read(byte[] b, int off, int len) throws IOException {
/* 224 */       if (len == 0) {
/* 225 */         return 0;
/*     */       }
/*     */       
/* 228 */       int max = (int)Math.min(this.blockLength - this.read, len);
/* 229 */       int readSoFar = 0;
/* 230 */       while (readSoFar < max) {
/*     */         int readNow;
/* 232 */         if (HuffmanDecoder.this.reader.bitsCached() > 0) {
/* 233 */           byte next = (byte)(int)HuffmanDecoder.this.readBits(8);
/* 234 */           b[off + readSoFar] = HuffmanDecoder.this.memory.add(next);
/* 235 */           readNow = 1;
/*     */         } else {
/* 237 */           readNow = HuffmanDecoder.this.in.read(b, off + readSoFar, max - readSoFar);
/* 238 */           if (readNow == -1) {
/* 239 */             throw new EOFException("Truncated Deflate64 Stream");
/*     */           }
/* 241 */           HuffmanDecoder.this.memory.add(b, off + readSoFar, readNow);
/*     */         } 
/* 243 */         this.read += readNow;
/* 244 */         readSoFar += readNow;
/*     */       } 
/* 246 */       return max;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean hasData() {
/* 251 */       return (this.read < this.blockLength);
/*     */     }
/*     */ 
/*     */     
/*     */     int available() throws IOException {
/* 256 */       return (int)Math.min(this.blockLength - this.read, HuffmanDecoder.this.reader.bitsAvailable() / 8L);
/*     */     } }
/*     */   
/*     */   private static class InitialState extends DecoderState {
/*     */     private InitialState() {}
/*     */     
/*     */     HuffmanState state() {
/* 263 */       return HuffmanState.INITIAL;
/*     */     }
/*     */ 
/*     */     
/*     */     int read(byte[] b, int off, int len) throws IOException {
/* 268 */       if (len == 0) {
/* 269 */         return 0;
/*     */       }
/* 271 */       throw new IllegalStateException("Cannot read in this state");
/*     */     }
/*     */ 
/*     */     
/*     */     boolean hasData() {
/* 276 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     int available() {
/* 281 */       return 0;
/*     */     }
/*     */   }
/*     */   
/*     */   private class HuffmanCodes
/*     */     extends DecoderState {
/*     */     private boolean endOfBlock;
/*     */     private final HuffmanState state;
/*     */     private final HuffmanDecoder.BinaryTreeNode lengthTree;
/*     */     private final HuffmanDecoder.BinaryTreeNode distanceTree;
/*     */     private int runBufferPos;
/* 292 */     private byte[] runBuffer = ByteUtils.EMPTY_BYTE_ARRAY;
/*     */     private int runBufferLength;
/*     */     
/*     */     HuffmanCodes(HuffmanState state, int[] lengths, int[] distance) {
/* 296 */       this.state = state;
/* 297 */       this.lengthTree = HuffmanDecoder.buildTree(lengths);
/* 298 */       this.distanceTree = HuffmanDecoder.buildTree(distance);
/*     */     }
/*     */ 
/*     */     
/*     */     HuffmanState state() {
/* 303 */       return this.endOfBlock ? HuffmanState.INITIAL : this.state;
/*     */     }
/*     */ 
/*     */     
/*     */     int read(byte[] b, int off, int len) throws IOException {
/* 308 */       if (len == 0) {
/* 309 */         return 0;
/*     */       }
/* 311 */       return decodeNext(b, off, len);
/*     */     }
/*     */     
/*     */     private int decodeNext(byte[] b, int off, int len) throws IOException {
/* 315 */       if (this.endOfBlock) {
/* 316 */         return -1;
/*     */       }
/* 318 */       int result = copyFromRunBuffer(b, off, len);
/*     */       
/* 320 */       while (result < len) {
/* 321 */         int symbol = HuffmanDecoder.nextSymbol(HuffmanDecoder.this.reader, this.lengthTree);
/* 322 */         if (symbol < 256) {
/* 323 */           b[off + result++] = HuffmanDecoder.this.memory.add((byte)symbol); continue;
/* 324 */         }  if (symbol > 256) {
/* 325 */           int runMask = HuffmanDecoder.RUN_LENGTH_TABLE[symbol - 257];
/* 326 */           int run = runMask >>> 5;
/* 327 */           int runXtra = runMask & 0x1F;
/* 328 */           run = (int)(run + HuffmanDecoder.this.readBits(runXtra));
/*     */           
/* 330 */           int distSym = HuffmanDecoder.nextSymbol(HuffmanDecoder.this.reader, this.distanceTree);
/*     */           
/* 332 */           int distMask = HuffmanDecoder.DISTANCE_TABLE[distSym];
/* 333 */           int dist = distMask >>> 4;
/* 334 */           int distXtra = distMask & 0xF;
/* 335 */           dist = (int)(dist + HuffmanDecoder.this.readBits(distXtra));
/*     */           
/* 337 */           if (this.runBuffer.length < run) {
/* 338 */             this.runBuffer = new byte[run];
/*     */           }
/* 340 */           this.runBufferLength = run;
/* 341 */           this.runBufferPos = 0;
/* 342 */           HuffmanDecoder.this.memory.recordToBuffer(dist, run, this.runBuffer);
/*     */           
/* 344 */           result += copyFromRunBuffer(b, off + result, len - result); continue;
/*     */         } 
/* 346 */         this.endOfBlock = true;
/* 347 */         return result;
/*     */       } 
/*     */ 
/*     */       
/* 351 */       return result;
/*     */     }
/*     */     
/*     */     private int copyFromRunBuffer(byte[] b, int off, int len) {
/* 355 */       int bytesInBuffer = this.runBufferLength - this.runBufferPos;
/* 356 */       int copiedBytes = 0;
/* 357 */       if (bytesInBuffer > 0) {
/* 358 */         copiedBytes = Math.min(len, bytesInBuffer);
/* 359 */         System.arraycopy(this.runBuffer, this.runBufferPos, b, off, copiedBytes);
/* 360 */         this.runBufferPos += copiedBytes;
/*     */       } 
/* 362 */       return copiedBytes;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean hasData() {
/* 367 */       return !this.endOfBlock;
/*     */     }
/*     */ 
/*     */     
/*     */     int available() {
/* 372 */       return this.runBufferLength - this.runBufferPos;
/*     */     }
/*     */   }
/*     */   
/*     */   private static int nextSymbol(BitInputStream reader, BinaryTreeNode tree) throws IOException {
/* 377 */     BinaryTreeNode node = tree;
/* 378 */     while (node != null && node.literal == -1) {
/* 379 */       long bit = readBits(reader, 1);
/* 380 */       node = (bit == 0L) ? node.leftNode : node.rightNode;
/*     */     } 
/* 382 */     return (node != null) ? node.literal : -1;
/*     */   }
/*     */   
/*     */   private static void populateDynamicTables(BitInputStream reader, int[] literals, int[] distances) throws IOException {
/* 386 */     int codeLengths = (int)(readBits(reader, 4) + 4L);
/*     */     
/* 388 */     int[] codeLengthValues = new int[19];
/* 389 */     for (int cLen = 0; cLen < codeLengths; cLen++) {
/* 390 */       codeLengthValues[CODE_LENGTHS_ORDER[cLen]] = (int)readBits(reader, 3);
/*     */     }
/*     */     
/* 393 */     BinaryTreeNode codeLengthTree = buildTree(codeLengthValues);
/*     */     
/* 395 */     int[] auxBuffer = new int[literals.length + distances.length];
/*     */     
/* 397 */     int value = -1;
/* 398 */     int length = 0;
/* 399 */     int off = 0;
/* 400 */     while (off < auxBuffer.length) {
/* 401 */       if (length > 0) {
/* 402 */         auxBuffer[off++] = value;
/* 403 */         length--; continue;
/*     */       } 
/* 405 */       int symbol = nextSymbol(reader, codeLengthTree);
/* 406 */       if (symbol < 16) {
/* 407 */         value = symbol;
/* 408 */         auxBuffer[off++] = value; continue;
/*     */       } 
/* 410 */       switch (symbol) {
/*     */         case 16:
/* 412 */           length = (int)(readBits(reader, 2) + 3L);
/*     */         
/*     */         case 17:
/* 415 */           value = 0;
/* 416 */           length = (int)(readBits(reader, 3) + 3L);
/*     */         
/*     */         case 18:
/* 419 */           value = 0;
/* 420 */           length = (int)(readBits(reader, 7) + 11L);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 429 */     System.arraycopy(auxBuffer, 0, literals, 0, literals.length);
/* 430 */     System.arraycopy(auxBuffer, literals.length, distances, 0, distances.length);
/*     */   }
/*     */   
/*     */   private static class BinaryTreeNode {
/*     */     private final int bits;
/* 435 */     int literal = -1;
/*     */     BinaryTreeNode leftNode;
/*     */     BinaryTreeNode rightNode;
/*     */     
/*     */     private BinaryTreeNode(int bits) {
/* 440 */       this.bits = bits;
/*     */     }
/*     */     
/*     */     void leaf(int symbol) {
/* 444 */       this.literal = symbol;
/* 445 */       this.leftNode = null;
/* 446 */       this.rightNode = null;
/*     */     }
/*     */     
/*     */     BinaryTreeNode left() {
/* 450 */       if (this.leftNode == null && this.literal == -1) {
/* 451 */         this.leftNode = new BinaryTreeNode(this.bits + 1);
/*     */       }
/* 453 */       return this.leftNode;
/*     */     }
/*     */     
/*     */     BinaryTreeNode right() {
/* 457 */       if (this.rightNode == null && this.literal == -1) {
/* 458 */         this.rightNode = new BinaryTreeNode(this.bits + 1);
/*     */       }
/* 460 */       return this.rightNode;
/*     */     }
/*     */   }
/*     */   
/*     */   private static BinaryTreeNode buildTree(int[] litTable) {
/* 465 */     int[] literalCodes = getCodes(litTable);
/*     */     
/* 467 */     BinaryTreeNode root = new BinaryTreeNode(0);
/*     */     
/* 469 */     for (int i = 0; i < litTable.length; i++) {
/* 470 */       int len = litTable[i];
/* 471 */       if (len != 0) {
/* 472 */         BinaryTreeNode node = root;
/* 473 */         int lit = literalCodes[len - 1];
/* 474 */         for (int p = len - 1; p >= 0; p--) {
/* 475 */           int bit = lit & 1 << p;
/* 476 */           node = (bit == 0) ? node.left() : node.right();
/* 477 */           if (node == null) {
/* 478 */             throw new IllegalStateException("node doesn't exist in Huffman tree");
/*     */           }
/*     */         } 
/* 481 */         node.leaf(i);
/* 482 */         literalCodes[len - 1] = literalCodes[len - 1] + 1;
/*     */       } 
/*     */     } 
/* 485 */     return root;
/*     */   }
/*     */   
/*     */   private static int[] getCodes(int[] litTable) {
/* 489 */     int max = 0;
/* 490 */     int[] blCount = new int[65];
/*     */     
/* 492 */     for (int aLitTable : litTable) {
/* 493 */       if (aLitTable < 0 || aLitTable > 64) {
/* 494 */         throw new IllegalArgumentException("Invalid code " + aLitTable + " in literal table");
/*     */       }
/*     */       
/* 497 */       max = Math.max(max, aLitTable);
/* 498 */       blCount[aLitTable] = blCount[aLitTable] + 1;
/*     */     } 
/* 500 */     blCount = Arrays.copyOf(blCount, max + 1);
/*     */     
/* 502 */     int code = 0;
/* 503 */     int[] nextCode = new int[max + 1];
/* 504 */     for (int i = 0; i <= max; i++) {
/* 505 */       code = code + blCount[i] << 1;
/* 506 */       nextCode[i] = code;
/*     */     } 
/*     */     
/* 509 */     return nextCode;
/*     */   }
/*     */   
/*     */   private static class DecodingMemory {
/*     */     private final byte[] memory;
/*     */     private final int mask;
/*     */     private int wHead;
/*     */     private boolean wrappedAround;
/*     */     
/*     */     private DecodingMemory() {
/* 519 */       this(16);
/*     */     }
/*     */     
/*     */     private DecodingMemory(int bits) {
/* 523 */       this.memory = new byte[1 << bits];
/* 524 */       this.mask = this.memory.length - 1;
/*     */     }
/*     */     
/*     */     byte add(byte b) {
/* 528 */       this.memory[this.wHead] = b;
/* 529 */       this.wHead = incCounter(this.wHead);
/* 530 */       return b;
/*     */     }
/*     */     
/*     */     void add(byte[] b, int off, int len) {
/* 534 */       for (int i = off; i < off + len; i++) {
/* 535 */         add(b[i]);
/*     */       }
/*     */     }
/*     */     
/*     */     void recordToBuffer(int distance, int length, byte[] buff) {
/* 540 */       if (distance > this.memory.length) {
/* 541 */         throw new IllegalStateException("Illegal distance parameter: " + distance);
/*     */       }
/* 543 */       int start = this.wHead - distance & this.mask;
/* 544 */       if (!this.wrappedAround && start >= this.wHead)
/* 545 */         throw new IllegalStateException("Attempt to read beyond memory: dist=" + distance); 
/*     */       int pos;
/* 547 */       for (int i = 0; i < length; i++, pos = incCounter(pos)) {
/* 548 */         buff[i] = add(this.memory[pos]);
/*     */       }
/*     */     }
/*     */     
/*     */     private int incCounter(int counter) {
/* 553 */       int newCounter = counter + 1 & this.mask;
/* 554 */       if (!this.wrappedAround && newCounter < counter) {
/* 555 */         this.wrappedAround = true;
/*     */       }
/* 557 */       return newCounter;
/*     */     }
/*     */   }
/*     */   
/*     */   private long readBits(int numBits) throws IOException {
/* 562 */     return readBits(this.reader, numBits);
/*     */   }
/*     */   
/*     */   private static long readBits(BitInputStream reader, int numBits) throws IOException {
/* 566 */     long r = reader.readBits(numBits);
/* 567 */     if (r == -1L) {
/* 568 */       throw new EOFException("Truncated Deflate64 Stream");
/*     */     }
/* 570 */     return r;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\deflate64\HuffmanDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */