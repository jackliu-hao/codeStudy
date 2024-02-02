package com.github.jaiimageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageReadParam;

public class TIFFImageReadParam extends ImageReadParam {
   List allowedTagSets = new ArrayList(4);
   TIFFDecompressor decompressor = null;
   TIFFColorConverter colorConverter = null;

   public TIFFImageReadParam() {
      this.addAllowedTagSet(BaselineTIFFTagSet.getInstance());
      this.addAllowedTagSet(FaxTIFFTagSet.getInstance());
      this.addAllowedTagSet(EXIFParentTIFFTagSet.getInstance());
      this.addAllowedTagSet(GeoTIFFTagSet.getInstance());
   }

   public void addAllowedTagSet(TIFFTagSet tagSet) {
      if (tagSet == null) {
         throw new IllegalArgumentException("tagSet == null!");
      } else {
         this.allowedTagSets.add(tagSet);
      }
   }

   public void removeAllowedTagSet(TIFFTagSet tagSet) {
      if (tagSet == null) {
         throw new IllegalArgumentException("tagSet == null!");
      } else {
         this.allowedTagSets.remove(tagSet);
      }
   }

   public List getAllowedTagSets() {
      return this.allowedTagSets;
   }

   public void setTIFFDecompressor(TIFFDecompressor decompressor) {
      this.decompressor = decompressor;
   }

   public TIFFDecompressor getTIFFDecompressor() {
      return this.decompressor;
   }

   public void setColorConverter(TIFFColorConverter colorConverter) {
      this.colorConverter = colorConverter;
   }

   public TIFFColorConverter getColorConverter() {
      return this.colorConverter;
   }
}
