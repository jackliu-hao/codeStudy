package cn.hutool.poi.ofd;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.PathUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import org.ofdrw.font.Font;
import org.ofdrw.layout.OFDDoc;
import org.ofdrw.layout.edit.Annotation;
import org.ofdrw.layout.element.Div;
import org.ofdrw.layout.element.Img;
import org.ofdrw.layout.element.Paragraph;
import org.ofdrw.reader.OFDReader;

public class OfdWriter implements Serializable, Closeable {
   private static final long serialVersionUID = 1L;
   private final OFDDoc doc;

   public OfdWriter(File file) {
      this(file.toPath());
   }

   public OfdWriter(Path file) {
      try {
         if (PathUtil.exists(file, true)) {
            this.doc = new OFDDoc(new OFDReader(file), file);
         } else {
            this.doc = new OFDDoc(file);
         }

      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public OfdWriter(OutputStream out) {
      this.doc = new OFDDoc(out);
   }

   public OfdWriter addText(Font font, String... texts) {
      Paragraph paragraph = new Paragraph();
      if (null != font) {
         paragraph.setDefaultFont(font);
      }

      String[] var4 = texts;
      int var5 = texts.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String text = var4[var6];
         paragraph.add(text);
      }

      return this.add(paragraph);
   }

   public OfdWriter addPicture(File picFile, int width, int height) {
      return this.addPicture(picFile.toPath(), width, height);
   }

   public OfdWriter addPicture(Path picFile, int width, int height) {
      Img img;
      try {
         img = new Img((double)width, (double)height, picFile);
      } catch (IOException var6) {
         throw new IORuntimeException(var6);
      }

      return this.add(img);
   }

   public OfdWriter add(Div div) {
      this.doc.add(div);
      return this;
   }

   public OfdWriter add(int page, Annotation annotation) {
      try {
         this.doc.addAnnotation(page, annotation);
         return this;
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public void close() {
      IoUtil.close(this.doc);
   }
}
