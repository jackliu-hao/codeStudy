package com.sun.activation.viewers;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.CommandObject;
import javax.activation.DataHandler;

public class ImageViewer extends Panel implements CommandObject {
   private ImageViewerCanvas canvas = null;
   private Image image = null;
   private DataHandler _dh = null;
   private boolean DEBUG = false;

   public ImageViewer() {
      this.canvas = new ImageViewerCanvas();
      this.add(this.canvas);
   }

   public void setCommandContext(String verb, DataHandler dh) throws IOException {
      this._dh = dh;
      this.setInputStream(this._dh.getInputStream());
   }

   private void setInputStream(InputStream ins) throws IOException {
      MediaTracker mt = new MediaTracker(this);
      int bytes_read = false;
      byte[] data = new byte[1024];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      int bytes_read;
      while((bytes_read = ins.read(data)) > 0) {
         baos.write(data, 0, bytes_read);
      }

      ins.close();
      this.image = this.getToolkit().createImage(baos.toByteArray());
      mt.addImage(this.image, 0);

      try {
         mt.waitForID(0);
         mt.waitForAll();
         if (mt.statusID(0, true) != 8) {
            System.out.println("Error occured in image loading = " + mt.getErrorsID(0));
         }
      } catch (InterruptedException var7) {
         throw new IOException("Error reading image data");
      }

      this.canvas.setImage(this.image);
      if (this.DEBUG) {
         System.out.println("calling invalidate");
      }

   }

   public void addNotify() {
      super.addNotify();
      this.invalidate();
      this.validate();
      this.doLayout();
   }

   public Dimension getPreferredSize() {
      return this.canvas.getPreferredSize();
   }
}
