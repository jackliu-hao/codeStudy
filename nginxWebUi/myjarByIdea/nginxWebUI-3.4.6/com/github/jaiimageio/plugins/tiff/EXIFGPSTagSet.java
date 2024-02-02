package com.github.jaiimageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

public class EXIFGPSTagSet extends TIFFTagSet {
   private static EXIFGPSTagSet theInstance = null;
   public static final int TAG_GPS_VERSION_ID = 0;
   public static byte[] GPS_VERSION_2_2 = new byte[]{50, 50, 48, 48};
   public static final int TAG_GPS_LATITUDE_REF = 1;
   public static final int TAG_GPS_LATITUDE = 2;
   public static final int TAG_GPS_LONGITUDE_REF = 3;
   public static final int TAG_GPS_LONGITUDE = 4;
   public static final int TAG_GPS_ALTITUDE_REF = 5;
   public static final int TAG_GPS_ALTITUDE = 6;
   public static final int TAG_GPS_TIME_STAMP = 7;
   public static final int TAG_GPS_SATELLITES = 8;
   public static final int TAG_GPS_STATUS = 9;
   public static final int TAG_GPS_MEASURE_MODE = 10;
   public static final int TAG_GPS_DOP = 11;
   public static final int TAG_GPS_SPEED_REF = 12;
   public static final int TAG_GPS_SPEED = 13;
   public static final int TAG_GPS_TRACK_REF = 14;
   public static final int TAG_GPS_TRACK = 15;
   public static final int TAG_GPS_IMG_DIRECTION_REF = 16;
   public static final int TAG_GPS_IMG_DIRECTION = 17;
   public static final int TAG_GPS_MAP_DATUM = 18;
   public static final int TAG_GPS_DEST_LATITUDE_REF = 19;
   public static final int TAG_GPS_DEST_LATITUDE = 20;
   public static final int TAG_GPS_DEST_LONGITUDE_REF = 21;
   public static final int TAG_GPS_DEST_LONGITUDE = 22;
   public static final int TAG_GPS_DEST_BEARING_REF = 23;
   public static final int TAG_GPS_DEST_BEARING = 24;
   public static final int TAG_GPS_DEST_DISTANCE_REF = 25;
   public static final int TAG_GPS_DEST_DISTANCE = 26;
   public static final int TAG_GPS_PROCESSING_METHOD = 27;
   public static final int TAG_GPS_AREA_INFORMATION = 28;
   public static final int TAG_GPS_DATE_STAMP = 29;
   public static final int TAG_GPS_DIFFERENTIAL = 30;
   public static final String LATITUDE_REF_NORTH = "N";
   public static final String LATITUDE_REF_SOUTH = "S";
   public static final String LONGITUDE_REF_EAST = "E";
   public static final String LONGITUDE_REF_WEST = "W";
   public static final int ALTITUDE_REF_SEA_LEVEL = 0;
   public static final int ALTITUDE_REF_SEA_LEVEL_REFERENCE = 1;
   public static final String STATUS_MEASUREMENT_IN_PROGRESS = "A";
   public static final String STATUS_MEASUREMENT_INTEROPERABILITY = "V";
   public static final String MEASURE_MODE_2D = "2";
   public static final String MEASURE_MODE_3D = "3";
   public static final String SPEED_REF_KILOMETERS_PER_HOUR = "K";
   public static final String SPEED_REF_MILES_PER_HOUR = "M";
   public static final String SPEED_REF_KNOTS = "N";
   public static final String DIRECTION_REF_TRUE = "T";
   public static final String DIRECTION_REF_MAGNETIC = "M";
   public static final String DEST_DISTANCE_REF_KILOMETERS = "K";
   public static final String DEST_DISTANCE_REF_MILES = "M";
   public static final String DEST_DISTANCE_REF_KNOTS = "N";
   public static int DIFFERENTIAL_CORRECTION_NONE = 0;
   public static int DIFFERENTIAL_CORRECTION_APPLIED = 1;

   private static List initTags() {
      ArrayList tags = new ArrayList(31);
      tags.add(new GPSVersionID());
      tags.add(new GPSLatitudeRef());
      tags.add(new GPSLatitude());
      tags.add(new GPSLongitudeRef());
      tags.add(new GPSLongitude());
      tags.add(new GPSAltitudeRef());
      tags.add(new GPSAltitude());
      tags.add(new GPSTimeStamp());
      tags.add(new GPSSatellites());
      tags.add(new GPSStatus());
      tags.add(new GPSMeasureMode());
      tags.add(new GPSDOP());
      tags.add(new GPSSpeedRef());
      tags.add(new GPSSpeed());
      tags.add(new GPSTrackRef());
      tags.add(new GPSTrack());
      tags.add(new GPSImgDirectionRef());
      tags.add(new GPSImgDirection());
      tags.add(new GPSMapDatum());
      tags.add(new GPSDestLatitudeRef());
      tags.add(new GPSDestLatitude());
      tags.add(new GPSDestLongitudeRef());
      tags.add(new GPSDestLongitude());
      tags.add(new GPSDestBearingRef());
      tags.add(new GPSDestBearing());
      tags.add(new GPSDestDistanceRef());
      tags.add(new GPSDestDistance());
      tags.add(new GPSProcessingMethod());
      tags.add(new GPSAreaInformation());
      tags.add(new GPSDateStamp());
      tags.add(new GPSDifferential());
      return tags;
   }

   private EXIFGPSTagSet() {
      super(initTags());
   }

   public static synchronized EXIFGPSTagSet getInstance() {
      if (theInstance == null) {
         theInstance = new EXIFGPSTagSet();
      }

      return theInstance;
   }

   static class GPSDifferential extends TIFFTag {
      public GPSDifferential() {
         super("GPSDifferential", 30, 8);
         this.addValueName(EXIFGPSTagSet.DIFFERENTIAL_CORRECTION_NONE, "Measurement without differential correction");
         this.addValueName(EXIFGPSTagSet.DIFFERENTIAL_CORRECTION_APPLIED, "Differential correction applied");
      }
   }

   static class GPSDateStamp extends TIFFTag {
      public GPSDateStamp() {
         super("GPSDateStamp", 29, 4);
      }
   }

   static class GPSAreaInformation extends TIFFTag {
      public GPSAreaInformation() {
         super("GPSAreaInformation", 28, 128);
      }
   }

   static class GPSProcessingMethod extends TIFFTag {
      public GPSProcessingMethod() {
         super("GPSProcessingMethod", 27, 128);
      }
   }

   static class GPSDestDistance extends TIFFTag {
      public GPSDestDistance() {
         super("GPSDestDistance", 26, 32);
      }
   }

   static class GPSDestDistanceRef extends TIFFTag {
      public GPSDestDistanceRef() {
         super("GPSDestDistanceRef", 25, 4);
      }
   }

   static class GPSDestBearing extends TIFFTag {
      public GPSDestBearing() {
         super("GPSDestBearing", 24, 32);
      }
   }

   static class GPSDestBearingRef extends TIFFTag {
      public GPSDestBearingRef() {
         super("GPSDestBearingRef", 23, 4);
      }
   }

   static class GPSDestLongitude extends TIFFTag {
      public GPSDestLongitude() {
         super("GPSDestLongitude", 22, 32);
      }
   }

   static class GPSDestLongitudeRef extends TIFFTag {
      public GPSDestLongitudeRef() {
         super("GPSDestLongitudeRef", 21, 4);
      }
   }

   static class GPSDestLatitude extends TIFFTag {
      public GPSDestLatitude() {
         super("GPSDestLatitude", 20, 32);
      }
   }

   static class GPSDestLatitudeRef extends TIFFTag {
      public GPSDestLatitudeRef() {
         super("GPSDestLatitudeRef", 19, 4);
      }
   }

   static class GPSMapDatum extends TIFFTag {
      public GPSMapDatum() {
         super("GPSMapDatum", 18, 4);
      }
   }

   static class GPSImgDirection extends TIFFTag {
      public GPSImgDirection() {
         super("GPSImgDirection", 17, 32);
      }
   }

   static class GPSImgDirectionRef extends TIFFTag {
      public GPSImgDirectionRef() {
         super("GPSImgDirectionRef", 16, 4);
      }
   }

   static class GPSTrack extends TIFFTag {
      public GPSTrack() {
         super("GPSTrack", 15, 32);
      }
   }

   static class GPSTrackRef extends TIFFTag {
      public GPSTrackRef() {
         super("GPSTrackRef", 14, 4);
      }
   }

   static class GPSSpeed extends TIFFTag {
      public GPSSpeed() {
         super("GPSSpeed", 13, 32);
      }
   }

   static class GPSSpeedRef extends TIFFTag {
      public GPSSpeedRef() {
         super("GPSSpeedRef", 12, 4);
      }
   }

   static class GPSDOP extends TIFFTag {
      public GPSDOP() {
         super("GPSDOP", 11, 32);
      }
   }

   static class GPSMeasureMode extends TIFFTag {
      public GPSMeasureMode() {
         super("GPSMeasureMode", 10, 4);
      }
   }

   static class GPSStatus extends TIFFTag {
      public GPSStatus() {
         super("GPSStatus", 9, 4);
      }
   }

   static class GPSSatellites extends TIFFTag {
      public GPSSatellites() {
         super("GPSSatellites", 8, 4);
      }
   }

   static class GPSTimeStamp extends TIFFTag {
      public GPSTimeStamp() {
         super("GPSTimeStamp", 7, 32);
      }
   }

   static class GPSAltitude extends TIFFTag {
      public GPSAltitude() {
         super("GPSAltitude", 6, 32);
      }
   }

   static class GPSAltitudeRef extends TIFFTag {
      public GPSAltitudeRef() {
         super("GPSAltitudeRef", 5, 2);
         this.addValueName(0, "Sea level");
         this.addValueName(1, "Sea level reference (negative value)");
      }
   }

   static class GPSLongitude extends TIFFTag {
      public GPSLongitude() {
         super("GPSLongitude", 4, 32);
      }
   }

   static class GPSLongitudeRef extends TIFFTag {
      public GPSLongitudeRef() {
         super("GPSLongitudeRef", 3, 4);
      }
   }

   static class GPSLatitude extends TIFFTag {
      public GPSLatitude() {
         super("GPSLatitude", 2, 32);
      }
   }

   static class GPSLatitudeRef extends TIFFTag {
      public GPSLatitudeRef() {
         super("GPSLatitudeRef", 1, 4);
      }
   }

   static class GPSVersionID extends TIFFTag {
      public GPSVersionID() {
         super("GPSVersionID", 0, 2);
      }
   }
}
