/*     */ package org.slf4j;
/*     */ 
/*     */ import org.slf4j.helpers.BasicMarkerFactory;
/*     */ import org.slf4j.helpers.Util;
/*     */ import org.slf4j.impl.StaticMarkerBinder;
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
/*     */ public class MarkerFactory
/*     */ {
/*     */   static IMarkerFactory MARKER_FACTORY;
/*     */   
/*     */   private static IMarkerFactory bwCompatibleGetMarkerFactoryFromBinder() throws NoClassDefFoundError {
/*     */     try {
/*  61 */       return StaticMarkerBinder.getSingleton().getMarkerFactory();
/*  62 */     } catch (NoSuchMethodError nsme) {
/*     */       
/*  64 */       return StaticMarkerBinder.SINGLETON.getMarkerFactory();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/*  71 */       MARKER_FACTORY = bwCompatibleGetMarkerFactoryFromBinder();
/*  72 */     } catch (NoClassDefFoundError e) {
/*  73 */       MARKER_FACTORY = (IMarkerFactory)new BasicMarkerFactory();
/*  74 */     } catch (Exception e) {
/*     */       
/*  76 */       Util.report("Unexpected failure while binding MarkerFactory", e);
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
/*     */   public static Marker getMarker(String name) {
/*  89 */     return MARKER_FACTORY.getMarker(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Marker getDetachedMarker(String name) {
/* 100 */     return MARKER_FACTORY.getDetachedMarker(name);
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
/*     */   public static IMarkerFactory getIMarkerFactory() {
/* 112 */     return MARKER_FACTORY;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\MarkerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */