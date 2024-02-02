package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.EofSensorInputStream;
import org.apache.http.conn.EofSensorWatcher;
import org.apache.http.entity.HttpEntityWrapper;

class ResponseEntityProxy extends HttpEntityWrapper implements EofSensorWatcher {
   private final ConnectionHolder connHolder;

   public static void enchance(HttpResponse response, ConnectionHolder connHolder) {
      HttpEntity entity = response.getEntity();
      if (entity != null && entity.isStreaming() && connHolder != null) {
         response.setEntity(new ResponseEntityProxy(entity, connHolder));
      }

   }

   ResponseEntityProxy(HttpEntity entity, ConnectionHolder connHolder) {
      super(entity);
      this.connHolder = connHolder;
   }

   private void cleanup() throws IOException {
      if (this.connHolder != null) {
         this.connHolder.close();
      }

   }

   private void abortConnection() {
      if (this.connHolder != null) {
         this.connHolder.abortConnection();
      }

   }

   public void releaseConnection() {
      if (this.connHolder != null) {
         this.connHolder.releaseConnection();
      }

   }

   public boolean isRepeatable() {
      return false;
   }

   public InputStream getContent() throws IOException {
      return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
   }

   public void consumeContent() throws IOException {
      this.releaseConnection();
   }

   public void writeTo(OutputStream outStream) throws IOException {
      try {
         if (outStream != null) {
            this.wrappedEntity.writeTo(outStream);
         }

         this.releaseConnection();
      } catch (IOException var7) {
         this.abortConnection();
         throw var7;
      } catch (RuntimeException var8) {
         this.abortConnection();
         throw var8;
      } finally {
         this.cleanup();
      }

   }

   public boolean eofDetected(InputStream wrapped) throws IOException {
      try {
         if (wrapped != null) {
            wrapped.close();
         }

         this.releaseConnection();
      } catch (IOException var7) {
         this.abortConnection();
         throw var7;
      } catch (RuntimeException var8) {
         this.abortConnection();
         throw var8;
      } finally {
         this.cleanup();
      }

      return false;
   }

   public boolean streamClosed(InputStream wrapped) throws IOException {
      try {
         boolean open = this.connHolder != null && !this.connHolder.isReleased();

         try {
            if (wrapped != null) {
               wrapped.close();
            }

            this.releaseConnection();
         } catch (SocketException var9) {
            if (open) {
               throw var9;
            }
         }
      } catch (IOException var10) {
         this.abortConnection();
         throw var10;
      } catch (RuntimeException var11) {
         this.abortConnection();
         throw var11;
      } finally {
         this.cleanup();
      }

      return false;
   }

   public boolean streamAbort(InputStream wrapped) throws IOException {
      this.cleanup();
      return false;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("ResponseEntityProxy{");
      sb.append(this.wrappedEntity);
      sb.append('}');
      return sb.toString();
   }
}
