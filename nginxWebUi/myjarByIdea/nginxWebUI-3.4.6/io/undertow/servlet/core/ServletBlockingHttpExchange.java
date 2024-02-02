package io.undertow.servlet.core;

import io.undertow.io.BlockingReceiverImpl;
import io.undertow.io.BlockingSenderImpl;
import io.undertow.io.Receiver;
import io.undertow.io.Sender;
import io.undertow.server.BlockingHttpExchange;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.servlet.spec.HttpServletResponseImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletBlockingHttpExchange implements BlockingHttpExchange {
   private final HttpServerExchange exchange;

   public ServletBlockingHttpExchange(HttpServerExchange exchange) {
      this.exchange = exchange;
   }

   public InputStream getInputStream() {
      ServletRequest request = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletRequest();

      try {
         return request.getInputStream();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public OutputStream getOutputStream() {
      ServletResponse response = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletResponse();

      try {
         return response.getOutputStream();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public Sender getSender() {
      try {
         return new BlockingSenderImpl(this.exchange, this.getOutputStream());
      } catch (IllegalStateException var5) {
         ServletResponse response = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletResponse();

         try {
            return new BlockingWriterSenderImpl(this.exchange, response.getWriter(), response.getCharacterEncoding());
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   public void close() throws IOException {
      ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      HttpServletRequestImpl var2;
      HttpServletResponseImpl response;
      if (!this.exchange.isComplete()) {
         boolean var12 = false;

         try {
            var12 = true;
            var2 = servletRequestContext.getOriginalRequest();
            var2.closeAndDrainRequest();
            var12 = false;
         } finally {
            if (var12) {
               HttpServletResponseImpl var4 = servletRequestContext.getOriginalResponse();
               var4.closeStreamAndWriter();
            }
         }

         response = servletRequestContext.getOriginalResponse();
         response.closeStreamAndWriter();
      } else {
         boolean var9 = false;

         try {
            var9 = true;
            var2 = servletRequestContext.getOriginalRequest();
            var2.freeResources();
            var9 = false;
         } finally {
            if (var9) {
               HttpServletResponseImpl var6 = servletRequestContext.getOriginalResponse();
               var6.freeResources();
            }
         }

         response = servletRequestContext.getOriginalResponse();
         response.freeResources();
      }

   }

   public Receiver getReceiver() {
      return new BlockingReceiverImpl(this.exchange, this.getInputStream());
   }
}
