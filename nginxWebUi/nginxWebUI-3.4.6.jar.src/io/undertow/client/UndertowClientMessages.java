/*    */ package io.undertow.client;
/*    */ 
/*    */ import io.undertow.util.HttpString;
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
/*    */ import org.jboss.logging.Messages;
/*    */ import org.jboss.logging.annotations.Message;
/*    */ import org.jboss.logging.annotations.MessageBundle;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @MessageBundle(projectCode = "UT")
/*    */ public interface UndertowClientMessages
/*    */ {
/* 37 */   public static final UndertowClientMessages MESSAGES = (UndertowClientMessages)Messages.getBundle(UndertowClientMessages.class);
/*    */   
/*    */   @Message(id = 1000, value = "Connection closed")
/*    */   String connectionClosed();
/*    */   
/*    */   @Message(id = 1001, value = "Request already written")
/*    */   IllegalStateException requestAlreadyWritten();
/*    */   
/*    */   @Message(id = 1020, value = "Failed to upgrade channel due to response %s (%s)")
/*    */   String failedToUpgradeChannel(int paramInt, String paramString);
/*    */   
/*    */   @Message(id = 1030, value = "invalid content length %d")
/*    */   IllegalArgumentException illegalContentLength(long paramLong);
/*    */   
/*    */   @Message(id = 1031, value = "Unknown scheme in URI %s")
/*    */   IllegalArgumentException unknownScheme(URI paramURI);
/*    */   
/*    */   @Message(id = 1032, value = "Unknown transfer encoding %s")
/*    */   IOException unknownTransferEncoding(String paramString);
/*    */   
/*    */   @Message(id = 1033, value = "Invalid connection state")
/*    */   IOException invalidConnectionState();
/*    */   
/*    */   @Message(id = 1034, value = "Unknown AJP packet type %s")
/*    */   IOException unknownAjpMessageType(byte paramByte);
/*    */   
/*    */   @Message(id = 1035, value = "Unknown method type for AJP request %s")
/*    */   IOException unknownMethod(HttpString paramHttpString);
/*    */   
/*    */   @Message(id = 1036, value = "Data still remaining in chunk %s")
/*    */   IOException dataStillRemainingInChunk(long paramLong);
/*    */   
/*    */   @Message(id = 1037, value = "Wrong magic number, expected %s, actual %s")
/*    */   IOException wrongMagicNumber(String paramString1, String paramString2);
/*    */   
/*    */   @Message(id = 1038, value = "Received invalid AJP chunk %s with response already complete")
/*    */   IOException receivedInvalidChunk(byte paramByte);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\UndertowClientMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */