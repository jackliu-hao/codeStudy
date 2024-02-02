/*    */ package cn.hutool.poi.excel.cell.setters;
/*    */ 
/*    */ import cn.hutool.poi.excel.cell.CellSetter;
/*    */ import java.time.temporal.TemporalAccessor;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import org.apache.poi.ss.usermodel.Hyperlink;
/*    */ import org.apache.poi.ss.usermodel.RichTextString;
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
/*    */ public class CellSetterFactory
/*    */ {
/*    */   public static CellSetter createCellSetter(Object value) {
/* 26 */     if (null == value)
/* 27 */       return NullCellSetter.INSTANCE; 
/* 28 */     if (value instanceof CellSetter)
/* 29 */       return (CellSetter)value; 
/* 30 */     if (value instanceof Date)
/* 31 */       return new DateCellSetter((Date)value); 
/* 32 */     if (value instanceof TemporalAccessor)
/* 33 */       return new TemporalAccessorCellSetter((TemporalAccessor)value); 
/* 34 */     if (value instanceof Calendar)
/* 35 */       return new CalendarCellSetter((Calendar)value); 
/* 36 */     if (value instanceof Boolean)
/* 37 */       return new BooleanCellSetter((Boolean)value); 
/* 38 */     if (value instanceof RichTextString)
/* 39 */       return new RichTextCellSetter((RichTextString)value); 
/* 40 */     if (value instanceof Number)
/* 41 */       return new NumberCellSetter((Number)value); 
/* 42 */     if (value instanceof Hyperlink) {
/* 43 */       return new HyperlinkCellSetter((Hyperlink)value);
/*    */     }
/* 45 */     return new CharSequenceCellSetter(value.toString());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\setters\CellSetterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */