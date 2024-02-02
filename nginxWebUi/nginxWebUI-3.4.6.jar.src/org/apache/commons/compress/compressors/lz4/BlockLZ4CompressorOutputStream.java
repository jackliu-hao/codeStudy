/*     */ package org.apache.commons.compress.compressors.lz4;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;
/*     */ import org.apache.commons.compress.compressors.lz77support.Parameters;
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
/*     */ public class BlockLZ4CompressorOutputStream
/*     */   extends CompressorOutputStream
/*     */ {
/*     */   private static final int MIN_BACK_REFERENCE_LENGTH = 4;
/*     */   private static final int MIN_OFFSET_OF_LAST_BACK_REFERENCE = 12;
/*     */   private final LZ77Compressor compressor;
/*     */   private final OutputStream os;
/*  82 */   private final byte[] oneByte = new byte[1];
/*     */   
/*     */   private boolean finished;
/*     */   
/*  86 */   private final Deque<Pair> pairs = new LinkedList<>();
/*     */ 
/*     */   
/*  89 */   private final Deque<byte[]> expandedBlocks = (Deque)new LinkedList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockLZ4CompressorOutputStream(OutputStream os) throws IOException {
/* 100 */     this(os, createParameterBuilder().build());
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
/*     */   public BlockLZ4CompressorOutputStream(OutputStream os, Parameters params) throws IOException {
/* 114 */     this.os = os;
/* 115 */     this.compressor = new LZ77Compressor(params, block -> {
/*     */           switch (block.getType()) {
/*     */             case LITERAL:
/*     */               addLiteralBlock((LZ77Compressor.LiteralBlock)block);
/*     */               break;
/*     */             case BACK_REFERENCE:
/*     */               addBackReference((LZ77Compressor.BackReference)block);
/*     */               break;
/*     */             case EOD:
/*     */               writeFinalLiteralBlock();
/*     */               break;
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 133 */     this.oneByte[0] = (byte)(b & 0xFF);
/* 134 */     write(this.oneByte);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] data, int off, int len) throws IOException {
/* 139 */     this.compressor.compress(data, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 145 */       finish();
/*     */     } finally {
/* 147 */       this.os.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finish() throws IOException {
/* 157 */     if (!this.finished) {
/* 158 */       this.compressor.finish();
/* 159 */       this.finished = true;
/*     */     } 
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
/*     */   public void prefill(byte[] data, int off, int len) {
/* 173 */     if (len > 0) {
/* 174 */       byte[] b = Arrays.copyOfRange(data, off, off + len);
/* 175 */       this.compressor.prefill(b);
/* 176 */       recordLiteral(b);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addLiteralBlock(LZ77Compressor.LiteralBlock block) throws IOException {
/* 181 */     Pair last = writeBlocksAndReturnUnfinishedPair(block.getLength());
/* 182 */     recordLiteral(last.addLiteral(block));
/* 183 */     clearUnusedBlocksAndPairs();
/*     */   }
/*     */   
/*     */   private void addBackReference(LZ77Compressor.BackReference block) throws IOException {
/* 187 */     Pair last = writeBlocksAndReturnUnfinishedPair(block.getLength());
/* 188 */     last.setBackReference(block);
/* 189 */     recordBackReference(block);
/* 190 */     clearUnusedBlocksAndPairs();
/*     */   }
/*     */   
/*     */   private Pair writeBlocksAndReturnUnfinishedPair(int length) throws IOException {
/* 194 */     writeWritablePairs(length);
/* 195 */     Pair last = this.pairs.peekLast();
/* 196 */     if (last == null || last.hasBackReference()) {
/* 197 */       last = new Pair();
/* 198 */       this.pairs.addLast(last);
/*     */     } 
/* 200 */     return last;
/*     */   }
/*     */   
/*     */   private void recordLiteral(byte[] b) {
/* 204 */     this.expandedBlocks.addFirst(b);
/*     */   }
/*     */   
/*     */   private void clearUnusedBlocksAndPairs() {
/* 208 */     clearUnusedBlocks();
/* 209 */     clearUnusedPairs();
/*     */   }
/*     */   
/*     */   private void clearUnusedBlocks() {
/* 213 */     int blockLengths = 0;
/* 214 */     int blocksToKeep = 0;
/* 215 */     for (byte[] b : this.expandedBlocks) {
/* 216 */       blocksToKeep++;
/* 217 */       blockLengths += b.length;
/* 218 */       if (blockLengths >= 65536) {
/*     */         break;
/*     */       }
/*     */     } 
/* 222 */     int size = this.expandedBlocks.size();
/* 223 */     for (int i = blocksToKeep; i < size; i++) {
/* 224 */       this.expandedBlocks.removeLast();
/*     */     }
/*     */   }
/*     */   
/*     */   private void recordBackReference(LZ77Compressor.BackReference block) {
/* 229 */     this.expandedBlocks.addFirst(expand(block.getOffset(), block.getLength()));
/*     */   }
/*     */   
/*     */   private byte[] expand(int offset, int length) {
/* 233 */     byte[] expanded = new byte[length];
/* 234 */     if (offset == 1) {
/* 235 */       byte[] block = this.expandedBlocks.peekFirst();
/* 236 */       byte b = block[block.length - 1];
/* 237 */       if (b != 0) {
/* 238 */         Arrays.fill(expanded, b);
/*     */       }
/*     */     } else {
/* 241 */       expandFromList(expanded, offset, length);
/*     */     } 
/* 243 */     return expanded;
/*     */   }
/*     */   
/*     */   private void expandFromList(byte[] expanded, int offset, int length) {
/* 247 */     int offsetRemaining = offset;
/* 248 */     int lengthRemaining = length;
/* 249 */     int writeOffset = 0;
/* 250 */     while (lengthRemaining > 0) {
/*     */       int copyLen, copyOffset;
/* 252 */       byte[] block = null;
/*     */ 
/*     */       
/* 255 */       if (offsetRemaining > 0) {
/* 256 */         int blockOffset = 0;
/* 257 */         for (byte[] b : this.expandedBlocks) {
/* 258 */           if (b.length + blockOffset >= offsetRemaining) {
/* 259 */             block = b;
/*     */             break;
/*     */           } 
/* 262 */           blockOffset += b.length;
/*     */         } 
/* 264 */         if (block == null)
/*     */         {
/* 266 */           throw new IllegalStateException("Failed to find a block containing offset " + offset);
/*     */         }
/* 268 */         copyOffset = blockOffset + block.length - offsetRemaining;
/* 269 */         copyLen = Math.min(lengthRemaining, block.length - copyOffset);
/*     */       } else {
/*     */         
/* 272 */         block = expanded;
/* 273 */         copyOffset = -offsetRemaining;
/* 274 */         copyLen = Math.min(lengthRemaining, writeOffset + offsetRemaining);
/*     */       } 
/* 276 */       System.arraycopy(block, copyOffset, expanded, writeOffset, copyLen);
/* 277 */       offsetRemaining -= copyLen;
/* 278 */       lengthRemaining -= copyLen;
/* 279 */       writeOffset += copyLen;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void clearUnusedPairs() {
/* 284 */     int pairLengths = 0;
/* 285 */     int pairsToKeep = 0;
/* 286 */     for (Iterator<Pair> it = this.pairs.descendingIterator(); it.hasNext(); ) {
/* 287 */       Pair p = it.next();
/* 288 */       pairsToKeep++;
/* 289 */       pairLengths += p.length();
/* 290 */       if (pairLengths >= 65536) {
/*     */         break;
/*     */       }
/*     */     } 
/* 294 */     int size = this.pairs.size();
/* 295 */     for (int i = pairsToKeep; i < size; i++) {
/* 296 */       Pair p = this.pairs.peekFirst();
/* 297 */       if (!p.hasBeenWritten()) {
/*     */         break;
/*     */       }
/* 300 */       this.pairs.removeFirst();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeFinalLiteralBlock() throws IOException {
/* 305 */     rewriteLastPairs();
/* 306 */     for (Pair p : this.pairs) {
/* 307 */       if (!p.hasBeenWritten()) {
/* 308 */         p.writeTo(this.os);
/*     */       }
/*     */     } 
/* 311 */     this.pairs.clear();
/*     */   }
/*     */   
/*     */   private void writeWritablePairs(int lengthOfBlocksAfterLastPair) throws IOException {
/* 315 */     int unwrittenLength = lengthOfBlocksAfterLastPair; Iterator<Pair> it;
/* 316 */     for (it = this.pairs.descendingIterator(); it.hasNext(); ) {
/* 317 */       Pair p = it.next();
/* 318 */       if (p.hasBeenWritten()) {
/*     */         break;
/*     */       }
/* 321 */       unwrittenLength += p.length();
/*     */     } 
/* 323 */     for (it = this.pairs.iterator(); it.hasNext(); ) { Pair p = it.next();
/* 324 */       if (p.hasBeenWritten()) {
/*     */         continue;
/*     */       }
/* 327 */       unwrittenLength -= p.length();
/* 328 */       if (!p.canBeWritten(unwrittenLength)) {
/*     */         break;
/*     */       }
/* 331 */       p.writeTo(this.os); }
/*     */   
/*     */   }
/*     */   
/*     */   private void rewriteLastPairs() {
/* 336 */     LinkedList<Pair> lastPairs = new LinkedList<>();
/* 337 */     LinkedList<Integer> pairLength = new LinkedList<>();
/* 338 */     int offset = 0; Iterator<Pair> it;
/* 339 */     for (it = this.pairs.descendingIterator(); it.hasNext(); ) {
/* 340 */       Pair p = it.next();
/* 341 */       if (p.hasBeenWritten()) {
/*     */         break;
/*     */       }
/* 344 */       int len = p.length();
/* 345 */       pairLength.addFirst(Integer.valueOf(len));
/* 346 */       lastPairs.addFirst(p);
/* 347 */       offset += len;
/* 348 */       if (offset >= 12) {
/*     */         break;
/*     */       }
/*     */     } 
/* 352 */     for (it = lastPairs.iterator(); it.hasNext(); ) { Pair p = it.next();
/* 353 */       this.pairs.remove(p); }
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
/* 379 */     int lastPairsSize = lastPairs.size();
/* 380 */     int toExpand = 0;
/* 381 */     for (int i = 1; i < lastPairsSize; i++) {
/* 382 */       toExpand += ((Integer)pairLength.get(i)).intValue();
/*     */     }
/* 384 */     Pair replacement = new Pair();
/* 385 */     if (toExpand > 0) {
/* 386 */       replacement.prependLiteral(expand(toExpand, toExpand));
/*     */     }
/* 388 */     Pair splitCandidate = lastPairs.get(0);
/* 389 */     int stillNeeded = 12 - toExpand;
/* 390 */     int brLen = splitCandidate.hasBackReference() ? splitCandidate.backReferenceLength() : 0;
/* 391 */     if (splitCandidate.hasBackReference() && brLen >= 4 + stillNeeded) {
/* 392 */       replacement.prependLiteral(expand(toExpand + stillNeeded, stillNeeded));
/* 393 */       this.pairs.add(splitCandidate.splitWithNewBackReferenceLengthOf(brLen - stillNeeded));
/*     */     } else {
/* 395 */       if (splitCandidate.hasBackReference()) {
/* 396 */         replacement.prependLiteral(expand(toExpand + brLen, brLen));
/*     */       }
/* 398 */       splitCandidate.prependTo(replacement);
/*     */     } 
/* 400 */     this.pairs.add(replacement);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Parameters.Builder createParameterBuilder() {
/* 408 */     int maxLen = 65535;
/* 409 */     return Parameters.builder(65536)
/* 410 */       .withMinBackReferenceLength(4)
/* 411 */       .withMaxBackReferenceLength(65535)
/* 412 */       .withMaxOffset(65535)
/* 413 */       .withMaxLiteralLength(65535);
/*     */   }
/*     */   
/*     */   static final class Pair {
/* 417 */     private final Deque<byte[]> literals = (Deque)new LinkedList<>(); private int brOffset;
/*     */     private int brLength;
/*     */     private boolean written;
/*     */     
/*     */     private void prependLiteral(byte[] data) {
/* 422 */       this.literals.addFirst(data);
/*     */     }
/*     */     byte[] addLiteral(LZ77Compressor.LiteralBlock block) {
/* 425 */       byte[] copy = Arrays.copyOfRange(block.getData(), block.getOffset(), block
/* 426 */           .getOffset() + block.getLength());
/* 427 */       this.literals.add(copy);
/* 428 */       return copy;
/*     */     }
/*     */     void setBackReference(LZ77Compressor.BackReference block) {
/* 431 */       if (hasBackReference()) {
/* 432 */         throw new IllegalStateException();
/*     */       }
/* 434 */       this.brOffset = block.getOffset();
/* 435 */       this.brLength = block.getLength();
/*     */     }
/*     */     boolean hasBackReference() {
/* 438 */       return (this.brOffset > 0);
/*     */     }
/*     */     boolean canBeWritten(int lengthOfBlocksAfterThisPair) {
/* 441 */       return (hasBackReference() && lengthOfBlocksAfterThisPair >= 16);
/*     */     }
/*     */     
/*     */     int length() {
/* 445 */       return literalLength() + this.brLength;
/*     */     }
/*     */     private boolean hasBeenWritten() {
/* 448 */       return this.written;
/*     */     }
/*     */     void writeTo(OutputStream out) throws IOException {
/* 451 */       int litLength = literalLength();
/* 452 */       out.write(lengths(litLength, this.brLength));
/* 453 */       if (litLength >= 15) {
/* 454 */         writeLength(litLength - 15, out);
/*     */       }
/* 456 */       for (byte[] b : this.literals) {
/* 457 */         out.write(b);
/*     */       }
/* 459 */       if (hasBackReference()) {
/* 460 */         ByteUtils.toLittleEndian(out, this.brOffset, 2);
/* 461 */         if (this.brLength - 4 >= 15) {
/* 462 */           writeLength(this.brLength - 4 - 15, out);
/*     */         }
/*     */       } 
/*     */       
/* 466 */       this.written = true;
/*     */     }
/*     */     private int literalLength() {
/* 469 */       int length = 0;
/* 470 */       for (byte[] b : this.literals) {
/* 471 */         length += b.length;
/*     */       }
/* 473 */       return length;
/*     */     }
/*     */     private static int lengths(int litLength, int brLength) {
/* 476 */       int l = (litLength < 15) ? litLength : 15;
/* 477 */       int br = (brLength < 4) ? 0 : ((brLength < 19) ? (brLength - 4) : 15);
/* 478 */       return l << 4 | br;
/*     */     }
/*     */     private static void writeLength(int length, OutputStream out) throws IOException {
/* 481 */       while (length >= 255) {
/* 482 */         out.write(255);
/* 483 */         length -= 255;
/*     */       } 
/* 485 */       out.write(length);
/*     */     }
/*     */     private int backReferenceLength() {
/* 488 */       return this.brLength;
/*     */     }
/*     */     private void prependTo(Pair other) {
/* 491 */       Iterator<byte[]> listBackwards = (Iterator)this.literals.descendingIterator();
/* 492 */       while (listBackwards.hasNext())
/* 493 */         other.prependLiteral(listBackwards.next()); 
/*     */     }
/*     */     
/*     */     private Pair splitWithNewBackReferenceLengthOf(int newBackReferenceLength) {
/* 497 */       Pair p = new Pair();
/* 498 */       p.literals.addAll(this.literals);
/* 499 */       p.brOffset = this.brOffset;
/* 500 */       p.brLength = newBackReferenceLength;
/* 501 */       return p;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lz4\BlockLZ4CompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */