package com.github.jaiimageio.impl.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageTypeSpecifier;

public class PaletteBuilder {
   protected static final int MAXLEVEL = 8;
   protected RenderedImage src;
   protected ColorModel srcColorModel;
   protected Raster srcRaster;
   protected int requiredSize;
   protected ColorNode root;
   protected int numNodes;
   protected int maxNodes;
   protected int currLevel;
   protected int currSize;
   protected ColorNode[] reduceList;
   protected ColorNode[] palette;
   protected int transparency;
   protected ColorNode transColor;

   public static RenderedImage createIndexedImage(RenderedImage src) {
      PaletteBuilder pb = new PaletteBuilder(src);
      pb.buildPalette();
      return pb.getIndexedImage();
   }

   public static IndexColorModel createIndexColorModel(RenderedImage img) {
      PaletteBuilder pb = new PaletteBuilder(img);
      pb.buildPalette();
      return pb.getIndexColorModel();
   }

   public static boolean canCreatePalette(ImageTypeSpecifier type) {
      if (type == null) {
         throw new IllegalArgumentException("type == null");
      } else {
         return true;
      }
   }

   public static boolean canCreatePalette(RenderedImage image) {
      if (image == null) {
         throw new IllegalArgumentException("image == null");
      } else {
         ImageTypeSpecifier type = new ImageTypeSpecifier(image);
         return canCreatePalette(type);
      }
   }

   protected RenderedImage getIndexedImage() {
      IndexColorModel icm = this.getIndexColorModel();
      BufferedImage dst = new BufferedImage(this.src.getWidth(), this.src.getHeight(), 13, icm);
      WritableRaster wr = dst.getRaster();
      int minX = this.src.getMinX();
      int minY = this.src.getMinY();

      for(int y = 0; y < dst.getHeight(); ++y) {
         for(int x = 0; x < dst.getWidth(); ++x) {
            Color aColor = this.getSrcColor(x + minX, y + minY);
            wr.setSample(x, y, 0, this.findColorIndex(this.root, aColor));
         }
      }

      return dst;
   }

   protected PaletteBuilder(RenderedImage src) {
      this(src, 256);
   }

   protected PaletteBuilder(RenderedImage src, int size) {
      this.src = src;
      this.srcColorModel = src.getColorModel();
      this.srcRaster = src.getData();
      this.transparency = this.srcColorModel.getTransparency();
      if (this.transparency != 1) {
         this.requiredSize = size - 1;
         this.transColor = new ColorNode();
         this.transColor.isLeaf = true;
      } else {
         this.requiredSize = size;
      }

   }

   private Color getSrcColor(int x, int y) {
      int argb = this.srcColorModel.getRGB(this.srcRaster.getDataElements(x, y, (Object)null));
      return new Color(argb, this.transparency != 1);
   }

   protected int findColorIndex(ColorNode aNode, Color aColor) {
      if (this.transparency != 1 && aColor.getAlpha() != 255) {
         return 0;
      } else if (aNode.isLeaf) {
         return aNode.paletteIndex;
      } else {
         int childIndex = this.getBranchIndex(aColor, aNode.level);
         return this.findColorIndex(aNode.children[childIndex], aColor);
      }
   }

   protected void buildPalette() {
      this.reduceList = new ColorNode[9];

      int w;
      for(w = 0; w < this.reduceList.length; ++w) {
         this.reduceList[w] = null;
      }

      this.numNodes = 0;
      this.maxNodes = 0;
      this.root = null;
      this.currSize = 0;
      this.currLevel = 8;
      w = this.src.getWidth();
      int h = this.src.getHeight();
      int minX = this.src.getMinX();
      int minY = this.src.getMinY();

      for(int y = 0; y < h; ++y) {
         for(int x = 0; x < w; ++x) {
            Color aColor = this.getSrcColor(w - x + minX - 1, h - y + minY - 1);
            if (this.transparency != 1 && aColor.getAlpha() != 255) {
               this.transColor = this.insertNode(this.transColor, aColor, 0);
            } else {
               this.root = this.insertNode(this.root, aColor, 0);
            }

            if (this.currSize > this.requiredSize) {
               this.reduceTree();
            }
         }
      }

   }

   protected ColorNode insertNode(ColorNode aNode, Color aColor, int aLevel) {
      if (aNode == null) {
         aNode = new ColorNode();
         ++this.numNodes;
         if (this.numNodes > this.maxNodes) {
            this.maxNodes = this.numNodes;
         }

         aNode.level = aLevel;
         aNode.isLeaf = aLevel > 8;
         if (aNode.isLeaf) {
            ++this.currSize;
         }
      }

      ++aNode.colorCount;
      aNode.red += (long)aColor.getRed();
      aNode.green += (long)aColor.getGreen();
      aNode.blue += (long)aColor.getBlue();
      if (!aNode.isLeaf) {
         int branchIndex = this.getBranchIndex(aColor, aLevel);
         if (aNode.children[branchIndex] == null) {
            ++aNode.childCount;
            if (aNode.childCount == 2) {
               aNode.nextReducible = this.reduceList[aLevel];
               this.reduceList[aLevel] = aNode;
            }
         }

         aNode.children[branchIndex] = this.insertNode(aNode.children[branchIndex], aColor, aLevel + 1);
      }

      return aNode;
   }

   protected IndexColorModel getIndexColorModel() {
      int size = this.currSize;
      if (this.transparency != 1) {
         ++size;
      }

      byte[] red = new byte[size];
      byte[] green = new byte[size];
      byte[] blue = new byte[size];
      int index = 0;
      this.palette = new ColorNode[size];
      if (this.transparency != 1) {
         ++index;
      }

      this.findPaletteEntry(this.root, index, red, green, blue);
      IndexColorModel icm = null;
      if (this.transparency != 1) {
         icm = new IndexColorModel(8, size, red, green, blue, 0);
      } else {
         icm = new IndexColorModel(8, this.currSize, red, green, blue);
      }

      return icm;
   }

   protected int findPaletteEntry(ColorNode aNode, int index, byte[] red, byte[] green, byte[] blue) {
      if (aNode.isLeaf) {
         red[index] = (byte)((int)(aNode.red / (long)aNode.colorCount));
         green[index] = (byte)((int)(aNode.green / (long)aNode.colorCount));
         blue[index] = (byte)((int)(aNode.blue / (long)aNode.colorCount));
         aNode.paletteIndex = index;
         this.palette[index] = aNode;
         ++index;
      } else {
         for(int i = 0; i < 8; ++i) {
            if (aNode.children[i] != null) {
               index = this.findPaletteEntry(aNode.children[i], index, red, green, blue);
            }
         }
      }

      return index;
   }

   protected int getBranchIndex(Color aColor, int aLevel) {
      if (aLevel <= 8 && aLevel >= 0) {
         int shift = 8 - aLevel;
         int red_index = 1 & (255 & aColor.getRed()) >> shift;
         int green_index = 1 & (255 & aColor.getGreen()) >> shift;
         int blue_index = 1 & (255 & aColor.getBlue()) >> shift;
         int index = red_index << 2 | green_index << 1 | blue_index;
         return index;
      } else {
         throw new IllegalArgumentException("Invalid octree node depth: " + aLevel);
      }
   }

   protected void reduceTree() {
      int level;
      for(level = this.reduceList.length - 1; this.reduceList[level] == null && level >= 0; --level) {
      }

      ColorNode thisNode = this.reduceList[level];
      if (thisNode != null) {
         ColorNode pList = thisNode;
         int minColorCount = thisNode.colorCount;

         for(int cnt = 1; pList.nextReducible != null; ++cnt) {
            if (minColorCount > pList.nextReducible.colorCount) {
               thisNode = pList;
               minColorCount = pList.colorCount;
            }

            pList = pList.nextReducible;
         }

         if (thisNode == this.reduceList[level]) {
            this.reduceList[level] = thisNode.nextReducible;
         } else {
            pList = thisNode.nextReducible;
            thisNode.nextReducible = pList.nextReducible;
            thisNode = pList;
         }

         if (!thisNode.isLeaf) {
            int leafChildCount = thisNode.getLeafChildCount();
            thisNode.isLeaf = true;
            this.currSize -= leafChildCount - 1;
            int aDepth = thisNode.level;

            for(int i = 0; i < 8; ++i) {
               thisNode.children[i] = this.freeTree(thisNode.children[i]);
            }

            thisNode.childCount = 0;
         }
      }
   }

   protected ColorNode freeTree(ColorNode aNode) {
      if (aNode == null) {
         return null;
      } else {
         for(int i = 0; i < 8; ++i) {
            aNode.children[i] = this.freeTree(aNode.children[i]);
         }

         --this.numNodes;
         return null;
      }
   }

   protected class ColorNode {
      public boolean isLeaf = false;
      public int childCount = 0;
      ColorNode[] children = new ColorNode[8];
      public int colorCount;
      public long red;
      public long blue;
      public long green;
      public int paletteIndex;
      public int level = 0;
      ColorNode nextReducible;

      public ColorNode() {
         for(int i = 0; i < 8; ++i) {
            this.children[i] = null;
         }

         this.colorCount = 0;
         this.red = this.green = this.blue = 0L;
         this.paletteIndex = 0;
      }

      public int getLeafChildCount() {
         if (this.isLeaf) {
            return 0;
         } else {
            int cnt = 0;

            for(int i = 0; i < this.children.length; ++i) {
               if (this.children[i] != null) {
                  if (this.children[i].isLeaf) {
                     ++cnt;
                  } else {
                     cnt += this.children[i].getLeafChildCount();
                  }
               }
            }

            return cnt;
         }
      }

      public int getRGB() {
         int r = (int)this.red / this.colorCount;
         int g = (int)this.green / this.colorCount;
         int b = (int)this.blue / this.colorCount;
         int c = -16777216 | (255 & r) << 16 | (255 & g) << 8 | 255 & b;
         return c;
      }
   }
}
