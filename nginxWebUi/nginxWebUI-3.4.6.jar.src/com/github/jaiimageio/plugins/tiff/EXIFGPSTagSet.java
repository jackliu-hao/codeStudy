/*     */ package com.github.jaiimageio.plugins.tiff;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EXIFGPSTagSet
/*     */   extends TIFFTagSet
/*     */ {
/*  61 */   private static EXIFGPSTagSet theInstance = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_VERSION_ID = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   public static byte[] GPS_VERSION_2_2 = new byte[] { 50, 50, 48, 48 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_LATITUDE_REF = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_LATITUDE = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_LONGITUDE_REF = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_LONGITUDE = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_ALTITUDE_REF = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_ALTITUDE = 6;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_TIME_STAMP = 7;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_SATELLITES = 8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_STATUS = 9;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_MEASURE_MODE = 10;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DOP = 11;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_SPEED_REF = 12;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_SPEED = 13;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_TRACK_REF = 14;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_TRACK = 15;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_IMG_DIRECTION_REF = 16;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_IMG_DIRECTION = 17;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_MAP_DATUM = 18;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DEST_LATITUDE_REF = 19;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DEST_LATITUDE = 20;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DEST_LONGITUDE_REF = 21;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DEST_LONGITUDE = 22;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DEST_BEARING_REF = 23;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DEST_BEARING = 24;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DEST_DISTANCE_REF = 25;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DEST_DISTANCE = 26;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_PROCESSING_METHOD = 27;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_AREA_INFORMATION = 28;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DATE_STAMP = 29;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int TAG_GPS_DIFFERENTIAL = 30;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LATITUDE_REF_NORTH = "N";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LATITUDE_REF_SOUTH = "S";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LONGITUDE_REF_EAST = "E";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LONGITUDE_REF_WEST = "W";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int ALTITUDE_REF_SEA_LEVEL = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int ALTITUDE_REF_SEA_LEVEL_REFERENCE = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String STATUS_MEASUREMENT_IN_PROGRESS = "A";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String STATUS_MEASUREMENT_INTEROPERABILITY = "V";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String MEASURE_MODE_2D = "2";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String MEASURE_MODE_3D = "3";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SPEED_REF_KILOMETERS_PER_HOUR = "K";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SPEED_REF_MILES_PER_HOUR = "M";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SPEED_REF_KNOTS = "N";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DIRECTION_REF_TRUE = "T";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DIRECTION_REF_MAGNETIC = "M";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEST_DISTANCE_REF_KILOMETERS = "K";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEST_DISTANCE_REF_MILES = "M";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEST_DISTANCE_REF_KNOTS = "N";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 423 */   public static int DIFFERENTIAL_CORRECTION_NONE = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 430 */   public static int DIFFERENTIAL_CORRECTION_APPLIED = 1;
/*     */   
/*     */   static class GPSVersionID extends TIFFTag {
/*     */     public GPSVersionID() {
/* 434 */       super("GPSVersionID", 0, 2);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSLatitudeRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSLatitudeRef() {
/* 442 */       super("GPSLatitudeRef", 1, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSLatitude
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSLatitude() {
/* 450 */       super("GPSLatitude", 2, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSLongitudeRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSLongitudeRef() {
/* 458 */       super("GPSLongitudeRef", 3, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSLongitude
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSLongitude() {
/* 466 */       super("GPSLongitude", 4, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSAltitudeRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSAltitudeRef() {
/* 474 */       super("GPSAltitudeRef", 5, 2);
/*     */ 
/*     */ 
/*     */       
/* 478 */       addValueName(0, "Sea level");
/* 479 */       addValueName(1, "Sea level reference (negative value)");
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSAltitude
/*     */     extends TIFFTag {
/*     */     public GPSAltitude() {
/* 486 */       super("GPSAltitude", 6, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSTimeStamp
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSTimeStamp() {
/* 494 */       super("GPSTimeStamp", 7, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSSatellites
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSSatellites() {
/* 502 */       super("GPSSatellites", 8, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSStatus
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSStatus() {
/* 510 */       super("GPSStatus", 9, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSMeasureMode
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSMeasureMode() {
/* 518 */       super("GPSMeasureMode", 10, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDOP
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDOP() {
/* 526 */       super("GPSDOP", 11, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSSpeedRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSSpeedRef() {
/* 534 */       super("GPSSpeedRef", 12, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSSpeed
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSSpeed() {
/* 542 */       super("GPSSpeed", 13, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSTrackRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSTrackRef() {
/* 550 */       super("GPSTrackRef", 14, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSTrack
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSTrack() {
/* 558 */       super("GPSTrack", 15, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSImgDirectionRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSImgDirectionRef() {
/* 566 */       super("GPSImgDirectionRef", 16, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSImgDirection
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSImgDirection() {
/* 574 */       super("GPSImgDirection", 17, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSMapDatum
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSMapDatum() {
/* 582 */       super("GPSMapDatum", 18, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDestLatitudeRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDestLatitudeRef() {
/* 590 */       super("GPSDestLatitudeRef", 19, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDestLatitude
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDestLatitude() {
/* 598 */       super("GPSDestLatitude", 20, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDestLongitudeRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDestLongitudeRef() {
/* 606 */       super("GPSDestLongitudeRef", 21, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDestLongitude
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDestLongitude() {
/* 614 */       super("GPSDestLongitude", 22, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDestBearingRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDestBearingRef() {
/* 622 */       super("GPSDestBearingRef", 23, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDestBearing
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDestBearing() {
/* 630 */       super("GPSDestBearing", 24, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDestDistanceRef
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDestDistanceRef() {
/* 638 */       super("GPSDestDistanceRef", 25, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDestDistance
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDestDistance() {
/* 646 */       super("GPSDestDistance", 26, 32);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSProcessingMethod
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSProcessingMethod() {
/* 654 */       super("GPSProcessingMethod", 27, 128);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSAreaInformation
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSAreaInformation() {
/* 662 */       super("GPSAreaInformation", 28, 128);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDateStamp
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDateStamp() {
/* 670 */       super("GPSDateStamp", 29, 4);
/*     */     }
/*     */   }
/*     */   
/*     */   static class GPSDifferential
/*     */     extends TIFFTag
/*     */   {
/*     */     public GPSDifferential() {
/* 678 */       super("GPSDifferential", 30, 8);
/*     */ 
/*     */       
/* 681 */       addValueName(EXIFGPSTagSet.DIFFERENTIAL_CORRECTION_NONE, "Measurement without differential correction");
/*     */       
/* 683 */       addValueName(EXIFGPSTagSet.DIFFERENTIAL_CORRECTION_APPLIED, "Differential correction applied");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static List initTags() {
/* 690 */     ArrayList<GPSVersionID> tags = new ArrayList(31);
/*     */     
/* 692 */     tags.add(new GPSVersionID());
/* 693 */     tags.add(new GPSLatitudeRef());
/* 694 */     tags.add(new GPSLatitude());
/* 695 */     tags.add(new GPSLongitudeRef());
/* 696 */     tags.add(new GPSLongitude());
/* 697 */     tags.add(new GPSAltitudeRef());
/* 698 */     tags.add(new GPSAltitude());
/* 699 */     tags.add(new GPSTimeStamp());
/* 700 */     tags.add(new GPSSatellites());
/* 701 */     tags.add(new GPSStatus());
/* 702 */     tags.add(new GPSMeasureMode());
/* 703 */     tags.add(new GPSDOP());
/* 704 */     tags.add(new GPSSpeedRef());
/* 705 */     tags.add(new GPSSpeed());
/* 706 */     tags.add(new GPSTrackRef());
/* 707 */     tags.add(new GPSTrack());
/* 708 */     tags.add(new GPSImgDirectionRef());
/* 709 */     tags.add(new GPSImgDirection());
/* 710 */     tags.add(new GPSMapDatum());
/* 711 */     tags.add(new GPSDestLatitudeRef());
/* 712 */     tags.add(new GPSDestLatitude());
/* 713 */     tags.add(new GPSDestLongitudeRef());
/* 714 */     tags.add(new GPSDestLongitude());
/* 715 */     tags.add(new GPSDestBearingRef());
/* 716 */     tags.add(new GPSDestBearing());
/* 717 */     tags.add(new GPSDestDistanceRef());
/* 718 */     tags.add(new GPSDestDistance());
/* 719 */     tags.add(new GPSProcessingMethod());
/* 720 */     tags.add(new GPSAreaInformation());
/* 721 */     tags.add(new GPSDateStamp());
/* 722 */     tags.add(new GPSDifferential());
/* 723 */     return tags;
/*     */   }
/*     */   
/*     */   private EXIFGPSTagSet() {
/* 727 */     super(initTags());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized EXIFGPSTagSet getInstance() {
/* 736 */     if (theInstance == null) {
/* 737 */       theInstance = new EXIFGPSTagSet();
/*     */     }
/* 739 */     return theInstance;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\EXIFGPSTagSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */