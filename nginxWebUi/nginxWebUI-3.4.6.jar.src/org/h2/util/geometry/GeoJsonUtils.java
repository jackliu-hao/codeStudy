/*     */ package org.h2.util.geometry;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.math.BigDecimal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.json.JSONArray;
/*     */ import org.h2.util.json.JSONByteArrayTarget;
/*     */ import org.h2.util.json.JSONBytesSource;
/*     */ import org.h2.util.json.JSONNumber;
/*     */ import org.h2.util.json.JSONObject;
/*     */ import org.h2.util.json.JSONString;
/*     */ import org.h2.util.json.JSONTarget;
/*     */ import org.h2.util.json.JSONValue;
/*     */ import org.h2.util.json.JSONValueTarget;
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
/*     */ public final class GeoJsonUtils
/*     */ {
/*  49 */   static final String[] TYPES = new String[] { "Point", "LineString", "Polygon", "MultiPoint", "MultiLineString", "MultiPolygon", "GeometryCollection" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class GeoJsonTarget
/*     */     extends GeometryUtils.Target
/*     */   {
/*     */     private final JSONByteArrayTarget output;
/*     */ 
/*     */ 
/*     */     
/*     */     private final int dimensionSystem;
/*     */ 
/*     */ 
/*     */     
/*     */     private int type;
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean inMulti;
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean inMultiLine;
/*     */ 
/*     */     
/*     */     private boolean wasEmpty;
/*     */ 
/*     */ 
/*     */     
/*     */     public GeoJsonTarget(JSONByteArrayTarget param1JSONByteArrayTarget, int param1Int) {
/*  81 */       if (param1Int == 2) {
/*  82 */         throw DbException.get(22018, "M (XYM) dimension system is not supported in GeoJson");
/*     */       }
/*     */       
/*  85 */       this.output = param1JSONByteArrayTarget;
/*  86 */       this.dimensionSystem = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPoint() {
/*  91 */       this.type = 1;
/*  92 */       this.wasEmpty = false;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startLineString(int param1Int) {
/*  97 */       writeHeader(2);
/*  98 */       if (param1Int == 0) {
/*  99 */         this.output.endArray();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygon(int param1Int1, int param1Int2) {
/* 105 */       writeHeader(3);
/* 106 */       if (param1Int2 == 0) {
/* 107 */         this.output.endArray();
/*     */       } else {
/* 109 */         this.output.startArray();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygonInner(int param1Int) {
/* 115 */       this.output.startArray();
/* 116 */       if (param1Int == 0) {
/* 117 */         this.output.endArray();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected void endNonEmptyPolygon() {
/* 123 */       this.output.endArray();
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startCollection(int param1Int1, int param1Int2) {
/* 128 */       writeHeader(param1Int1);
/* 129 */       if (param1Int1 != 7) {
/* 130 */         this.inMulti = true;
/* 131 */         if (param1Int1 == 5 || param1Int1 == 6) {
/* 132 */           this.inMultiLine = true;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected GeometryUtils.Target startCollectionItem(int param1Int1, int param1Int2) {
/* 139 */       if (this.inMultiLine) {
/* 140 */         this.output.startArray();
/*     */       }
/* 142 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void endObject(int param1Int) {
/* 147 */       switch (param1Int) {
/*     */         case 4:
/*     */         case 5:
/*     */         case 6:
/* 151 */           this.inMultiLine = this.inMulti = false;
/*     */         
/*     */         case 7:
/* 154 */           this.output.endArray(); break;
/*     */       } 
/* 156 */       if (!this.inMulti && !this.wasEmpty) {
/* 157 */         this.output.endObject();
/*     */       }
/*     */     }
/*     */     
/*     */     private void writeHeader(int param1Int) {
/* 162 */       this.type = param1Int;
/* 163 */       this.wasEmpty = false;
/* 164 */       if (!this.inMulti) {
/* 165 */         writeStartObject(param1Int);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected void addCoordinate(double param1Double1, double param1Double2, double param1Double3, double param1Double4, int param1Int1, int param1Int2) {
/* 171 */       if (this.type == 1) {
/* 172 */         if (Double.isNaN(param1Double1) && Double.isNaN(param1Double2) && Double.isNaN(param1Double3) && Double.isNaN(param1Double4)) {
/* 173 */           this.wasEmpty = true;
/* 174 */           this.output.valueNull();
/*     */           return;
/*     */         } 
/* 177 */         if (!this.inMulti) {
/* 178 */           writeStartObject(1);
/*     */         }
/*     */       } 
/* 181 */       this.output.startArray();
/* 182 */       writeDouble(param1Double1);
/* 183 */       writeDouble(param1Double2);
/* 184 */       if ((this.dimensionSystem & 0x1) != 0) {
/* 185 */         writeDouble(param1Double3);
/*     */       }
/* 187 */       if ((this.dimensionSystem & 0x2) != 0) {
/* 188 */         writeDouble(param1Double4);
/*     */       }
/* 190 */       this.output.endArray();
/* 191 */       if (this.type != 1 && param1Int1 + 1 == param1Int2) {
/* 192 */         this.output.endArray();
/*     */       }
/*     */     }
/*     */     
/*     */     private void writeStartObject(int param1Int) {
/* 197 */       this.output.startObject();
/* 198 */       this.output.member("type");
/* 199 */       this.output.valueString(GeoJsonUtils.TYPES[param1Int - 1]);
/* 200 */       this.output.member((param1Int != 7) ? "coordinates" : "geometries");
/* 201 */       if (param1Int != 1) {
/* 202 */         this.output.startArray();
/*     */       }
/*     */     }
/*     */     
/*     */     private void writeDouble(double param1Double) {
/* 207 */       this.output.valueNumber(BigDecimal.valueOf(GeometryUtils.checkFinite(param1Double)).stripTrailingZeros());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] ewkbToGeoJson(byte[] paramArrayOfbyte, int paramInt) {
/* 226 */     JSONByteArrayTarget jSONByteArrayTarget = new JSONByteArrayTarget();
/* 227 */     GeoJsonTarget geoJsonTarget = new GeoJsonTarget(jSONByteArrayTarget, paramInt);
/* 228 */     EWKBUtils.parseEWKB(paramArrayOfbyte, geoJsonTarget);
/* 229 */     return jSONByteArrayTarget.getResult();
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
/*     */   public static byte[] geoJsonToEwkb(byte[] paramArrayOfbyte, int paramInt) {
/* 244 */     JSONValue jSONValue = (JSONValue)JSONBytesSource.parse(paramArrayOfbyte, (JSONTarget)new JSONValueTarget());
/* 245 */     GeometryUtils.DimensionSystemTarget dimensionSystemTarget = new GeometryUtils.DimensionSystemTarget();
/* 246 */     parse(jSONValue, dimensionSystemTarget);
/* 247 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 248 */     EWKBUtils.EWKBTarget eWKBTarget = new EWKBUtils.EWKBTarget(byteArrayOutputStream, dimensionSystemTarget.getDimensionSystem());
/* 249 */     eWKBTarget.init(paramInt);
/* 250 */     parse(jSONValue, eWKBTarget);
/* 251 */     return byteArrayOutputStream.toByteArray();
/*     */   }
/*     */   
/*     */   private static void parse(JSONValue paramJSONValue, GeometryUtils.Target paramTarget) {
/* 255 */     if (paramJSONValue instanceof org.h2.util.json.JSONNull)
/* 256 */     { paramTarget.startPoint();
/* 257 */       paramTarget.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
/* 258 */       paramTarget.endObject(1); }
/* 259 */     else { if (paramJSONValue instanceof JSONObject) {
/* 260 */         JSONObject jSONObject = (JSONObject)paramJSONValue;
/* 261 */         JSONValue jSONValue = jSONObject.getFirst("type");
/* 262 */         if (!(jSONValue instanceof JSONString)) {
/* 263 */           throw new IllegalArgumentException();
/*     */         }
/* 265 */         switch (((JSONString)jSONValue).getString()) {
/*     */           case "Point":
/* 267 */             parse(jSONObject, paramTarget, 1);
/*     */             return;
/*     */           case "LineString":
/* 270 */             parse(jSONObject, paramTarget, 2);
/*     */             return;
/*     */           case "Polygon":
/* 273 */             parse(jSONObject, paramTarget, 3);
/*     */             return;
/*     */           case "MultiPoint":
/* 276 */             parse(jSONObject, paramTarget, 4);
/*     */             return;
/*     */           case "MultiLineString":
/* 279 */             parse(jSONObject, paramTarget, 5);
/*     */             return;
/*     */           case "MultiPolygon":
/* 282 */             parse(jSONObject, paramTarget, 6);
/*     */             return;
/*     */           case "GeometryCollection":
/* 285 */             parseGeometryCollection(jSONObject, paramTarget);
/*     */             return;
/*     */         } 
/* 288 */         throw new IllegalArgumentException();
/*     */       } 
/*     */       
/* 291 */       throw new IllegalArgumentException(); }
/*     */      } private static void parse(JSONObject paramJSONObject, GeometryUtils.Target paramTarget, int paramInt) {
/*     */     JSONValue[] arrayOfJSONValue;
/*     */     int i;
/*     */     byte b;
/* 296 */     JSONValue jSONValue = paramJSONObject.getFirst("coordinates");
/* 297 */     if (!(jSONValue instanceof JSONArray)) {
/* 298 */       throw new IllegalArgumentException();
/*     */     }
/* 300 */     JSONArray jSONArray = (JSONArray)jSONValue;
/* 301 */     switch (paramInt) {
/*     */       case 1:
/* 303 */         paramTarget.startPoint();
/* 304 */         parseCoordinate((JSONValue)jSONArray, paramTarget, 0, 1);
/* 305 */         paramTarget.endObject(1);
/*     */         return;
/*     */       case 2:
/* 308 */         parseLineString(jSONArray, paramTarget);
/*     */         return;
/*     */       
/*     */       case 3:
/* 312 */         parsePolygon(jSONArray, paramTarget);
/*     */         return;
/*     */       
/*     */       case 4:
/* 316 */         arrayOfJSONValue = jSONArray.getArray();
/* 317 */         i = arrayOfJSONValue.length;
/* 318 */         paramTarget.startCollection(4, i);
/* 319 */         for (b = 0; b < i; b++) {
/* 320 */           paramTarget.startPoint();
/* 321 */           parseCoordinate(arrayOfJSONValue[b], paramTarget, 0, 1);
/* 322 */           paramTarget.endObject(1);
/* 323 */           paramTarget.endCollectionItem(paramTarget, 4, b, i);
/*     */         } 
/* 325 */         paramTarget.endObject(4);
/*     */         return;
/*     */       
/*     */       case 5:
/* 329 */         arrayOfJSONValue = jSONArray.getArray();
/* 330 */         i = arrayOfJSONValue.length;
/* 331 */         paramTarget.startCollection(5, i);
/* 332 */         for (b = 0; b < i; b++) {
/* 333 */           JSONValue jSONValue1 = arrayOfJSONValue[b];
/* 334 */           if (!(jSONValue1 instanceof JSONArray)) {
/* 335 */             throw new IllegalArgumentException();
/*     */           }
/* 337 */           parseLineString((JSONArray)jSONValue1, paramTarget);
/* 338 */           paramTarget.endCollectionItem(paramTarget, 5, b, i);
/*     */         } 
/* 340 */         paramTarget.endObject(5);
/*     */         return;
/*     */       
/*     */       case 6:
/* 344 */         arrayOfJSONValue = jSONArray.getArray();
/* 345 */         i = arrayOfJSONValue.length;
/* 346 */         paramTarget.startCollection(6, i);
/* 347 */         for (b = 0; b < i; b++) {
/* 348 */           JSONValue jSONValue1 = arrayOfJSONValue[b];
/* 349 */           if (!(jSONValue1 instanceof JSONArray)) {
/* 350 */             throw new IllegalArgumentException();
/*     */           }
/* 352 */           parsePolygon((JSONArray)jSONValue1, paramTarget);
/* 353 */           paramTarget.endCollectionItem(paramTarget, 6, b, i);
/*     */         } 
/* 355 */         paramTarget.endObject(6);
/*     */         return;
/*     */     } 
/*     */     
/* 359 */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseGeometryCollection(JSONObject paramJSONObject, GeometryUtils.Target paramTarget) {
/* 364 */     JSONValue jSONValue = paramJSONObject.getFirst("geometries");
/* 365 */     if (!(jSONValue instanceof JSONArray)) {
/* 366 */       throw new IllegalArgumentException();
/*     */     }
/* 368 */     JSONArray jSONArray = (JSONArray)jSONValue;
/* 369 */     JSONValue[] arrayOfJSONValue = jSONArray.getArray();
/* 370 */     int i = arrayOfJSONValue.length;
/* 371 */     paramTarget.startCollection(7, i);
/* 372 */     for (byte b = 0; b < i; b++) {
/* 373 */       JSONValue jSONValue1 = arrayOfJSONValue[b];
/* 374 */       parse(jSONValue1, paramTarget);
/* 375 */       paramTarget.endCollectionItem(paramTarget, 7, b, i);
/*     */     } 
/* 377 */     paramTarget.endObject(7);
/*     */   }
/*     */   
/*     */   private static void parseLineString(JSONArray paramJSONArray, GeometryUtils.Target paramTarget) {
/* 381 */     JSONValue[] arrayOfJSONValue = paramJSONArray.getArray();
/* 382 */     int i = arrayOfJSONValue.length;
/* 383 */     paramTarget.startLineString(i);
/* 384 */     for (byte b = 0; b < i; b++) {
/* 385 */       parseCoordinate(arrayOfJSONValue[b], paramTarget, b, i);
/*     */     }
/* 387 */     paramTarget.endObject(2);
/*     */   }
/*     */   
/*     */   private static void parsePolygon(JSONArray paramJSONArray, GeometryUtils.Target paramTarget) {
/* 391 */     JSONValue[] arrayOfJSONValue = paramJSONArray.getArray();
/* 392 */     int i = arrayOfJSONValue.length;
/* 393 */     if (i == 0) {
/* 394 */       paramTarget.startPolygon(0, 0);
/*     */     } else {
/* 396 */       JSONValue jSONValue = arrayOfJSONValue[0];
/* 397 */       if (!(jSONValue instanceof JSONArray)) {
/* 398 */         throw new IllegalArgumentException();
/*     */       }
/* 400 */       JSONValue[] arrayOfJSONValue1 = ((JSONArray)jSONValue).getArray();
/* 401 */       paramTarget.startPolygon(i - 1, arrayOfJSONValue1.length);
/* 402 */       parseRing(arrayOfJSONValue1, paramTarget);
/* 403 */       for (byte b = 1; b < i; b++) {
/* 404 */         jSONValue = arrayOfJSONValue[b];
/* 405 */         if (!(jSONValue instanceof JSONArray)) {
/* 406 */           throw new IllegalArgumentException();
/*     */         }
/* 408 */         arrayOfJSONValue1 = ((JSONArray)jSONValue).getArray();
/* 409 */         paramTarget.startPolygonInner(arrayOfJSONValue1.length);
/* 410 */         parseRing(arrayOfJSONValue1, paramTarget);
/*     */       } 
/* 412 */       paramTarget.endNonEmptyPolygon();
/*     */     } 
/* 414 */     paramTarget.endObject(3);
/*     */   }
/*     */   
/*     */   private static void parseRing(JSONValue[] paramArrayOfJSONValue, GeometryUtils.Target paramTarget) {
/* 418 */     int i = paramArrayOfJSONValue.length;
/* 419 */     for (byte b = 0; b < i; b++) {
/* 420 */       parseCoordinate(paramArrayOfJSONValue[b], paramTarget, b, i);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void parseCoordinate(JSONValue paramJSONValue, GeometryUtils.Target paramTarget, int paramInt1, int paramInt2) {
/* 425 */     if (paramJSONValue instanceof org.h2.util.json.JSONNull) {
/* 426 */       paramTarget.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
/*     */       return;
/*     */     } 
/* 429 */     if (!(paramJSONValue instanceof JSONArray)) {
/* 430 */       throw new IllegalArgumentException();
/*     */     }
/* 432 */     JSONValue[] arrayOfJSONValue = ((JSONArray)paramJSONValue).getArray();
/* 433 */     int i = arrayOfJSONValue.length;
/* 434 */     if (i < 2) {
/* 435 */       throw new IllegalArgumentException();
/*     */     }
/* 437 */     paramTarget.addCoordinate(readCoordinate(arrayOfJSONValue, 0), readCoordinate(arrayOfJSONValue, 1), readCoordinate(arrayOfJSONValue, 2), 
/* 438 */         readCoordinate(arrayOfJSONValue, 3), paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */   private static double readCoordinate(JSONValue[] paramArrayOfJSONValue, int paramInt) {
/* 442 */     if (paramInt >= paramArrayOfJSONValue.length) {
/* 443 */       return Double.NaN;
/*     */     }
/* 445 */     JSONValue jSONValue = paramArrayOfJSONValue[paramInt];
/* 446 */     if (!(jSONValue instanceof JSONNumber)) {
/* 447 */       throw new IllegalArgumentException();
/*     */     }
/* 449 */     return ((JSONNumber)jSONValue).getBigDecimal().doubleValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\geometry\GeoJsonUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */