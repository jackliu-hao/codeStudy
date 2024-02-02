package com.mysql.cj.protocol;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.jdbc.SocketFactoryWrapper;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class AbstractSocketConnection implements SocketConnection {
   protected String host = null;
   protected int port = 3306;
   protected SocketFactory socketFactory = null;
   protected Socket mysqlSocket = null;
   protected FullReadInputStream mysqlInput = null;
   protected BufferedOutputStream mysqlOutput = null;
   protected ExceptionInterceptor exceptionInterceptor;
   protected PropertySet propertySet;

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public Socket getMysqlSocket() {
      return this.mysqlSocket;
   }

   public FullReadInputStream getMysqlInput() throws IOException {
      if (this.mysqlInput != null) {
         return this.mysqlInput;
      } else {
         throw new IOException(Messages.getString("SocketConnection.2"));
      }
   }

   public void setMysqlInput(FullReadInputStream mysqlInput) {
      this.mysqlInput = mysqlInput;
   }

   public BufferedOutputStream getMysqlOutput() throws IOException {
      if (this.mysqlOutput != null) {
         return this.mysqlOutput;
      } else {
         throw new IOException(Messages.getString("SocketConnection.2"));
      }
   }

   public boolean isSSLEstablished() {
      return ExportControlled.enabled() && ExportControlled.isSSLEstablished(this.getMysqlSocket());
   }

   public SocketFactory getSocketFactory() {
      return this.socketFactory;
   }

   public void setSocketFactory(SocketFactory socketFactory) {
      this.socketFactory = socketFactory;
   }

   public void forceClose() {
      try {
         this.getNetworkResources().forceClose();
      } finally {
         this.mysqlSocket = null;
         this.mysqlInput = null;
         this.mysqlOutput = null;
      }

   }

   public NetworkResources getNetworkResources() {
      return new NetworkResources(this.mysqlSocket, this.mysqlInput, this.mysqlOutput);
   }

   public ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   public PropertySet getPropertySet() {
      return this.propertySet;
   }

   protected SocketFactory createSocketFactory(String socketFactoryClassName) {
      try {
         if (socketFactoryClassName == null) {
            throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("SocketConnection.0"), this.getExceptionInterceptor());
         } else {
            Object sf = Class.forName(socketFactoryClassName).newInstance();
            return (SocketFactory)(sf instanceof SocketFactory ? (SocketFactory)((SocketFactory)Class.forName(socketFactoryClassName).newInstance()) : new SocketFactoryWrapper(sf));
         }
      } catch (IllegalAccessException | ClassNotFoundException | CJException | InstantiationException var3) {
         throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("SocketConnection.1", new String[]{socketFactoryClassName}), this.getExceptionInterceptor());
      }
   }
}
