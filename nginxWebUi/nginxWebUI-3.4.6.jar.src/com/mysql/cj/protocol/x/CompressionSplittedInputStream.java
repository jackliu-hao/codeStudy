/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.InvalidProtocolBufferException;
/*     */ import com.google.protobuf.Parser;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.x.protobuf.Mysqlx;
/*     */ import com.mysql.cj.x.protobuf.MysqlxConnection;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompressionSplittedInputStream
/*     */   extends FilterInputStream
/*     */ {
/*     */   private CompressorStreamsFactory compressorIoStreamsFactory;
/*  56 */   private byte[] frameHeader = new byte[5];
/*  57 */   private int frameHeaderConsumed = 0;
/*  58 */   private int framePayloadLength = 0;
/*  59 */   private int framePayloadConsumed = 0;
/*     */   
/*     */   private XMessageHeader xMessageHeader;
/*  62 */   private InputStream compressorIn = null;
/*     */   
/*  64 */   private byte[] singleByte = new byte[1];
/*     */   
/*     */   private boolean closed = false;
/*     */   
/*     */   public CompressionSplittedInputStream(InputStream in, CompressorStreamsFactory streamsFactory) {
/*  69 */     super(in);
/*  70 */     this.compressorIoStreamsFactory = streamsFactory;
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
/*     */   public int available() throws IOException {
/*  83 */     ensureOpen();
/*  84 */     if (this.compressorIn != null) {
/*  85 */       return this.compressorIn.available();
/*     */     }
/*  87 */     return ((this.frameHeaderConsumed > 0) ? (5 - this.frameHeaderConsumed) : 0) + this.in.available();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  97 */     if (!this.closed) {
/*  98 */       super.close();
/*  99 */       this.in = null;
/* 100 */       if (this.compressorIn != null) {
/* 101 */         this.compressorIn.close();
/*     */       }
/* 103 */       this.compressorIn = null;
/* 104 */       this.closed = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 115 */     ensureOpen();
/* 116 */     int read = read(this.singleByte, 0, 1);
/* 117 */     if (read >= 0) {
/* 118 */       return this.singleByte[0] & 0xFF;
/*     */     }
/* 120 */     return read;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 130 */     ensureOpen();
/* 131 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 142 */     ensureOpen();
/* 143 */     if (len <= 0) {
/* 144 */       return 0;
/*     */     }
/*     */     
/* 147 */     peekNextFrame();
/*     */     
/*     */     try {
/* 150 */       if (isCompressedDataAvailable()) {
/* 151 */         int bytesRead = readFully(this.compressorIn, b, off, len);
/* 152 */         if (isCompressedDataReadComplete()) {
/* 153 */           this.compressorIn.close();
/* 154 */           this.compressorIn = null;
/*     */         } 
/* 156 */         return bytesRead;
/*     */       } 
/* 158 */     } catch (IOException e) {
/* 159 */       throw e;
/*     */     } 
/*     */     
/* 162 */     int headerBytesRead = 0;
/* 163 */     if (!isFrameHeaderFullyConsumed()) {
/* 164 */       int lenToConsume = Math.min(len, 5 - this.frameHeaderConsumed);
/* 165 */       System.arraycopy(this.frameHeader, this.frameHeaderConsumed, b, off, lenToConsume);
/* 166 */       off += lenToConsume;
/* 167 */       len -= lenToConsume;
/* 168 */       this.frameHeaderConsumed += lenToConsume;
/* 169 */       headerBytesRead = lenToConsume;
/*     */     } 
/*     */ 
/*     */     
/* 173 */     int payloadBytesRead = readFully(b, off, len);
/* 174 */     this.framePayloadConsumed += payloadBytesRead;
/*     */     
/* 176 */     return headerBytesRead + payloadBytesRead;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void peekNextFrame() throws IOException {
/* 187 */     if (isDataAvailable()) {
/*     */       return;
/*     */     }
/*     */     
/* 191 */     readFully(this.frameHeader, 0, 5);
/* 192 */     this.xMessageHeader = new XMessageHeader(this.frameHeader);
/* 193 */     this.framePayloadLength = this.xMessageHeader.getMessageSize();
/* 194 */     this.frameHeaderConsumed = 0;
/* 195 */     this.framePayloadConsumed = 0;
/*     */     
/* 197 */     if (isCompressedFrame()) {
/* 198 */       MysqlxConnection.Compression compressedMessage = parseCompressedMessage();
/* 199 */       this
/*     */         
/* 201 */         .compressorIn = new ConfinedInputStream(this.compressorIoStreamsFactory.getInputStreamInstance(new ByteArrayInputStream(compressedMessage.getPayload().toByteArray())), (int)compressedMessage.getUncompressedSize());
/*     */ 
/*     */       
/* 204 */       this.frameHeaderConsumed = 5;
/* 205 */       this.framePayloadConsumed = this.framePayloadLength;
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
/*     */   private boolean isCompressedFrame() {
/* 217 */     return (Mysqlx.ServerMessages.Type.forNumber(this.xMessageHeader.getMessageType()) == Mysqlx.ServerMessages.Type.COMPRESSION);
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
/*     */   private MysqlxConnection.Compression parseCompressedMessage() {
/* 229 */     Parser<MysqlxConnection.Compression> parser = (Parser<MysqlxConnection.Compression>)MessageConstants.MESSAGE_CLASS_TO_PARSER.get(MessageConstants.MESSAGE_TYPE_TO_CLASS.get(Integer.valueOf(19)));
/* 230 */     byte[] packet = new byte[this.xMessageHeader.getMessageSize()];
/*     */     
/*     */     try {
/* 233 */       readFully(packet);
/* 234 */     } catch (IOException e) {
/* 235 */       throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, Messages.getString("Protocol.Compression.Streams.0"), e);
/*     */     } 
/*     */     
/*     */     try {
/* 239 */       return (MysqlxConnection.Compression)parser.parseFrom(packet);
/* 240 */     } catch (InvalidProtocolBufferException e) {
/* 241 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.Streams.1"), e);
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
/*     */   private boolean isDataAvailable() throws IOException {
/* 255 */     return (isCompressedDataAvailable() || (this.frameHeaderConsumed > 0 && this.frameHeaderConsumed < 5) || (
/* 256 */       isFrameHeaderFullyConsumed() && this.framePayloadConsumed < this.framePayloadLength));
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
/*     */   private boolean isCompressedDataAvailable() throws IOException {
/* 269 */     return (this.compressorIn != null && this.compressorIn.available() > 0);
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
/*     */   private boolean isCompressedDataReadComplete() throws IOException {
/* 282 */     return (this.compressorIn != null && this.compressorIn.available() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isFrameHeaderFullyConsumed() {
/* 292 */     return (this.frameHeaderConsumed == 5);
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
/*     */   public int readFully(byte[] b) throws IOException {
/* 305 */     return readFully(b, 0, b.length);
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
/*     */   private final int readFully(byte[] b, int off, int len) throws IOException {
/* 322 */     return readFully(this.in, b, off, len);
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
/*     */   private final int readFully(InputStream inStream, byte[] b, int off, int len) throws IOException {
/* 342 */     if (len < 0) {
/* 343 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 346 */     int total = 0;
/* 347 */     while (total < len) {
/* 348 */       int count = inStream.read(b, off + total, len - total);
/* 349 */       if (count < 0) {
/* 350 */         throw new EOFException();
/*     */       }
/* 352 */       total += count;
/*     */     } 
/* 354 */     return total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureOpen() throws IOException {
/* 364 */     if (this.closed)
/* 365 */       throw new IOException("Stream closed"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\CompressionSplittedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */