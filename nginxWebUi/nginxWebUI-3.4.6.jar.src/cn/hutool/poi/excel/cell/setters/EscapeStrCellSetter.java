/*    */ package cn.hutool.poi.excel.cell.setters;
/*    */ 
/*    */ import cn.hutool.core.util.ReUtil;
/*    */ import cn.hutool.core.util.StrUtil;
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
/*    */ public class EscapeStrCellSetter
/*    */   extends CharSequenceCellSetter
/*    */ {
/* 18 */   private static final Pattern utfPtrn = Pattern.compile("_x[0-9A-Fa-f]{4}_");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EscapeStrCellSetter(CharSequence value) {
/* 26 */     super(escape(StrUtil.str(value)));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String escape(String value) {
/* 37 */     if (value == null || false == value.contains("_x")) {
/* 38 */       return value;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 43 */     return ReUtil.replaceAll(value, utfPtrn, "_x005F$0");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\setters\EscapeStrCellSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */