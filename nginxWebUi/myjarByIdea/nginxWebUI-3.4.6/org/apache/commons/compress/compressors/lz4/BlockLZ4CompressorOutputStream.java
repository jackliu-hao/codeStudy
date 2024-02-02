package org.apache.commons.compress.compressors.lz4;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.utils.ByteUtils;

public class BlockLZ4CompressorOutputStream extends CompressorOutputStream {
   private static final int MIN_BACK_REFERENCE_LENGTH = 4;
   private static final int MIN_OFFSET_OF_LAST_BACK_REFERENCE = 12;
   private final LZ77Compressor compressor;
   private final OutputStream os;
   private final byte[] oneByte;
   private boolean finished;
   private final Deque<Pair> pairs;
   private final Deque<byte[]> expandedBlocks;

   public BlockLZ4CompressorOutputStream(OutputStream os) throws IOException {
      this(os, createParameterBuilder().build());
   }

   public BlockLZ4CompressorOutputStream(OutputStream os, Parameters params) throws IOException {
      this.oneByte = new byte[1];
      this.pairs = new LinkedList();
      this.expandedBlocks = new LinkedList();
      this.os = os;
      this.compressor = new LZ77Compressor(params, (block) -> {
         switch (block.getType()) {
            case LITERAL:
               this.addLiteralBlock((LZ77Compressor.LiteralBlock)block);
               break;
            case BACK_REFERENCE:
               this.addBackReference((LZ77Compressor.BackReference)block);
               break;
            case EOD:
               this.writeFinalLiteralBlock();
         }

      });
   }

   public void write(int b) throws IOException {
      this.oneByte[0] = (byte)(b & 255);
      this.write(this.oneByte);
   }

   public void write(byte[] data, int off, int len) throws IOException {
      this.compressor.compress(data, off, len);
   }

   public void close() throws IOException {
      try {
         this.finish();
      } finally {
         this.os.close();
      }

   }

   public void finish() throws IOException {
      if (!this.finished) {
         this.compressor.finish();
         this.finished = true;
      }

   }

   public void prefill(byte[] data, int off, int len) {
      if (len > 0) {
         byte[] b = Arrays.copyOfRange(data, off, off + len);
         this.compressor.prefill(b);
         this.recordLiteral(b);
      }

   }

   private void addLiteralBlock(LZ77Compressor.LiteralBlock block) throws IOException {
      Pair last = this.writeBlocksAndReturnUnfinishedPair(block.getLength());
      this.recordLiteral(last.addLiteral(block));
      this.clearUnusedBlocksAndPairs();
   }

   private void addBackReference(LZ77Compressor.BackReference block) throws IOException {
      Pair last = this.writeBlocksAndReturnUnfinishedPair(block.getLength());
      last.setBackReference(block);
      this.recordBackReference(block);
      this.clearUnusedBlocksAndPairs();
   }

   private Pair writeBlocksAndReturnUnfinishedPair(int length) throws IOException {
      this.writeWritablePairs(length);
      Pair last = (Pair)this.pairs.peekLast();
      if (last == null || last.hasBackReference()) {
         last = new Pair();
         this.pairs.addLast(last);
      }

      return last;
   }

   private void recordLiteral(byte[] b) {
      this.expandedBlocks.addFirst(b);
   }

   private void clearUnusedBlocksAndPairs() {
      this.clearUnusedBlocks();
      this.clearUnusedPairs();
   }

   private void clearUnusedBlocks() {
      int blockLengths = 0;
      int blocksToKeep = 0;
      Iterator var3 = this.expandedBlocks.iterator();

      while(var3.hasNext()) {
         byte[] b = (byte[])var3.next();
         ++blocksToKeep;
         blockLengths += b.length;
         if (blockLengths >= 65536) {
            break;
         }
      }

      int size = this.expandedBlocks.size();

      for(int i = blocksToKeep; i < size; ++i) {
         this.expandedBlocks.removeLast();
      }

   }

   private void recordBackReference(LZ77Compressor.BackReference block) {
      this.expandedBlocks.addFirst(this.expand(block.getOffset(), block.getLength()));
   }

   private byte[] expand(int offset, int length) {
      byte[] expanded = new byte[length];
      if (offset == 1) {
         byte[] block = (byte[])this.expandedBlocks.peekFirst();
         byte b = block[block.length - 1];
         if (b != 0) {
            Arrays.fill(expanded, b);
         }
      } else {
         this.expandFromList(expanded, offset, length);
      }

      return expanded;
   }

   private void expandFromList(byte[] expanded, int offset, int length) {
      int offsetRemaining = offset;
      int lengthRemaining = length;

      int copyLen;
      for(int writeOffset = 0; lengthRemaining > 0; writeOffset += copyLen) {
         byte[] block = null;
         int copyOffset;
         if (offsetRemaining <= 0) {
            block = expanded;
            copyOffset = -offsetRemaining;
            copyLen = Math.min(lengthRemaining, writeOffset + offsetRemaining);
         } else {
            int blockOffset = 0;

            byte[] b;
            for(Iterator var11 = this.expandedBlocks.iterator(); var11.hasNext(); blockOffset += b.length) {
               b = (byte[])var11.next();
               if (b.length + blockOffset >= offsetRemaining) {
                  block = b;
                  break;
               }
            }

            if (block == null) {
               throw new IllegalStateException("Failed to find a block containing offset " + offset);
            }

            copyOffset = blockOffset + block.length - offsetRemaining;
            copyLen = Math.min(lengthRemaining, block.length - copyOffset);
         }

         System.arraycopy(block, copyOffset, expanded, writeOffset, copyLen);
         offsetRemaining -= copyLen;
         lengthRemaining -= copyLen;
      }

   }

   private void clearUnusedPairs() {
      int pairLengths = 0;
      int pairsToKeep = 0;
      Iterator<Pair> it = this.pairs.descendingIterator();

      while(it.hasNext()) {
         Pair p = (Pair)it.next();
         ++pairsToKeep;
         pairLengths += p.length();
         if (pairLengths >= 65536) {
            break;
         }
      }

      int size = this.pairs.size();

      for(int i = pairsToKeep; i < size; ++i) {
         Pair p = (Pair)this.pairs.peekFirst();
         if (!p.hasBeenWritten()) {
            break;
         }

         this.pairs.removeFirst();
      }

   }

   private void writeFinalLiteralBlock() throws IOException {
      this.rewriteLastPairs();
      Iterator var1 = this.pairs.iterator();

      while(var1.hasNext()) {
         Pair p = (Pair)var1.next();
         if (!p.hasBeenWritten()) {
            p.writeTo(this.os);
         }
      }

      this.pairs.clear();
   }

   private void writeWritablePairs(int lengthOfBlocksAfterLastPair) throws IOException {
      int unwrittenLength = lengthOfBlocksAfterLastPair;

      Iterator it;
      Pair p;
      for(it = this.pairs.descendingIterator(); it.hasNext(); unwrittenLength += p.length()) {
         p = (Pair)it.next();
         if (p.hasBeenWritten()) {
            break;
         }
      }

      it = this.pairs.iterator();

      while(it.hasNext()) {
         p = (Pair)it.next();
         if (!p.hasBeenWritten()) {
            unwrittenLength -= p.length();
            if (!p.canBeWritten(unwrittenLength)) {
               break;
            }

            p.writeTo(this.os);
         }
      }

   }

   private void rewriteLastPairs() {
      LinkedList<Pair> lastPairs = new LinkedList();
      LinkedList<Integer> pairLength = new LinkedList();
      int offset = 0;
      Iterator<Pair> it = this.pairs.descendingIterator();

      Pair p;
      int i;
      while(it.hasNext()) {
         p = (Pair)it.next();
         if (p.hasBeenWritten()) {
            break;
         }

         i = p.length();
         pairLength.addFirst(i);
         lastPairs.addFirst(p);
         offset += i;
         if (offset >= 12) {
            break;
         }
      }

      it = lastPairs.iterator();

      while(it.hasNext()) {
         p = (Pair)it.next();
         this.pairs.remove(p);
      }

      int lastPairsSize = lastPairs.size();
      int toExpand = 0;

      for(i = 1; i < lastPairsSize; ++i) {
         toExpand += (Integer)pairLength.get(i);
      }

      Pair replacement = new Pair();
      if (toExpand > 0) {
         replacement.prependLiteral(this.expand(toExpand, toExpand));
      }

      Pair splitCandidate = (Pair)lastPairs.get(0);
      int stillNeeded = 12 - toExpand;
      int brLen = splitCandidate.hasBackReference() ? splitCandidate.backReferenceLength() : 0;
      if (splitCandidate.hasBackReference() && brLen >= 4 + stillNeeded) {
         replacement.prependLiteral(this.expand(toExpand + stillNeeded, stillNeeded));
         this.pairs.add(splitCandidate.splitWithNewBackReferenceLengthOf(brLen - stillNeeded));
      } else {
         if (splitCandidate.hasBackReference()) {
            replacement.prependLiteral(this.expand(toExpand + brLen, brLen));
         }

         splitCandidate.prependTo(replacement);
      }

      this.pairs.add(replacement);
   }

   public static Parameters.Builder createParameterBuilder() {
      int maxLen = '\uffff';
      return Parameters.builder(65536).withMinBackReferenceLength(4).withMaxBackReferenceLength(65535).withMaxOffset(65535).withMaxLiteralLength(65535);
   }

   static final class Pair {
      private final Deque<byte[]> literals = new LinkedList();
      private int brOffset;
      private int brLength;
      private boolean written;

      private void prependLiteral(byte[] data) {
         this.literals.addFirst(data);
      }

      byte[] addLiteral(LZ77Compressor.LiteralBlock block) {
         byte[] copy = Arrays.copyOfRange(block.getData(), block.getOffset(), block.getOffset() + block.getLength());
         this.literals.add(copy);
         return copy;
      }

      void setBackReference(LZ77Compressor.BackReference block) {
         if (this.hasBackReference()) {
            throw new IllegalStateException();
         } else {
            this.brOffset = block.getOffset();
            this.brLength = block.getLength();
         }
      }

      boolean hasBackReference() {
         return this.brOffset > 0;
      }

      boolean canBeWritten(int lengthOfBlocksAfterThisPair) {
         return this.hasBackReference() && lengthOfBlocksAfterThisPair >= 16;
      }

      int length() {
         return this.literalLength() + this.brLength;
      }

      private boolean hasBeenWritten() {
         return this.written;
      }

      void writeTo(OutputStream out) throws IOException {
         int litLength = this.literalLength();
         out.write(lengths(litLength, this.brLength));
         if (litLength >= 15) {
            writeLength(litLength - 15, out);
         }

         Iterator var3 = this.literals.iterator();

         while(var3.hasNext()) {
            byte[] b = (byte[])var3.next();
            out.write(b);
         }

         if (this.hasBackReference()) {
            ByteUtils.toLittleEndian((OutputStream)out, (long)this.brOffset, 2);
            if (this.brLength - 4 >= 15) {
               writeLength(this.brLength - 4 - 15, out);
            }
         }

         this.written = true;
      }

      private int literalLength() {
         int length = 0;

         byte[] b;
         for(Iterator var2 = this.literals.iterator(); var2.hasNext(); length += b.length) {
            b = (byte[])var2.next();
         }

         return length;
      }

      private static int lengths(int litLength, int brLength) {
         int l = litLength < 15 ? litLength : 15;
         int br = brLength < 4 ? 0 : (brLength < 19 ? brLength - 4 : 15);
         return l << 4 | br;
      }

      private static void writeLength(int length, OutputStream out) throws IOException {
         while(length >= 255) {
            out.write(255);
            length -= 255;
         }

         out.write(length);
      }

      private int backReferenceLength() {
         return this.brLength;
      }

      private void prependTo(Pair other) {
         Iterator<byte[]> listBackwards = this.literals.descendingIterator();

         while(listBackwards.hasNext()) {
            other.prependLiteral((byte[])listBackwards.next());
         }

      }

      private Pair splitWithNewBackReferenceLengthOf(int newBackReferenceLength) {
         Pair p = new Pair();
         p.literals.addAll(this.literals);
         p.brOffset = this.brOffset;
         p.brLength = newBackReferenceLength;
         return p;
      }
   }
}
