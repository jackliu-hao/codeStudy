package com.sun.mail.handlers;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class image_gif implements DataContentHandler {
   private static ActivationDataFlavor myDF;

   protected ActivationDataFlavor getDF() {
      return myDF;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{this.getDF()};
   }

   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
      return this.getDF().equals(df) ? this.getContent(ds) : null;
   }

   public Object getContent(DataSource ds) throws IOException {
      InputStream is = ds.getInputStream();
      int pos = 0;
      byte[] buf = new byte[1024];

      int count;
      while((count = is.read(buf, pos, buf.length - pos)) != -1) {
         pos += count;
         if (pos >= buf.length) {
            int size = buf.length;
            if (size < 262144) {
               size += size;
            } else {
               size += 262144;
            }

            byte[] tbuf = new byte[size];
            System.arraycopy(buf, 0, tbuf, 0, pos);
            buf = tbuf;
         }
      }

      Toolkit tk = Toolkit.getDefaultToolkit();
      return tk.createImage(buf, 0, pos);
   }

   public void writeTo(Object obj, String type, OutputStream os) throws IOException {
      if (!(obj instanceof Image)) {
         throw new IOException("\"" + this.getDF().getMimeType() + "\" DataContentHandler requires Image object, " + "was given object of type " + obj.getClass().toString());
      } else {
         throw new IOException(this.getDF().getMimeType() + " encoding not supported");
      }
   }

   static {
      myDF = new ActivationDataFlavor(Image.class, "image/gif", "GIF Image");
   }
}
