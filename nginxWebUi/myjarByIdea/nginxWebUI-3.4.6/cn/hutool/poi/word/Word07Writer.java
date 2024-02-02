package cn.hutool.poi.word;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.poi.exceptions.POIException;
import java.awt.Font;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class Word07Writer implements Closeable {
   private final XWPFDocument doc;
   protected File destFile;
   protected boolean isClosed;

   public Word07Writer() {
      this(new XWPFDocument());
   }

   public Word07Writer(File destFile) {
      this(DocUtil.create(destFile), destFile);
   }

   public Word07Writer(XWPFDocument doc) {
      this(doc, (File)null);
   }

   public Word07Writer(XWPFDocument doc, File destFile) {
      this.doc = doc;
      this.destFile = destFile;
   }

   public XWPFDocument getDoc() {
      return this.doc;
   }

   public Word07Writer setDestFile(File destFile) {
      this.destFile = destFile;
      return this;
   }

   public Word07Writer addText(Font font, String... texts) {
      return this.addText((ParagraphAlignment)null, font, texts);
   }

   public Word07Writer addText(ParagraphAlignment align, Font font, String... texts) {
      XWPFParagraph p = this.doc.createParagraph();
      if (null != align) {
         p.setAlignment(align);
      }

      if (ArrayUtil.isNotEmpty((Object[])texts)) {
         String[] var6 = texts;
         int var7 = texts.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String text = var6[var8];
            XWPFRun run = p.createRun();
            run.setText(text);
            if (null != font) {
               run.setFontFamily(font.getFamily());
               run.setFontSize(font.getSize());
               run.setBold(font.isBold());
               run.setItalic(font.isItalic());
            }
         }
      }

      return this;
   }

   public Word07Writer addTable(Iterable<?> data) {
      TableUtil.createTable(this.doc, data);
      return this;
   }

   public Word07Writer addPicture(File picFile, int width, int height) {
      String fileName = picFile.getName();
      String extName = FileUtil.extName(fileName).toUpperCase();

      PicType picType;
      try {
         picType = PicType.valueOf(extName);
      } catch (IllegalArgumentException var8) {
         picType = PicType.JPEG;
      }

      return this.addPicture(FileUtil.getInputStream(picFile), picType, fileName, width, height);
   }

   public Word07Writer addPicture(InputStream in, PicType picType, String fileName, int width, int height) {
      return this.addPicture(in, picType, fileName, width, height, ParagraphAlignment.CENTER);
   }

   public Word07Writer addPicture(InputStream in, PicType picType, String fileName, int width, int height, ParagraphAlignment align) {
      XWPFParagraph paragraph = this.doc.createParagraph();
      paragraph.setAlignment(align);
      XWPFRun run = paragraph.createRun();

      try {
         run.addPicture(in, picType.getValue(), fileName, Units.toEMU((double)width), Units.toEMU((double)height));
      } catch (InvalidFormatException var14) {
         throw new POIException(var14);
      } catch (IOException var15) {
         throw new IORuntimeException(var15);
      } finally {
         IoUtil.close(in);
      }

      return this;
   }

   public Word07Writer flush() throws IORuntimeException {
      return this.flush(this.destFile);
   }

   public Word07Writer flush(File destFile) throws IORuntimeException {
      Assert.notNull(destFile, "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).");
      return this.flush(FileUtil.getOutputStream(destFile), true);
   }

   public Word07Writer flush(OutputStream out) throws IORuntimeException {
      return this.flush(out, false);
   }

   public Word07Writer flush(OutputStream out, boolean isCloseOut) throws IORuntimeException {
      Assert.isFalse(this.isClosed, "WordWriter has been closed!");

      try {
         this.doc.write(out);
         out.flush();
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      } finally {
         if (isCloseOut) {
            IoUtil.close(out);
         }

      }

      return this;
   }

   public void close() {
      if (null != this.destFile) {
         this.flush();
      }

      this.closeWithoutFlush();
   }

   protected void closeWithoutFlush() {
      IoUtil.close(this.doc);
      this.isClosed = true;
   }
}
