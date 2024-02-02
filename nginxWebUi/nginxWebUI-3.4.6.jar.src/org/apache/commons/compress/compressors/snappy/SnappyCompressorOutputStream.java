/*     */ package org.apache.commons.compress.compressors.snappy;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ public class SnappyCompressorOutputStream
/*     */   extends CompressorOutputStream
/*     */ {
/*     */   private final LZ77Compressor compressor;
/*     */   private final OutputStream os;
/*     */   private final ByteUtils.ByteConsumer consumer;
/*  60 */   private final byte[] oneByte = new byte[1];
/*     */   private boolean finished;
/*     */   private static final int MAX_LITERAL_SIZE_WITHOUT_SIZE_BYTES = 60;
/*     */   private static final int MAX_LITERAL_SIZE_WITH_ONE_SIZE_BYTE = 256;
/*     */   private static final int MAX_LITERAL_SIZE_WITH_TWO_SIZE_BYTES = 65536;
/*     */   private static final int MAX_LITERAL_SIZE_WITH_THREE_SIZE_BYTES = 16777216;
/*     */   private static final int ONE_SIZE_BYTE_MARKER = 240;
/*     */   private static final int TWO_SIZE_BYTE_MARKER = 244;
/*     */   private static final int THREE_SIZE_BYTE_MARKER = 248;
/*     */   private static final int FOUR_SIZE_BYTE_MARKER = 252;
/*     */   
/*     */   public SnappyCompressorOutputStream(OutputStream os, long uncompressedSize) throws IOException {
/*  72 */     this(os, uncompressedSize, 32768);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final int MIN_MATCH_LENGTH_WITH_ONE_OFFSET_BYTE = 4;
/*     */   
/*     */   private static final int MAX_MATCH_LENGTH_WITH_ONE_OFFSET_BYTE = 11;
/*     */   
/*     */   private static final int MAX_OFFSET_WITH_ONE_OFFSET_BYTE = 1024;
/*     */   
/*     */   private static final int MAX_OFFSET_WITH_TWO_OFFSET_BYTES = 32768;
/*     */   
/*     */   public SnappyCompressorOutputStream(OutputStream os, long uncompressedSize, int blockSize) throws IOException {
/*  85 */     this(os, uncompressedSize, createParameterBuilder(blockSize).build());
/*     */   }
/*     */ 
/*     */   
/*     */   private static final int ONE_BYTE_COPY_TAG = 1;
/*     */   
/*     */   private static final int TWO_BYTE_COPY_TAG = 2;
/*     */   
/*     */   private static final int FOUR_BYTE_COPY_TAG = 3;
/*     */   
/*     */   private static final int MIN_MATCH_LENGTH = 4;
/*     */   
/*     */   private static final int MAX_MATCH_LENGTH = 64;
/*     */   
/*     */   public SnappyCompressorOutputStream(OutputStream os, long uncompressedSize, Parameters params) throws IOException {
/* 100 */     this.os = os;
/* 101 */     this.consumer = (ByteUtils.ByteConsumer)new ByteUtils.OutputStreamByteConsumer(os);
/* 102 */     this.compressor = new LZ77Compressor(params, block -> {
/*     */           switch (block.getType()) {
/*     */             case LITERAL:
/*     */               writeLiteralBlock((LZ77Compressor.LiteralBlock)block);
/*     */               break;
/*     */             
/*     */             case BACK_REFERENCE:
/*     */               writeBackReference((LZ77Compressor.BackReference)block);
/*     */               break;
/*     */           } 
/*     */         
/*     */         });
/* 114 */     writeUncompressedSize(uncompressedSize);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 119 */     this.oneByte[0] = (byte)(b & 0xFF);
/* 120 */     write(this.oneByte);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] data, int off, int len) throws IOException {
/* 125 */     this.compressor.compress(data, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 131 */       finish();
/*     */     } finally {
/* 133 */       this.os.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finish() throws IOException {
/* 143 */     if (!this.finished) {
/* 144 */       this.compressor.finish();
/* 145 */       this.finished = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeUncompressedSize(long uncompressedSize) throws IOException {
/* 150 */     boolean more = false;
/*     */     do {
/* 152 */       int currentByte = (int)(uncompressedSize & 0x7FL);
/* 153 */       more = (uncompressedSize > currentByte);
/* 154 */       if (more) {
/* 155 */         currentByte |= 0x80;
/*     */       }
/* 157 */       this.os.write(currentByte);
/* 158 */       uncompressedSize >>= 7L;
/* 159 */     } while (more);
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
/*     */   private void writeLiteralBlock(LZ77Compressor.LiteralBlock block) throws IOException {
/* 175 */     int len = block.getLength();
/* 176 */     if (len <= 60) {
/* 177 */       writeLiteralBlockNoSizeBytes(block, len);
/* 178 */     } else if (len <= 256) {
/* 179 */       writeLiteralBlockOneSizeByte(block, len);
/* 180 */     } else if (len <= 65536) {
/* 181 */       writeLiteralBlockTwoSizeBytes(block, len);
/* 182 */     } else if (len <= 16777216) {
/* 183 */       writeLiteralBlockThreeSizeBytes(block, len);
/*     */     } else {
/* 185 */       writeLiteralBlockFourSizeBytes(block, len);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeLiteralBlockNoSizeBytes(LZ77Compressor.LiteralBlock block, int len) throws IOException {
/* 190 */     writeLiteralBlockWithSize(len - 1 << 2, 0, len, block);
/*     */   }
/*     */   
/*     */   private void writeLiteralBlockOneSizeByte(LZ77Compressor.LiteralBlock block, int len) throws IOException {
/* 194 */     writeLiteralBlockWithSize(240, 1, len, block);
/*     */   }
/*     */   
/*     */   private void writeLiteralBlockTwoSizeBytes(LZ77Compressor.LiteralBlock block, int len) throws IOException {
/* 198 */     writeLiteralBlockWithSize(244, 2, len, block);
/*     */   }
/*     */   
/*     */   private void writeLiteralBlockThreeSizeBytes(LZ77Compressor.LiteralBlock block, int len) throws IOException {
/* 202 */     writeLiteralBlockWithSize(248, 3, len, block);
/*     */   }
/*     */   
/*     */   private void writeLiteralBlockFourSizeBytes(LZ77Compressor.LiteralBlock block, int len) throws IOException {
/* 206 */     writeLiteralBlockWithSize(252, 4, len, block);
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeLiteralBlockWithSize(int tagByte, int sizeBytes, int len, LZ77Compressor.LiteralBlock block) throws IOException {
/* 211 */     this.os.write(tagByte);
/* 212 */     writeLittleEndian(sizeBytes, len - 1);
/* 213 */     this.os.write(block.getData(), block.getOffset(), len);
/*     */   }
/*     */   
/*     */   private void writeLittleEndian(int numBytes, int num) throws IOException {
/* 217 */     ByteUtils.toLittleEndian(this.consumer, num, numBytes);
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
/*     */   private void writeBackReference(LZ77Compressor.BackReference block) throws IOException {
/* 232 */     int len = block.getLength();
/* 233 */     int offset = block.getOffset();
/* 234 */     if (len >= 4 && len <= 11 && offset <= 1024) {
/*     */       
/* 236 */       writeBackReferenceWithOneOffsetByte(len, offset);
/* 237 */     } else if (offset < 32768) {
/* 238 */       writeBackReferenceWithTwoOffsetBytes(len, offset);
/*     */     } else {
/* 240 */       writeBackReferenceWithFourOffsetBytes(len, offset);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeBackReferenceWithOneOffsetByte(int len, int offset) throws IOException {
/* 245 */     this.os.write(0x1 | len - 4 << 2 | (offset & 0x700) >> 3);
/* 246 */     this.os.write(offset & 0xFF);
/*     */   }
/*     */   
/*     */   private void writeBackReferenceWithTwoOffsetBytes(int len, int offset) throws IOException {
/* 250 */     writeBackReferenceWithLittleEndianOffset(2, 2, len, offset);
/*     */   }
/*     */   
/*     */   private void writeBackReferenceWithFourOffsetBytes(int len, int offset) throws IOException {
/* 254 */     writeBackReferenceWithLittleEndianOffset(3, 4, len, offset);
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeBackReferenceWithLittleEndianOffset(int tag, int offsetBytes, int len, int offset) throws IOException {
/* 259 */     this.os.write(tag | len - 1 << 2);
/* 260 */     writeLittleEndian(offsetBytes, offset);
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
/*     */   public static Parameters.Builder createParameterBuilder(int blockSize) {
/* 279 */     return Parameters.builder(blockSize)
/* 280 */       .withMinBackReferenceLength(4)
/* 281 */       .withMaxBackReferenceLength(64)
/* 282 */       .withMaxOffset(blockSize)
/* 283 */       .withMaxLiteralLength(blockSize);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\snappy\SnappyCompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */