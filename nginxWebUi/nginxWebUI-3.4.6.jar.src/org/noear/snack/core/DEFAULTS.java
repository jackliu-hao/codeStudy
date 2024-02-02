/*    */ package org.noear.snack.core;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import java.util.TimeZone;
/*    */ import org.noear.snack.from.Fromer;
/*    */ import org.noear.snack.from.JsonFromer;
/*    */ import org.noear.snack.from.ObjectFromer;
/*    */ import org.noear.snack.to.JsonToer;
/*    */ import org.noear.snack.to.ObjectToer;
/*    */ import org.noear.snack.to.Toer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DEFAULTS
/*    */ {
/* 18 */   public static final int DEF_FEATURES = Feature.QuoteFieldNames.code;
/*    */ 
/*    */   
/*    */   public static final String DEF_TYPE_PROPERTY_NAME = "@type";
/*    */   
/* 23 */   public static final TimeZone DEF_TIME_ZONE = TimeZone.getDefault();
/*    */   
/* 25 */   public static final Locale DEF_LOCALE = Locale.getDefault();
/*    */   
/* 27 */   public static String DEF_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
/*    */ 
/*    */   
/* 30 */   public static final Fromer DEF_OBJECT_FROMER = (Fromer)new ObjectFromer();
/*    */   
/* 32 */   public static final Toer DEF_OBJECT_TOER = (Toer)new ObjectToer();
/*    */ 
/*    */ 
/*    */   
/* 36 */   public static final Fromer DEF_STRING_FROMER = (Fromer)new JsonFromer();
/*    */   
/* 38 */   public static final Toer DEF_STRING_TOER = (Toer)new JsonToer();
/*    */ 
/*    */   
/* 41 */   public static final Toer DEF_JSON_TOER = (Toer)new JsonToer();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\DEFAULTS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */