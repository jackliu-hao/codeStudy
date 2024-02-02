package org.apache.commons.compress.compressors.deflate64;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.apache.commons.compress.utils.BitInputStream;
import org.apache.commons.compress.utils.ByteUtils;

class HuffmanDecoder implements Closeable {
   private static final short[] RUN_LENGTH_TABLE = new short[]{96, 128, 160, 192, 224, 256, 288, 320, 353, 417, 481, 545, 610, 738, 866, 994, 1123, 1379, 1635, 1891, 2148, 2660, 3172, 3684, 4197, 5221, 6245, 7269, 112};
   private static final int[] DISTANCE_TABLE = new int[]{16, 32, 48, 64, 81, 113, 146, 210, 275, 403, 532, 788, 1045, 1557, 2070, 3094, 4119, 6167, 8216, 12312, 16409, 24601, 32794, 49178, 65563, 98331, 131100, 196636, 262173, 393245, 524318, 786462};
   private static final int[] CODE_LENGTHS_ORDER = new int[]{16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
   private static final int[] FIXED_LITERALS = new int[288];
   private static final int[] FIXED_DISTANCE;
   private boolean finalBlock;
   private DecoderState state;
   private BitInputStream reader;
   private final InputStream in;
   private final DecodingMemory memory = new DecodingMemory();

   HuffmanDecoder(InputStream in) {
      this.reader = new BitInputStream(in, ByteOrder.LITTLE_ENDIAN);
      this.in = in;
      this.state = new InitialState();
   }

   public void close() {
      this.state = new InitialState();
      this.reader = null;
   }

   public int decode(byte[] b) throws IOException {
      return this.decode(b, 0, b.length);
   }

   public int decode(byte[] b, int off, int len) throws IOException {
      while(!this.finalBlock || this.state.hasData()) {
         int r;
         if (this.state.state() == HuffmanState.INITIAL) {
            this.finalBlock = this.readBits(1) == 1L;
            r = (int)this.readBits(2);
            switch (r) {
               case 0:
                  this.switchToUncompressedState();
                  break;
               case 1:
                  this.state = new HuffmanCodes(HuffmanState.FIXED_CODES, FIXED_LITERALS, FIXED_DISTANCE);
                  break;
               case 2:
                  int[][] tables = this.readDynamicTables();
                  this.state = new HuffmanCodes(HuffmanState.DYNAMIC_CODES, tables[0], tables[1]);
                  break;
               default:
                  throw new IllegalStateException("Unsupported compression: " + r);
            }
         } else {
            r = this.state.read(b, off, len);
            if (r != 0) {
               return r;
            }
         }
      }

      return -1;
   }

   long getBytesRead() {
      return this.reader.getBytesRead();
   }

   private void switchToUncompressedState() throws IOException {
      this.reader.alignWithByteBoundary();
      long bLen = this.readBits(16);
      long bNLen = this.readBits(16);
      if (((bLen ^ 65535L) & 65535L) != bNLen) {
         throw new IllegalStateException("Illegal LEN / NLEN values");
      } else {
         this.state = new UncompressedState(bLen);
      }
   }

   private int[][] readDynamicTables() throws IOException {
      int[][] result = new int[2][];
      int literals = (int)(this.readBits(5) + 257L);
      result[0] = new int[literals];
      int distances = (int)(this.readBits(5) + 1L);
      result[1] = new int[distances];
      populateDynamicTables(this.reader, result[0], result[1]);
      return result;
   }

   int available() throws IOException {
      return this.state.available();
   }

   private static int nextSymbol(BitInputStream reader, BinaryTreeNode tree) throws IOException {
      BinaryTreeNode node;
      long bit;
      for(node = tree; node != null && node.literal == -1; node = bit == 0L ? node.leftNode : node.rightNode) {
         bit = readBits(reader, 1);
      }

      return node != null ? node.literal : -1;
   }

   private static void populateDynamicTables(BitInputStream reader, int[] literals, int[] distances) throws IOException {
      int codeLengths = (int)(readBits(reader, 4) + 4L);
      int[] codeLengthValues = new int[19];

      for(int cLen = 0; cLen < codeLengths; ++cLen) {
         codeLengthValues[CODE_LENGTHS_ORDER[cLen]] = (int)readBits(reader, 3);
      }

      BinaryTreeNode codeLengthTree = buildTree(codeLengthValues);
      int[] auxBuffer = new int[literals.length + distances.length];
      int value = -1;
      int length = 0;
      int off = 0;

      while(off < auxBuffer.length) {
         if (length > 0) {
            auxBuffer[off++] = value;
            --length;
         } else {
            int symbol = nextSymbol(reader, codeLengthTree);
            if (symbol < 16) {
               value = symbol;
               auxBuffer[off++] = symbol;
            } else {
               switch (symbol) {
                  case 16:
                     length = (int)(readBits(reader, 2) + 3L);
                     break;
                  case 17:
                     value = 0;
                     length = (int)(readBits(reader, 3) + 3L);
                     break;
                  case 18:
                     value = 0;
                     length = (int)(readBits(reader, 7) + 11L);
               }
            }
         }
      }

      System.arraycopy(auxBuffer, 0, literals, 0, literals.length);
      System.arraycopy(auxBuffer, literals.length, distances, 0, distances.length);
   }

   private static BinaryTreeNode buildTree(int[] litTable) {
      int[] literalCodes = getCodes(litTable);
      BinaryTreeNode root = new BinaryTreeNode(0);

      for(int i = 0; i < litTable.length; ++i) {
         int len = litTable[i];
         if (len != 0) {
            BinaryTreeNode node = root;
            int lit = literalCodes[len - 1];

            for(int p = len - 1; p >= 0; --p) {
               int bit = lit & 1 << p;
               node = bit == 0 ? node.left() : node.right();
               if (node == null) {
                  throw new IllegalStateException("node doesn't exist in Huffman tree");
               }
            }

            node.leaf(i);
            ++literalCodes[len - 1];
         }
      }

      return root;
   }

   private static int[] getCodes(int[] litTable) {
      int max = 0;
      int[] blCount = new int[65];
      int[] var3 = litTable;
      int var4 = litTable.length;

      int i;
      for(i = 0; i < var4; ++i) {
         int aLitTable = var3[i];
         if (aLitTable < 0 || aLitTable > 64) {
            throw new IllegalArgumentException("Invalid code " + aLitTable + " in literal table");
         }

         max = Math.max(max, aLitTable);
         int var10002 = blCount[aLitTable]++;
      }

      blCount = Arrays.copyOf(blCount, max + 1);
      int code = 0;
      int[] nextCode = new int[max + 1];

      for(i = 0; i <= max; ++i) {
         code = code + blCount[i] << 1;
         nextCode[i] = code;
      }

      return nextCode;
   }

   private long readBits(int numBits) throws IOException {
      return readBits(this.reader, numBits);
   }

   private static long readBits(BitInputStream reader, int numBits) throws IOException {
      long r = reader.readBits(numBits);
      if (r == -1L) {
         throw new EOFException("Truncated Deflate64 Stream");
      } else {
         return r;
      }
   }

   static {
      Arrays.fill(FIXED_LITERALS, 0, 144, 8);
      Arrays.fill(FIXED_LITERALS, 144, 256, 9);
      Arrays.fill(FIXED_LITERALS, 256, 280, 7);
      Arrays.fill(FIXED_LITERALS, 280, 288, 8);
      FIXED_DISTANCE = new int[32];
      Arrays.fill(FIXED_DISTANCE, 5);
   }

   private static class DecodingMemory {
      private final byte[] memory;
      private final int mask;
      private int wHead;
      private boolean wrappedAround;

      private DecodingMemory() {
         this(16);
      }

      private DecodingMemory(int bits) {
         this.memory = new byte[1 << bits];
         this.mask = this.memory.length - 1;
      }

      byte add(byte b) {
         this.memory[this.wHead] = b;
         this.wHead = this.incCounter(this.wHead);
         return b;
      }

      void add(byte[] b, int off, int len) {
         for(int i = off; i < off + len; ++i) {
            this.add(b[i]);
         }

      }

      void recordToBuffer(int distance, int length, byte[] buff) {
         if (distance > this.memory.length) {
            throw new IllegalStateException("Illegal distance parameter: " + distance);
         } else {
            int start = this.wHead - distance & this.mask;
            if (!this.wrappedAround && start >= this.wHead) {
               throw new IllegalStateException("Attempt to read beyond memory: dist=" + distance);
            } else {
               int i = 0;

               for(int pos = start; i < length; pos = this.incCounter(pos)) {
                  buff[i] = this.add(this.memory[pos]);
                  ++i;
               }

            }
         }
      }

      private int incCounter(int counter) {
         int newCounter = counter + 1 & this.mask;
         if (!this.wrappedAround && newCounter < counter) {
            this.wrappedAround = true;
         }

         return newCounter;
      }

      // $FF: synthetic method
      DecodingMemory(Object x0) {
         this();
      }
   }

   private static class BinaryTreeNode {
      private final int bits;
      int literal;
      BinaryTreeNode leftNode;
      BinaryTreeNode rightNode;

      private BinaryTreeNode(int bits) {
         this.literal = -1;
         this.bits = bits;
      }

      void leaf(int symbol) {
         this.literal = symbol;
         this.leftNode = null;
         this.rightNode = null;
      }

      BinaryTreeNode left() {
         if (this.leftNode == null && this.literal == -1) {
            this.leftNode = new BinaryTreeNode(this.bits + 1);
         }

         return this.leftNode;
      }

      BinaryTreeNode right() {
         if (this.rightNode == null && this.literal == -1) {
            this.rightNode = new BinaryTreeNode(this.bits + 1);
         }

         return this.rightNode;
      }

      // $FF: synthetic method
      BinaryTreeNode(int x0, Object x1) {
         this(x0);
      }
   }

   private class HuffmanCodes extends DecoderState {
      private boolean endOfBlock;
      private final HuffmanState state;
      private final BinaryTreeNode lengthTree;
      private final BinaryTreeNode distanceTree;
      private int runBufferPos;
      private byte[] runBuffer;
      private int runBufferLength;

      HuffmanCodes(HuffmanState state, int[] lengths, int[] distance) {
         super(null);
         this.runBuffer = ByteUtils.EMPTY_BYTE_ARRAY;
         this.state = state;
         this.lengthTree = HuffmanDecoder.buildTree(lengths);
         this.distanceTree = HuffmanDecoder.buildTree(distance);
      }

      HuffmanState state() {
         return this.endOfBlock ? HuffmanState.INITIAL : this.state;
      }

      int read(byte[] b, int off, int len) throws IOException {
         return len == 0 ? 0 : this.decodeNext(b, off, len);
      }

      private int decodeNext(byte[] b, int off, int len) throws IOException {
         if (this.endOfBlock) {
            return -1;
         } else {
            int result = this.copyFromRunBuffer(b, off, len);

            while(result < len) {
               int symbol = HuffmanDecoder.nextSymbol(HuffmanDecoder.this.reader, this.lengthTree);
               if (symbol < 256) {
                  b[off + result++] = HuffmanDecoder.this.memory.add((byte)symbol);
               } else {
                  if (symbol <= 256) {
                     this.endOfBlock = true;
                     return result;
                  }

                  int runMask = HuffmanDecoder.RUN_LENGTH_TABLE[symbol - 257];
                  int run = runMask >>> 5;
                  int runXtra = runMask & 31;
                  run = (int)((long)run + HuffmanDecoder.this.readBits(runXtra));
                  int distSym = HuffmanDecoder.nextSymbol(HuffmanDecoder.this.reader, this.distanceTree);
                  int distMask = HuffmanDecoder.DISTANCE_TABLE[distSym];
                  int dist = distMask >>> 4;
                  int distXtra = distMask & 15;
                  dist = (int)((long)dist + HuffmanDecoder.this.readBits(distXtra));
                  if (this.runBuffer.length < run) {
                     this.runBuffer = new byte[run];
                  }

                  this.runBufferLength = run;
                  this.runBufferPos = 0;
                  HuffmanDecoder.this.memory.recordToBuffer(dist, run, this.runBuffer);
                  result += this.copyFromRunBuffer(b, off + result, len - result);
               }
            }

            return result;
         }
      }

      private int copyFromRunBuffer(byte[] b, int off, int len) {
         int bytesInBuffer = this.runBufferLength - this.runBufferPos;
         int copiedBytes = 0;
         if (bytesInBuffer > 0) {
            copiedBytes = Math.min(len, bytesInBuffer);
            System.arraycopy(this.runBuffer, this.runBufferPos, b, off, copiedBytes);
            this.runBufferPos += copiedBytes;
         }

         return copiedBytes;
      }

      boolean hasData() {
         return !this.endOfBlock;
      }

      int available() {
         return this.runBufferLength - this.runBufferPos;
      }
   }

   private static class InitialState extends DecoderState {
      private InitialState() {
         super(null);
      }

      HuffmanState state() {
         return HuffmanState.INITIAL;
      }

      int read(byte[] b, int off, int len) throws IOException {
         if (len == 0) {
            return 0;
         } else {
            throw new IllegalStateException("Cannot read in this state");
         }
      }

      boolean hasData() {
         return false;
      }

      int available() {
         return 0;
      }

      // $FF: synthetic method
      InitialState(Object x0) {
         this();
      }
   }

   private class UncompressedState extends DecoderState {
      private final long blockLength;
      private long read;

      private UncompressedState(long blockLength) {
         super(null);
         this.blockLength = blockLength;
      }

      HuffmanState state() {
         return this.read < this.blockLength ? HuffmanState.STORED : HuffmanState.INITIAL;
      }

      int read(byte[] b, int off, int len) throws IOException {
         if (len == 0) {
            return 0;
         } else {
            int max = (int)Math.min(this.blockLength - this.read, (long)len);

            int readNow;
            for(int readSoFar = 0; readSoFar < max; readSoFar += readNow) {
               if (HuffmanDecoder.this.reader.bitsCached() > 0) {
                  byte next = (byte)((int)HuffmanDecoder.this.readBits(8));
                  b[off + readSoFar] = HuffmanDecoder.this.memory.add(next);
                  readNow = 1;
               } else {
                  readNow = HuffmanDecoder.this.in.read(b, off + readSoFar, max - readSoFar);
                  if (readNow == -1) {
                     throw new EOFException("Truncated Deflate64 Stream");
                  }

                  HuffmanDecoder.this.memory.add(b, off + readSoFar, readNow);
               }

               this.read += (long)readNow;
            }

            return max;
         }
      }

      boolean hasData() {
         return this.read < this.blockLength;
      }

      int available() throws IOException {
         return (int)Math.min(this.blockLength - this.read, HuffmanDecoder.this.reader.bitsAvailable() / 8L);
      }

      // $FF: synthetic method
      UncompressedState(long x1, Object x2) {
         this(x1);
      }
   }

   private abstract static class DecoderState {
      private DecoderState() {
      }

      abstract HuffmanState state();

      abstract int read(byte[] var1, int var2, int var3) throws IOException;

      abstract boolean hasData();

      abstract int available() throws IOException;

      // $FF: synthetic method
      DecoderState(Object x0) {
         this();
      }
   }
}
