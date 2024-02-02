package com.sun.activation.viewers;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.CommandObject;
import javax.activation.DataHandler;

public class TextViewer extends Panel implements CommandObject {
   private TextArea text_area = null;
   private File text_file = null;
   private String text_buffer = null;
   private DataHandler _dh = null;
   private boolean DEBUG = false;

   public TextViewer() {
      this.setLayout(new GridLayout(1, 1));
      this.text_area = new TextArea("", 24, 80, 1);
      this.text_area.setEditable(false);
      this.add(this.text_area);
   }

   public void setCommandContext(String verb, DataHandler dh) throws IOException {
      this._dh = dh;
      this.setInputStream(this._dh.getInputStream());
   }

   public void setInputStream(InputStream ins) throws IOException {
      int bytes_read = false;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] data = new byte[1024];

      int bytes_read;
      while((bytes_read = ins.read(data)) > 0) {
         baos.write(data, 0, bytes_read);
      }

      ins.close();
      this.text_buffer = baos.toString();
      this.text_area.setText(this.text_buffer);
   }

   public void addNotify() {
      super.addNotify();
      this.invalidate();
   }

   public Dimension getPreferredSize() {
      return this.text_area.getMinimumSize(24, 80);
   }
}
