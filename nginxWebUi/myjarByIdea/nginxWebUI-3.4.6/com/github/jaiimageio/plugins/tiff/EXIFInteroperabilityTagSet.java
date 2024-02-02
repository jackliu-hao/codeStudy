package com.github.jaiimageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

public class EXIFInteroperabilityTagSet extends TIFFTagSet {
   public static final int TAG_INTEROPERABILITY_INDEX = 1;
   public static final String INTEROPERABILITY_INDEX_R98 = "R98";
   public static final String INTEROPERABILITY_INDEX_THM = "THM";
   private static EXIFInteroperabilityTagSet theInstance = null;
   private static List tags;

   private static void initTags() {
      tags = new ArrayList(42);
      tags.add(new InteroperabilityIndex());
   }

   private EXIFInteroperabilityTagSet() {
      super(tags);
   }

   public static synchronized EXIFInteroperabilityTagSet getInstance() {
      if (theInstance == null) {
         initTags();
         theInstance = new EXIFInteroperabilityTagSet();
         tags = null;
      }

      return theInstance;
   }

   static class InteroperabilityIndex extends TIFFTag {
      public InteroperabilityIndex() {
         super("InteroperabilityIndex", 1, 4);
      }
   }
}
