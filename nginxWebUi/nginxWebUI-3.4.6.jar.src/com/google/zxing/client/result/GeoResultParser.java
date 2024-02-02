/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ import com.google.zxing.Result;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class GeoResultParser
/*    */   extends ResultParser
/*    */ {
/* 35 */   private static final Pattern GEO_URL_PATTERN = Pattern.compile("geo:([\\-0-9.]+),([\\-0-9.]+)(?:,([\\-0-9.]+))?(?:\\?(.*))?", 2);
/*    */   
/*    */   public GeoParsedResult parse(Result result) {
/*    */     double latitude, longitude, altitude;
/* 39 */     CharSequence rawText = getMassagedText(result);
/*    */     Matcher matcher;
/* 41 */     if (!(matcher = GEO_URL_PATTERN.matcher(rawText)).matches()) {
/* 42 */       return null;
/*    */     }
/*    */     
/* 45 */     String query = matcher.group(4);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 52 */       if ((latitude = Double.parseDouble(matcher.group(1))) > 90.0D || latitude < -90.0D) {
/* 53 */         return null;
/*    */       }
/*    */       
/* 56 */       if ((longitude = Double.parseDouble(matcher.group(2))) > 180.0D || longitude < -180.0D) {
/* 57 */         return null;
/*    */       }
/* 59 */       if (matcher.group(3) == null) {
/* 60 */         altitude = 0.0D;
/*    */       
/*    */       }
/* 63 */       else if ((altitude = Double.parseDouble(matcher.group(3))) < 0.0D) {
/* 64 */         return null;
/*    */       }
/*    */     
/* 67 */     } catch (NumberFormatException numberFormatException) {
/* 68 */       return null;
/*    */     } 
/* 70 */     return new GeoParsedResult(latitude, longitude, altitude, query);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\GeoResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */