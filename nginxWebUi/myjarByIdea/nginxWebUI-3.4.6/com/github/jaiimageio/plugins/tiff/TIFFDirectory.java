package com.github.jaiimageio.plugins.tiff;

import com.github.jaiimageio.impl.plugins.tiff.TIFFIFD;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageMetadata;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;

public class TIFFDirectory implements Cloneable {
   private static final int MAX_LOW_FIELD_TAG_NUM = 532;
   private List tagSets;
   private TIFFTag parentTag;
   private TIFFField[] lowFields = new TIFFField[533];
   private int numLowFields = 0;
   private Map highFields = new TreeMap();

   public static TIFFDirectory createFromMetadata(IIOMetadata tiffImageMetadata) throws IIOInvalidTreeException {
      if (tiffImageMetadata == null) {
         throw new IllegalArgumentException("tiffImageMetadata == null");
      } else {
         TIFFImageMetadata tim;
         if (tiffImageMetadata instanceof TIFFImageMetadata) {
            tim = (TIFFImageMetadata)tiffImageMetadata;
         } else {
            ArrayList l = new ArrayList(1);
            l.add(BaselineTIFFTagSet.getInstance());
            tim = new TIFFImageMetadata(l);
            String formatName = null;
            if ("com_sun_media_imageio_plugins_tiff_image_1.0".equals(tiffImageMetadata.getNativeMetadataFormatName())) {
               formatName = "com_sun_media_imageio_plugins_tiff_image_1.0";
            } else {
               String[] extraNames = tiffImageMetadata.getExtraMetadataFormatNames();
               if (extraNames != null) {
                  for(int i = 0; i < extraNames.length; ++i) {
                     if ("com_sun_media_imageio_plugins_tiff_image_1.0".equals(extraNames[i])) {
                        formatName = extraNames[i];
                        break;
                     }
                  }
               }

               if (formatName == null) {
                  if (!tiffImageMetadata.isStandardMetadataFormatSupported()) {
                     throw new IllegalArgumentException("Parameter does not support required metadata format!");
                  }

                  formatName = "javax_imageio_1.0";
               }
            }

            tim.setFromTree(formatName, tiffImageMetadata.getAsTree(formatName));
         }

         return tim.getRootIFD();
      }
   }

   private static TIFFIFD getDirectoryAsIFD(TIFFDirectory dir) {
      if (dir instanceof TIFFIFD) {
         return (TIFFIFD)dir;
      } else {
         TIFFIFD ifd = new TIFFIFD(Arrays.asList(dir.getTagSets()), dir.getParentTag());
         TIFFField[] fields = dir.getTIFFFields();
         int numFields = fields.length;

         for(int i = 0; i < numFields; ++i) {
            TIFFField f = fields[i];
            TIFFTag tag = f.getTag();
            if (tag.isIFDPointer()) {
               TIFFDirectory subIFD = getDirectoryAsIFD((TIFFDirectory)f.getData());
               f = new TIFFField(tag, f.getType(), f.getCount(), subIFD);
            }

            ifd.addTIFFField(f);
         }

         return ifd;
      }
   }

   public TIFFDirectory(TIFFTagSet[] tagSets, TIFFTag parentTag) {
      if (tagSets == null) {
         throw new IllegalArgumentException("tagSets == null!");
      } else {
         this.tagSets = new ArrayList(tagSets.length);
         int numTagSets = tagSets.length;

         for(int i = 0; i < numTagSets; ++i) {
            this.tagSets.add(tagSets[i]);
         }

         this.parentTag = parentTag;
      }
   }

   public TIFFTagSet[] getTagSets() {
      return (TIFFTagSet[])((TIFFTagSet[])this.tagSets.toArray(new TIFFTagSet[this.tagSets.size()]));
   }

   public void addTagSet(TIFFTagSet tagSet) {
      if (tagSet == null) {
         throw new IllegalArgumentException("tagSet == null");
      } else {
         if (!this.tagSets.contains(tagSet)) {
            this.tagSets.add(tagSet);
         }

      }
   }

   public void removeTagSet(TIFFTagSet tagSet) {
      if (tagSet == null) {
         throw new IllegalArgumentException("tagSet == null");
      } else {
         if (this.tagSets.contains(tagSet)) {
            this.tagSets.remove(tagSet);
         }

      }
   }

   public TIFFTag getParentTag() {
      return this.parentTag;
   }

   public TIFFTag getTag(int tagNumber) {
      return TIFFIFD.getTag(tagNumber, this.tagSets);
   }

   public int getNumTIFFFields() {
      return this.numLowFields + this.highFields.size();
   }

   public boolean containsTIFFField(int tagNumber) {
      return tagNumber >= 0 && tagNumber <= 532 && this.lowFields[tagNumber] != null || this.highFields.containsKey(new Integer(tagNumber));
   }

   public void addTIFFField(TIFFField f) {
      if (f == null) {
         throw new IllegalArgumentException("f == null");
      } else {
         int tagNumber = f.getTagNumber();
         if (tagNumber >= 0 && tagNumber <= 532) {
            if (this.lowFields[tagNumber] == null) {
               ++this.numLowFields;
            }

            this.lowFields[tagNumber] = f;
         } else {
            this.highFields.put(new Integer(tagNumber), f);
         }

      }
   }

   public TIFFField getTIFFField(int tagNumber) {
      TIFFField f;
      if (tagNumber >= 0 && tagNumber <= 532) {
         f = this.lowFields[tagNumber];
      } else {
         f = (TIFFField)this.highFields.get(new Integer(tagNumber));
      }

      return f;
   }

   public void removeTIFFField(int tagNumber) {
      if (tagNumber >= 0 && tagNumber <= 532) {
         if (this.lowFields[tagNumber] != null) {
            --this.numLowFields;
            this.lowFields[tagNumber] = null;
         }
      } else {
         this.highFields.remove(new Integer(tagNumber));
      }

   }

   public TIFFField[] getTIFFFields() {
      TIFFField[] fields = new TIFFField[this.numLowFields + this.highFields.size()];
      int nextIndex = 0;

      for(int i = 0; i <= 532; ++i) {
         if (this.lowFields[i] != null) {
            fields[nextIndex++] = this.lowFields[i];
            if (nextIndex == this.numLowFields) {
               break;
            }
         }
      }

      if (!this.highFields.isEmpty()) {
         for(Iterator keys = this.highFields.keySet().iterator(); keys.hasNext(); fields[nextIndex++] = (TIFFField)this.highFields.get(keys.next())) {
         }
      }

      return fields;
   }

   public void removeTIFFFields() {
      Arrays.fill(this.lowFields, (Object)null);
      this.numLowFields = 0;
      this.highFields.clear();
   }

   public IIOMetadata getAsMetadata() {
      return new TIFFImageMetadata(getDirectoryAsIFD(this));
   }

   public Object clone() {
      TIFFDirectory dir = new TIFFDirectory(this.getTagSets(), this.getParentTag());
      TIFFField[] fields = this.getTIFFFields();
      int numFields = fields.length;

      for(int i = 0; i < numFields; ++i) {
         dir.addTIFFField(fields[i]);
      }

      return dir;
   }
}
