package com.github.jaiimageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

public class EXIFParentTIFFTagSet extends TIFFTagSet {
   private static EXIFParentTIFFTagSet theInstance = null;
   public static final int TAG_EXIF_IFD_POINTER = 34665;
   public static final int TAG_GPS_INFO_IFD_POINTER = 34853;
   private static List tags;

   private static void initTags() {
      tags = new ArrayList(1);
      tags.add(new EXIFIFDPointer());
      tags.add(new GPSInfoIFDPointer());
   }

   private EXIFParentTIFFTagSet() {
      super(tags);
   }

   public static synchronized EXIFParentTIFFTagSet getInstance() {
      if (theInstance == null) {
         initTags();
         theInstance = new EXIFParentTIFFTagSet();
         tags = null;
      }

      return theInstance;
   }

   static class GPSInfoIFDPointer extends TIFFTag {
      public GPSInfoIFDPointer() {
         super("GPSInfoIFDPointer", 34853, 16, EXIFGPSTagSet.getInstance());
      }
   }

   static class EXIFIFDPointer extends TIFFTag {
      public EXIFIFDPointer() {
         super("EXIFIFDPointer", 34665, 16, EXIFTIFFTagSet.getInstance());
      }
   }
}
