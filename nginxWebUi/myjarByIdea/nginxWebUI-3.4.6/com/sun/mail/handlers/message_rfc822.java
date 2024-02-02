package com.sun.mail.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessageAware;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class message_rfc822 implements DataContentHandler {
   ActivationDataFlavor ourDataFlavor;

   public message_rfc822() {
      this.ourDataFlavor = new ActivationDataFlavor(Message.class, "message/rfc822", "Message");
   }

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{this.ourDataFlavor};
   }

   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
      return this.ourDataFlavor.equals(df) ? this.getContent(ds) : null;
   }

   public Object getContent(DataSource ds) throws IOException {
      try {
         Session session;
         if (ds instanceof MessageAware) {
            MessageContext mc = ((MessageAware)ds).getMessageContext();
            session = mc.getSession();
         } else {
            session = Session.getDefaultInstance(new Properties(), (Authenticator)null);
         }

         return new MimeMessage(session, ds.getInputStream());
      } catch (MessagingException var4) {
         throw new IOException("Exception creating MimeMessage in message/rfc822 DataContentHandler: " + var4.toString());
      }
   }

   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
      if (obj instanceof Message) {
         Message m = (Message)obj;

         try {
            m.writeTo(os);
         } catch (MessagingException var6) {
            throw new IOException(var6.toString());
         }
      } else {
         throw new IOException("unsupported object");
      }
   }
}
