/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.ServerVersion;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.UnableToConnectException;
/*     */ import com.mysql.cj.protocol.ServerCapabilities;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NativeCapabilities
/*     */   implements ServerCapabilities
/*     */ {
/*     */   private NativePacketPayload initialHandshakePacket;
/*  45 */   private byte protocolVersion = 0;
/*     */   private ServerVersion serverVersion;
/*  47 */   private long threadId = -1L;
/*     */   private String seed;
/*     */   private int capabilityFlags;
/*     */   private int serverDefaultCollationIndex;
/*  51 */   private int statusFlags = 0;
/*  52 */   private int authPluginDataLength = 0;
/*     */   private boolean serverHasFracSecsSupport = true;
/*     */   
/*     */   public NativeCapabilities(NativePacketPayload initialHandshakePacket) {
/*  56 */     this.initialHandshakePacket = initialHandshakePacket;
/*     */ 
/*     */     
/*  59 */     this.protocolVersion = (byte)(int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
/*     */     
/*     */     try {
/*  62 */       this.serverVersion = ServerVersion.parseVersion(initialHandshakePacket.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII"));
/*     */ 
/*     */       
/*  65 */       this.threadId = initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT4);
/*     */ 
/*     */       
/*  68 */       this.seed = initialHandshakePacket.readString(NativeConstants.StringLengthDataType.STRING_FIXED, "ASCII", 8);
/*     */ 
/*     */       
/*  71 */       initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
/*     */       
/*  73 */       int flags = 0;
/*     */ 
/*     */       
/*  76 */       if (initialHandshakePacket.getPosition() < initialHandshakePacket.getPayloadLength()) {
/*  77 */         flags = (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT2);
/*     */       }
/*     */ 
/*     */       
/*  81 */       this.serverDefaultCollationIndex = (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
/*     */       
/*  83 */       this.statusFlags = (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT2);
/*     */ 
/*     */       
/*  86 */       flags |= (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT2) << 16;
/*     */       
/*  88 */       setCapabilityFlags(flags);
/*     */       
/*  90 */       if ((flags & 0x80000) != 0) {
/*     */         
/*  92 */         this.authPluginDataLength = (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
/*     */       } else {
/*     */         
/*  95 */         initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
/*     */       } 
/*     */       
/*  98 */       initialHandshakePacket.setPosition(initialHandshakePacket.getPosition() + 10);
/*     */       
/* 100 */       this.serverHasFracSecsSupport = this.serverVersion.meetsMinimum(new ServerVersion(5, 6, 4));
/* 101 */     } catch (Throwable t) {
/*     */ 
/*     */       
/* 104 */       if (this.protocolVersion == 11 && IndexOutOfBoundsException.class.isAssignableFrom(t.getClass())) {
/* 105 */         throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, 
/* 106 */             Messages.getString("NativeCapabilites.001", new Object[] { Byte.valueOf(this.protocolVersion) }));
/*     */       }
/*     */       
/* 109 */       throw t;
/*     */     } 
/*     */   }
/*     */   
/*     */   public NativePacketPayload getInitialHandshakePacket() {
/* 114 */     return this.initialHandshakePacket;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCapabilityFlags() {
/* 119 */     return this.capabilityFlags;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCapabilityFlags(int capabilityFlags) {
/* 124 */     this.capabilityFlags = capabilityFlags;
/*     */   }
/*     */   
/*     */   public ServerVersion getServerVersion() {
/* 128 */     return this.serverVersion;
/*     */   }
/*     */   
/*     */   public long getThreadId() {
/* 132 */     return this.threadId;
/*     */   }
/*     */   
/*     */   public void setThreadId(long threadId) {
/* 136 */     this.threadId = threadId;
/*     */   }
/*     */   
/*     */   public String getSeed() {
/* 140 */     return this.seed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getServerDefaultCollationIndex() {
/* 148 */     return this.serverDefaultCollationIndex;
/*     */   }
/*     */   
/*     */   public int getStatusFlags() {
/* 152 */     return this.statusFlags;
/*     */   }
/*     */   
/*     */   public int getAuthPluginDataLength() {
/* 156 */     return this.authPluginDataLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean serverSupportsFracSecs() {
/* 161 */     return this.serverHasFracSecsSupport;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativeCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */