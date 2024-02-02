/*      */ package com.github.jaiimageio.plugins.tiff;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class EXIFTIFFTagSet
/*      */   extends TIFFTagSet
/*      */ {
/*   64 */   private static EXIFTIFFTagSet theInstance = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_GPS_INFO_IFD_POINTER = 34853;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_INTEROPERABILITY_IFD_POINTER = 40965;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_EXIF_VERSION = 36864;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   94 */   public static byte[] EXIF_VERSION_2_1 = new byte[] { 48, 50, 49, 48 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  103 */   public static byte[] EXIF_VERSION_2_2 = new byte[] { 48, 50, 50, 48 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FLASHPIX_VERSION = 40960;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_COLOR_SPACE = 40961;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COLOR_SPACE_SRGB = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COLOR_SPACE_UNCALIBRATED = 65535;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_COMPONENTS_CONFIGURATION = 37121;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPONENTS_CONFIGURATION_DOES_NOT_EXIST = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPONENTS_CONFIGURATION_Y = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPONENTS_CONFIGURATION_CB = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPONENTS_CONFIGURATION_CR = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPONENTS_CONFIGURATION_R = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPONENTS_CONFIGURATION_G = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPONENTS_CONFIGURATION_B = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_COMPRESSED_BITS_PER_PIXEL = 37122;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_PIXEL_X_DIMENSION = 40962;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_PIXEL_Y_DIMENSION = 40963;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_MAKER_NOTE = 37500;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_MARKER_NOTE = 37500;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_USER_COMMENT = 37510;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_RELATED_SOUND_FILE = 40964;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_DATE_TIME_ORIGINAL = 36867;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_DATE_TIME_DIGITIZED = 36868;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SUB_SEC_TIME = 37520;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SUB_SEC_TIME_ORIGINAL = 37521;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SUB_SEC_TIME_DIGITIZED = 37522;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_EXPOSURE_TIME = 33434;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_F_NUMBER = 33437;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_EXPOSURE_PROGRAM = 34850;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_NOT_DEFINED = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_MANUAL = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_NORMAL_PROGRAM = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_APERTURE_PRIORITY = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_SHUTTER_PRIORITY = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_CREATIVE_PROGRAM = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_ACTION_PROGRAM = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_PORTRAIT_MODE = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_LANDSCAPE_MODE = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_PROGRAM_MAX_RESERVED = 255;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SPECTRAL_SENSITIVITY = 34852;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_ISO_SPEED_RATINGS = 34855;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_OECF = 34856;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SHUTTER_SPEED_VALUE = 37377;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_APERTURE_VALUE = 37378;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_BRIGHTNESS_VALUE = 37379;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_EXPOSURE_BIAS_VALUE = 37380;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_MAX_APERTURE_VALUE = 37381;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SUBJECT_DISTANCE = 37382;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_METERING_MODE = 37383;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_UNKNOWN = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_AVERAGE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_CENTER_WEIGHTED_AVERAGE = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_SPOT = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_MULTI_SPOT = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_PATTERN = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_PARTIAL = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_MIN_RESERVED = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_MAX_RESERVED = 254;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int METERING_MODE_OTHER = 255;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_LIGHT_SOURCE = 37384;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_UNKNOWN = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_DAYLIGHT = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_FLUORESCENT = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_TUNGSTEN = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_FLASH = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_FINE_WEATHER = 9;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_CLOUDY_WEATHER = 10;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_SHADE = 11;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_DAYLIGHT_FLUORESCENT = 12;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_DAY_WHITE_FLUORESCENT = 13;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_COOL_WHITE_FLUORESCENT = 14;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_WHITE_FLUORESCENT = 15;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_STANDARD_LIGHT_A = 17;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_STANDARD_LIGHT_B = 18;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_STANDARD_LIGHT_C = 19;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_D55 = 20;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_D65 = 21;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_D75 = 22;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_D50 = 23;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_ISO_STUDIO_TUNGSTEN = 24;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LIGHT_SOURCE_OTHER = 255;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FLASH = 37385;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_DID_NOT_FIRE = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_FIRED = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_STROBE_RETURN_LIGHT_NOT_DETECTED = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_STROBE_RETURN_LIGHT_DETECTED = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_MASK_FIRED = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_MASK_RETURN_NOT_DETECTED = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_MASK_RETURN_DETECTED = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_MASK_MODE_FLASH_FIRING = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_MASK_MODE_FLASH_SUPPRESSION = 16;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_MASK_MODE_AUTO = 24;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_MASK_FUNCTION_NOT_PRESENT = 32;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FLASH_MASK_RED_EYE_REDUCTION = 64;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FOCAL_LENGTH = 37386;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SUBJECT_AREA = 37396;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FLASH_ENERGY = 41483;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SPATIAL_FREQUENCY_RESPONSE = 41484;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FOCAL_PLANE_X_RESOLUTION = 41486;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FOCAL_PLANE_Y_RESOLUTION = 41487;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FOCAL_PLANE_RESOLUTION_UNIT = 41488;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FOCAL_PLANE_RESOLUTION_UNIT_NONE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FOCAL_PLANE_RESOLUTION_UNIT_INCH = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FOCAL_PLANE_RESOLUTION_UNIT_CENTIMETER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SUBJECT_LOCATION = 41492;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_EXPOSURE_INDEX = 41493;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SENSING_METHOD = 41495;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SENSING_METHOD_NOT_DEFINED = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SENSING_METHOD_ONE_CHIP_COLOR_AREA_SENSOR = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SENSING_METHOD_TWO_CHIP_COLOR_AREA_SENSOR = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SENSING_METHOD_THREE_CHIP_COLOR_AREA_SENSOR = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SENSING_METHOD_COLOR_SEQUENTIAL_AREA_SENSOR = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SENSING_METHOD_TRILINEAR_SENSOR = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SENSING_METHOD_COLOR_SEQUENTIAL_LINEAR_SENSOR = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FILE_SOURCE = 41728;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FILE_SOURCE_DSC = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SCENE_TYPE = 41729;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SCENE_TYPE_DSC = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_CFA_PATTERN = 41730;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_CUSTOM_RENDERED = 41985;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int CUSTOM_RENDERED_NORMAL = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int CUSTOM_RENDERED_CUSTOM = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_EXPOSURE_MODE = 41986;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_MODE_AUTO_EXPOSURE = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_MODE_MANUAL_EXPOSURE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXPOSURE_MODE_AUTO_BRACKET = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_WHITE_BALANCE = 41987;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int WHITE_BALANCE_AUTO = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int WHITE_BALANCE_MANUAL = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_DIGITAL_ZOOM_RATIO = 41988;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FOCAL_LENGTH_IN_35MM_FILM = 41989;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SCENE_CAPTURE_TYPE = 41990;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SCENE_CAPTURE_TYPE_STANDARD = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SCENE_CAPTURE_TYPE_LANDSCAPE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SCENE_CAPTURE_TYPE_PORTRAIT = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SCENE_CAPTURE_TYPE_NIGHT_SCENE = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_GAIN_CONTROL = 41991;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GAIN_CONTROL_NONE = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GAIN_CONTROL_LOW_GAIN_UP = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GAIN_CONTROL_HIGH_GAIN_UP = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GAIN_CONTROL_LOW_GAIN_DOWN = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GAIN_CONTROL_HIGH_GAIN_DOWN = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_CONTRAST = 41992;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int CONTRAST_NORMAL = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int CONTRAST_SOFT = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int CONTRAST_HARD = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SATURATION = 41993;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SATURATION_NORMAL = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SATURATION_LOW = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SATURATION_HIGH = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SHARPNESS = 41994;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SHARPNESS_NORMAL = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SHARPNESS_SOFT = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SHARPNESS_HARD = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_DEVICE_SETTING_DESCRIPTION = 41995;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SUBJECT_DISTANCE_RANGE = 41996;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SUBJECT_DISTANCE_RANGE_UNKNOWN = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SUBJECT_DISTANCE_RANGE_MACRO = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SUBJECT_DISTANCE_RANGE_CLOSE_VIEW = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SUBJECT_DISTANCE_RANGE_DISTANT_VIEW = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_IMAGE_UNIQUE_ID = 42016;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static List tags;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class EXIFVersion
/*      */     extends TIFFTag
/*      */   {
/*      */     public EXIFVersion() {
/* 1279 */       super("EXIFversion", 36864, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class FlashPixVersion
/*      */     extends TIFFTag
/*      */   {
/*      */     public FlashPixVersion() {
/* 1288 */       super("FlashPixVersion", 40960, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class ColorSpace
/*      */     extends TIFFTag
/*      */   {
/*      */     public ColorSpace() {
/* 1297 */       super("ColorSpace", 40961, 8);
/*      */ 
/*      */ 
/*      */       
/* 1301 */       addValueName(1, "sRGB");
/* 1302 */       addValueName(65535, "Uncalibrated");
/*      */     }
/*      */   }
/*      */   
/*      */   static class ComponentsConfiguration
/*      */     extends TIFFTag {
/*      */     public ComponentsConfiguration() {
/* 1309 */       super("ComponentsConfiguration", 37121, 128);
/*      */ 
/*      */ 
/*      */       
/* 1313 */       addValueName(0, "DoesNotExist");
/*      */       
/* 1315 */       addValueName(1, "Y");
/* 1316 */       addValueName(2, "Cb");
/* 1317 */       addValueName(3, "Cr");
/* 1318 */       addValueName(4, "R");
/* 1319 */       addValueName(5, "G");
/* 1320 */       addValueName(6, "B");
/*      */     }
/*      */   }
/*      */   
/*      */   static class CompressedBitsPerPixel
/*      */     extends TIFFTag {
/*      */     public CompressedBitsPerPixel() {
/* 1327 */       super("CompressedBitsPerPixel", 37122, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class PixelXDimension
/*      */     extends TIFFTag
/*      */   {
/*      */     public PixelXDimension() {
/* 1336 */       super("PixelXDimension", 40962, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class PixelYDimension
/*      */     extends TIFFTag
/*      */   {
/*      */     public PixelYDimension() {
/* 1346 */       super("PixelYDimension", 40963, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class MakerNote
/*      */     extends TIFFTag
/*      */   {
/*      */     public MakerNote() {
/* 1356 */       super("MakerNote", 37500, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class UserComment
/*      */     extends TIFFTag
/*      */   {
/*      */     public UserComment() {
/* 1365 */       super("UserComment", 37510, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class RelatedSoundFile
/*      */     extends TIFFTag
/*      */   {
/*      */     public RelatedSoundFile() {
/* 1374 */       super("RelatedSoundFile", 40964, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class DateTimeOriginal
/*      */     extends TIFFTag
/*      */   {
/*      */     public DateTimeOriginal() {
/* 1383 */       super("DateTimeOriginal", 36867, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class DateTimeDigitized
/*      */     extends TIFFTag
/*      */   {
/*      */     public DateTimeDigitized() {
/* 1392 */       super("DateTimeDigitized", 36868, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SubSecTime
/*      */     extends TIFFTag
/*      */   {
/*      */     public SubSecTime() {
/* 1401 */       super("SubSecTime", 37520, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SubSecTimeOriginal
/*      */     extends TIFFTag
/*      */   {
/*      */     public SubSecTimeOriginal() {
/* 1410 */       super("SubSecTimeOriginal", 37521, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SubSecTimeDigitized
/*      */     extends TIFFTag
/*      */   {
/*      */     public SubSecTimeDigitized() {
/* 1419 */       super("SubSecTimeDigitized", 37522, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class ExposureTime
/*      */     extends TIFFTag
/*      */   {
/*      */     public ExposureTime() {
/* 1428 */       super("ExposureTime", 33434, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class FNumber
/*      */     extends TIFFTag
/*      */   {
/*      */     public FNumber() {
/* 1437 */       super("FNumber", 33437, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class ExposureProgram
/*      */     extends TIFFTag
/*      */   {
/*      */     public ExposureProgram() {
/* 1446 */       super("ExposureProgram", 34850, 8);
/*      */ 
/*      */ 
/*      */       
/* 1450 */       addValueName(0, "Not Defined");
/* 1451 */       addValueName(1, "Manual");
/* 1452 */       addValueName(2, "Normal Program");
/* 1453 */       addValueName(3, "Aperture Priority");
/*      */       
/* 1455 */       addValueName(4, "Shutter Priority");
/*      */       
/* 1457 */       addValueName(5, "Creative Program");
/*      */       
/* 1459 */       addValueName(6, "Action Program");
/* 1460 */       addValueName(7, "Portrait Mode");
/* 1461 */       addValueName(8, "Landscape Mode");
/*      */     }
/*      */   }
/*      */   
/*      */   static class SpectralSensitivity extends TIFFTag {
/*      */     public SpectralSensitivity() {
/* 1467 */       super("SpectralSensitivity", 34852, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class ISOSpeedRatings
/*      */     extends TIFFTag
/*      */   {
/*      */     public ISOSpeedRatings() {
/* 1476 */       super("ISOSpeedRatings", 34855, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class OECF
/*      */     extends TIFFTag
/*      */   {
/*      */     public OECF() {
/* 1485 */       super("OECF", 34856, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class ShutterSpeedValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public ShutterSpeedValue() {
/* 1494 */       super("ShutterSpeedValue", 37377, 1024);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class ApertureValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public ApertureValue() {
/* 1503 */       super("ApertureValue", 37378, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class BrightnessValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public BrightnessValue() {
/* 1512 */       super("BrightnessValue", 37379, 1024);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class ExposureBiasValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public ExposureBiasValue() {
/* 1521 */       super("ExposureBiasValue", 37380, 1024);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class MaxApertureValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public MaxApertureValue() {
/* 1530 */       super("MaxApertureValue", 37381, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SubjectDistance
/*      */     extends TIFFTag
/*      */   {
/*      */     public SubjectDistance() {
/* 1539 */       super("SubjectDistance", 37382, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class MeteringMode
/*      */     extends TIFFTag
/*      */   {
/*      */     public MeteringMode() {
/* 1548 */       super("MeteringMode", 37383, 8);
/*      */ 
/*      */ 
/*      */       
/* 1552 */       addValueName(0, "Unknown");
/* 1553 */       addValueName(1, "Average");
/* 1554 */       addValueName(2, "CenterWeightedAverage");
/*      */       
/* 1556 */       addValueName(3, "Spot");
/* 1557 */       addValueName(4, "MultiSpot");
/* 1558 */       addValueName(5, "Pattern");
/* 1559 */       addValueName(6, "Partial");
/* 1560 */       addValueName(255, "Other");
/*      */     }
/*      */   }
/*      */   
/*      */   static class LightSource
/*      */     extends TIFFTag {
/*      */     public LightSource() {
/* 1567 */       super("LightSource", 37384, 8);
/*      */ 
/*      */ 
/*      */       
/* 1571 */       addValueName(0, "Unknown");
/* 1572 */       addValueName(1, "Daylight");
/* 1573 */       addValueName(2, "Fluorescent");
/* 1574 */       addValueName(3, "Tungsten");
/* 1575 */       addValueName(17, "Standard Light A");
/* 1576 */       addValueName(18, "Standard Light B");
/* 1577 */       addValueName(19, "Standard Light C");
/* 1578 */       addValueName(20, "D55");
/* 1579 */       addValueName(21, "D65");
/* 1580 */       addValueName(22, "D75");
/* 1581 */       addValueName(255, "Other");
/*      */     }
/*      */   }
/*      */   
/*      */   static class Flash
/*      */     extends TIFFTag {
/*      */     public Flash() {
/* 1588 */       super("Flash", 37385, 8);
/*      */ 
/*      */ 
/*      */       
/* 1592 */       addValueName(0, "Flash Did Not Fire");
/* 1593 */       addValueName(1, "Flash Fired");
/* 1594 */       addValueName(5, "Strobe Return Light Not Detected");
/*      */       
/* 1596 */       addValueName(7, "Strobe Return Light Detected");
/*      */     }
/*      */   }
/*      */   
/*      */   static class FocalLength
/*      */     extends TIFFTag
/*      */   {
/*      */     public FocalLength() {
/* 1604 */       super("FocalLength", 37386, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SubjectArea
/*      */     extends TIFFTag
/*      */   {
/*      */     public SubjectArea() {
/* 1613 */       super("SubjectArea", 37396, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class FlashEnergy
/*      */     extends TIFFTag
/*      */   {
/*      */     public FlashEnergy() {
/* 1622 */       super("FlashEnergy", 41483, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SpatialFrequencyResponse
/*      */     extends TIFFTag
/*      */   {
/*      */     public SpatialFrequencyResponse() {
/* 1631 */       super("SpatialFrequencyResponse", 41484, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class FocalPlaneXResolution
/*      */     extends TIFFTag
/*      */   {
/*      */     public FocalPlaneXResolution() {
/* 1640 */       super("FocalPlaneXResolution", 41486, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class FocalPlaneYResolution
/*      */     extends TIFFTag
/*      */   {
/*      */     public FocalPlaneYResolution() {
/* 1649 */       super("FocalPlaneYResolution", 41487, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class FocalPlaneResolutionUnit
/*      */     extends TIFFTag
/*      */   {
/*      */     public FocalPlaneResolutionUnit() {
/* 1658 */       super("FocalPlaneResolutionUnit", 41488, 8);
/*      */ 
/*      */ 
/*      */       
/* 1662 */       addValueName(1, "None");
/* 1663 */       addValueName(2, "Inch");
/* 1664 */       addValueName(3, "Centimeter");
/*      */     }
/*      */   }
/*      */   
/*      */   static class SubjectLocation
/*      */     extends TIFFTag {
/*      */     public SubjectLocation() {
/* 1671 */       super("SubjectLocation", 41492, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class ExposureIndex
/*      */     extends TIFFTag
/*      */   {
/*      */     public ExposureIndex() {
/* 1680 */       super("ExposureIndex", 41493, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SensingMethod
/*      */     extends TIFFTag
/*      */   {
/*      */     public SensingMethod() {
/* 1689 */       super("SensingMethod", 41495, 8);
/*      */ 
/*      */ 
/*      */       
/* 1693 */       addValueName(1, "Not Defined");
/* 1694 */       addValueName(2, "One-chip color area sensor");
/*      */       
/* 1696 */       addValueName(3, "Two-chip color area sensor");
/*      */       
/* 1698 */       addValueName(4, "Three-chip color area sensor");
/*      */       
/* 1700 */       addValueName(5, "Color sequential area sensor");
/*      */       
/* 1702 */       addValueName(7, "Trilinear sensor");
/* 1703 */       addValueName(8, "Color sequential linear sensor");
/*      */     }
/*      */   }
/*      */   
/*      */   static class FileSource
/*      */     extends TIFFTag
/*      */   {
/*      */     public FileSource() {
/* 1711 */       super("FileSource", 41728, 128);
/*      */ 
/*      */ 
/*      */       
/* 1715 */       addValueName(3, "DSC");
/*      */     }
/*      */   }
/*      */   
/*      */   static class SceneType
/*      */     extends TIFFTag {
/*      */     public SceneType() {
/* 1722 */       super("SceneType", 41729, 128);
/*      */ 
/*      */ 
/*      */       
/* 1726 */       addValueName(1, "A directly photographed image");
/*      */     }
/*      */   }
/*      */   
/*      */   static class CFAPattern
/*      */     extends TIFFTag {
/*      */     public CFAPattern() {
/* 1733 */       super("CFAPattern", 41730, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class CustomRendered
/*      */     extends TIFFTag
/*      */   {
/*      */     public CustomRendered() {
/* 1742 */       super("CustomRendered", 41985, 8);
/*      */ 
/*      */ 
/*      */       
/* 1746 */       addValueName(0, "Normal process");
/* 1747 */       addValueName(1, "Custom process");
/*      */     }
/*      */   }
/*      */   
/*      */   static class ExposureMode
/*      */     extends TIFFTag {
/*      */     public ExposureMode() {
/* 1754 */       super("ExposureMode", 41986, 8);
/*      */ 
/*      */ 
/*      */       
/* 1758 */       addValueName(0, "Auto exposure");
/* 1759 */       addValueName(1, "Manual exposure");
/* 1760 */       addValueName(2, "Auto bracket");
/*      */     }
/*      */   }
/*      */   
/*      */   static class WhiteBalance
/*      */     extends TIFFTag {
/*      */     public WhiteBalance() {
/* 1767 */       super("WhiteBalance", 41987, 8);
/*      */ 
/*      */ 
/*      */       
/* 1771 */       addValueName(0, "Auto white balance");
/* 1772 */       addValueName(1, "Manual white balance");
/*      */     }
/*      */   }
/*      */   
/*      */   static class DigitalZoomRatio
/*      */     extends TIFFTag {
/*      */     public DigitalZoomRatio() {
/* 1779 */       super("DigitalZoomRatio", 41988, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class FocalLengthIn35mmFilm
/*      */     extends TIFFTag
/*      */   {
/*      */     public FocalLengthIn35mmFilm() {
/* 1788 */       super("FocalLengthIn35mmFilm", 41989, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SceneCaptureType
/*      */     extends TIFFTag
/*      */   {
/*      */     public SceneCaptureType() {
/* 1797 */       super("SceneCaptureType", 41990, 8);
/*      */ 
/*      */ 
/*      */       
/* 1801 */       addValueName(0, "Standard");
/* 1802 */       addValueName(1, "Landscape");
/* 1803 */       addValueName(2, "Portrait");
/* 1804 */       addValueName(3, "Night scene");
/*      */     }
/*      */   }
/*      */   
/*      */   static class GainControl
/*      */     extends TIFFTag {
/*      */     public GainControl() {
/* 1811 */       super("GainControl", 41991, 8);
/*      */ 
/*      */ 
/*      */       
/* 1815 */       addValueName(0, "None");
/* 1816 */       addValueName(1, "Low gain up");
/* 1817 */       addValueName(2, "High gain up");
/* 1818 */       addValueName(3, "Low gain down");
/* 1819 */       addValueName(4, "High gain down");
/*      */     }
/*      */   }
/*      */   
/*      */   static class Contrast
/*      */     extends TIFFTag {
/*      */     public Contrast() {
/* 1826 */       super("Contrast", 41992, 8);
/*      */ 
/*      */ 
/*      */       
/* 1830 */       addValueName(0, "Normal");
/* 1831 */       addValueName(1, "Soft");
/* 1832 */       addValueName(2, "Hard");
/*      */     }
/*      */   }
/*      */   
/*      */   static class Saturation
/*      */     extends TIFFTag {
/*      */     public Saturation() {
/* 1839 */       super("Saturation", 41993, 8);
/*      */ 
/*      */ 
/*      */       
/* 1843 */       addValueName(0, "Normal");
/* 1844 */       addValueName(1, "Low saturation");
/* 1845 */       addValueName(2, "High saturation");
/*      */     }
/*      */   }
/*      */   
/*      */   static class Sharpness
/*      */     extends TIFFTag {
/*      */     public Sharpness() {
/* 1852 */       super("Sharpness", 41994, 8);
/*      */ 
/*      */ 
/*      */       
/* 1856 */       addValueName(0, "Normal");
/* 1857 */       addValueName(1, "Soft");
/* 1858 */       addValueName(2, "Hard");
/*      */     }
/*      */   }
/*      */   
/*      */   static class DeviceSettingDescription
/*      */     extends TIFFTag {
/*      */     public DeviceSettingDescription() {
/* 1865 */       super("DeviceSettingDescription", 41995, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SubjectDistanceRange
/*      */     extends TIFFTag
/*      */   {
/*      */     public SubjectDistanceRange() {
/* 1874 */       super("SubjectDistanceRange", 41996, 8);
/*      */ 
/*      */ 
/*      */       
/* 1878 */       addValueName(0, "unknown");
/* 1879 */       addValueName(1, "Macro");
/* 1880 */       addValueName(2, "Close view");
/* 1881 */       addValueName(3, "Distant view");
/*      */     }
/*      */   }
/*      */   
/*      */   static class ImageUniqueID
/*      */     extends TIFFTag {
/*      */     public ImageUniqueID() {
/* 1888 */       super("ImageUniqueID", 42016, 4);
/*      */     }
/*      */   }
/*      */   
/*      */   static class InteroperabilityIFD
/*      */     extends TIFFTag
/*      */   {
/*      */     public InteroperabilityIFD() {
/* 1896 */       super("InteroperabilityIFD", 40965, 16, 
/*      */ 
/*      */           
/* 1899 */           EXIFInteroperabilityTagSet.getInstance());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void initTags() {
/* 1906 */     tags = new ArrayList(42);
/*      */     
/* 1908 */     tags.add(new EXIFVersion());
/* 1909 */     tags.add(new FlashPixVersion());
/* 1910 */     tags.add(new ColorSpace());
/* 1911 */     tags.add(new ComponentsConfiguration());
/* 1912 */     tags.add(new CompressedBitsPerPixel());
/* 1913 */     tags.add(new PixelXDimension());
/* 1914 */     tags.add(new PixelYDimension());
/* 1915 */     tags.add(new MakerNote());
/* 1916 */     tags.add(new UserComment());
/* 1917 */     tags.add(new RelatedSoundFile());
/* 1918 */     tags.add(new DateTimeOriginal());
/* 1919 */     tags.add(new DateTimeDigitized());
/* 1920 */     tags.add(new SubSecTime());
/* 1921 */     tags.add(new SubSecTimeOriginal());
/* 1922 */     tags.add(new SubSecTimeDigitized());
/* 1923 */     tags.add(new ExposureTime());
/* 1924 */     tags.add(new FNumber());
/* 1925 */     tags.add(new ExposureProgram());
/* 1926 */     tags.add(new SpectralSensitivity());
/* 1927 */     tags.add(new ISOSpeedRatings());
/* 1928 */     tags.add(new OECF());
/* 1929 */     tags.add(new ShutterSpeedValue());
/* 1930 */     tags.add(new ApertureValue());
/* 1931 */     tags.add(new BrightnessValue());
/* 1932 */     tags.add(new ExposureBiasValue());
/* 1933 */     tags.add(new MaxApertureValue());
/* 1934 */     tags.add(new SubjectDistance());
/* 1935 */     tags.add(new MeteringMode());
/* 1936 */     tags.add(new LightSource());
/* 1937 */     tags.add(new Flash());
/* 1938 */     tags.add(new FocalLength());
/* 1939 */     tags.add(new SubjectArea());
/* 1940 */     tags.add(new FlashEnergy());
/* 1941 */     tags.add(new SpatialFrequencyResponse());
/* 1942 */     tags.add(new FocalPlaneXResolution());
/* 1943 */     tags.add(new FocalPlaneYResolution());
/* 1944 */     tags.add(new FocalPlaneResolutionUnit());
/* 1945 */     tags.add(new SubjectLocation());
/* 1946 */     tags.add(new ExposureIndex());
/* 1947 */     tags.add(new SensingMethod());
/* 1948 */     tags.add(new FileSource());
/* 1949 */     tags.add(new SceneType());
/* 1950 */     tags.add(new CFAPattern());
/* 1951 */     tags.add(new CustomRendered());
/* 1952 */     tags.add(new ExposureMode());
/* 1953 */     tags.add(new WhiteBalance());
/* 1954 */     tags.add(new DigitalZoomRatio());
/* 1955 */     tags.add(new FocalLengthIn35mmFilm());
/* 1956 */     tags.add(new SceneCaptureType());
/* 1957 */     tags.add(new GainControl());
/* 1958 */     tags.add(new Contrast());
/* 1959 */     tags.add(new Saturation());
/* 1960 */     tags.add(new Sharpness());
/* 1961 */     tags.add(new DeviceSettingDescription());
/* 1962 */     tags.add(new SubjectDistanceRange());
/* 1963 */     tags.add(new ImageUniqueID());
/* 1964 */     tags.add(new InteroperabilityIFD());
/*      */   }
/*      */   
/*      */   private EXIFTIFFTagSet() {
/* 1968 */     super(tags);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static synchronized EXIFTIFFTagSet getInstance() {
/* 1977 */     if (theInstance == null) {
/* 1978 */       initTags();
/* 1979 */       theInstance = new EXIFTIFFTagSet();
/* 1980 */       tags = null;
/*      */     } 
/* 1982 */     return theInstance;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\EXIFTIFFTagSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */