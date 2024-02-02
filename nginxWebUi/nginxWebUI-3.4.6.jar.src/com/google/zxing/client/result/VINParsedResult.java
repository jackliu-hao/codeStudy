/*     */ package com.google.zxing.client.result;
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
/*     */ public final class VINParsedResult
/*     */   extends ParsedResult
/*     */ {
/*     */   private final String vin;
/*     */   private final String worldManufacturerID;
/*     */   private final String vehicleDescriptorSection;
/*     */   private final String vehicleIdentifierSection;
/*     */   private final String countryCode;
/*     */   private final String vehicleAttributes;
/*     */   private final int modelYear;
/*     */   private final char plantCode;
/*     */   private final String sequentialNumber;
/*     */   
/*     */   public VINParsedResult(String vin, String worldManufacturerID, String vehicleDescriptorSection, String vehicleIdentifierSection, String countryCode, String vehicleAttributes, int modelYear, char plantCode, String sequentialNumber) {
/*  44 */     super(ParsedResultType.VIN);
/*  45 */     this.vin = vin;
/*  46 */     this.worldManufacturerID = worldManufacturerID;
/*  47 */     this.vehicleDescriptorSection = vehicleDescriptorSection;
/*  48 */     this.vehicleIdentifierSection = vehicleIdentifierSection;
/*  49 */     this.countryCode = countryCode;
/*  50 */     this.vehicleAttributes = vehicleAttributes;
/*  51 */     this.modelYear = modelYear;
/*  52 */     this.plantCode = plantCode;
/*  53 */     this.sequentialNumber = sequentialNumber;
/*     */   }
/*     */   
/*     */   public String getVIN() {
/*  57 */     return this.vin;
/*     */   }
/*     */   
/*     */   public String getWorldManufacturerID() {
/*  61 */     return this.worldManufacturerID;
/*     */   }
/*     */   
/*     */   public String getVehicleDescriptorSection() {
/*  65 */     return this.vehicleDescriptorSection;
/*     */   }
/*     */   
/*     */   public String getVehicleIdentifierSection() {
/*  69 */     return this.vehicleIdentifierSection;
/*     */   }
/*     */   
/*     */   public String getCountryCode() {
/*  73 */     return this.countryCode;
/*     */   }
/*     */   
/*     */   public String getVehicleAttributes() {
/*  77 */     return this.vehicleAttributes;
/*     */   }
/*     */   
/*     */   public int getModelYear() {
/*  81 */     return this.modelYear;
/*     */   }
/*     */   
/*     */   public char getPlantCode() {
/*  85 */     return this.plantCode;
/*     */   }
/*     */   
/*     */   public String getSequentialNumber() {
/*  89 */     return this.sequentialNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayResult() {
/*     */     StringBuilder result;
/*  95 */     (result = new StringBuilder(50)).append(this.worldManufacturerID).append(' ');
/*  96 */     result.append(this.vehicleDescriptorSection).append(' ');
/*  97 */     result.append(this.vehicleIdentifierSection).append('\n');
/*  98 */     if (this.countryCode != null) {
/*  99 */       result.append(this.countryCode).append(' ');
/*     */     }
/* 101 */     result.append(this.modelYear).append(' ');
/* 102 */     result.append(this.plantCode).append(' ');
/* 103 */     result.append(this.sequentialNumber).append('\n');
/* 104 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\VINParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */