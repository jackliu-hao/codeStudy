/*     */ package com.mysql.cj.exceptions;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.protocol.PacketReceivedTimeHolder;
/*     */ import com.mysql.cj.protocol.PacketSentTimeHolder;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExceptionFactory
/*     */ {
/*     */   private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 28800L;
/*     */   private static final int DUE_TO_TIMEOUT_FALSE = 0;
/*     */   private static final int DUE_TO_TIMEOUT_MAYBE = 2;
/*     */   private static final int DUE_TO_TIMEOUT_TRUE = 1;
/*     */   
/*     */   public static CJException createException(String message) {
/*  53 */     return createException(CJException.class, message);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends CJException> T createException(Class<T> clazz, String message) {
/*     */     CJException cJException;
/*     */     try {
/*  61 */       cJException = clazz.getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
/*  62 */     } catch (Throwable e) {
/*  63 */       cJException = new CJException(message);
/*     */     } 
/*  65 */     return (T)cJException;
/*     */   }
/*     */   
/*     */   public static CJException createException(String message, ExceptionInterceptor interceptor) {
/*  69 */     return createException(CJException.class, message, interceptor);
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
/*     */   public static <T extends CJException> T createException(Class<T> clazz, String message, ExceptionInterceptor interceptor) {
/*  85 */     T sqlEx = createException(clazz, message);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     return sqlEx;
/*     */   }
/*     */   
/*     */   public static CJException createException(String message, Throwable cause) {
/* 100 */     return createException(CJException.class, message, cause);
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T extends CJException> T createException(Class<T> clazz, String message, Throwable cause) {
/* 105 */     T sqlEx = createException(clazz, message);
/*     */     
/* 107 */     if (cause != null) {
/*     */       try {
/* 109 */         sqlEx.initCause(cause);
/* 110 */       } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */       
/* 114 */       if (cause instanceof CJException) {
/* 115 */         sqlEx.setSQLState(((CJException)cause).getSQLState());
/* 116 */         sqlEx.setVendorCode(((CJException)cause).getVendorCode());
/* 117 */         sqlEx.setTransient(((CJException)cause).isTransient());
/*     */       } 
/*     */     } 
/* 120 */     return sqlEx;
/*     */   }
/*     */   
/*     */   public static CJException createException(String message, Throwable cause, ExceptionInterceptor interceptor) {
/* 124 */     return createException(CJException.class, message, cause, interceptor);
/*     */   }
/*     */ 
/*     */   
/*     */   public static CJException createException(String message, String sqlState, int vendorErrorCode, boolean isTransient, Throwable cause, ExceptionInterceptor interceptor) {
/* 129 */     CJException ex = createException(CJException.class, message, cause, interceptor);
/* 130 */     ex.setSQLState(sqlState);
/* 131 */     ex.setVendorCode(vendorErrorCode);
/* 132 */     ex.setTransient(isTransient);
/* 133 */     return ex;
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
/*     */   public static <T extends CJException> T createException(Class<T> clazz, String message, Throwable cause, ExceptionInterceptor interceptor) {
/* 151 */     T sqlEx = createException(clazz, message, cause);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     return sqlEx;
/*     */   }
/*     */ 
/*     */   
/*     */   public static CJCommunicationsException createCommunicationsException(PropertySet propertySet, ServerSession serverSession, PacketSentTimeHolder packetSentTimeHolder, PacketReceivedTimeHolder packetReceivedTimeHolder, Throwable cause, ExceptionInterceptor interceptor) {
/* 167 */     CJCommunicationsException sqlEx = createException(CJCommunicationsException.class, null, cause, interceptor);
/* 168 */     sqlEx.init(propertySet, serverSession, packetSentTimeHolder, packetReceivedTimeHolder);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 179 */     return sqlEx;
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
/*     */   public static String createLinkFailureMessageBasedOnHeuristics(PropertySet propertySet, ServerSession serverSession, PacketSentTimeHolder packetSentTimeHolder, PacketReceivedTimeHolder packetReceivedTimeHolder, Throwable underlyingException) {
/* 200 */     long serverTimeoutSeconds = 0L;
/* 201 */     boolean isInteractiveClient = false;
/*     */     
/* 203 */     long lastPacketReceivedTimeMs = (packetReceivedTimeHolder == null) ? 0L : packetReceivedTimeHolder.getLastPacketReceivedTime();
/* 204 */     long lastPacketSentTimeMs = packetSentTimeHolder.getLastPacketSentTime();
/* 205 */     if (lastPacketSentTimeMs > lastPacketReceivedTimeMs) {
/* 206 */       lastPacketSentTimeMs = packetSentTimeHolder.getPreviousPacketSentTime();
/*     */     }
/*     */     
/* 209 */     if (propertySet != null) {
/* 210 */       isInteractiveClient = ((Boolean)propertySet.getBooleanProperty(PropertyKey.interactiveClient).getValue()).booleanValue();
/*     */       
/* 212 */       String serverTimeoutSecondsStr = null;
/*     */       
/* 214 */       if (serverSession != null)
/*     */       {
/* 216 */         serverTimeoutSecondsStr = isInteractiveClient ? serverSession.getServerVariable("interactive_timeout") : serverSession.getServerVariable("wait_timeout");
/*     */       }
/*     */       
/* 219 */       if (serverTimeoutSecondsStr != null) {
/*     */         try {
/* 221 */           serverTimeoutSeconds = Long.parseLong(serverTimeoutSecondsStr);
/* 222 */         } catch (NumberFormatException nfe) {
/* 223 */           serverTimeoutSeconds = 0L;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 228 */     StringBuilder exceptionMessageBuf = new StringBuilder();
/*     */     
/* 230 */     long nowMs = System.currentTimeMillis();
/*     */     
/* 232 */     if (lastPacketSentTimeMs == 0L) {
/* 233 */       lastPacketSentTimeMs = nowMs;
/*     */     }
/*     */     
/* 236 */     long timeSinceLastPacketSentMs = nowMs - lastPacketSentTimeMs;
/* 237 */     long timeSinceLastPacketSeconds = timeSinceLastPacketSentMs / 1000L;
/*     */     
/* 239 */     long timeSinceLastPacketReceivedMs = nowMs - lastPacketReceivedTimeMs;
/*     */     
/* 241 */     int dueToTimeout = 0;
/*     */     
/* 243 */     StringBuilder timeoutMessageBuf = null;
/*     */     
/* 245 */     if (serverTimeoutSeconds != 0L) {
/* 246 */       if (timeSinceLastPacketSeconds > serverTimeoutSeconds) {
/* 247 */         dueToTimeout = 1;
/*     */         
/* 249 */         timeoutMessageBuf = new StringBuilder();
/* 250 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.2"));
/* 251 */         timeoutMessageBuf.append(Messages.getString(isInteractiveClient ? "CommunicationsException.4" : "CommunicationsException.3"));
/*     */       }
/*     */     
/* 254 */     } else if (timeSinceLastPacketSeconds > 28800L) {
/* 255 */       dueToTimeout = 2;
/*     */       
/* 257 */       timeoutMessageBuf = new StringBuilder();
/* 258 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.5"));
/* 259 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.6"));
/* 260 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.7"));
/* 261 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.8"));
/*     */     } 
/*     */     
/* 264 */     if (dueToTimeout == 1 || dueToTimeout == 2) {
/*     */       
/* 266 */       exceptionMessageBuf.append((lastPacketReceivedTimeMs != 0L) ? 
/* 267 */           Messages.getString("CommunicationsException.ServerPacketTimingInfo", new Object[] {
/* 268 */               Long.valueOf(timeSinceLastPacketReceivedMs), Long.valueOf(timeSinceLastPacketSentMs)
/* 269 */             }) : Messages.getString("CommunicationsException.ServerPacketTimingInfoNoRecv", new Object[] { Long.valueOf(timeSinceLastPacketSentMs) }));
/*     */       
/* 271 */       if (timeoutMessageBuf != null) {
/* 272 */         exceptionMessageBuf.append(timeoutMessageBuf);
/*     */       }
/*     */       
/* 275 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.11"));
/* 276 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.12"));
/* 277 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.13"));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 283 */     else if (underlyingException instanceof java.net.BindException) {
/* 284 */       String localSocketAddress = (String)propertySet.getStringProperty(PropertyKey.localSocketAddress).getValue();
/* 285 */       exceptionMessageBuf.append((localSocketAddress != null && !Util.interfaceExists(localSocketAddress)) ? 
/* 286 */           Messages.getString("CommunicationsException.LocalSocketAddressNotAvailable") : 
/* 287 */           Messages.getString("CommunicationsException.TooManyClientConnections"));
/*     */     } 
/*     */ 
/*     */     
/* 291 */     if (exceptionMessageBuf.length() == 0) {
/*     */       
/* 293 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.20"));
/*     */       
/* 295 */       if (((Boolean)propertySet.getBooleanProperty(PropertyKey.maintainTimeStats).getValue()).booleanValue() && !((Boolean)propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue()) {
/* 296 */         exceptionMessageBuf.append("\n\n");
/* 297 */         exceptionMessageBuf.append((lastPacketReceivedTimeMs != 0L) ? 
/* 298 */             Messages.getString("CommunicationsException.ServerPacketTimingInfo", new Object[] {
/* 299 */                 Long.valueOf(timeSinceLastPacketReceivedMs), Long.valueOf(timeSinceLastPacketSentMs)
/* 300 */               }) : Messages.getString("CommunicationsException.ServerPacketTimingInfoNoRecv", new Object[] { Long.valueOf(timeSinceLastPacketSentMs) }));
/*     */       } 
/*     */     } 
/*     */     
/* 304 */     return exceptionMessageBuf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\ExceptionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */