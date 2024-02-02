package com.mysql.cj.protocol;

import com.mysql.cj.MessageBuilder;
import com.mysql.cj.QueryResult;
import com.mysql.cj.Session;
import com.mysql.cj.TransactionEventHandler;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import java.io.IOException;
import java.io.InputStream;

public interface Protocol<M extends Message> {
   void init(Session var1, SocketConnection var2, PropertySet var3, TransactionEventHandler var4);

   PropertySet getPropertySet();

   void setPropertySet(PropertySet var1);

   MessageBuilder<M> getMessageBuilder();

   ServerCapabilities readServerCapabilities();

   ServerSession getServerSession();

   SocketConnection getSocketConnection();

   AuthenticationProvider<M> getAuthenticationProvider();

   ExceptionInterceptor getExceptionInterceptor();

   PacketSentTimeHolder getPacketSentTimeHolder();

   void setPacketSentTimeHolder(PacketSentTimeHolder var1);

   PacketReceivedTimeHolder getPacketReceivedTimeHolder();

   void setPacketReceivedTimeHolder(PacketReceivedTimeHolder var1);

   void connect(String var1, String var2, String var3);

   void negotiateSSLConnection();

   void beforeHandshake();

   void afterHandshake();

   void changeDatabase(String var1);

   void changeUser(String var1, String var2, String var3);

   boolean versionMeetsMinimum(int var1, int var2, int var3);

   M readMessage(M var1);

   M checkErrorMessage();

   void send(Message var1, int var2);

   ColumnDefinition readMetadata();

   M sendCommand(Message var1, boolean var2, int var3);

   <T extends ProtocolEntity> T read(Class<T> var1, ProtocolEntityFactory<T, M> var2) throws IOException;

   <T extends ProtocolEntity> T read(Class<Resultset> var1, int var2, boolean var3, M var4, boolean var5, ColumnDefinition var6, ProtocolEntityFactory<T, M> var7) throws IOException;

   void setLocalInfileInputStream(InputStream var1);

   InputStream getLocalInfileInputStream();

   String getQueryComment();

   void setQueryComment(String var1);

   <T extends QueryResult> T readQueryResult(ResultBuilder<T> var1);

   void close() throws IOException;

   void configureTimeZone();

   void initServerSession();

   void reset();

   String getQueryTimingUnits();

   public interface ProtocolEventHandler {
      void addListener(ProtocolEventListener var1);

      void removeListener(ProtocolEventListener var1);

      void invokeListeners(ProtocolEventListener.EventType var1, Throwable var2);
   }

   public interface ProtocolEventListener {
      void handleEvent(EventType var1, Object var2, Throwable var3);

      public static enum EventType {
         SERVER_SHUTDOWN,
         SERVER_CLOSED_SESSION;
      }
   }
}
