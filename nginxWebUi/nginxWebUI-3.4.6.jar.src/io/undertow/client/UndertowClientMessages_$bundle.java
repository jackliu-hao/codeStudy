/*     */ package io.undertow.client;
/*     */ 
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.net.URI;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UndertowClientMessages_$bundle
/*     */   implements UndertowClientMessages, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  21 */   public static final UndertowClientMessages_$bundle INSTANCE = new UndertowClientMessages_$bundle();
/*     */   protected Object readResolve() {
/*  23 */     return INSTANCE;
/*     */   }
/*  25 */   private static final Locale LOCALE = Locale.ROOT;
/*     */   protected Locale getLoggingLocale() {
/*  27 */     return LOCALE;
/*     */   }
/*     */   protected String connectionClosed$str() {
/*  30 */     return "UT001000: Connection closed";
/*     */   }
/*     */   
/*     */   public final String connectionClosed() {
/*  34 */     return String.format(getLoggingLocale(), connectionClosed$str(), new Object[0]);
/*     */   }
/*     */   protected String requestAlreadyWritten$str() {
/*  37 */     return "UT001001: Request already written";
/*     */   }
/*     */   
/*     */   public final IllegalStateException requestAlreadyWritten() {
/*  41 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), requestAlreadyWritten$str(), new Object[0]));
/*  42 */     _copyStackTraceMinusOne(result);
/*  43 */     return result;
/*     */   }
/*     */   private static void _copyStackTraceMinusOne(Throwable e) {
/*  46 */     StackTraceElement[] st = e.getStackTrace();
/*  47 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*     */   }
/*     */   protected String failedToUpgradeChannel$str() {
/*  50 */     return "UT001020: Failed to upgrade channel due to response %s (%s)";
/*     */   }
/*     */   
/*     */   public final String failedToUpgradeChannel(int responseCode, String reason) {
/*  54 */     return String.format(getLoggingLocale(), failedToUpgradeChannel$str(), new Object[] { Integer.valueOf(responseCode), reason });
/*     */   }
/*     */   protected String illegalContentLength$str() {
/*  57 */     return "UT001030: invalid content length %d";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException illegalContentLength(long length) {
/*  61 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), illegalContentLength$str(), new Object[] { Long.valueOf(length) }));
/*  62 */     _copyStackTraceMinusOne(result);
/*  63 */     return result;
/*     */   }
/*     */   protected String unknownScheme$str() {
/*  66 */     return "UT001031: Unknown scheme in URI %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException unknownScheme(URI uri) {
/*  70 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), unknownScheme$str(), new Object[] { uri }));
/*  71 */     _copyStackTraceMinusOne(result);
/*  72 */     return result;
/*     */   }
/*     */   protected String unknownTransferEncoding$str() {
/*  75 */     return "UT001032: Unknown transfer encoding %s";
/*     */   }
/*     */   
/*     */   public final IOException unknownTransferEncoding(String transferEncodingString) {
/*  79 */     IOException result = new IOException(String.format(getLoggingLocale(), unknownTransferEncoding$str(), new Object[] { transferEncodingString }));
/*  80 */     _copyStackTraceMinusOne(result);
/*  81 */     return result;
/*     */   }
/*     */   protected String invalidConnectionState$str() {
/*  84 */     return "UT001033: Invalid connection state";
/*     */   }
/*     */   
/*     */   public final IOException invalidConnectionState() {
/*  88 */     IOException result = new IOException(String.format(getLoggingLocale(), invalidConnectionState$str(), new Object[0]));
/*  89 */     _copyStackTraceMinusOne(result);
/*  90 */     return result;
/*     */   }
/*     */   protected String unknownAjpMessageType$str() {
/*  93 */     return "UT001034: Unknown AJP packet type %s";
/*     */   }
/*     */   
/*     */   public final IOException unknownAjpMessageType(byte packetType) {
/*  97 */     IOException result = new IOException(String.format(getLoggingLocale(), unknownAjpMessageType$str(), new Object[] { Byte.valueOf(packetType) }));
/*  98 */     _copyStackTraceMinusOne(result);
/*  99 */     return result;
/*     */   }
/*     */   protected String unknownMethod$str() {
/* 102 */     return "UT001035: Unknown method type for AJP request %s";
/*     */   }
/*     */   
/*     */   public final IOException unknownMethod(HttpString method) {
/* 106 */     IOException result = new IOException(String.format(getLoggingLocale(), unknownMethod$str(), new Object[] { method }));
/* 107 */     _copyStackTraceMinusOne(result);
/* 108 */     return result;
/*     */   }
/*     */   protected String dataStillRemainingInChunk$str() {
/* 111 */     return "UT001036: Data still remaining in chunk %s";
/*     */   }
/*     */   
/*     */   public final IOException dataStillRemainingInChunk(long remaining) {
/* 115 */     IOException result = new IOException(String.format(getLoggingLocale(), dataStillRemainingInChunk$str(), new Object[] { Long.valueOf(remaining) }));
/* 116 */     _copyStackTraceMinusOne(result);
/* 117 */     return result;
/*     */   }
/*     */   protected String wrongMagicNumber$str() {
/* 120 */     return "UT001037: Wrong magic number, expected %s, actual %s";
/*     */   }
/*     */   
/*     */   public final IOException wrongMagicNumber(String expected, String actual) {
/* 124 */     IOException result = new IOException(String.format(getLoggingLocale(), wrongMagicNumber$str(), new Object[] { expected, actual }));
/* 125 */     _copyStackTraceMinusOne(result);
/* 126 */     return result;
/*     */   }
/*     */   protected String receivedInvalidChunk$str() {
/* 129 */     return "UT001038: Received invalid AJP chunk %s with response already complete";
/*     */   }
/*     */   
/*     */   public final IOException receivedInvalidChunk(byte prefix) {
/* 133 */     IOException result = new IOException(String.format(getLoggingLocale(), receivedInvalidChunk$str(), new Object[] { Byte.valueOf(prefix) }));
/* 134 */     _copyStackTraceMinusOne(result);
/* 135 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\UndertowClientMessages_$bundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */