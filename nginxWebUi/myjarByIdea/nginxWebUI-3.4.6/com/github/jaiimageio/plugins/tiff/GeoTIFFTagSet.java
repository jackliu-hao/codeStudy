package com.github.jaiimageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

public class GeoTIFFTagSet extends TIFFTagSet {
   private static GeoTIFFTagSet theInstance = null;
   public static final int TAG_MODEL_PIXEL_SCALE = 33550;
   public static final int TAG_MODEL_TRANSFORMATION = 34264;
   public static final int TAG_MODEL_TIE_POINT = 33922;
   public static final int TAG_GEO_KEY_DIRECTORY = 34735;
   public static final int TAG_GEO_DOUBLE_PARAMS = 34736;
   public static final int TAG_GEO_ASCII_PARAMS = 34737;
   private static List tags;

   private static void initTags() {
      tags = new ArrayList(42);
      tags.add(new ModelPixelScale());
      tags.add(new ModelTransformation());
      tags.add(new ModelTiePoint());
      tags.add(new GeoKeyDirectory());
      tags.add(new GeoDoubleParams());
      tags.add(new GeoAsciiParams());
   }

   private GeoTIFFTagSet() {
      super(tags);
   }

   public static synchronized GeoTIFFTagSet getInstance() {
      if (theInstance == null) {
         initTags();
         theInstance = new GeoTIFFTagSet();
         tags = null;
      }

      return theInstance;
   }

   static class GeoAsciiParams extends TIFFTag {
      public GeoAsciiParams() {
         super("GeoAsciiParams", 34737, 4);
      }
   }

   static class GeoDoubleParams extends TIFFTag {
      public GeoDoubleParams() {
         super("GeoDoubleParams", 34736, 4096);
      }
   }

   static class GeoKeyDirectory extends TIFFTag {
      public GeoKeyDirectory() {
         super("GeoKeyDirectory", 34735, 8);
      }
   }

   static class ModelTiePoint extends TIFFTag {
      public ModelTiePoint() {
         super("ModelTiePointTag", 33922, 4096);
      }
   }

   static class ModelTransformation extends TIFFTag {
      public ModelTransformation() {
         super("ModelTransformationTag", 34264, 4096);
      }
   }

   static class ModelPixelScale extends TIFFTag {
      public ModelPixelScale() {
         super("ModelPixelScaleTag", 33550, 4096);
      }
   }
}
