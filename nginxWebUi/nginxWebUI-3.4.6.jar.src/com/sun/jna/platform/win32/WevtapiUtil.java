/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class WevtapiUtil
/*     */ {
/*     */   public static String EvtGetExtendedStatus() {
/*  46 */     IntByReference buffUsed = new IntByReference();
/*  47 */     int errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus(0, null, buffUsed);
/*  48 */     if (errorCode != 0 && errorCode != 122) {
/*  49 */       throw new Win32Exception(errorCode);
/*     */     }
/*  51 */     if (buffUsed.getValue() == 0) {
/*  52 */       return "";
/*     */     }
/*  54 */     char[] mem = new char[buffUsed.getValue()];
/*  55 */     errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus(mem.length, mem, buffUsed);
/*  56 */     if (errorCode != 0) {
/*  57 */       throw new Win32Exception(errorCode);
/*     */     }
/*  59 */     return Native.toString(mem);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Memory EvtRender(Winevt.EVT_HANDLE context, Winevt.EVT_HANDLE fragment, int flags, IntByReference propertyCount) {
/*  86 */     IntByReference buffUsed = new IntByReference();
/*  87 */     boolean result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, 0, null, buffUsed, propertyCount);
/*  88 */     int errorCode = Kernel32.INSTANCE.GetLastError();
/*  89 */     if (!result && errorCode != 122) {
/*  90 */       throw new Win32Exception(errorCode);
/*     */     }
/*  92 */     Memory mem = new Memory(buffUsed.getValue());
/*  93 */     result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, (int)mem.size(), (Pointer)mem, buffUsed, propertyCount);
/*  94 */     if (!result) {
/*  95 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*  97 */     return mem;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String EvtFormatMessage(Winevt.EVT_HANDLE publisherMetadata, Winevt.EVT_HANDLE event, int messageId, int valueCount, Winevt.EVT_VARIANT[] values, int flags) {
/* 141 */     IntByReference bufferUsed = new IntByReference();
/* 142 */     boolean result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, 0, null, bufferUsed);
/* 143 */     int errorCode = Kernel32.INSTANCE.GetLastError();
/* 144 */     if (!result && errorCode != 122) {
/* 145 */       throw new Win32Exception(errorCode);
/*     */     }
/*     */     
/* 148 */     char[] buffer = new char[bufferUsed.getValue()];
/* 149 */     result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, buffer.length, buffer, bufferUsed);
/*     */     
/* 151 */     if (!result) {
/* 152 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 154 */     return Native.toString(buffer);
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
/*     */   public static Winevt.EVT_VARIANT EvtGetChannelConfigProperty(Winevt.EVT_HANDLE channelHandle, int propertyId) {
/* 167 */     IntByReference propertyValueBufferUsed = new IntByReference();
/* 168 */     boolean result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, 0, null, propertyValueBufferUsed);
/* 169 */     int errorCode = Kernel32.INSTANCE.GetLastError();
/* 170 */     if (!result && errorCode != 122) {
/* 171 */       throw new Win32Exception(errorCode);
/*     */     }
/*     */     
/* 174 */     Memory propertyValueBuffer = new Memory(propertyValueBufferUsed.getValue());
/* 175 */     result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, (int)propertyValueBuffer.size(), (Pointer)propertyValueBuffer, propertyValueBufferUsed);
/*     */     
/* 177 */     if (!result) {
/* 178 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 180 */     Winevt.EVT_VARIANT resultEvt = new Winevt.EVT_VARIANT((Pointer)propertyValueBuffer);
/* 181 */     resultEvt.read();
/* 182 */     return resultEvt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String EvtNextPublisherId(Winevt.EVT_HANDLE publisherEnum) {
/* 193 */     IntByReference publisherIdBufferUsed = new IntByReference();
/* 194 */     boolean result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, 0, null, publisherIdBufferUsed);
/* 195 */     int errorCode = Kernel32.INSTANCE.GetLastError();
/* 196 */     if (!result && errorCode != 122) {
/* 197 */       throw new Win32Exception(errorCode);
/*     */     }
/*     */     
/* 200 */     char[] publisherIdBuffer = new char[publisherIdBufferUsed.getValue()];
/* 201 */     result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, publisherIdBuffer.length, publisherIdBuffer, publisherIdBufferUsed);
/* 202 */     if (!result) {
/* 203 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 205 */     return Native.toString(publisherIdBuffer);
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
/*     */   public static Memory EvtGetPublisherMetadataProperty(Winevt.EVT_HANDLE PublisherMetadata, int PropertyId, int Flags) {
/* 220 */     IntByReference publisherMetadataPropertyBufferUsed = new IntByReference();
/* 221 */     boolean result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, 0, null, publisherMetadataPropertyBufferUsed);
/*     */     
/* 223 */     int errorCode = Kernel32.INSTANCE.GetLastError();
/* 224 */     if (!result && errorCode != 122) {
/* 225 */       throw new Win32Exception(errorCode);
/*     */     }
/* 227 */     Memory publisherMetadataPropertyBuffer = new Memory(publisherMetadataPropertyBufferUsed.getValue());
/* 228 */     result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, 
/* 229 */         (int)publisherMetadataPropertyBuffer.size(), (Pointer)publisherMetadataPropertyBuffer, publisherMetadataPropertyBufferUsed);
/* 230 */     if (!result) {
/* 231 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/* 233 */     return publisherMetadataPropertyBuffer;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WevtapiUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */