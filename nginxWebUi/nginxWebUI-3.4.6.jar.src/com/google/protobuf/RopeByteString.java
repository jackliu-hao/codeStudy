/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class RopeByteString
/*     */   extends ByteString
/*     */ {
/*  83 */   static final int[] minLengthByDepth = new int[] { 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465, 14930352, 24157817, 39088169, 63245986, 102334155, 165580141, 267914296, 433494437, 701408733, 1134903170, 1836311903, Integer.MAX_VALUE };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int totalLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ByteString left;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ByteString right;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int leftLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int treeDepth;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private RopeByteString(ByteString left, ByteString right) {
/* 147 */     this.left = left;
/* 148 */     this.right = right;
/* 149 */     this.leftLength = left.size();
/* 150 */     this.totalLength = this.leftLength + right.size();
/* 151 */     this.treeDepth = Math.max(left.getTreeDepth(), right.getTreeDepth()) + 1;
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
/*     */   static ByteString concatenate(ByteString left, ByteString right) {
/* 168 */     if (right.size() == 0) {
/* 169 */       return left;
/*     */     }
/*     */     
/* 172 */     if (left.size() == 0) {
/* 173 */       return right;
/*     */     }
/*     */     
/* 176 */     int newLength = left.size() + right.size();
/* 177 */     if (newLength < 128)
/*     */     {
/*     */       
/* 180 */       return concatenateBytes(left, right);
/*     */     }
/*     */     
/* 183 */     if (left instanceof RopeByteString) {
/* 184 */       RopeByteString leftRope = (RopeByteString)left;
/* 185 */       if (leftRope.right.size() + right.size() < 128) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 196 */         ByteString newRight = concatenateBytes(leftRope.right, right);
/* 197 */         return new RopeByteString(leftRope.left, newRight);
/*     */       } 
/*     */       
/* 200 */       if (leftRope.left.getTreeDepth() > leftRope.right.getTreeDepth() && leftRope
/* 201 */         .getTreeDepth() > right.getTreeDepth()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 207 */         ByteString newRight = new RopeByteString(leftRope.right, right);
/* 208 */         return new RopeByteString(leftRope.left, newRight);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 213 */     int newDepth = Math.max(left.getTreeDepth(), right.getTreeDepth()) + 1;
/* 214 */     if (newLength >= minLength(newDepth))
/*     */     {
/* 216 */       return new RopeByteString(left, right);
/*     */     }
/*     */     
/* 219 */     return (new Balancer()).balance(left, right);
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
/*     */   private static ByteString concatenateBytes(ByteString left, ByteString right) {
/* 231 */     int leftSize = left.size();
/* 232 */     int rightSize = right.size();
/* 233 */     byte[] bytes = new byte[leftSize + rightSize];
/* 234 */     left.copyTo(bytes, 0, 0, leftSize);
/* 235 */     right.copyTo(bytes, 0, leftSize, rightSize);
/* 236 */     return ByteString.wrap(bytes);
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
/*     */   static RopeByteString newInstanceForTest(ByteString left, ByteString right) {
/* 250 */     return new RopeByteString(left, right);
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
/*     */   static int minLength(int depth) {
/* 263 */     if (depth >= minLengthByDepth.length) {
/* 264 */       return Integer.MAX_VALUE;
/*     */     }
/* 266 */     return minLengthByDepth[depth];
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
/*     */   public byte byteAt(int index) {
/* 280 */     checkIndex(index, this.totalLength);
/* 281 */     return internalByteAt(index);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   byte internalByteAt(int index) {
/* 287 */     if (index < this.leftLength) {
/* 288 */       return this.left.internalByteAt(index);
/*     */     }
/*     */     
/* 291 */     return this.right.internalByteAt(index - this.leftLength);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 296 */     return this.totalLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteString.ByteIterator iterator() {
/* 301 */     return new ByteString.AbstractByteIterator() {
/* 302 */         final RopeByteString.PieceIterator pieces = new RopeByteString.PieceIterator(RopeByteString.this);
/* 303 */         ByteString.ByteIterator current = nextPiece();
/*     */ 
/*     */ 
/*     */         
/*     */         private ByteString.ByteIterator nextPiece() {
/* 308 */           return this.pieces.hasNext() ? this.pieces.next().iterator() : null;
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean hasNext() {
/* 313 */           return (this.current != null);
/*     */         }
/*     */ 
/*     */         
/*     */         public byte nextByte() {
/* 318 */           if (this.current == null) {
/* 319 */             throw new NoSuchElementException();
/*     */           }
/* 321 */           byte b = this.current.nextByte();
/* 322 */           if (!this.current.hasNext()) {
/* 323 */             this.current = nextPiece();
/*     */           }
/* 325 */           return b;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getTreeDepth() {
/* 335 */     return this.treeDepth;
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
/*     */   protected boolean isBalanced() {
/* 347 */     return (this.totalLength >= minLength(this.treeDepth));
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
/*     */   public ByteString substring(int beginIndex, int endIndex) {
/* 365 */     int length = checkRange(beginIndex, endIndex, this.totalLength);
/*     */     
/* 367 */     if (length == 0)
/*     */     {
/* 369 */       return ByteString.EMPTY;
/*     */     }
/*     */     
/* 372 */     if (length == this.totalLength)
/*     */     {
/* 374 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 378 */     if (endIndex <= this.leftLength)
/*     */     {
/* 380 */       return this.left.substring(beginIndex, endIndex);
/*     */     }
/*     */     
/* 383 */     if (beginIndex >= this.leftLength)
/*     */     {
/* 385 */       return this.right.substring(beginIndex - this.leftLength, endIndex - this.leftLength);
/*     */     }
/*     */ 
/*     */     
/* 389 */     ByteString leftSub = this.left.substring(beginIndex);
/* 390 */     ByteString rightSub = this.right.substring(0, endIndex - this.leftLength);
/*     */ 
/*     */ 
/*     */     
/* 394 */     return new RopeByteString(leftSub, rightSub);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
/* 403 */     if (sourceOffset + numberToCopy <= this.leftLength) {
/* 404 */       this.left.copyToInternal(target, sourceOffset, targetOffset, numberToCopy);
/* 405 */     } else if (sourceOffset >= this.leftLength) {
/* 406 */       this.right.copyToInternal(target, sourceOffset - this.leftLength, targetOffset, numberToCopy);
/*     */     } else {
/* 408 */       int leftLength = this.leftLength - sourceOffset;
/* 409 */       this.left.copyToInternal(target, sourceOffset, targetOffset, leftLength);
/* 410 */       this.right.copyToInternal(target, 0, targetOffset + leftLength, numberToCopy - leftLength);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void copyTo(ByteBuffer target) {
/* 416 */     this.left.copyTo(target);
/* 417 */     this.right.copyTo(target);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer asReadOnlyByteBuffer() {
/* 422 */     ByteBuffer byteBuffer = ByteBuffer.wrap(toByteArray());
/* 423 */     return byteBuffer.asReadOnlyBuffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<ByteBuffer> asReadOnlyByteBufferList() {
/* 430 */     List<ByteBuffer> result = new ArrayList<>();
/* 431 */     PieceIterator pieces = new PieceIterator(this);
/* 432 */     while (pieces.hasNext()) {
/* 433 */       ByteString.LeafByteString byteString = pieces.next();
/* 434 */       result.add(byteString.asReadOnlyByteBuffer());
/*     */     } 
/* 436 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outputStream) throws IOException {
/* 441 */     this.left.writeTo(outputStream);
/* 442 */     this.right.writeTo(outputStream);
/*     */   }
/*     */ 
/*     */   
/*     */   void writeToInternal(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
/* 447 */     if (sourceOffset + numberToWrite <= this.leftLength) {
/* 448 */       this.left.writeToInternal(out, sourceOffset, numberToWrite);
/* 449 */     } else if (sourceOffset >= this.leftLength) {
/* 450 */       this.right.writeToInternal(out, sourceOffset - this.leftLength, numberToWrite);
/*     */     } else {
/* 452 */       int numberToWriteInLeft = this.leftLength - sourceOffset;
/* 453 */       this.left.writeToInternal(out, sourceOffset, numberToWriteInLeft);
/* 454 */       this.right.writeToInternal(out, 0, numberToWrite - numberToWriteInLeft);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void writeTo(ByteOutput output) throws IOException {
/* 460 */     this.left.writeTo(output);
/* 461 */     this.right.writeTo(output);
/*     */   }
/*     */ 
/*     */   
/*     */   void writeToReverse(ByteOutput output) throws IOException {
/* 466 */     this.right.writeToReverse(output);
/* 467 */     this.left.writeToReverse(output);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String toStringInternal(Charset charset) {
/* 472 */     return new String(toByteArray(), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isValidUtf8() {
/* 480 */     int leftPartial = this.left.partialIsValidUtf8(0, 0, this.leftLength);
/* 481 */     int state = this.right.partialIsValidUtf8(leftPartial, 0, this.right.size());
/* 482 */     return (state == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int partialIsValidUtf8(int state, int offset, int length) {
/* 487 */     int toIndex = offset + length;
/* 488 */     if (toIndex <= this.leftLength)
/* 489 */       return this.left.partialIsValidUtf8(state, offset, length); 
/* 490 */     if (offset >= this.leftLength) {
/* 491 */       return this.right.partialIsValidUtf8(state, offset - this.leftLength, length);
/*     */     }
/* 493 */     int leftLength = this.leftLength - offset;
/* 494 */     int leftPartial = this.left.partialIsValidUtf8(state, offset, leftLength);
/* 495 */     return this.right.partialIsValidUtf8(leftPartial, 0, length - leftLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 504 */     if (other == this) {
/* 505 */       return true;
/*     */     }
/* 507 */     if (!(other instanceof ByteString)) {
/* 508 */       return false;
/*     */     }
/*     */     
/* 511 */     ByteString otherByteString = (ByteString)other;
/* 512 */     if (this.totalLength != otherByteString.size()) {
/* 513 */       return false;
/*     */     }
/* 515 */     if (this.totalLength == 0) {
/* 516 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 524 */     int thisHash = peekCachedHashCode();
/* 525 */     int thatHash = otherByteString.peekCachedHashCode();
/* 526 */     if (thisHash != 0 && thatHash != 0 && thisHash != thatHash) {
/* 527 */       return false;
/*     */     }
/*     */     
/* 530 */     return equalsFragments(otherByteString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean equalsFragments(ByteString other) {
/* 541 */     int thisOffset = 0;
/* 542 */     Iterator<ByteString.LeafByteString> thisIter = new PieceIterator(this);
/* 543 */     ByteString.LeafByteString thisString = thisIter.next();
/*     */     
/* 545 */     int thatOffset = 0;
/* 546 */     Iterator<ByteString.LeafByteString> thatIter = new PieceIterator(other);
/* 547 */     ByteString.LeafByteString thatString = thatIter.next();
/*     */     
/* 549 */     int pos = 0;
/*     */     while (true) {
/* 551 */       int thisRemaining = thisString.size() - thisOffset;
/* 552 */       int thatRemaining = thatString.size() - thatOffset;
/* 553 */       int bytesToCompare = Math.min(thisRemaining, thatRemaining);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 559 */       boolean stillEqual = (thisOffset == 0) ? thisString.equalsRange(thatString, thatOffset, bytesToCompare) : thatString.equalsRange(thisString, thisOffset, bytesToCompare);
/* 560 */       if (!stillEqual) {
/* 561 */         return false;
/*     */       }
/*     */       
/* 564 */       pos += bytesToCompare;
/* 565 */       if (pos >= this.totalLength) {
/* 566 */         if (pos == this.totalLength) {
/* 567 */           return true;
/*     */         }
/* 569 */         throw new IllegalStateException();
/*     */       } 
/*     */       
/* 572 */       if (bytesToCompare == thisRemaining) {
/* 573 */         thisOffset = 0;
/* 574 */         thisString = thisIter.next();
/*     */       } else {
/* 576 */         thisOffset += bytesToCompare;
/*     */       } 
/* 578 */       if (bytesToCompare == thatRemaining) {
/* 579 */         thatOffset = 0;
/* 580 */         thatString = thatIter.next(); continue;
/*     */       } 
/* 582 */       thatOffset += bytesToCompare;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int partialHash(int h, int offset, int length) {
/* 589 */     int toIndex = offset + length;
/* 590 */     if (toIndex <= this.leftLength)
/* 591 */       return this.left.partialHash(h, offset, length); 
/* 592 */     if (offset >= this.leftLength) {
/* 593 */       return this.right.partialHash(h, offset - this.leftLength, length);
/*     */     }
/* 595 */     int leftLength = this.leftLength - offset;
/* 596 */     int leftPartial = this.left.partialHash(h, offset, leftLength);
/* 597 */     return this.right.partialHash(leftPartial, 0, length - leftLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodedInputStream newCodedInput() {
/* 606 */     return CodedInputStream.newInstance(new RopeInputStream());
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream newInput() {
/* 611 */     return new RopeInputStream();
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
/*     */   private static class Balancer
/*     */   {
/* 627 */     private final ArrayDeque<ByteString> prefixesStack = new ArrayDeque<>();
/*     */     
/*     */     private ByteString balance(ByteString left, ByteString right) {
/* 630 */       doBalance(left);
/* 631 */       doBalance(right);
/*     */ 
/*     */       
/* 634 */       ByteString partialString = this.prefixesStack.pop();
/* 635 */       while (!this.prefixesStack.isEmpty()) {
/* 636 */         ByteString newLeft = this.prefixesStack.pop();
/* 637 */         partialString = new RopeByteString(newLeft, partialString);
/*     */       } 
/*     */ 
/*     */       
/* 641 */       return partialString;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void doBalance(ByteString root) {
/* 649 */       if (root.isBalanced()) {
/* 650 */         insert(root);
/* 651 */       } else if (root instanceof RopeByteString) {
/* 652 */         RopeByteString rbs = (RopeByteString)root;
/* 653 */         doBalance(rbs.left);
/* 654 */         doBalance(rbs.right);
/*     */       } else {
/* 656 */         throw new IllegalArgumentException("Has a new type of ByteString been created? Found " + root
/* 657 */             .getClass());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void insert(ByteString byteString) {
/* 674 */       int depthBin = getDepthBinForLength(byteString.size());
/* 675 */       int binEnd = RopeByteString.minLength(depthBin + 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 681 */       if (this.prefixesStack.isEmpty() || ((ByteString)this.prefixesStack.peek()).size() >= binEnd) {
/* 682 */         this.prefixesStack.push(byteString);
/*     */       } else {
/* 684 */         int binStart = RopeByteString.minLength(depthBin);
/*     */ 
/*     */         
/* 687 */         ByteString newTree = this.prefixesStack.pop();
/* 688 */         while (!this.prefixesStack.isEmpty() && ((ByteString)this.prefixesStack.peek()).size() < binStart) {
/* 689 */           ByteString left = this.prefixesStack.pop();
/* 690 */           newTree = new RopeByteString(left, newTree);
/*     */         } 
/*     */ 
/*     */         
/* 694 */         newTree = new RopeByteString(newTree, byteString);
/*     */ 
/*     */         
/* 697 */         while (!this.prefixesStack.isEmpty()) {
/* 698 */           depthBin = getDepthBinForLength(newTree.size());
/* 699 */           binEnd = RopeByteString.minLength(depthBin + 1);
/* 700 */           if (((ByteString)this.prefixesStack.peek()).size() < binEnd) {
/* 701 */             ByteString left = this.prefixesStack.pop();
/* 702 */             newTree = new RopeByteString(left, newTree);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 707 */         this.prefixesStack.push(newTree);
/*     */       } 
/*     */     }
/*     */     
/*     */     private int getDepthBinForLength(int length) {
/* 712 */       int depth = Arrays.binarySearch(RopeByteString.minLengthByDepth, length);
/* 713 */       if (depth < 0) {
/*     */ 
/*     */         
/* 716 */         int insertionPoint = -(depth + 1);
/* 717 */         depth = insertionPoint - 1;
/*     */       } 
/*     */       
/* 720 */       return depth;
/*     */     }
/*     */ 
/*     */     
/*     */     private Balancer() {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class PieceIterator
/*     */     implements Iterator<ByteString.LeafByteString>
/*     */   {
/*     */     private final ArrayDeque<RopeByteString> breadCrumbs;
/*     */     
/*     */     private ByteString.LeafByteString next;
/*     */     
/*     */     private PieceIterator(ByteString root) {
/* 736 */       if (root instanceof RopeByteString) {
/* 737 */         RopeByteString rbs = (RopeByteString)root;
/* 738 */         this.breadCrumbs = new ArrayDeque<>(rbs.getTreeDepth());
/* 739 */         this.breadCrumbs.push(rbs);
/* 740 */         this.next = getLeafByLeft(rbs.left);
/*     */       } else {
/* 742 */         this.breadCrumbs = null;
/* 743 */         this.next = (ByteString.LeafByteString)root;
/*     */       } 
/*     */     }
/*     */     
/*     */     private ByteString.LeafByteString getLeafByLeft(ByteString root) {
/* 748 */       ByteString pos = root;
/* 749 */       while (pos instanceof RopeByteString) {
/* 750 */         RopeByteString rbs = (RopeByteString)pos;
/* 751 */         this.breadCrumbs.push(rbs);
/* 752 */         pos = rbs.left;
/*     */       } 
/* 754 */       return (ByteString.LeafByteString)pos;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private ByteString.LeafByteString getNextNonEmptyLeaf() {
/*     */       while (true) {
/* 761 */         if (this.breadCrumbs == null || this.breadCrumbs.isEmpty()) {
/* 762 */           return null;
/*     */         }
/* 764 */         ByteString.LeafByteString result = getLeafByLeft((this.breadCrumbs.pop()).right);
/* 765 */         if (!result.isEmpty()) {
/* 766 */           return result;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 774 */       return (this.next != null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteString.LeafByteString next() {
/* 784 */       if (this.next == null) {
/* 785 */         throw new NoSuchElementException();
/*     */       }
/* 787 */       ByteString.LeafByteString result = this.next;
/* 788 */       this.next = getNextNonEmptyLeaf();
/* 789 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 794 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object writeReplace() {
/* 804 */     return ByteString.wrap(toByteArray());
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream in) throws IOException {
/* 808 */     throw new InvalidObjectException("RopeByteStream instances are not to be serialized directly");
/*     */   }
/*     */ 
/*     */   
/*     */   private class RopeInputStream
/*     */     extends InputStream
/*     */   {
/*     */     private RopeByteString.PieceIterator pieceIterator;
/*     */     
/*     */     private ByteString.LeafByteString currentPiece;
/*     */     
/*     */     private int currentPieceSize;
/*     */     
/*     */     private int currentPieceIndex;
/*     */     
/*     */     private int currentPieceOffsetInRope;
/*     */     private int mark;
/*     */     
/*     */     public RopeInputStream() {
/* 827 */       initialize();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] b, int offset, int length) {
/* 842 */       if (b == null)
/* 843 */         throw new NullPointerException(); 
/* 844 */       if (offset < 0 || length < 0 || length > b.length - offset) {
/* 845 */         throw new IndexOutOfBoundsException();
/*     */       }
/* 847 */       int bytesRead = readSkipInternal(b, offset, length);
/* 848 */       if (bytesRead == 0) {
/* 849 */         return -1;
/*     */       }
/* 851 */       return bytesRead;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long skip(long length) {
/* 857 */       if (length < 0L)
/* 858 */         throw new IndexOutOfBoundsException(); 
/* 859 */       if (length > 2147483647L) {
/* 860 */         length = 2147483647L;
/*     */       }
/* 862 */       return readSkipInternal(null, 0, (int)length);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int readSkipInternal(byte[] b, int offset, int length) {
/* 875 */       int bytesRemaining = length;
/* 876 */       while (bytesRemaining > 0) {
/* 877 */         advanceIfCurrentPieceFullyRead();
/* 878 */         if (this.currentPiece == null) {
/*     */           break;
/*     */         }
/*     */         
/* 882 */         int currentPieceRemaining = this.currentPieceSize - this.currentPieceIndex;
/* 883 */         int count = Math.min(currentPieceRemaining, bytesRemaining);
/* 884 */         if (b != null) {
/* 885 */           this.currentPiece.copyTo(b, this.currentPieceIndex, offset, count);
/* 886 */           offset += count;
/*     */         } 
/* 888 */         this.currentPieceIndex += count;
/* 889 */         bytesRemaining -= count;
/*     */       } 
/*     */ 
/*     */       
/* 893 */       return length - bytesRemaining;
/*     */     }
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/* 898 */       advanceIfCurrentPieceFullyRead();
/* 899 */       if (this.currentPiece == null) {
/* 900 */         return -1;
/*     */       }
/* 902 */       return this.currentPiece.byteAt(this.currentPieceIndex++) & 0xFF;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int available() throws IOException {
/* 908 */       int bytesRead = this.currentPieceOffsetInRope + this.currentPieceIndex;
/* 909 */       return RopeByteString.this.size() - bytesRead;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean markSupported() {
/* 914 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void mark(int readAheadLimit) {
/* 920 */       this.mark = this.currentPieceOffsetInRope + this.currentPieceIndex;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public synchronized void reset() {
/* 926 */       initialize();
/* 927 */       readSkipInternal(null, 0, this.mark);
/*     */     }
/*     */ 
/*     */     
/*     */     private void initialize() {
/* 932 */       this.pieceIterator = new RopeByteString.PieceIterator(RopeByteString.this);
/* 933 */       this.currentPiece = this.pieceIterator.next();
/* 934 */       this.currentPieceSize = this.currentPiece.size();
/* 935 */       this.currentPieceIndex = 0;
/* 936 */       this.currentPieceOffsetInRope = 0;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void advanceIfCurrentPieceFullyRead() {
/* 944 */       if (this.currentPiece != null && this.currentPieceIndex == this.currentPieceSize) {
/*     */ 
/*     */         
/* 947 */         this.currentPieceOffsetInRope += this.currentPieceSize;
/* 948 */         this.currentPieceIndex = 0;
/* 949 */         if (this.pieceIterator.hasNext()) {
/* 950 */           this.currentPiece = this.pieceIterator.next();
/* 951 */           this.currentPieceSize = this.currentPiece.size();
/*     */         } else {
/* 953 */           this.currentPiece = null;
/* 954 */           this.currentPieceSize = 0;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\RopeByteString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */