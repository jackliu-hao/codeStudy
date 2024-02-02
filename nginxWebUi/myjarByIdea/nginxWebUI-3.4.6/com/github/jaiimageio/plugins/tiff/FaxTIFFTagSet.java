package com.github.jaiimageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

public class FaxTIFFTagSet extends TIFFTagSet {
   private static FaxTIFFTagSet theInstance = null;
   public static final int TAG_BAD_FAX_LINES = 326;
   public static final int TAG_CLEAN_FAX_DATA = 327;
   public static final int CLEAN_FAX_DATA_NO_ERRORS = 0;
   public static final int CLEAN_FAX_DATA_ERRORS_CORRECTED = 1;
   public static final int CLEAN_FAX_DATA_ERRORS_UNCORRECTED = 2;
   public static final int TAG_CONSECUTIVE_BAD_LINES = 328;
   private static List tags;

   private static void initTags() {
      tags = new ArrayList(42);
      tags.add(new BadFaxLines());
      tags.add(new CleanFaxData());
      tags.add(new ConsecutiveBadFaxLines());
   }

   private FaxTIFFTagSet() {
      super(tags);
   }

   public static synchronized FaxTIFFTagSet getInstance() {
      if (theInstance == null) {
         initTags();
         theInstance = new FaxTIFFTagSet();
         tags = null;
      }

      return theInstance;
   }

   static class ConsecutiveBadFaxLines extends TIFFTag {
      public ConsecutiveBadFaxLines() {
         super("ConsecutiveBadFaxLines", 328, 24);
      }
   }

   static class CleanFaxData extends TIFFTag {
      public CleanFaxData() {
         super("CleanFaxData", 327, 8);
         this.addValueName(0, "No errors");
         this.addValueName(1, "Errors corrected");
         this.addValueName(2, "Errors uncorrected");
      }
   }

   static class BadFaxLines extends TIFFTag {
      public BadFaxLines() {
         super("BadFaxLines", 326, 24);
      }
   }
}
