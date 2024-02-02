/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.GeneratedMessageV3;
/*     */ import com.google.protobuf.InvalidProtocolBufferException;
/*     */ import com.google.protobuf.Message;
/*     */ import com.google.protobuf.Parser;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.FullReadInputStream;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.MessageHeader;
/*     */ import com.mysql.cj.protocol.MessageListener;
/*     */ import com.mysql.cj.protocol.MessageReader;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.x.protobuf.Mysqlx;
/*     */ import com.mysql.cj.x.protobuf.MysqlxNotice;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SyncMessageReader
/*     */   implements MessageReader<XMessageHeader, XMessage>
/*     */ {
/*     */   private FullReadInputStream inputStream;
/*  64 */   LinkedList<XMessageHeader> headersQueue = new LinkedList<>();
/*  65 */   LinkedList<GeneratedMessageV3> messagesQueue = new LinkedList<>();
/*     */ 
/*     */   
/*  68 */   BlockingQueue<MessageListener<XMessage>> messageListenerQueue = new LinkedBlockingQueue<>();
/*     */ 
/*     */   
/*  71 */   Object dispatchingThreadMonitor = new Object();
/*     */   
/*  73 */   Object waitingSyncOperationMonitor = new Object();
/*     */   
/*  75 */   Thread dispatchingThread = null;
/*     */   
/*  77 */   private Protocol.ProtocolEventHandler protocolEventHandler = null;
/*     */   
/*     */   public SyncMessageReader(FullReadInputStream inputStream, Protocol.ProtocolEventHandler protocolEventHandler) {
/*  80 */     this.inputStream = inputStream;
/*  81 */     this.protocolEventHandler = protocolEventHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public XMessageHeader readHeader() throws IOException {
/*  87 */     synchronized (this.waitingSyncOperationMonitor) {
/*     */       XMessageHeader header;
/*  89 */       if ((header = this.headersQueue.peek()) == null) {
/*  90 */         header = readHeaderLocal();
/*     */       }
/*  92 */       if (header.getMessageType() == 1) {
/*  93 */         throw new XProtocolError((Mysqlx.Error)readMessageLocal(Mysqlx.Error.class, true));
/*     */       }
/*  95 */       return header;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getNextNonNoticeMessageType() throws IOException {
/* 100 */     synchronized (this.waitingSyncOperationMonitor) {
/* 101 */       XMessageHeader header; if (!this.headersQueue.isEmpty()) {
/* 102 */         for (XMessageHeader hdr : this.headersQueue) {
/* 103 */           if (hdr.getMessageType() != 11) {
/* 104 */             return hdr.getMessageType();
/*     */           }
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       do {
/* 111 */         header = readHeaderLocal();
/* 112 */         if (header.getMessageType() == 1) {
/*     */           Mysqlx.Error msg;
/* 114 */           this.messagesQueue.addLast(msg = readMessageLocal(Mysqlx.Error.class, false));
/* 115 */           throw new XProtocolError(msg);
/*     */         } 
/* 117 */         if (header.getMessageType() != 11)
/* 118 */           continue;  this.messagesQueue.addLast((GeneratedMessageV3)readMessageLocal(MysqlxNotice.Frame.class, false));
/*     */       }
/* 120 */       while (header.getMessageType() == 11);
/*     */       
/* 122 */       return header.getMessageType();
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
/*     */   private XMessageHeader readHeaderLocal() throws IOException {
/*     */     XMessageHeader header;
/*     */     try {
/* 136 */       byte[] buf = new byte[5];
/* 137 */       this.inputStream.readFully(buf);
/* 138 */       header = new XMessageHeader(buf);
/* 139 */       this.headersQueue.add(header);
/* 140 */     } catch (IOException ex) {
/*     */       
/* 142 */       throw new CJCommunicationsException("Cannot read packet header", ex);
/*     */     } 
/* 144 */     return header;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private <T extends GeneratedMessageV3> T readMessageLocal(Class<T> messageClass, boolean fromQueue) {
/*     */     XMessageHeader header;
/* 151 */     if (fromQueue) {
/* 152 */       header = this.headersQueue.poll();
/* 153 */       GeneratedMessageV3 generatedMessageV3 = this.messagesQueue.poll();
/* 154 */       if (generatedMessageV3 != null) {
/* 155 */         return (T)generatedMessageV3;
/*     */       }
/*     */     } else {
/* 158 */       header = this.headersQueue.getLast();
/*     */     } 
/*     */     
/* 161 */     Parser<T> parser = (Parser<T>)MessageConstants.MESSAGE_CLASS_TO_PARSER.get(messageClass);
/* 162 */     byte[] packet = new byte[header.getMessageSize()];
/*     */     
/*     */     try {
/* 165 */       this.inputStream.readFully(packet);
/* 166 */     } catch (IOException ex) {
/*     */       
/* 168 */       throw new CJCommunicationsException("Cannot read packet payload", ex);
/*     */     } 
/*     */     
/*     */     try {
/* 172 */       GeneratedMessageV3 generatedMessageV3 = (GeneratedMessageV3)parser.parseFrom(packet);
/*     */       
/* 174 */       if (generatedMessageV3 instanceof MysqlxNotice.Frame && ((MysqlxNotice.Frame)generatedMessageV3).getType() == 1 && ((MysqlxNotice.Frame)generatedMessageV3).getScope() == MysqlxNotice.Frame.Scope.GLOBAL) {
/* 175 */         Notice.XWarning w = new Notice.XWarning((MysqlxNotice.Frame)generatedMessageV3);
/* 176 */         int code = (int)w.getCode();
/* 177 */         if (code == 1053 || code == 1810 || code == 3169) {
/*     */ 
/*     */           
/* 180 */           CJCommunicationsException ex = new CJCommunicationsException(w.getMessage());
/* 181 */           ex.setVendorCode(code);
/* 182 */           if (this.protocolEventHandler != null) {
/* 183 */             this.protocolEventHandler.invokeListeners((code == 1053) ? Protocol.ProtocolEventListener.EventType.SERVER_SHUTDOWN : Protocol.ProtocolEventListener.EventType.SERVER_CLOSED_SESSION, (Throwable)ex);
/*     */           }
/*     */ 
/*     */           
/* 187 */           throw ex;
/*     */         } 
/*     */       } 
/*     */       
/* 191 */       return (T)generatedMessageV3;
/*     */     }
/* 193 */     catch (InvalidProtocolBufferException ex) {
/* 194 */       throw new WrongArgumentException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public XMessage readMessage(Optional<XMessage> reuse, XMessageHeader hdr) throws IOException {
/* 200 */     return readMessage(reuse, hdr.getMessageType());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public XMessage readMessage(Optional<XMessage> reuse, int expectedType) throws IOException {
/* 206 */     synchronized (this.waitingSyncOperationMonitor) {
/*     */       
/* 208 */       Class<? extends GeneratedMessageV3> expectedClass = MessageConstants.getMessageClassForType(expectedType);
/*     */       
/* 210 */       List<Notice> notices = null;
/*     */       XMessageHeader hdr;
/* 212 */       while ((hdr = readHeader()).getMessageType() == 11 && expectedType != 11) {
/* 213 */         if (notices == null) {
/* 214 */           notices = new ArrayList<>();
/*     */         }
/* 216 */         notices.add(
/* 217 */             Notice.getInstance(new XMessage((Message)readMessageLocal(MessageConstants.getMessageClassForType(11), true))));
/*     */       } 
/*     */       
/* 220 */       Class<? extends GeneratedMessageV3> messageClass = MessageConstants.getMessageClassForType(hdr.getMessageType());
/*     */       
/* 222 */       if (expectedClass != messageClass) {
/* 223 */         throw new WrongArgumentException("Unexpected message class. Expected '" + expectedClass.getSimpleName() + "' but actually received '" + messageClass
/* 224 */             .getSimpleName() + "'");
/*     */       }
/*     */       
/* 227 */       return (new XMessage((Message)readMessageLocal(messageClass, true))).addNotices(notices);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pushMessageListener(MessageListener<XMessage> listener) {
/*     */     try {
/* 236 */       this.messageListenerQueue.put(listener);
/* 237 */     } catch (InterruptedException e) {
/* 238 */       throw new CJCommunicationsException("Cannot queue message listener.", e);
/*     */     } 
/*     */     
/* 241 */     synchronized (this.dispatchingThreadMonitor) {
/* 242 */       if (this.dispatchingThread == null) {
/* 243 */         ListenersDispatcher ld = new ListenersDispatcher();
/* 244 */         this.dispatchingThread = new Thread(ld, "Message listeners dispatching thread");
/* 245 */         this.dispatchingThread.start();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 251 */         int millis = 5000;
/* 252 */         while (!ld.started) {
/*     */           try {
/* 254 */             Thread.sleep(10L);
/* 255 */             millis -= 10;
/* 256 */           } catch (InterruptedException e) {
/* 257 */             throw new XProtocolError(e.getMessage(), e);
/*     */           } 
/* 259 */           if (millis <= 0) {
/* 260 */             throw new XProtocolError("Timeout for starting ListenersDispatcher exceeded.");
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ListenersDispatcher
/*     */     implements Runnable
/*     */   {
/*     */     private static final long POLL_TIMEOUT = 100L;
/*     */ 
/*     */     
/*     */     boolean started = false;
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 281 */       synchronized (SyncMessageReader.this.waitingSyncOperationMonitor) {
/* 282 */         this.started = true; try {
/*     */           label32: while (true) {
/*     */             MessageListener<XMessage> l;
/*     */             while (true) {
/* 286 */               if ((l = SyncMessageReader.this.messageListenerQueue.poll(100L, TimeUnit.MILLISECONDS)) == null)
/* 287 */               { synchronized (SyncMessageReader.this.dispatchingThreadMonitor) {
/* 288 */                   if (SyncMessageReader.this.messageListenerQueue.peek() == null)
/* 289 */                   { SyncMessageReader.this.dispatchingThread = null; }
/*     */                   else { continue; }
/*     */                 
/*     */                 }  }
/*     */               else { break; }
/*     */             
/* 295 */             }  try { XMessage msg = null;
/*     */               while (true)
/* 297 */               { XMessageHeader hdr = SyncMessageReader.this.readHeader();
/* 298 */                 msg = SyncMessageReader.this.readMessage((Optional<XMessage>)null, hdr);
/* 299 */                 if (l.processMessage(msg))
/* 300 */                   continue label32;  }  } catch (Throwable t)
/* 301 */             { l.error(t); }
/*     */ 
/*     */           
/*     */           } 
/* 305 */         } catch (InterruptedException e) {
/* 306 */           throw new CJCommunicationsException("Read operation interrupted.", e);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\SyncMessageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */