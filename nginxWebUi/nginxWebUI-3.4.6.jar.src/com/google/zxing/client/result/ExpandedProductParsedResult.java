/*     */ package com.google.zxing.client.result;
/*     */ 
/*     */ import java.util.Map;
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
/*     */ public final class ExpandedProductParsedResult
/*     */   extends ParsedResult
/*     */ {
/*     */   public static final String KILOGRAM = "KG";
/*     */   public static final String POUND = "LB";
/*     */   private final String rawText;
/*     */   private final String productID;
/*     */   private final String sscc;
/*     */   private final String lotNumber;
/*     */   private final String productionDate;
/*     */   private final String packagingDate;
/*     */   private final String bestBeforeDate;
/*     */   private final String expirationDate;
/*     */   private final String weight;
/*     */   private final String weightType;
/*     */   private final String weightIncrement;
/*     */   private final String price;
/*     */   private final String priceIncrement;
/*     */   private final String priceCurrency;
/*     */   private final Map<String, String> uncommonAIs;
/*     */   
/*     */   public ExpandedProductParsedResult(String rawText, String productID, String sscc, String lotNumber, String productionDate, String packagingDate, String bestBeforeDate, String expirationDate, String weight, String weightType, String weightIncrement, String price, String priceIncrement, String priceCurrency, Map<String, String> uncommonAIs) {
/*  75 */     super(ParsedResultType.PRODUCT);
/*  76 */     this.rawText = rawText;
/*  77 */     this.productID = productID;
/*  78 */     this.sscc = sscc;
/*  79 */     this.lotNumber = lotNumber;
/*  80 */     this.productionDate = productionDate;
/*  81 */     this.packagingDate = packagingDate;
/*  82 */     this.bestBeforeDate = bestBeforeDate;
/*  83 */     this.expirationDate = expirationDate;
/*  84 */     this.weight = weight;
/*  85 */     this.weightType = weightType;
/*  86 */     this.weightIncrement = weightIncrement;
/*  87 */     this.price = price;
/*  88 */     this.priceIncrement = priceIncrement;
/*  89 */     this.priceCurrency = priceCurrency;
/*  90 */     this.uncommonAIs = uncommonAIs;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  95 */     if (!(o instanceof ExpandedProductParsedResult)) {
/*  96 */       return false;
/*     */     }
/*     */     
/*  99 */     ExpandedProductParsedResult other = (ExpandedProductParsedResult)o;
/*     */     
/* 101 */     if (equalsOrNull(this.productID, other.productID) && 
/* 102 */       equalsOrNull(this.sscc, other.sscc) && 
/* 103 */       equalsOrNull(this.lotNumber, other.lotNumber) && 
/* 104 */       equalsOrNull(this.productionDate, other.productionDate) && 
/* 105 */       equalsOrNull(this.bestBeforeDate, other.bestBeforeDate) && 
/* 106 */       equalsOrNull(this.expirationDate, other.expirationDate) && 
/* 107 */       equalsOrNull(this.weight, other.weight) && 
/* 108 */       equalsOrNull(this.weightType, other.weightType) && 
/* 109 */       equalsOrNull(this.weightIncrement, other.weightIncrement) && 
/* 110 */       equalsOrNull(this.price, other.price) && 
/* 111 */       equalsOrNull(this.priceIncrement, other.priceIncrement) && 
/* 112 */       equalsOrNull(this.priceCurrency, other.priceCurrency) && 
/* 113 */       equalsOrNull(this.uncommonAIs, other.uncommonAIs)) return true; 
/*     */     return false;
/*     */   }
/*     */   private static boolean equalsOrNull(Object o1, Object o2) {
/* 117 */     return (o1 == null) ? ((o2 == null)) : o1.equals(o2);
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
/*     */   public int hashCode() {
/* 135 */     return 0x0 ^ hashNotNull(this.productID) ^ hashNotNull(this.sscc) ^ hashNotNull(this.lotNumber) ^ hashNotNull(this.productionDate) ^ hashNotNull(this.bestBeforeDate) ^ hashNotNull(this.expirationDate) ^ hashNotNull(this.weight) ^ hashNotNull(this.weightType) ^ hashNotNull(this.weightIncrement) ^ hashNotNull(this.price) ^ hashNotNull(this.priceIncrement) ^ hashNotNull(this.priceCurrency) ^ hashNotNull(this.uncommonAIs);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int hashNotNull(Object o) {
/* 140 */     return (o == null) ? 0 : o.hashCode();
/*     */   }
/*     */   
/*     */   public String getRawText() {
/* 144 */     return this.rawText;
/*     */   }
/*     */   
/*     */   public String getProductID() {
/* 148 */     return this.productID;
/*     */   }
/*     */   
/*     */   public String getSscc() {
/* 152 */     return this.sscc;
/*     */   }
/*     */   
/*     */   public String getLotNumber() {
/* 156 */     return this.lotNumber;
/*     */   }
/*     */   
/*     */   public String getProductionDate() {
/* 160 */     return this.productionDate;
/*     */   }
/*     */   
/*     */   public String getPackagingDate() {
/* 164 */     return this.packagingDate;
/*     */   }
/*     */   
/*     */   public String getBestBeforeDate() {
/* 168 */     return this.bestBeforeDate;
/*     */   }
/*     */   
/*     */   public String getExpirationDate() {
/* 172 */     return this.expirationDate;
/*     */   }
/*     */   
/*     */   public String getWeight() {
/* 176 */     return this.weight;
/*     */   }
/*     */   
/*     */   public String getWeightType() {
/* 180 */     return this.weightType;
/*     */   }
/*     */   
/*     */   public String getWeightIncrement() {
/* 184 */     return this.weightIncrement;
/*     */   }
/*     */   
/*     */   public String getPrice() {
/* 188 */     return this.price;
/*     */   }
/*     */   
/*     */   public String getPriceIncrement() {
/* 192 */     return this.priceIncrement;
/*     */   }
/*     */   
/*     */   public String getPriceCurrency() {
/* 196 */     return this.priceCurrency;
/*     */   }
/*     */   
/*     */   public Map<String, String> getUncommonAIs() {
/* 200 */     return this.uncommonAIs;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayResult() {
/* 205 */     return String.valueOf(this.rawText);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\ExpandedProductParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */