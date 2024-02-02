package com.github.jaiimageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

public class EXIFTIFFTagSet extends TIFFTagSet {
   private static EXIFTIFFTagSet theInstance = null;
   /** @deprecated */
   public static final int TAG_GPS_INFO_IFD_POINTER = 34853;
   public static final int TAG_INTEROPERABILITY_IFD_POINTER = 40965;
   public static final int TAG_EXIF_VERSION = 36864;
   public static byte[] EXIF_VERSION_2_1 = new byte[]{48, 50, 49, 48};
   public static byte[] EXIF_VERSION_2_2 = new byte[]{48, 50, 50, 48};
   public static final int TAG_FLASHPIX_VERSION = 40960;
   public static final int TAG_COLOR_SPACE = 40961;
   public static final int COLOR_SPACE_SRGB = 1;
   public static final int COLOR_SPACE_UNCALIBRATED = 65535;
   public static final int TAG_COMPONENTS_CONFIGURATION = 37121;
   public static final int COMPONENTS_CONFIGURATION_DOES_NOT_EXIST = 0;
   public static final int COMPONENTS_CONFIGURATION_Y = 1;
   public static final int COMPONENTS_CONFIGURATION_CB = 2;
   public static final int COMPONENTS_CONFIGURATION_CR = 3;
   public static final int COMPONENTS_CONFIGURATION_R = 4;
   public static final int COMPONENTS_CONFIGURATION_G = 5;
   public static final int COMPONENTS_CONFIGURATION_B = 6;
   public static final int TAG_COMPRESSED_BITS_PER_PIXEL = 37122;
   public static final int TAG_PIXEL_X_DIMENSION = 40962;
   public static final int TAG_PIXEL_Y_DIMENSION = 40963;
   public static final int TAG_MAKER_NOTE = 37500;
   /** @deprecated */
   public static final int TAG_MARKER_NOTE = 37500;
   public static final int TAG_USER_COMMENT = 37510;
   public static final int TAG_RELATED_SOUND_FILE = 40964;
   public static final int TAG_DATE_TIME_ORIGINAL = 36867;
   public static final int TAG_DATE_TIME_DIGITIZED = 36868;
   public static final int TAG_SUB_SEC_TIME = 37520;
   public static final int TAG_SUB_SEC_TIME_ORIGINAL = 37521;
   public static final int TAG_SUB_SEC_TIME_DIGITIZED = 37522;
   public static final int TAG_EXPOSURE_TIME = 33434;
   public static final int TAG_F_NUMBER = 33437;
   public static final int TAG_EXPOSURE_PROGRAM = 34850;
   public static final int EXPOSURE_PROGRAM_NOT_DEFINED = 0;
   public static final int EXPOSURE_PROGRAM_MANUAL = 1;
   public static final int EXPOSURE_PROGRAM_NORMAL_PROGRAM = 2;
   public static final int EXPOSURE_PROGRAM_APERTURE_PRIORITY = 3;
   public static final int EXPOSURE_PROGRAM_SHUTTER_PRIORITY = 4;
   public static final int EXPOSURE_PROGRAM_CREATIVE_PROGRAM = 5;
   public static final int EXPOSURE_PROGRAM_ACTION_PROGRAM = 6;
   public static final int EXPOSURE_PROGRAM_PORTRAIT_MODE = 7;
   public static final int EXPOSURE_PROGRAM_LANDSCAPE_MODE = 8;
   public static final int EXPOSURE_PROGRAM_MAX_RESERVED = 255;
   public static final int TAG_SPECTRAL_SENSITIVITY = 34852;
   public static final int TAG_ISO_SPEED_RATINGS = 34855;
   public static final int TAG_OECF = 34856;
   public static final int TAG_SHUTTER_SPEED_VALUE = 37377;
   public static final int TAG_APERTURE_VALUE = 37378;
   public static final int TAG_BRIGHTNESS_VALUE = 37379;
   public static final int TAG_EXPOSURE_BIAS_VALUE = 37380;
   public static final int TAG_MAX_APERTURE_VALUE = 37381;
   public static final int TAG_SUBJECT_DISTANCE = 37382;
   public static final int TAG_METERING_MODE = 37383;
   public static final int METERING_MODE_UNKNOWN = 0;
   public static final int METERING_MODE_AVERAGE = 1;
   public static final int METERING_MODE_CENTER_WEIGHTED_AVERAGE = 2;
   public static final int METERING_MODE_SPOT = 3;
   public static final int METERING_MODE_MULTI_SPOT = 4;
   public static final int METERING_MODE_PATTERN = 5;
   public static final int METERING_MODE_PARTIAL = 6;
   public static final int METERING_MODE_MIN_RESERVED = 7;
   public static final int METERING_MODE_MAX_RESERVED = 254;
   public static final int METERING_MODE_OTHER = 255;
   public static final int TAG_LIGHT_SOURCE = 37384;
   public static final int LIGHT_SOURCE_UNKNOWN = 0;
   public static final int LIGHT_SOURCE_DAYLIGHT = 1;
   public static final int LIGHT_SOURCE_FLUORESCENT = 2;
   public static final int LIGHT_SOURCE_TUNGSTEN = 3;
   public static final int LIGHT_SOURCE_FLASH = 4;
   public static final int LIGHT_SOURCE_FINE_WEATHER = 9;
   public static final int LIGHT_SOURCE_CLOUDY_WEATHER = 10;
   public static final int LIGHT_SOURCE_SHADE = 11;
   public static final int LIGHT_SOURCE_DAYLIGHT_FLUORESCENT = 12;
   public static final int LIGHT_SOURCE_DAY_WHITE_FLUORESCENT = 13;
   public static final int LIGHT_SOURCE_COOL_WHITE_FLUORESCENT = 14;
   public static final int LIGHT_SOURCE_WHITE_FLUORESCENT = 15;
   public static final int LIGHT_SOURCE_STANDARD_LIGHT_A = 17;
   public static final int LIGHT_SOURCE_STANDARD_LIGHT_B = 18;
   public static final int LIGHT_SOURCE_STANDARD_LIGHT_C = 19;
   public static final int LIGHT_SOURCE_D55 = 20;
   public static final int LIGHT_SOURCE_D65 = 21;
   public static final int LIGHT_SOURCE_D75 = 22;
   public static final int LIGHT_SOURCE_D50 = 23;
   public static final int LIGHT_SOURCE_ISO_STUDIO_TUNGSTEN = 24;
   public static final int LIGHT_SOURCE_OTHER = 255;
   public static final int TAG_FLASH = 37385;
   public static final int FLASH_DID_NOT_FIRE = 0;
   public static final int FLASH_FIRED = 1;
   public static final int FLASH_STROBE_RETURN_LIGHT_NOT_DETECTED = 5;
   public static final int FLASH_STROBE_RETURN_LIGHT_DETECTED = 7;
   public static final int FLASH_MASK_FIRED = 1;
   public static final int FLASH_MASK_RETURN_NOT_DETECTED = 4;
   public static final int FLASH_MASK_RETURN_DETECTED = 6;
   public static final int FLASH_MASK_MODE_FLASH_FIRING = 8;
   public static final int FLASH_MASK_MODE_FLASH_SUPPRESSION = 16;
   public static final int FLASH_MASK_MODE_AUTO = 24;
   public static final int FLASH_MASK_FUNCTION_NOT_PRESENT = 32;
   public static final int FLASH_MASK_RED_EYE_REDUCTION = 64;
   public static final int TAG_FOCAL_LENGTH = 37386;
   public static final int TAG_SUBJECT_AREA = 37396;
   public static final int TAG_FLASH_ENERGY = 41483;
   public static final int TAG_SPATIAL_FREQUENCY_RESPONSE = 41484;
   public static final int TAG_FOCAL_PLANE_X_RESOLUTION = 41486;
   public static final int TAG_FOCAL_PLANE_Y_RESOLUTION = 41487;
   public static final int TAG_FOCAL_PLANE_RESOLUTION_UNIT = 41488;
   public static final int FOCAL_PLANE_RESOLUTION_UNIT_NONE = 1;
   public static final int FOCAL_PLANE_RESOLUTION_UNIT_INCH = 2;
   public static final int FOCAL_PLANE_RESOLUTION_UNIT_CENTIMETER = 3;
   public static final int TAG_SUBJECT_LOCATION = 41492;
   public static final int TAG_EXPOSURE_INDEX = 41493;
   public static final int TAG_SENSING_METHOD = 41495;
   public static final int SENSING_METHOD_NOT_DEFINED = 1;
   public static final int SENSING_METHOD_ONE_CHIP_COLOR_AREA_SENSOR = 2;
   public static final int SENSING_METHOD_TWO_CHIP_COLOR_AREA_SENSOR = 3;
   public static final int SENSING_METHOD_THREE_CHIP_COLOR_AREA_SENSOR = 4;
   public static final int SENSING_METHOD_COLOR_SEQUENTIAL_AREA_SENSOR = 5;
   public static final int SENSING_METHOD_TRILINEAR_SENSOR = 7;
   public static final int SENSING_METHOD_COLOR_SEQUENTIAL_LINEAR_SENSOR = 8;
   public static final int TAG_FILE_SOURCE = 41728;
   public static final int FILE_SOURCE_DSC = 3;
   public static final int TAG_SCENE_TYPE = 41729;
   public static final int SCENE_TYPE_DSC = 1;
   public static final int TAG_CFA_PATTERN = 41730;
   public static final int TAG_CUSTOM_RENDERED = 41985;
   public static final int CUSTOM_RENDERED_NORMAL = 0;
   public static final int CUSTOM_RENDERED_CUSTOM = 1;
   public static final int TAG_EXPOSURE_MODE = 41986;
   public static final int EXPOSURE_MODE_AUTO_EXPOSURE = 0;
   public static final int EXPOSURE_MODE_MANUAL_EXPOSURE = 1;
   public static final int EXPOSURE_MODE_AUTO_BRACKET = 2;
   public static final int TAG_WHITE_BALANCE = 41987;
   public static final int WHITE_BALANCE_AUTO = 0;
   public static final int WHITE_BALANCE_MANUAL = 1;
   public static final int TAG_DIGITAL_ZOOM_RATIO = 41988;
   public static final int TAG_FOCAL_LENGTH_IN_35MM_FILM = 41989;
   public static final int TAG_SCENE_CAPTURE_TYPE = 41990;
   public static final int SCENE_CAPTURE_TYPE_STANDARD = 0;
   public static final int SCENE_CAPTURE_TYPE_LANDSCAPE = 1;
   public static final int SCENE_CAPTURE_TYPE_PORTRAIT = 2;
   public static final int SCENE_CAPTURE_TYPE_NIGHT_SCENE = 3;
   public static final int TAG_GAIN_CONTROL = 41991;
   public static final int GAIN_CONTROL_NONE = 0;
   public static final int GAIN_CONTROL_LOW_GAIN_UP = 1;
   public static final int GAIN_CONTROL_HIGH_GAIN_UP = 2;
   public static final int GAIN_CONTROL_LOW_GAIN_DOWN = 3;
   public static final int GAIN_CONTROL_HIGH_GAIN_DOWN = 4;
   public static final int TAG_CONTRAST = 41992;
   public static final int CONTRAST_NORMAL = 0;
   public static final int CONTRAST_SOFT = 1;
   public static final int CONTRAST_HARD = 2;
   public static final int TAG_SATURATION = 41993;
   public static final int SATURATION_NORMAL = 0;
   public static final int SATURATION_LOW = 1;
   public static final int SATURATION_HIGH = 2;
   public static final int TAG_SHARPNESS = 41994;
   public static final int SHARPNESS_NORMAL = 0;
   public static final int SHARPNESS_SOFT = 1;
   public static final int SHARPNESS_HARD = 2;
   public static final int TAG_DEVICE_SETTING_DESCRIPTION = 41995;
   public static final int TAG_SUBJECT_DISTANCE_RANGE = 41996;
   public static final int SUBJECT_DISTANCE_RANGE_UNKNOWN = 0;
   public static final int SUBJECT_DISTANCE_RANGE_MACRO = 1;
   public static final int SUBJECT_DISTANCE_RANGE_CLOSE_VIEW = 2;
   public static final int SUBJECT_DISTANCE_RANGE_DISTANT_VIEW = 3;
   public static final int TAG_IMAGE_UNIQUE_ID = 42016;
   private static List tags;

   private static void initTags() {
      tags = new ArrayList(42);
      tags.add(new EXIFVersion());
      tags.add(new FlashPixVersion());
      tags.add(new ColorSpace());
      tags.add(new ComponentsConfiguration());
      tags.add(new CompressedBitsPerPixel());
      tags.add(new PixelXDimension());
      tags.add(new PixelYDimension());
      tags.add(new MakerNote());
      tags.add(new UserComment());
      tags.add(new RelatedSoundFile());
      tags.add(new DateTimeOriginal());
      tags.add(new DateTimeDigitized());
      tags.add(new SubSecTime());
      tags.add(new SubSecTimeOriginal());
      tags.add(new SubSecTimeDigitized());
      tags.add(new ExposureTime());
      tags.add(new FNumber());
      tags.add(new ExposureProgram());
      tags.add(new SpectralSensitivity());
      tags.add(new ISOSpeedRatings());
      tags.add(new OECF());
      tags.add(new ShutterSpeedValue());
      tags.add(new ApertureValue());
      tags.add(new BrightnessValue());
      tags.add(new ExposureBiasValue());
      tags.add(new MaxApertureValue());
      tags.add(new SubjectDistance());
      tags.add(new MeteringMode());
      tags.add(new LightSource());
      tags.add(new Flash());
      tags.add(new FocalLength());
      tags.add(new SubjectArea());
      tags.add(new FlashEnergy());
      tags.add(new SpatialFrequencyResponse());
      tags.add(new FocalPlaneXResolution());
      tags.add(new FocalPlaneYResolution());
      tags.add(new FocalPlaneResolutionUnit());
      tags.add(new SubjectLocation());
      tags.add(new ExposureIndex());
      tags.add(new SensingMethod());
      tags.add(new FileSource());
      tags.add(new SceneType());
      tags.add(new CFAPattern());
      tags.add(new CustomRendered());
      tags.add(new ExposureMode());
      tags.add(new WhiteBalance());
      tags.add(new DigitalZoomRatio());
      tags.add(new FocalLengthIn35mmFilm());
      tags.add(new SceneCaptureType());
      tags.add(new GainControl());
      tags.add(new Contrast());
      tags.add(new Saturation());
      tags.add(new Sharpness());
      tags.add(new DeviceSettingDescription());
      tags.add(new SubjectDistanceRange());
      tags.add(new ImageUniqueID());
      tags.add(new InteroperabilityIFD());
   }

   private EXIFTIFFTagSet() {
      super(tags);
   }

   public static synchronized EXIFTIFFTagSet getInstance() {
      if (theInstance == null) {
         initTags();
         theInstance = new EXIFTIFFTagSet();
         tags = null;
      }

      return theInstance;
   }

   static class InteroperabilityIFD extends TIFFTag {
      public InteroperabilityIFD() {
         super("InteroperabilityIFD", 40965, 16, EXIFInteroperabilityTagSet.getInstance());
      }
   }

   static class ImageUniqueID extends TIFFTag {
      public ImageUniqueID() {
         super("ImageUniqueID", 42016, 4);
      }
   }

   static class SubjectDistanceRange extends TIFFTag {
      public SubjectDistanceRange() {
         super("SubjectDistanceRange", 41996, 8);
         this.addValueName(0, "unknown");
         this.addValueName(1, "Macro");
         this.addValueName(2, "Close view");
         this.addValueName(3, "Distant view");
      }
   }

   static class DeviceSettingDescription extends TIFFTag {
      public DeviceSettingDescription() {
         super("DeviceSettingDescription", 41995, 128);
      }
   }

   static class Sharpness extends TIFFTag {
      public Sharpness() {
         super("Sharpness", 41994, 8);
         this.addValueName(0, "Normal");
         this.addValueName(1, "Soft");
         this.addValueName(2, "Hard");
      }
   }

   static class Saturation extends TIFFTag {
      public Saturation() {
         super("Saturation", 41993, 8);
         this.addValueName(0, "Normal");
         this.addValueName(1, "Low saturation");
         this.addValueName(2, "High saturation");
      }
   }

   static class Contrast extends TIFFTag {
      public Contrast() {
         super("Contrast", 41992, 8);
         this.addValueName(0, "Normal");
         this.addValueName(1, "Soft");
         this.addValueName(2, "Hard");
      }
   }

   static class GainControl extends TIFFTag {
      public GainControl() {
         super("GainControl", 41991, 8);
         this.addValueName(0, "None");
         this.addValueName(1, "Low gain up");
         this.addValueName(2, "High gain up");
         this.addValueName(3, "Low gain down");
         this.addValueName(4, "High gain down");
      }
   }

   static class SceneCaptureType extends TIFFTag {
      public SceneCaptureType() {
         super("SceneCaptureType", 41990, 8);
         this.addValueName(0, "Standard");
         this.addValueName(1, "Landscape");
         this.addValueName(2, "Portrait");
         this.addValueName(3, "Night scene");
      }
   }

   static class FocalLengthIn35mmFilm extends TIFFTag {
      public FocalLengthIn35mmFilm() {
         super("FocalLengthIn35mmFilm", 41989, 8);
      }
   }

   static class DigitalZoomRatio extends TIFFTag {
      public DigitalZoomRatio() {
         super("DigitalZoomRatio", 41988, 32);
      }
   }

   static class WhiteBalance extends TIFFTag {
      public WhiteBalance() {
         super("WhiteBalance", 41987, 8);
         this.addValueName(0, "Auto white balance");
         this.addValueName(1, "Manual white balance");
      }
   }

   static class ExposureMode extends TIFFTag {
      public ExposureMode() {
         super("ExposureMode", 41986, 8);
         this.addValueName(0, "Auto exposure");
         this.addValueName(1, "Manual exposure");
         this.addValueName(2, "Auto bracket");
      }
   }

   static class CustomRendered extends TIFFTag {
      public CustomRendered() {
         super("CustomRendered", 41985, 8);
         this.addValueName(0, "Normal process");
         this.addValueName(1, "Custom process");
      }
   }

   static class CFAPattern extends TIFFTag {
      public CFAPattern() {
         super("CFAPattern", 41730, 128);
      }
   }

   static class SceneType extends TIFFTag {
      public SceneType() {
         super("SceneType", 41729, 128);
         this.addValueName(1, "A directly photographed image");
      }
   }

   static class FileSource extends TIFFTag {
      public FileSource() {
         super("FileSource", 41728, 128);
         this.addValueName(3, "DSC");
      }
   }

   static class SensingMethod extends TIFFTag {
      public SensingMethod() {
         super("SensingMethod", 41495, 8);
         this.addValueName(1, "Not Defined");
         this.addValueName(2, "One-chip color area sensor");
         this.addValueName(3, "Two-chip color area sensor");
         this.addValueName(4, "Three-chip color area sensor");
         this.addValueName(5, "Color sequential area sensor");
         this.addValueName(7, "Trilinear sensor");
         this.addValueName(8, "Color sequential linear sensor");
      }
   }

   static class ExposureIndex extends TIFFTag {
      public ExposureIndex() {
         super("ExposureIndex", 41493, 32);
      }
   }

   static class SubjectLocation extends TIFFTag {
      public SubjectLocation() {
         super("SubjectLocation", 41492, 8);
      }
   }

   static class FocalPlaneResolutionUnit extends TIFFTag {
      public FocalPlaneResolutionUnit() {
         super("FocalPlaneResolutionUnit", 41488, 8);
         this.addValueName(1, "None");
         this.addValueName(2, "Inch");
         this.addValueName(3, "Centimeter");
      }
   }

   static class FocalPlaneYResolution extends TIFFTag {
      public FocalPlaneYResolution() {
         super("FocalPlaneYResolution", 41487, 32);
      }
   }

   static class FocalPlaneXResolution extends TIFFTag {
      public FocalPlaneXResolution() {
         super("FocalPlaneXResolution", 41486, 32);
      }
   }

   static class SpatialFrequencyResponse extends TIFFTag {
      public SpatialFrequencyResponse() {
         super("SpatialFrequencyResponse", 41484, 128);
      }
   }

   static class FlashEnergy extends TIFFTag {
      public FlashEnergy() {
         super("FlashEnergy", 41483, 32);
      }
   }

   static class SubjectArea extends TIFFTag {
      public SubjectArea() {
         super("SubjectArea", 37396, 8);
      }
   }

   static class FocalLength extends TIFFTag {
      public FocalLength() {
         super("FocalLength", 37386, 32);
      }
   }

   static class Flash extends TIFFTag {
      public Flash() {
         super("Flash", 37385, 8);
         this.addValueName(0, "Flash Did Not Fire");
         this.addValueName(1, "Flash Fired");
         this.addValueName(5, "Strobe Return Light Not Detected");
         this.addValueName(7, "Strobe Return Light Detected");
      }
   }

   static class LightSource extends TIFFTag {
      public LightSource() {
         super("LightSource", 37384, 8);
         this.addValueName(0, "Unknown");
         this.addValueName(1, "Daylight");
         this.addValueName(2, "Fluorescent");
         this.addValueName(3, "Tungsten");
         this.addValueName(17, "Standard Light A");
         this.addValueName(18, "Standard Light B");
         this.addValueName(19, "Standard Light C");
         this.addValueName(20, "D55");
         this.addValueName(21, "D65");
         this.addValueName(22, "D75");
         this.addValueName(255, "Other");
      }
   }

   static class MeteringMode extends TIFFTag {
      public MeteringMode() {
         super("MeteringMode", 37383, 8);
         this.addValueName(0, "Unknown");
         this.addValueName(1, "Average");
         this.addValueName(2, "CenterWeightedAverage");
         this.addValueName(3, "Spot");
         this.addValueName(4, "MultiSpot");
         this.addValueName(5, "Pattern");
         this.addValueName(6, "Partial");
         this.addValueName(255, "Other");
      }
   }

   static class SubjectDistance extends TIFFTag {
      public SubjectDistance() {
         super("SubjectDistance", 37382, 32);
      }
   }

   static class MaxApertureValue extends TIFFTag {
      public MaxApertureValue() {
         super("MaxApertureValue", 37381, 32);
      }
   }

   static class ExposureBiasValue extends TIFFTag {
      public ExposureBiasValue() {
         super("ExposureBiasValue", 37380, 1024);
      }
   }

   static class BrightnessValue extends TIFFTag {
      public BrightnessValue() {
         super("BrightnessValue", 37379, 1024);
      }
   }

   static class ApertureValue extends TIFFTag {
      public ApertureValue() {
         super("ApertureValue", 37378, 32);
      }
   }

   static class ShutterSpeedValue extends TIFFTag {
      public ShutterSpeedValue() {
         super("ShutterSpeedValue", 37377, 1024);
      }
   }

   static class OECF extends TIFFTag {
      public OECF() {
         super("OECF", 34856, 128);
      }
   }

   static class ISOSpeedRatings extends TIFFTag {
      public ISOSpeedRatings() {
         super("ISOSpeedRatings", 34855, 8);
      }
   }

   static class SpectralSensitivity extends TIFFTag {
      public SpectralSensitivity() {
         super("SpectralSensitivity", 34852, 4);
      }
   }

   static class ExposureProgram extends TIFFTag {
      public ExposureProgram() {
         super("ExposureProgram", 34850, 8);
         this.addValueName(0, "Not Defined");
         this.addValueName(1, "Manual");
         this.addValueName(2, "Normal Program");
         this.addValueName(3, "Aperture Priority");
         this.addValueName(4, "Shutter Priority");
         this.addValueName(5, "Creative Program");
         this.addValueName(6, "Action Program");
         this.addValueName(7, "Portrait Mode");
         this.addValueName(8, "Landscape Mode");
      }
   }

   static class FNumber extends TIFFTag {
      public FNumber() {
         super("FNumber", 33437, 32);
      }
   }

   static class ExposureTime extends TIFFTag {
      public ExposureTime() {
         super("ExposureTime", 33434, 32);
      }
   }

   static class SubSecTimeDigitized extends TIFFTag {
      public SubSecTimeDigitized() {
         super("SubSecTimeDigitized", 37522, 4);
      }
   }

   static class SubSecTimeOriginal extends TIFFTag {
      public SubSecTimeOriginal() {
         super("SubSecTimeOriginal", 37521, 4);
      }
   }

   static class SubSecTime extends TIFFTag {
      public SubSecTime() {
         super("SubSecTime", 37520, 4);
      }
   }

   static class DateTimeDigitized extends TIFFTag {
      public DateTimeDigitized() {
         super("DateTimeDigitized", 36868, 4);
      }
   }

   static class DateTimeOriginal extends TIFFTag {
      public DateTimeOriginal() {
         super("DateTimeOriginal", 36867, 4);
      }
   }

   static class RelatedSoundFile extends TIFFTag {
      public RelatedSoundFile() {
         super("RelatedSoundFile", 40964, 4);
      }
   }

   static class UserComment extends TIFFTag {
      public UserComment() {
         super("UserComment", 37510, 128);
      }
   }

   static class MakerNote extends TIFFTag {
      public MakerNote() {
         super("MakerNote", 37500, 128);
      }
   }

   static class PixelYDimension extends TIFFTag {
      public PixelYDimension() {
         super("PixelYDimension", 40963, 24);
      }
   }

   static class PixelXDimension extends TIFFTag {
      public PixelXDimension() {
         super("PixelXDimension", 40962, 24);
      }
   }

   static class CompressedBitsPerPixel extends TIFFTag {
      public CompressedBitsPerPixel() {
         super("CompressedBitsPerPixel", 37122, 32);
      }
   }

   static class ComponentsConfiguration extends TIFFTag {
      public ComponentsConfiguration() {
         super("ComponentsConfiguration", 37121, 128);
         this.addValueName(0, "DoesNotExist");
         this.addValueName(1, "Y");
         this.addValueName(2, "Cb");
         this.addValueName(3, "Cr");
         this.addValueName(4, "R");
         this.addValueName(5, "G");
         this.addValueName(6, "B");
      }
   }

   static class ColorSpace extends TIFFTag {
      public ColorSpace() {
         super("ColorSpace", 40961, 8);
         this.addValueName(1, "sRGB");
         this.addValueName(65535, "Uncalibrated");
      }
   }

   static class FlashPixVersion extends TIFFTag {
      public FlashPixVersion() {
         super("FlashPixVersion", 40960, 128);
      }
   }

   static class EXIFVersion extends TIFFTag {
      public EXIFVersion() {
         super("EXIFversion", 36864, 128);
      }
   }
}
