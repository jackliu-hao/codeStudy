package cn.hutool.core.img.gif;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AnimatedGifEncoder {
   protected int width;
   protected int height;
   protected Color transparent = null;
   protected boolean transparentExactMatch = false;
   protected Color background = null;
   protected int transIndex;
   protected int repeat = -1;
   protected int delay = 0;
   protected boolean started = false;
   protected OutputStream out;
   protected BufferedImage image;
   protected byte[] pixels;
   protected byte[] indexedPixels;
   protected int colorDepth;
   protected byte[] colorTab;
   protected boolean[] usedEntry = new boolean[256];
   protected int palSize = 7;
   protected int dispose = -1;
   protected boolean closeStream = false;
   protected boolean firstFrame = true;
   protected boolean sizeSet = false;
   protected int sample = 10;

   public void setDelay(int ms) {
      this.delay = Math.round((float)ms / 10.0F);
   }

   public void setDispose(int code) {
      if (code >= 0) {
         this.dispose = code;
      }

   }

   public void setRepeat(int iter) {
      if (iter >= 0) {
         this.repeat = iter;
      }

   }

   public void setTransparent(Color c) {
      this.setTransparent(c, false);
   }

   public void setTransparent(Color c, boolean exactMatch) {
      this.transparent = c;
      this.transparentExactMatch = exactMatch;
   }

   public void setBackground(Color c) {
      this.background = c;
   }

   public boolean addFrame(BufferedImage im) {
      if (im != null && this.started) {
         boolean ok = true;

         try {
            if (!this.sizeSet) {
               this.setSize(im.getWidth(), im.getHeight());
            }

            this.image = im;
            this.getImagePixels();
            this.analyzePixels();
            if (this.firstFrame) {
               this.writeLSD();
               this.writePalette();
               if (this.repeat >= 0) {
                  this.writeNetscapeExt();
               }
            }

            this.writeGraphicCtrlExt();
            this.writeImageDesc();
            if (!this.firstFrame) {
               this.writePalette();
            }

            this.writePixels();
            this.firstFrame = false;
         } catch (IOException var4) {
            ok = false;
         }

         return ok;
      } else {
         return false;
      }
   }

   public boolean finish() {
      if (!this.started) {
         return false;
      } else {
         boolean ok = true;
         this.started = false;

         try {
            this.out.write(59);
            this.out.flush();
            if (this.closeStream) {
               this.out.close();
            }
         } catch (IOException var3) {
            ok = false;
         }

         this.transIndex = 0;
         this.out = null;
         this.image = null;
         this.pixels = null;
         this.indexedPixels = null;
         this.colorTab = null;
         this.closeStream = false;
         this.firstFrame = true;
         return ok;
      }
   }

   public void setFrameRate(float fps) {
      if (fps != 0.0F) {
         this.delay = Math.round(100.0F / fps);
      }

   }

   public void setQuality(int quality) {
      if (quality < 1) {
         quality = 1;
      }

      this.sample = quality;
   }

   public void setSize(int w, int h) {
      if (!this.started || this.firstFrame) {
         this.width = w;
         this.height = h;
         if (this.width < 1) {
            this.width = 320;
         }

         if (this.height < 1) {
            this.height = 240;
         }

         this.sizeSet = true;
      }
   }

   public boolean start(OutputStream os) {
      if (os == null) {
         return false;
      } else {
         boolean ok = true;
         this.closeStream = false;
         this.out = os;

         try {
            this.writeString("GIF89a");
         } catch (IOException var4) {
            ok = false;
         }

         return this.started = ok;
      }
   }

   public boolean start(String file) {
      boolean ok;
      try {
         this.out = new BufferedOutputStream(new FileOutputStream(file));
         ok = this.start(this.out);
         this.closeStream = true;
      } catch (IOException var4) {
         ok = false;
      }

      return this.started = ok;
   }

   public boolean isStarted() {
      return this.started;
   }

   protected void analyzePixels() {
      int len = this.pixels.length;
      int nPix = len / 3;
      this.indexedPixels = new byte[nPix];
      NeuQuant nq = new NeuQuant(this.pixels, len, this.sample);
      this.colorTab = nq.process();

      int k;
      int i;
      for(k = 0; k < this.colorTab.length; k += 3) {
         i = this.colorTab[k];
         this.colorTab[k] = this.colorTab[k + 2];
         this.colorTab[k + 2] = (byte)i;
         this.usedEntry[k / 3] = false;
      }

      k = 0;

      for(i = 0; i < nPix; ++i) {
         int index = nq.map(this.pixels[k++] & 255, this.pixels[k++] & 255, this.pixels[k++] & 255);
         this.usedEntry[index] = true;
         this.indexedPixels[i] = (byte)index;
      }

      this.pixels = null;
      this.colorDepth = 8;
      this.palSize = 7;
      if (this.transparent != null) {
         this.transIndex = this.transparentExactMatch ? this.findExact(this.transparent) : this.findClosest(this.transparent);
      }

   }

   protected int findClosest(Color c) {
      if (this.colorTab == null) {
         return -1;
      } else {
         int r = c.getRed();
         int g = c.getGreen();
         int b = c.getBlue();
         int minpos = 0;
         int dmin = 16777216;
         int len = this.colorTab.length;

         for(int i = 0; i < len; ++i) {
            int dr = r - (this.colorTab[i++] & 255);
            int dg = g - (this.colorTab[i++] & 255);
            int db = b - (this.colorTab[i] & 255);
            int d = dr * dr + dg * dg + db * db;
            int index = i / 3;
            if (this.usedEntry[index] && d < dmin) {
               dmin = d;
               minpos = index;
            }
         }

         return minpos;
      }
   }

   boolean isColorUsed(Color c) {
      return this.findExact(c) != -1;
   }

   protected int findExact(Color c) {
      if (this.colorTab == null) {
         return -1;
      } else {
         int r = c.getRed();
         int g = c.getGreen();
         int b = c.getBlue();
         int len = this.colorTab.length / 3;

         for(int index = 0; index < len; ++index) {
            int i = index * 3;
            if (this.usedEntry[index] && r == (this.colorTab[i] & 255) && g == (this.colorTab[i + 1] & 255) && b == (this.colorTab[i + 2] & 255)) {
               return index;
            }
         }

         return -1;
      }
   }

   protected void getImagePixels() {
      int w = this.image.getWidth();
      int h = this.image.getHeight();
      int type = this.image.getType();
      if (w != this.width || h != this.height || type != 5) {
         BufferedImage temp = new BufferedImage(this.width, this.height, 5);
         Graphics2D g = temp.createGraphics();
         g.setColor(this.background);
         g.fillRect(0, 0, this.width, this.height);
         g.drawImage(this.image, 0, 0, (ImageObserver)null);
         this.image = temp;
      }

      this.pixels = ((DataBufferByte)this.image.getRaster().getDataBuffer()).getData();
   }

   protected void writeGraphicCtrlExt() throws IOException {
      this.out.write(33);
      this.out.write(249);
      this.out.write(4);
      byte transp;
      int disp;
      if (this.transparent == null) {
         transp = 0;
         disp = 0;
      } else {
         transp = 1;
         disp = 2;
      }

      if (this.dispose >= 0) {
         disp = this.dispose & 7;
      }

      disp <<= 2;
      this.out.write(0 | disp | 0 | transp);
      this.writeShort(this.delay);
      this.out.write(this.transIndex);
      this.out.write(0);
   }

   protected void writeImageDesc() throws IOException {
      this.out.write(44);
      this.writeShort(0);
      this.writeShort(0);
      this.writeShort(this.width);
      this.writeShort(this.height);
      if (this.firstFrame) {
         this.out.write(0);
      } else {
         this.out.write(128 | this.palSize);
      }

   }

   protected void writeLSD() throws IOException {
      this.writeShort(this.width);
      this.writeShort(this.height);
      this.out.write(240 | this.palSize);
      this.out.write(0);
      this.out.write(0);
   }

   protected void writeNetscapeExt() throws IOException {
      this.out.write(33);
      this.out.write(255);
      this.out.write(11);
      this.writeString("NETSCAPE2.0");
      this.out.write(3);
      this.out.write(1);
      this.writeShort(this.repeat);
      this.out.write(0);
   }

   protected void writePalette() throws IOException {
      this.out.write(this.colorTab, 0, this.colorTab.length);
      int n = 768 - this.colorTab.length;

      for(int i = 0; i < n; ++i) {
         this.out.write(0);
      }

   }

   protected void writePixels() throws IOException {
      LZWEncoder encoder = new LZWEncoder(this.width, this.height, this.indexedPixels, this.colorDepth);
      encoder.encode(this.out);
   }

   protected void writeShort(int value) throws IOException {
      this.out.write(value & 255);
      this.out.write(value >> 8 & 255);
   }

   protected void writeString(String s) throws IOException {
      for(int i = 0; i < s.length(); ++i) {
         this.out.write((byte)s.charAt(i));
      }

   }
}
