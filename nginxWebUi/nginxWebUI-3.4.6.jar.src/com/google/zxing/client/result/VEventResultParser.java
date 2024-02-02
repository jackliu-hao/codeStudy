/*     */ package com.google.zxing.client.result;
/*     */ 
/*     */ import com.google.zxing.Result;
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
/*     */ public final class VEventResultParser
/*     */   extends ResultParser
/*     */ {
/*     */   public CalendarParsedResult parse(Result result) {
/*     */     double latitude, longitude;
/*     */     String rawText;
/*  34 */     if ((rawText = getMassagedText(result)).indexOf("BEGIN:VEVENT") < 
/*  35 */       0) {
/*  36 */       return null;
/*     */     }
/*     */     
/*  39 */     String summary = matchSingleVCardPrefixedField("SUMMARY", rawText, true);
/*     */     String start;
/*  41 */     if ((start = matchSingleVCardPrefixedField("DTSTART", rawText, true)) == null) {
/*  42 */       return null;
/*     */     }
/*  44 */     String end = matchSingleVCardPrefixedField("DTEND", rawText, true);
/*  45 */     String duration = matchSingleVCardPrefixedField("DURATION", rawText, true);
/*  46 */     String location = matchSingleVCardPrefixedField("LOCATION", rawText, true);
/*  47 */     String organizer = stripMailto(matchSingleVCardPrefixedField("ORGANIZER", rawText, true));
/*     */     
/*     */     String[] attendees;
/*  50 */     if ((attendees = matchVCardPrefixedField("ATTENDEE", rawText, true)) != null) {
/*  51 */       for (int i = 0; i < attendees.length; i++) {
/*  52 */         attendees[i] = stripMailto(attendees[i]);
/*     */       }
/*     */     }
/*  55 */     String description = matchSingleVCardPrefixedField("DESCRIPTION", rawText, true);
/*     */ 
/*     */     
/*     */     String geoString;
/*     */     
/*  60 */     if ((geoString = matchSingleVCardPrefixedField("GEO", rawText, true)) == null) {
/*  61 */       latitude = Double.NaN;
/*  62 */       longitude = Double.NaN;
/*     */     } else {
/*     */       int semicolon;
/*  65 */       if ((semicolon = geoString.indexOf(';')) < 0) {
/*  66 */         return null;
/*     */       }
/*     */       try {
/*  69 */         latitude = Double.parseDouble(geoString.substring(0, semicolon));
/*  70 */         longitude = Double.parseDouble(geoString.substring(semicolon + 1));
/*  71 */       } catch (NumberFormatException numberFormatException) {
/*  72 */         return null;
/*     */       } 
/*     */     } 
/*     */     
/*     */     try {
/*  77 */       return new CalendarParsedResult(summary, start, end, duration, location, organizer, attendees, description, latitude, longitude);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*  87 */     catch (IllegalArgumentException illegalArgumentException) {
/*  88 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String matchSingleVCardPrefixedField(CharSequence prefix, String rawText, boolean trim) {
/*     */     List<String> values;
/*  96 */     if ((values = VCardResultParser.matchSingleVCardPrefixedField(prefix, rawText, trim, false)) == null || values.isEmpty()) return null;  return values.get(0);
/*     */   }
/*     */   
/*     */   private static String[] matchVCardPrefixedField(CharSequence prefix, String rawText, boolean trim) {
/*     */     List<List<String>> values;
/* 101 */     if ((values = VCardResultParser.matchVCardPrefixedField(prefix, rawText, trim, false)) == null || values.isEmpty()) {
/* 102 */       return null;
/*     */     }
/*     */     int size;
/* 105 */     String[] result = new String[size = values.size()];
/* 106 */     for (int i = 0; i < size; i++) {
/* 107 */       result[i] = ((List<String>)values.get(i)).get(0);
/*     */     }
/* 109 */     return result;
/*     */   }
/*     */   
/*     */   private static String stripMailto(String s) {
/* 113 */     if (s != null && (s.startsWith("mailto:") || s.startsWith("MAILTO:"))) {
/* 114 */       s = s.substring(7);
/*     */     }
/* 116 */     return s;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\VEventResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */