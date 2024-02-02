package com.sun.mail.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public class multipart_mixed implements DataContentHandler {
   private ActivationDataFlavor myDF;

   public multipart_mixed() {
      this.myDF = new ActivationDataFlavor(MimeMultipart.class, "multipart/mixed", "Multipart");
   }

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{this.myDF};
   }

   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
      return this.myDF.equals(df) ? this.getContent(ds) : null;
   }

   public Object getContent(DataSource ds) throws IOException {
      try {
         return new MimeMultipart(ds);
      } catch (MessagingException var4) {
         IOException ioex = new IOException("Exception while constructing MimeMultipart");
         ioex.initCause(var4);
         throw ioex;
      }
   }

   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
      if (obj instanceof MimeMultipart) {
         try {
            ((MimeMultipart)obj).writeTo(os);
         } catch (MessagingException var5) {
            throw new IOException(var5.toString());
         }
      }

   }
}
