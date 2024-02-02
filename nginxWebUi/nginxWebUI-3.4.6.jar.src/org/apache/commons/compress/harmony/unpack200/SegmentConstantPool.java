/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantPoolEntry;
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
/*     */ public class SegmentConstantPool
/*     */ {
/*     */   private final CpBands bands;
/*  31 */   private final SegmentConstantPoolArrayCache arrayCache = new SegmentConstantPoolArrayCache();
/*     */   
/*     */   public static final int ALL = 0;
/*     */   public static final int UTF_8 = 1;
/*     */   
/*     */   public SegmentConstantPool(CpBands bands) {
/*  37 */     this.bands = bands;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final int CP_INT = 2;
/*     */   
/*     */   public static final int CP_FLOAT = 3;
/*     */   
/*     */   public static final int CP_LONG = 4;
/*     */   
/*     */   public static final int CP_DOUBLE = 5;
/*     */   
/*     */   public static final int CP_STRING = 6;
/*     */   public static final int CP_CLASS = 7;
/*     */   public static final int SIGNATURE = 8;
/*     */   public static final int CP_DESCR = 9;
/*     */   public static final int CP_FIELD = 10;
/*     */   public static final int CP_METHOD = 11;
/*     */   public static final int CP_IMETHOD = 12;
/*     */   protected static final String REGEX_MATCH_ALL = ".*";
/*     */   protected static final String INITSTRING = "<init>";
/*     */   protected static final String REGEX_MATCH_INIT = "^<init>.*";
/*     */   
/*     */   public ClassFileEntry getValue(int cp, long value) throws Pack200Exception {
/*  61 */     int index = (int)value;
/*  62 */     if (index == -1) {
/*  63 */       return null;
/*     */     }
/*  65 */     if (index < 0) {
/*  66 */       throw new Pack200Exception("Cannot have a negative range");
/*     */     }
/*  68 */     if (cp == 1) {
/*  69 */       return (ClassFileEntry)this.bands.cpUTF8Value(index);
/*     */     }
/*  71 */     if (cp == 2) {
/*  72 */       return (ClassFileEntry)this.bands.cpIntegerValue(index);
/*     */     }
/*  74 */     if (cp == 3) {
/*  75 */       return (ClassFileEntry)this.bands.cpFloatValue(index);
/*     */     }
/*  77 */     if (cp == 4) {
/*  78 */       return (ClassFileEntry)this.bands.cpLongValue(index);
/*     */     }
/*  80 */     if (cp == 5) {
/*  81 */       return (ClassFileEntry)this.bands.cpDoubleValue(index);
/*     */     }
/*  83 */     if (cp == 6) {
/*  84 */       return (ClassFileEntry)this.bands.cpStringValue(index);
/*     */     }
/*  86 */     if (cp == 7) {
/*  87 */       return (ClassFileEntry)this.bands.cpClassValue(index);
/*     */     }
/*  89 */     if (cp == 8) {
/*  90 */       return (ClassFileEntry)this.bands.cpSignatureValue(index);
/*     */     }
/*  92 */     if (cp == 9) {
/*  93 */       return (ClassFileEntry)this.bands.cpNameAndTypeValue(index);
/*     */     }
/*  95 */     throw new Error("Tried to get a value I don't know about: " + cp);
/*     */   }
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
/*     */   public ConstantPoolEntry getClassSpecificPoolEntry(int cp, long desiredIndex, String desiredClassName) throws Pack200Exception {
/* 110 */     int index = (int)desiredIndex;
/* 111 */     int realIndex = -1;
/* 112 */     String[] array = null;
/* 113 */     if (cp == 10) {
/* 114 */       array = this.bands.getCpFieldClass();
/* 115 */     } else if (cp == 11) {
/* 116 */       array = this.bands.getCpMethodClass();
/* 117 */     } else if (cp == 12) {
/* 118 */       array = this.bands.getCpIMethodClass();
/*     */     } else {
/* 120 */       throw new Error("Don't know how to handle " + cp);
/*     */     } 
/* 122 */     realIndex = matchSpecificPoolEntryIndex(array, desiredClassName, index);
/* 123 */     return getConstantPoolEntry(cp, realIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConstantPoolEntry getClassPoolEntry(String name) {
/* 133 */     String[] classes = this.bands.getCpClass();
/* 134 */     int index = matchSpecificPoolEntryIndex(classes, name, 0);
/* 135 */     if (index == -1) {
/* 136 */       return null;
/*     */     }
/*     */     try {
/* 139 */       return getConstantPoolEntry(7, index);
/* 140 */     } catch (Pack200Exception ex) {
/* 141 */       throw new Error("Error getting class pool entry");
/*     */     } 
/*     */   }
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
/*     */   public ConstantPoolEntry getInitMethodPoolEntry(int cp, long value, String desiredClassName) throws Pack200Exception {
/* 156 */     int realIndex = -1;
/* 157 */     String desiredRegex = "^<init>.*";
/* 158 */     if (cp != 11)
/*     */     {
/* 160 */       throw new Error("Nothing but CP_METHOD can be an <init>");
/*     */     }
/* 162 */     realIndex = matchSpecificPoolEntryIndex(this.bands.getCpMethodClass(), this.bands.getCpMethodDescriptor(), desiredClassName, "^<init>.*", (int)value);
/*     */     
/* 164 */     return getConstantPoolEntry(cp, realIndex);
/*     */   }
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
/*     */   protected int matchSpecificPoolEntryIndex(String[] nameArray, String compareString, int desiredIndex) {
/* 189 */     return matchSpecificPoolEntryIndex(nameArray, nameArray, compareString, ".*", desiredIndex);
/*     */   }
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
/*     */   protected int matchSpecificPoolEntryIndex(String[] primaryArray, String[] secondaryArray, String primaryCompareString, String secondaryCompareRegex, int desiredIndex) {
/* 207 */     int instanceCount = -1;
/* 208 */     List<Integer> indexList = this.arrayCache.indexesForArrayKey(primaryArray, primaryCompareString);
/* 209 */     if (indexList.isEmpty())
/*     */     {
/* 211 */       return -1;
/*     */     }
/*     */     
/* 214 */     for (int index = 0; index < indexList.size(); index++) {
/* 215 */       int arrayIndex = ((Integer)indexList.get(index)).intValue();
/*     */       
/* 217 */       instanceCount++;
/* 218 */       if (regexMatches(secondaryCompareRegex, secondaryArray[arrayIndex]) && instanceCount == desiredIndex) {
/* 219 */         return arrayIndex;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 225 */     return -1;
/*     */   }
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
/*     */   protected static boolean regexMatches(String regexString, String compareString) {
/* 239 */     if (".*".equals(regexString)) {
/* 240 */       return true;
/*     */     }
/* 242 */     if ("^<init>.*".equals(regexString)) {
/* 243 */       if (compareString.length() < "<init>".length()) {
/* 244 */         return false;
/*     */       }
/* 246 */       return "<init>".equals(compareString.substring(0, "<init>".length()));
/*     */     } 
/* 248 */     throw new Error("regex trying to match a pattern I don't know: " + regexString);
/*     */   }
/*     */   
/*     */   public ConstantPoolEntry getConstantPoolEntry(int cp, long value) throws Pack200Exception {
/* 252 */     int index = (int)value;
/* 253 */     if (index == -1) {
/* 254 */       return null;
/*     */     }
/* 256 */     if (index < 0) {
/* 257 */       throw new Pack200Exception("Cannot have a negative range");
/*     */     }
/* 259 */     if (cp == 1) {
/* 260 */       return (ConstantPoolEntry)this.bands.cpUTF8Value(index);
/*     */     }
/* 262 */     if (cp == 2) {
/* 263 */       return (ConstantPoolEntry)this.bands.cpIntegerValue(index);
/*     */     }
/* 265 */     if (cp == 3) {
/* 266 */       return (ConstantPoolEntry)this.bands.cpFloatValue(index);
/*     */     }
/* 268 */     if (cp == 4) {
/* 269 */       return (ConstantPoolEntry)this.bands.cpLongValue(index);
/*     */     }
/* 271 */     if (cp == 5) {
/* 272 */       return (ConstantPoolEntry)this.bands.cpDoubleValue(index);
/*     */     }
/* 274 */     if (cp == 6) {
/* 275 */       return (ConstantPoolEntry)this.bands.cpStringValue(index);
/*     */     }
/* 277 */     if (cp == 7) {
/* 278 */       return (ConstantPoolEntry)this.bands.cpClassValue(index);
/*     */     }
/* 280 */     if (cp == 8) {
/* 281 */       throw new Error("I don't know what to do with signatures yet");
/*     */     }
/*     */     
/* 284 */     if (cp == 9) {
/* 285 */       throw new Error("I don't know what to do with descriptors yet");
/*     */     }
/*     */ 
/*     */     
/* 289 */     if (cp == 10) {
/* 290 */       return (ConstantPoolEntry)this.bands.cpFieldValue(index);
/*     */     }
/* 292 */     if (cp == 11) {
/* 293 */       return (ConstantPoolEntry)this.bands.cpMethodValue(index);
/*     */     }
/* 295 */     if (cp == 12) {
/* 296 */       return (ConstantPoolEntry)this.bands.cpIMethodValue(index);
/*     */     }
/*     */     
/* 299 */     throw new Error("Get value incomplete");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\SegmentConstantPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */