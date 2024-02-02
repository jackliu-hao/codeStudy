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
/*      */ public class BaselineTIFFTagSet
/*      */   extends TIFFTagSet
/*      */ {
/*   82 */   private static BaselineTIFFTagSet theInstance = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_NEW_SUBFILE_TYPE = 254;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int NEW_SUBFILE_TYPE_REDUCED_RESOLUTION = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int NEW_SUBFILE_TYPE_SINGLE_PAGE = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int NEW_SUBFILE_TYPE_TRANSPARENCY = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SUBFILE_TYPE = 255;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SUBFILE_TYPE_FULL_RESOLUTION = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SUBFILE_TYPE_REDUCED_RESOLUTION = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SUBFILE_TYPE_SINGLE_PAGE = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_IMAGE_WIDTH = 256;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_IMAGE_LENGTH = 257;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_BITS_PER_SAMPLE = 258;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_COMPRESSION = 259;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_NONE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_CCITT_RLE = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_CCITT_T_4 = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_CCITT_T_6 = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_LZW = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_OLD_JPEG = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_JPEG = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_ZLIB = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_PACKBITS = 32773;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COMPRESSION_DEFLATE = 32946;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_PHOTOMETRIC_INTERPRETATION = 262;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_RGB = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_PALETTE_COLOR = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_TRANSPARENCY_MASK = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_CMYK = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_Y_CB_CR = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_CIELAB = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PHOTOMETRIC_INTERPRETATION_ICCLAB = 9;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_THRESHHOLDING = 263;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int THRESHHOLDING_NONE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int THRESHHOLDING_ORDERED_DITHER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int THRESHHOLDING_RANDOMIZED_DITHER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_CELL_WIDTH = 264;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_CELL_LENGTH = 265;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FILL_ORDER = 266;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FILL_ORDER_LEFT_TO_RIGHT = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int FILL_ORDER_RIGHT_TO_LEFT = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_DOCUMENT_NAME = 269;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_IMAGE_DESCRIPTION = 270;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_MAKE = 271;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_MODEL = 272;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_STRIP_OFFSETS = 273;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_ORIENTATION = 274;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ORIENTATION_ROW_0_TOP_COLUMN_0_LEFT = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ORIENTATION_ROW_0_TOP_COLUMN_0_RIGHT = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ORIENTATION_ROW_0_BOTTOM_COLUMN_0_RIGHT = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ORIENTATION_ROW_0_BOTTOM_COLUMN_0_LEFT = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ORIENTATION_ROW_0_LEFT_COLUMN_0_TOP = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ORIENTATION_ROW_0_RIGHT_COLUMN_0_TOP = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ORIENTATION_ROW_0_RIGHT_COLUMN_0_BOTTOM = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ORIENTATION_ROW_0_LEFT_COLUMN_0_BOTTOM = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SAMPLES_PER_PIXEL = 277;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_ROWS_PER_STRIP = 278;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_STRIP_BYTE_COUNTS = 279;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_MIN_SAMPLE_VALUE = 280;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_MAX_SAMPLE_VALUE = 281;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_X_RESOLUTION = 282;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_Y_RESOLUTION = 283;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_PLANAR_CONFIGURATION = 284;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PLANAR_CONFIGURATION_CHUNKY = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PLANAR_CONFIGURATION_PLANAR = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_PAGE_NAME = 285;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_X_POSITION = 286;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_Y_POSITION = 287;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FREE_OFFSETS = 288;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_FREE_BYTE_COUNTS = 289;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_GRAY_RESPONSE_UNIT = 290;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GRAY_RESPONSE_UNIT_TENTHS = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GRAY_RESPONSE_UNIT_HUNDREDTHS = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GRAY_RESPONSE_UNIT_THOUSANDTHS = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GRAY_RESPONSE_UNIT_TEN_THOUSANDTHS = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int GRAY_RESPONSE_UNIT_HUNDRED_THOUSANDTHS = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_GRAY_RESPONSE_CURVE = 291;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_T4_OPTIONS = 292;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int T4_OPTIONS_2D_CODING = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int T4_OPTIONS_UNCOMPRESSED = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int T4_OPTIONS_EOL_BYTE_ALIGNED = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_T6_OPTIONS = 293;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int T6_OPTIONS_UNCOMPRESSED = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_RESOLUTION_UNIT = 296;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RESOLUTION_UNIT_NONE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RESOLUTION_UNIT_INCH = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RESOLUTION_UNIT_CENTIMETER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_PAGE_NUMBER = 297;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_TRANSFER_FUNCTION = 301;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SOFTWARE = 305;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_DATE_TIME = 306;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_ARTIST = 315;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_HOST_COMPUTER = 316;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_PREDICTOR = 317;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PREDICTOR_NONE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int PREDICTOR_HORIZONTAL_DIFFERENCING = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_WHITE_POINT = 318;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_PRIMARY_CHROMATICITES = 319;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_COLOR_MAP = 320;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_HALFTONE_HINTS = 321;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_TILE_WIDTH = 322;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_TILE_LENGTH = 323;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_TILE_OFFSETS = 324;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_TILE_BYTE_COUNTS = 325;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_INK_SET = 332;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int INK_SET_CMYK = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int INK_SET_NOT_CMYK = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_INK_NAMES = 333;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_NUMBER_OF_INKS = 334;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_DOT_RANGE = 336;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_TARGET_PRINTER = 337;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_EXTRA_SAMPLES = 338;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXTRA_SAMPLES_UNSPECIFIED = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXTRA_SAMPLES_ASSOCIATED_ALPHA = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EXTRA_SAMPLES_UNASSOCIATED_ALPHA = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_SAMPLE_FORMAT = 339;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SAMPLE_FORMAT_UNSIGNED_INTEGER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SAMPLE_FORMAT_SIGNED_INTEGER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SAMPLE_FORMAT_FLOATING_POINT = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SAMPLE_FORMAT_UNDEFINED = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_S_MIN_SAMPLE_VALUE = 340;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_S_MAX_SAMPLE_VALUE = 341;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_TRANSFER_RANGE = 342;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_TABLES = 347;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_PROC = 512;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int JPEG_PROC_BASELINE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int JPEG_PROC_LOSSLESS = 14;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_INTERCHANGE_FORMAT = 513;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = 514;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_RESTART_INTERVAL = 515;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_LOSSLESS_PREDICTORS = 517;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_POINT_TRANSFORMS = 518;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_Q_TABLES = 519;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_DC_TABLES = 520;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_JPEG_AC_TABLES = 521;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_Y_CB_CR_COEFFICIENTS = 529;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_Y_CB_CR_SUBSAMPLING = 530;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_Y_CB_CR_POSITIONING = 531;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int Y_CB_CR_POSITIONING_CENTERED = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int Y_CB_CR_POSITIONING_COSITED = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_REFERENCE_BLACK_WHITE = 532;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_COPYRIGHT = 33432;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TAG_ICC_PROFILE = 34675;
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
/*      */   static class Artist
/*      */     extends TIFFTag
/*      */   {
/*      */     public Artist() {
/* 1044 */       super("Artist", 315, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class BitsPerSample
/*      */     extends TIFFTag
/*      */   {
/*      */     public BitsPerSample() {
/* 1055 */       super("BitsPerSample", 258, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class CellLength
/*      */     extends TIFFTag
/*      */   {
/*      */     public CellLength() {
/* 1066 */       super("CellLength", 265, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class CellWidth
/*      */     extends TIFFTag
/*      */   {
/*      */     public CellWidth() {
/* 1077 */       super("CellWidth", 264, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ColorMap
/*      */     extends TIFFTag
/*      */   {
/*      */     public ColorMap() {
/* 1088 */       super("ColorMap", 320, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class Compression
/*      */     extends TIFFTag
/*      */   {
/*      */     public Compression() {
/* 1099 */       super("Compression", 259, 8);
/*      */ 
/*      */ 
/*      */       
/* 1103 */       addValueName(1, "Uncompressed");
/* 1104 */       addValueName(2, "CCITT RLE");
/* 1105 */       addValueName(3, "CCITT T.4");
/* 1106 */       addValueName(4, "CCITT T.6");
/* 1107 */       addValueName(5, "LZW");
/* 1108 */       addValueName(6, "Old JPEG");
/* 1109 */       addValueName(7, "JPEG");
/* 1110 */       addValueName(8, "ZLib");
/* 1111 */       addValueName(32773, "PackBits");
/* 1112 */       addValueName(32946, "Deflate");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class Copyright
/*      */     extends TIFFTag
/*      */   {
/*      */     public Copyright() {
/* 1128 */       super("Copyright", 33432, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class DateTime
/*      */     extends TIFFTag
/*      */   {
/*      */     public DateTime() {
/* 1139 */       super("DateTime", 306, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class DocumentName
/*      */     extends TIFFTag
/*      */   {
/*      */     public DocumentName() {
/* 1150 */       super("DocumentName", 269, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class DotRange
/*      */     extends TIFFTag
/*      */   {
/*      */     public DotRange() {
/* 1161 */       super("DotRange", 336, 10);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ExtraSamples
/*      */     extends TIFFTag
/*      */   {
/*      */     public ExtraSamples() {
/* 1173 */       super("ExtraSamples", 338, 8);
/*      */ 
/*      */ 
/*      */       
/* 1177 */       addValueName(0, "Unspecified");
/*      */       
/* 1179 */       addValueName(1, "Associated Alpha");
/*      */       
/* 1181 */       addValueName(2, "Unassociated Alpha");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class FillOrder
/*      */     extends TIFFTag
/*      */   {
/*      */     public FillOrder() {
/* 1191 */       super("FillOrder", 266, 8);
/*      */ 
/*      */ 
/*      */       
/* 1195 */       addValueName(1, "LeftToRight");
/* 1196 */       addValueName(2, "RightToLeft");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class FreeByteCounts
/*      */     extends TIFFTag
/*      */   {
/*      */     public FreeByteCounts() {
/* 1205 */       super("FreeByteCounts", 289, 16);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class FreeOffsets
/*      */     extends TIFFTag
/*      */   {
/*      */     public FreeOffsets() {
/* 1216 */       super("FreeOffsets", 288, 16);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class GrayResponseCurve
/*      */     extends TIFFTag
/*      */   {
/*      */     public GrayResponseCurve() {
/* 1227 */       super("GrayResponseCurve", 291, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class GrayResponseUnit
/*      */     extends TIFFTag
/*      */   {
/*      */     public GrayResponseUnit() {
/* 1238 */       super("GrayResponseUnit", 290, 8);
/*      */ 
/*      */ 
/*      */       
/* 1242 */       addValueName(1, "Tenths");
/*      */       
/* 1244 */       addValueName(2, "Hundredths");
/*      */       
/* 1246 */       addValueName(3, "Thousandths");
/*      */       
/* 1248 */       addValueName(4, "Ten-Thousandths");
/*      */       
/* 1250 */       addValueName(5, "Hundred-Thousandths");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class HalftoneHints
/*      */     extends TIFFTag
/*      */   {
/*      */     public HalftoneHints() {
/* 1260 */       super("HalftoneHints", 321, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class HostComputer
/*      */     extends TIFFTag
/*      */   {
/*      */     public HostComputer() {
/* 1271 */       super("HostComputer", 316, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ImageDescription
/*      */     extends TIFFTag
/*      */   {
/*      */     public ImageDescription() {
/* 1282 */       super("ImageDescription", 270, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ImageLength
/*      */     extends TIFFTag
/*      */   {
/*      */     public ImageLength() {
/* 1293 */       super("ImageLength", 257, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ImageWidth
/*      */     extends TIFFTag
/*      */   {
/*      */     public ImageWidth() {
/* 1305 */       super("ImageWidth", 256, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class InkNames
/*      */     extends TIFFTag
/*      */   {
/*      */     public InkNames() {
/* 1317 */       super("InkNames", 333, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class InkSet
/*      */     extends TIFFTag
/*      */   {
/*      */     public InkSet() {
/* 1328 */       super("InkSet", 332, 8);
/*      */ 
/*      */ 
/*      */       
/* 1332 */       addValueName(1, "CMYK");
/* 1333 */       addValueName(2, "Not CMYK");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class JPEGTables
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGTables() {
/* 1342 */       super("JPEGTables", 347, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class JPEGACTables
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGACTables() {
/* 1353 */       super("JPEGACTables", 521, 16);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class JPEGDCTables
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGDCTables() {
/* 1364 */       super("JPEGDCTables", 520, 16);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class JPEGInterchangeFormat
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGInterchangeFormat() {
/* 1375 */       super("JPEGInterchangeFormat", 513, 16);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class JPEGInterchangeFormatLength
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGInterchangeFormatLength() {
/* 1386 */       super("JPEGInterchangeFormatLength", 514, 16);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class JPEGLosslessPredictors
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGLosslessPredictors() {
/* 1397 */       super("JPEGLosslessPredictors", 517, 8);
/*      */ 
/*      */ 
/*      */       
/* 1401 */       addValueName(1, "A");
/* 1402 */       addValueName(2, "B");
/* 1403 */       addValueName(3, "C");
/* 1404 */       addValueName(4, "A+B-C");
/* 1405 */       addValueName(5, "A+((B-C)/2)");
/* 1406 */       addValueName(6, "B+((A-C)/2)");
/* 1407 */       addValueName(7, "(A+B)/2");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class JPEGPointTransforms
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGPointTransforms() {
/* 1416 */       super("JPEGPointTransforms", 518, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class JPEGProc
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGProc() {
/* 1427 */       super("JPEGProc", 512, 8);
/*      */ 
/*      */ 
/*      */       
/* 1431 */       addValueName(1, "Baseline sequential process");
/* 1432 */       addValueName(14, "Lossless process with Huffman coding");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class JPEGQTables
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGQTables() {
/* 1442 */       super("JPEGQTables", 519, 16);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class JPEGRestartInterval
/*      */     extends TIFFTag
/*      */   {
/*      */     public JPEGRestartInterval() {
/* 1453 */       super("JPEGRestartInterval", 515, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class Make
/*      */     extends TIFFTag
/*      */   {
/*      */     public Make() {
/* 1464 */       super("Make", 271, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class MaxSampleValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public MaxSampleValue() {
/* 1475 */       super("MaxSampleValue", 281, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class MinSampleValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public MinSampleValue() {
/* 1486 */       super("MinSampleValue", 280, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class Model
/*      */     extends TIFFTag
/*      */   {
/*      */     public Model() {
/* 1497 */       super("Model", 272, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class NewSubfileType
/*      */     extends TIFFTag
/*      */   {
/*      */     public NewSubfileType() {
/* 1508 */       super("NewSubfileType", 254, 16);
/*      */ 
/*      */ 
/*      */       
/* 1512 */       addValueName(0, "Default");
/*      */       
/* 1514 */       addValueName(1, "ReducedResolution");
/*      */       
/* 1516 */       addValueName(2, "SinglePage");
/*      */       
/* 1518 */       addValueName(3, "SinglePage+ReducedResolution");
/*      */ 
/*      */       
/* 1521 */       addValueName(4, "Transparency");
/*      */       
/* 1523 */       addValueName(5, "Transparency+ReducedResolution");
/*      */ 
/*      */       
/* 1526 */       addValueName(6, "Transparency+SinglePage");
/*      */ 
/*      */       
/* 1529 */       addValueName(7, "Transparency+SinglePage+ReducedResolution");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class NumberOfInks
/*      */     extends TIFFTag
/*      */   {
/*      */     public NumberOfInks() {
/* 1541 */       super("NumberOfInks", 334, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class Orientation
/*      */     extends TIFFTag
/*      */   {
/*      */     public Orientation() {
/* 1552 */       super("Orientation", 274, 8);
/*      */ 
/*      */ 
/*      */       
/* 1556 */       addValueName(1, "Row 0=Top, Column 0=Left");
/*      */       
/* 1558 */       addValueName(2, "Row 0=Top, Column 0=Right");
/*      */       
/* 1560 */       addValueName(3, "Row 0=Bottom, Column 0=Right");
/*      */       
/* 1562 */       addValueName(4, "Row 0=Bottom, Column 0=Left");
/*      */       
/* 1564 */       addValueName(5, "Row 0=Left, Column 0=Top");
/*      */       
/* 1566 */       addValueName(6, "Row 0=Right, Column 0=Top");
/*      */       
/* 1568 */       addValueName(7, "Row 0=Right, Column 0=Bottom");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class PageName
/*      */     extends TIFFTag
/*      */   {
/*      */     public PageName() {
/* 1578 */       super("PageName", 285, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class PageNumber
/*      */     extends TIFFTag
/*      */   {
/*      */     public PageNumber() {
/* 1589 */       super("PageNumber", 297, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class PhotometricInterpretation
/*      */     extends TIFFTag
/*      */   {
/*      */     public PhotometricInterpretation() {
/* 1600 */       super("PhotometricInterpretation", 262, 8);
/*      */ 
/*      */ 
/*      */       
/* 1604 */       addValueName(0, "WhiteIsZero");
/*      */       
/* 1606 */       addValueName(1, "BlackIsZero");
/*      */       
/* 1608 */       addValueName(2, "RGB");
/*      */       
/* 1610 */       addValueName(3, "Palette Color");
/*      */       
/* 1612 */       addValueName(4, "Transparency Mask");
/*      */       
/* 1614 */       addValueName(5, "CMYK");
/*      */       
/* 1616 */       addValueName(6, "YCbCr");
/*      */       
/* 1618 */       addValueName(8, "CIELAB");
/*      */       
/* 1620 */       addValueName(9, "ICCLAB");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class PlanarConfiguration
/*      */     extends TIFFTag
/*      */   {
/*      */     public PlanarConfiguration() {
/* 1630 */       super("PlanarConfiguration", 284, 8);
/*      */ 
/*      */ 
/*      */       
/* 1634 */       addValueName(1, "Chunky");
/* 1635 */       addValueName(2, "Planar");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class Predictor
/*      */     extends TIFFTag
/*      */   {
/*      */     public Predictor() {
/* 1644 */       super("Predictor", 317, 8);
/*      */ 
/*      */ 
/*      */       
/* 1648 */       addValueName(1, "None");
/*      */       
/* 1650 */       addValueName(2, "Horizontal Differencing");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class PrimaryChromaticities
/*      */     extends TIFFTag
/*      */   {
/*      */     public PrimaryChromaticities() {
/* 1660 */       super("PrimaryChromaticities", 319, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ReferenceBlackWhite
/*      */     extends TIFFTag
/*      */   {
/*      */     public ReferenceBlackWhite() {
/* 1671 */       super("ReferenceBlackWhite", 532, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ResolutionUnit
/*      */     extends TIFFTag
/*      */   {
/*      */     public ResolutionUnit() {
/* 1682 */       super("ResolutionUnit", 296, 8);
/*      */ 
/*      */ 
/*      */       
/* 1686 */       addValueName(1, "None");
/* 1687 */       addValueName(2, "Inch");
/* 1688 */       addValueName(3, "Centimeter");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class RowsPerStrip
/*      */     extends TIFFTag
/*      */   {
/*      */     public RowsPerStrip() {
/* 1697 */       super("RowsPerStrip", 278, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class SampleFormat
/*      */     extends TIFFTag
/*      */   {
/*      */     public SampleFormat() {
/* 1709 */       super("SampleFormat", 339, 8);
/*      */ 
/*      */ 
/*      */       
/* 1713 */       addValueName(1, "Unsigned Integer");
/* 1714 */       addValueName(2, "Signed Integer");
/* 1715 */       addValueName(3, "Floating Point");
/* 1716 */       addValueName(4, "Undefined");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class SamplesPerPixel
/*      */     extends TIFFTag
/*      */   {
/*      */     public SamplesPerPixel() {
/* 1725 */       super("SamplesPerPixel", 277, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class SMaxSampleValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public SMaxSampleValue() {
/* 1736 */       super("SMaxSampleValue", 341, 8058);
/*      */     }
/*      */   }
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
/*      */   static class SMinSampleValue
/*      */     extends TIFFTag
/*      */   {
/*      */     public SMinSampleValue() {
/* 1756 */       super("SMinSampleValue", 340, 8058);
/*      */     }
/*      */   }
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
/*      */   static class Software
/*      */     extends TIFFTag
/*      */   {
/*      */     public Software() {
/* 1776 */       super("Software", 305, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class StripByteCounts
/*      */     extends TIFFTag
/*      */   {
/*      */     public StripByteCounts() {
/* 1787 */       super("StripByteCounts", 279, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class StripOffsets
/*      */     extends TIFFTag
/*      */   {
/*      */     public StripOffsets() {
/* 1799 */       super("StripOffsets", 273, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class SubfileType
/*      */     extends TIFFTag
/*      */   {
/*      */     public SubfileType() {
/* 1811 */       super("SubfileType", 255, 8);
/*      */ 
/*      */ 
/*      */       
/* 1815 */       addValueName(1, "FullResolution");
/* 1816 */       addValueName(2, "ReducedResolution");
/* 1817 */       addValueName(3, "SinglePage");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class T4Options
/*      */     extends TIFFTag
/*      */   {
/*      */     public T4Options() {
/* 1826 */       super("T4Options", 292, 16);
/*      */ 
/*      */ 
/*      */       
/* 1830 */       addValueName(0, "Default 1DCoding");
/*      */       
/* 1832 */       addValueName(1, "2DCoding");
/*      */       
/* 1834 */       addValueName(2, "Uncompressed");
/*      */       
/* 1836 */       addValueName(3, "2DCoding+Uncompressed");
/*      */ 
/*      */       
/* 1839 */       addValueName(4, "EOLByteAligned");
/*      */       
/* 1841 */       addValueName(5, "2DCoding+EOLByteAligned");
/*      */ 
/*      */       
/* 1844 */       addValueName(6, "Uncompressed+EOLByteAligned");
/*      */ 
/*      */       
/* 1847 */       addValueName(7, "2DCoding+Uncompressed+EOLByteAligned");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class T6Options
/*      */     extends TIFFTag
/*      */   {
/*      */     public T6Options() {
/* 1859 */       super("T6Options", 293, 16);
/*      */ 
/*      */ 
/*      */       
/* 1863 */       addValueName(0, "Default");
/*      */ 
/*      */       
/* 1866 */       addValueName(2, "Uncompressed");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class TargetPrinter
/*      */     extends TIFFTag
/*      */   {
/*      */     public TargetPrinter() {
/* 1876 */       super("TargetPrinter", 337, 4);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class Threshholding
/*      */     extends TIFFTag
/*      */   {
/*      */     public Threshholding() {
/* 1887 */       super("Threshholding", 263, 8);
/*      */ 
/*      */ 
/*      */       
/* 1891 */       addValueName(1, "None");
/* 1892 */       addValueName(2, "OrderedDither");
/* 1893 */       addValueName(3, "RandomizedDither");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class TileByteCounts
/*      */     extends TIFFTag
/*      */   {
/*      */     public TileByteCounts() {
/* 1902 */       super("TileByteCounts", 325, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class TileOffsets
/*      */     extends TIFFTag
/*      */   {
/*      */     public TileOffsets() {
/* 1914 */       super("TileOffsets", 324, 16);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class TileLength
/*      */     extends TIFFTag
/*      */   {
/*      */     public TileLength() {
/* 1925 */       super("TileLength", 323, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class TileWidth
/*      */     extends TIFFTag
/*      */   {
/*      */     public TileWidth() {
/* 1937 */       super("TileWidth", 322, 24);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class TransferFunction
/*      */     extends TIFFTag
/*      */   {
/*      */     public TransferFunction() {
/* 1949 */       super("TransferFunction", 301, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class TransferRange
/*      */     extends TIFFTag
/*      */   {
/*      */     public TransferRange() {
/* 1960 */       super("TransferRange", 342, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class WhitePoint
/*      */     extends TIFFTag
/*      */   {
/*      */     public WhitePoint() {
/* 1971 */       super("WhitePoint", 318, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class XPosition
/*      */     extends TIFFTag
/*      */   {
/*      */     public XPosition() {
/* 1982 */       super("XPosition", 286, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class XResolution
/*      */     extends TIFFTag
/*      */   {
/*      */     public XResolution() {
/* 1993 */       super("XResolution", 282, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class YCbCrCoefficients
/*      */     extends TIFFTag
/*      */   {
/*      */     public YCbCrCoefficients() {
/* 2004 */       super("YCbCrCoefficients", 529, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class YCbCrPositioning
/*      */     extends TIFFTag
/*      */   {
/*      */     public YCbCrPositioning() {
/* 2015 */       super("YCbCrPositioning", 531, 8);
/*      */ 
/*      */ 
/*      */       
/* 2019 */       addValueName(1, "Centered");
/* 2020 */       addValueName(2, "Cosited");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class YCbCrSubSampling
/*      */     extends TIFFTag
/*      */   {
/*      */     public YCbCrSubSampling() {
/* 2029 */       super("YCbCrSubSampling", 530, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class YPosition
/*      */     extends TIFFTag
/*      */   {
/*      */     public YPosition() {
/* 2040 */       super("YPosition", 287, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class YResolution
/*      */     extends TIFFTag
/*      */   {
/*      */     public YResolution() {
/* 2051 */       super("YResolution", 283, 32);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class ICCProfile
/*      */     extends TIFFTag
/*      */   {
/*      */     public ICCProfile() {
/* 2064 */       super("ICC Profile", 34675, 128);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void initTags() {
/* 2073 */     tags = new ArrayList(76);
/*      */     
/* 2075 */     tags.add(new Artist());
/* 2076 */     tags.add(new BitsPerSample());
/* 2077 */     tags.add(new CellLength());
/* 2078 */     tags.add(new CellWidth());
/* 2079 */     tags.add(new ColorMap());
/* 2080 */     tags.add(new Compression());
/* 2081 */     tags.add(new Copyright());
/* 2082 */     tags.add(new DateTime());
/* 2083 */     tags.add(new DocumentName());
/* 2084 */     tags.add(new DotRange());
/* 2085 */     tags.add(new ExtraSamples());
/* 2086 */     tags.add(new FillOrder());
/* 2087 */     tags.add(new FreeByteCounts());
/* 2088 */     tags.add(new FreeOffsets());
/* 2089 */     tags.add(new GrayResponseCurve());
/* 2090 */     tags.add(new GrayResponseUnit());
/* 2091 */     tags.add(new HalftoneHints());
/* 2092 */     tags.add(new HostComputer());
/* 2093 */     tags.add(new ImageDescription());
/* 2094 */     tags.add(new ICCProfile());
/* 2095 */     tags.add(new ImageLength());
/* 2096 */     tags.add(new ImageWidth());
/* 2097 */     tags.add(new InkNames());
/* 2098 */     tags.add(new InkSet());
/* 2099 */     tags.add(new JPEGACTables());
/* 2100 */     tags.add(new JPEGDCTables());
/* 2101 */     tags.add(new JPEGInterchangeFormat());
/* 2102 */     tags.add(new JPEGInterchangeFormatLength());
/* 2103 */     tags.add(new JPEGLosslessPredictors());
/* 2104 */     tags.add(new JPEGPointTransforms());
/* 2105 */     tags.add(new JPEGProc());
/* 2106 */     tags.add(new JPEGQTables());
/* 2107 */     tags.add(new JPEGRestartInterval());
/* 2108 */     tags.add(new JPEGTables());
/* 2109 */     tags.add(new Make());
/* 2110 */     tags.add(new MaxSampleValue());
/* 2111 */     tags.add(new MinSampleValue());
/* 2112 */     tags.add(new Model());
/* 2113 */     tags.add(new NewSubfileType());
/* 2114 */     tags.add(new NumberOfInks());
/* 2115 */     tags.add(new Orientation());
/* 2116 */     tags.add(new PageName());
/* 2117 */     tags.add(new PageNumber());
/* 2118 */     tags.add(new PhotometricInterpretation());
/* 2119 */     tags.add(new PlanarConfiguration());
/* 2120 */     tags.add(new Predictor());
/* 2121 */     tags.add(new PrimaryChromaticities());
/* 2122 */     tags.add(new ReferenceBlackWhite());
/* 2123 */     tags.add(new ResolutionUnit());
/* 2124 */     tags.add(new RowsPerStrip());
/* 2125 */     tags.add(new SampleFormat());
/* 2126 */     tags.add(new SamplesPerPixel());
/* 2127 */     tags.add(new SMaxSampleValue());
/* 2128 */     tags.add(new SMinSampleValue());
/* 2129 */     tags.add(new Software());
/* 2130 */     tags.add(new StripByteCounts());
/* 2131 */     tags.add(new StripOffsets());
/* 2132 */     tags.add(new SubfileType());
/* 2133 */     tags.add(new T4Options());
/* 2134 */     tags.add(new T6Options());
/* 2135 */     tags.add(new TargetPrinter());
/* 2136 */     tags.add(new Threshholding());
/* 2137 */     tags.add(new TileByteCounts());
/* 2138 */     tags.add(new TileOffsets());
/* 2139 */     tags.add(new TileLength());
/* 2140 */     tags.add(new TileWidth());
/* 2141 */     tags.add(new TransferFunction());
/* 2142 */     tags.add(new TransferRange());
/* 2143 */     tags.add(new WhitePoint());
/* 2144 */     tags.add(new XPosition());
/* 2145 */     tags.add(new XResolution());
/* 2146 */     tags.add(new YCbCrCoefficients());
/* 2147 */     tags.add(new YCbCrPositioning());
/* 2148 */     tags.add(new YCbCrSubSampling());
/* 2149 */     tags.add(new YPosition());
/* 2150 */     tags.add(new YResolution());
/*      */   }
/*      */   
/*      */   private BaselineTIFFTagSet() {
/* 2154 */     super(tags);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static synchronized BaselineTIFFTagSet getInstance() {
/* 2163 */     if (theInstance == null) {
/* 2164 */       initTags();
/* 2165 */       theInstance = new BaselineTIFFTagSet();
/* 2166 */       tags = null;
/*      */     } 
/* 2168 */     return theInstance;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\BaselineTIFFTagSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */