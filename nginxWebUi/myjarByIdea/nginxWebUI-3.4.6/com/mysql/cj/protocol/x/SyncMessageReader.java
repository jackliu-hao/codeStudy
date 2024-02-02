package com.mysql.cj.protocol.x;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.FullReadInputStream;
import com.mysql.cj.protocol.MessageListener;
import com.mysql.cj.protocol.MessageReader;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxNotice;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SyncMessageReader implements MessageReader<XMessageHeader, XMessage> {
   private FullReadInputStream inputStream;
   LinkedList<XMessageHeader> headersQueue = new LinkedList();
   LinkedList<GeneratedMessageV3> messagesQueue = new LinkedList();
   BlockingQueue<MessageListener<XMessage>> messageListenerQueue = new LinkedBlockingQueue();
   Object dispatchingThreadMonitor = new Object();
   Object waitingSyncOperationMonitor = new Object();
   Thread dispatchingThread = null;
   private Protocol.ProtocolEventHandler protocolEventHandler = null;

   public SyncMessageReader(FullReadInputStream inputStream, Protocol.ProtocolEventHandler protocolEventHandler) {
      this.inputStream = inputStream;
      this.protocolEventHandler = protocolEventHandler;
   }

   public XMessageHeader readHeader() throws IOException {
      synchronized(this.waitingSyncOperationMonitor) {
         XMessageHeader header;
         if ((header = (XMessageHeader)this.headersQueue.peek()) == null) {
            header = this.readHeaderLocal();
         }

         if (header.getMessageType() == 1) {
            throw new XProtocolError((Mysqlx.Error)this.readMessageLocal(Mysqlx.Error.class, true));
         } else {
            return header;
         }
      }
   }

   public int getNextNonNoticeMessageType() throws IOException {
      synchronized(this.waitingSyncOperationMonitor) {
         if (!this.headersQueue.isEmpty()) {
            Iterator var2 = this.headersQueue.iterator();

            while(var2.hasNext()) {
               XMessageHeader hdr = (XMessageHeader)var2.next();
               if (hdr.getMessageType() != 11) {
                  return hdr.getMessageType();
               }
            }
         }

         XMessageHeader header;
         do {
            header = this.readHeaderLocal();
            if (header.getMessageType() == 1) {
               Mysqlx.Error msg;
               this.messagesQueue.addLast(msg = (Mysqlx.Error)this.readMessageLocal(Mysqlx.Error.class, false));
               throw new XProtocolError(msg);
            }

            if (header.getMessageType() == 11) {
               this.messagesQueue.addLast(this.readMessageLocal(MysqlxNotice.Frame.class, false));
            }
         } while(header.getMessageType() == 11);

         return header.getMessageType();
      }
   }

   private XMessageHeader readHeaderLocal() throws IOException {
      try {
         byte[] buf = new byte[5];
         this.inputStream.readFully(buf);
         XMessageHeader header = new XMessageHeader(buf);
         this.headersQueue.add(header);
         return header;
      } catch (IOException var3) {
         throw new CJCommunicationsException("Cannot read packet header", var3);
      }
   }

   private <T extends GeneratedMessageV3> T readMessageLocal(Class<T> messageClass, boolean fromQueue) {
      XMessageHeader header;
      if (fromQueue) {
         header = (XMessageHeader)this.headersQueue.poll();
         T msg = (GeneratedMessageV3)this.messagesQueue.poll();
         if (msg != null) {
            return msg;
         }
      } else {
         header = (XMessageHeader)this.headersQueue.getLast();
      }

      Parser<T> parser = (Parser)MessageConstants.MESSAGE_CLASS_TO_PARSER.get(messageClass);
      byte[] packet = new byte[header.getMessageSize()];

      try {
         this.inputStream.readFully(packet);
      } catch (IOException var10) {
         throw new CJCommunicationsException("Cannot read packet payload", var10);
      }

      try {
         T msg = (GeneratedMessageV3)parser.parseFrom(packet);
         if (msg instanceof MysqlxNotice.Frame && ((MysqlxNotice.Frame)msg).getType() == 1 && ((MysqlxNotice.Frame)msg).getScope() == MysqlxNotice.Frame.Scope.GLOBAL) {
            Notice.XWarning w = new Notice.XWarning((MysqlxNotice.Frame)msg);
            int code = (int)w.getCode();
            if (code == 1053 || code == 1810 || code == 3169) {
               CJCommunicationsException ex = new CJCommunicationsException(w.getMessage());
               ex.setVendorCode(code);
               if (this.protocolEventHandler != null) {
                  this.protocolEventHandler.invokeListeners(code == 1053 ? Protocol.ProtocolEventListener.EventType.SERVER_SHUTDOWN : Protocol.ProtocolEventListener.EventType.SERVER_CLOSED_SESSION, ex);
               }

               throw ex;
            }
         }

         return msg;
      } catch (InvalidProtocolBufferException var11) {
         throw new WrongArgumentException(var11);
      }
   }

   public XMessage readMessage(Optional<XMessage> reuse, XMessageHeader hdr) throws IOException {
      return this.readMessage(reuse, hdr.getMessageType());
   }

   public XMessage readMessage(Optional<XMessage> reuse, int expectedType) throws IOException {
      synchronized(this.waitingSyncOperationMonitor) {
         XMessage var10000;
         try {
            Class<? extends GeneratedMessageV3> expectedClass = MessageConstants.getMessageClassForType(expectedType);

            ArrayList notices;
            XMessageHeader hdr;
            for(notices = null; (hdr = this.readHeader()).getMessageType() == 11 && expectedType != 11; notices.add(Notice.getInstance(new XMessage(this.readMessageLocal(MessageConstants.getMessageClassForType(11), true))))) {
               if (notices == null) {
                  notices = new ArrayList();
               }
            }

            Class<? extends GeneratedMessageV3> messageClass = MessageConstants.getMessageClassForType(hdr.getMessageType());
            if (expectedClass != messageClass) {
               throw new WrongArgumentException("Unexpected message class. Expected '" + expectedClass.getSimpleName() + "' but actually received '" + messageClass.getSimpleName() + "'");
            }

            var10000 = (new XMessage(this.readMessageLocal(messageClass, true))).addNotices(notices);
         } catch (IOException var9) {
            throw new XProtocolError(var9.getMessage(), var9);
         }

         return var10000;
      }
   }

   public void pushMessageListener(MessageListener<XMessage> listener) {
      try {
         this.messageListenerQueue.put(listener);
      } catch (InterruptedException var8) {
         throw new CJCommunicationsException("Cannot queue message listener.", var8);
      }

      synchronized(this.dispatchingThreadMonitor) {
         if (this.dispatchingThread == null) {
            ListenersDispatcher ld = new ListenersDispatcher();
            this.dispatchingThread = new Thread(ld, "Message listeners dispatching thread");
            this.dispatchingThread.start();
            int millis = 5000;

            while(!ld.started) {
               try {
                  Thread.sleep(10L);
                  millis -= 10;
               } catch (InterruptedException var7) {
                  throw new XProtocolError(var7.getMessage(), var7);
               }

               if (millis <= 0) {
                  throw new XProtocolError("Timeout for starting ListenersDispatcher exceeded.");
               }
            }
         }

      }
   }

   private class ListenersDispatcher implements Runnable {
      private static final long POLL_TIMEOUT = 100L;
      boolean started = false;

      public ListenersDispatcher() {
      }

      public void run() {
         synchronized(SyncMessageReader.this.waitingSyncOperationMonitor) {
            this.started = true;

            try {
               while(true) {
                  MessageListener l;
                  while((l = (MessageListener)SyncMessageReader.this.messageListenerQueue.poll(100L, TimeUnit.MILLISECONDS)) != null) {
                     try {
                        XMessage msg = null;

                        while(true) {
                           XMessageHeader hdr = SyncMessageReader.this.readHeader();
                           msg = SyncMessageReader.this.readMessage((Optional)null, (XMessageHeader)hdr);
                           if (l.processMessage(msg)) {
                              break;
                           }
                        }
                     } catch (Throwable var6) {
                        l.error(var6);
                     }
                  }

                  synchronized(SyncMessageReader.this.dispatchingThreadMonitor) {
                     if (SyncMessageReader.this.messageListenerQueue.peek() == null) {
                        SyncMessageReader.this.dispatchingThread = null;
                        return;
                     }
                  }
               }
            } catch (InterruptedException var8) {
               throw new CJCommunicationsException("Read operation interrupted.", var8);
            }
         }
      }
   }
}
