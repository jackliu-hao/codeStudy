/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio._private.Messages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class FramingMessageSinkConduit
/*     */   extends AbstractSinkConduit<StreamSinkConduit>
/*     */   implements MessageSinkConduit
/*     */ {
/*     */   private final boolean longLengths;
/*     */   private final Pooled<ByteBuffer> transmitBuffer;
/*     */   
/*     */   public FramingMessageSinkConduit(StreamSinkConduit next, boolean longLengths, Pooled<ByteBuffer> transmitBuffer) {
/*  46 */     super(next);
/*  47 */     this.longLengths = longLengths;
/*  48 */     this.transmitBuffer = transmitBuffer;
/*     */   }
/*     */   
/*     */   public boolean send(ByteBuffer src) throws IOException {
/*  52 */     if (!src.hasRemaining())
/*     */     {
/*  54 */       return false;
/*     */     }
/*  56 */     ByteBuffer transmitBuffer = (ByteBuffer)this.transmitBuffer.getResource();
/*  57 */     int remaining = src.remaining();
/*  58 */     boolean longLengths = this.longLengths;
/*  59 */     int lengthFieldSize = longLengths ? 4 : 2;
/*  60 */     if (remaining > transmitBuffer.capacity() - lengthFieldSize || (!longLengths && remaining > 65535)) {
/*  61 */       throw Messages.msg.txMsgTooLarge();
/*     */     }
/*  63 */     if (transmitBuffer.remaining() < lengthFieldSize + remaining && !writeBuffer()) {
/*  64 */       return false;
/*     */     }
/*  66 */     if (longLengths) {
/*  67 */       transmitBuffer.putInt(remaining);
/*     */     } else {
/*  69 */       transmitBuffer.putShort((short)remaining);
/*     */     } 
/*  71 */     transmitBuffer.put(src);
/*  72 */     writeBuffer();
/*  73 */     return true;
/*     */   }
/*     */   
/*     */   public boolean send(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  77 */     if (len == 1)
/*  78 */       return send(srcs[offs]); 
/*  79 */     if (!Buffers.hasRemaining((Buffer[])srcs, offs, len)) {
/*  80 */       return false;
/*     */     }
/*  82 */     ByteBuffer transmitBuffer = (ByteBuffer)this.transmitBuffer.getResource();
/*  83 */     long remaining = Buffers.remaining((Buffer[])srcs, offs, len);
/*  84 */     boolean longLengths = this.longLengths;
/*  85 */     int lengthFieldSize = longLengths ? 4 : 2;
/*  86 */     if (remaining > (transmitBuffer.capacity() - lengthFieldSize) || (!longLengths && remaining > 65535L)) {
/*  87 */       throw Messages.msg.txMsgTooLarge();
/*     */     }
/*  89 */     if (transmitBuffer.remaining() < lengthFieldSize + remaining && !writeBuffer()) {
/*  90 */       return false;
/*     */     }
/*  92 */     if (longLengths) {
/*  93 */       transmitBuffer.putInt((int)remaining);
/*     */     } else {
/*  95 */       transmitBuffer.putShort((short)(int)remaining);
/*     */     } 
/*  97 */     Buffers.copy(transmitBuffer, srcs, offs, len);
/*  98 */     writeBuffer();
/*  99 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer src) throws IOException {
/* 105 */     return Conduits.sendFinalBasic(this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 110 */     return Conduits.sendFinalBasic(this, srcs, offs, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean writeBuffer() throws IOException {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield transmitBuffer : Lorg/xnio/Pooled;
/*     */     //   4: invokeinterface getResource : ()Ljava/lang/Object;
/*     */     //   9: checkcast java/nio/ByteBuffer
/*     */     //   12: astore_1
/*     */     //   13: aload_1
/*     */     //   14: invokevirtual position : ()I
/*     */     //   17: ifle -> 25
/*     */     //   20: aload_1
/*     */     //   21: invokevirtual flip : ()Ljava/nio/Buffer;
/*     */     //   24: pop
/*     */     //   25: aload_1
/*     */     //   26: invokevirtual hasRemaining : ()Z
/*     */     //   29: ifeq -> 62
/*     */     //   32: aload_0
/*     */     //   33: getfield next : Lorg/xnio/conduits/Conduit;
/*     */     //   36: checkcast org/xnio/conduits/StreamSinkConduit
/*     */     //   39: aload_1
/*     */     //   40: invokeinterface write : (Ljava/nio/ByteBuffer;)I
/*     */     //   45: istore_2
/*     */     //   46: iload_2
/*     */     //   47: ifne -> 59
/*     */     //   50: iconst_0
/*     */     //   51: istore_3
/*     */     //   52: aload_1
/*     */     //   53: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*     */     //   56: pop
/*     */     //   57: iload_3
/*     */     //   58: ireturn
/*     */     //   59: goto -> 25
/*     */     //   62: iconst_1
/*     */     //   63: istore_2
/*     */     //   64: aload_1
/*     */     //   65: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*     */     //   68: pop
/*     */     //   69: iload_2
/*     */     //   70: ireturn
/*     */     //   71: astore #4
/*     */     //   73: aload_1
/*     */     //   74: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*     */     //   77: pop
/*     */     //   78: aload #4
/*     */     //   80: athrow
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #114	-> 0
/*     */     //   #115	-> 13
/*     */     //   #117	-> 25
/*     */     //   #118	-> 32
/*     */     //   #119	-> 46
/*     */     //   #120	-> 50
/*     */     //   #125	-> 52
/*     */     //   #120	-> 57
/*     */     //   #122	-> 59
/*     */     //   #123	-> 62
/*     */     //   #125	-> 64
/*     */     //   #123	-> 69
/*     */     //   #125	-> 71
/*     */     //   #126	-> 78
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   46	13	2	res	I
/*     */     //   0	81	0	this	Lorg/xnio/conduits/FramingMessageSinkConduit;
/*     */     //   13	68	1	buffer	Ljava/nio/ByteBuffer;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   25	52	71	finally
/*     */     //   59	64	71	finally
/*     */     //   71	73	71	finally
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 130 */     return (writeBuffer() && this.next.flush());
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 134 */     this.transmitBuffer.free();
/* 135 */     this.next.terminateWrites();
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 139 */     this.transmitBuffer.free();
/* 140 */     this.next.truncateWrites();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\FramingMessageSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */