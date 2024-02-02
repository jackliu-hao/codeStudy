/*     */ package cn.hutool.poi.word;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.poi.xwpf.usermodel.XWPFDocument;
/*     */ import org.apache.poi.xwpf.usermodel.XWPFTable;
/*     */ import org.apache.poi.xwpf.usermodel.XWPFTableCell;
/*     */ import org.apache.poi.xwpf.usermodel.XWPFTableRow;
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
/*     */ public class TableUtil
/*     */ {
/*     */   public static XWPFTable createTable(XWPFDocument doc) {
/*  32 */     return createTable(doc, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static XWPFTable createTable(XWPFDocument doc, Iterable<?> data) {
/*  43 */     Assert.notNull(doc, "XWPFDocument must be not null !", new Object[0]);
/*  44 */     XWPFTable table = doc.createTable();
/*     */     
/*  46 */     table.removeRow(0);
/*  47 */     return writeTable(table, data);
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
/*     */   public static XWPFTable writeTable(XWPFTable table, Iterable<?> data) {
/*  59 */     Assert.notNull(table, "XWPFTable must be not null !", new Object[0]);
/*  60 */     if (IterUtil.isEmpty(data))
/*     */     {
/*  62 */       return table;
/*     */     }
/*     */     
/*  65 */     boolean isFirst = true;
/*  66 */     for (Object rowData : data) {
/*  67 */       writeRow(table.createRow(), rowData, isFirst);
/*  68 */       if (isFirst) {
/*  69 */         isFirst = false;
/*     */       }
/*     */     } 
/*     */     
/*  73 */     return table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeRow(XWPFTableRow row, Object rowBean, boolean isWriteKeyAsHead) {
/*     */     Map<?, ?> rowMap;
/*  85 */     if (rowBean instanceof Iterable) {
/*  86 */       writeRow(row, (Iterable)rowBean);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  91 */     if (rowBean instanceof Map) {
/*  92 */       rowMap = (Map)rowBean;
/*  93 */     } else if (BeanUtil.isBean(rowBean.getClass())) {
/*  94 */       rowMap = BeanUtil.beanToMap(rowBean, new LinkedHashMap<>(), false, false);
/*     */     } else {
/*     */       
/*  97 */       writeRow(row, CollUtil.newArrayList(new Object[] { rowBean }, ), isWriteKeyAsHead);
/*     */       
/*     */       return;
/*     */     } 
/* 101 */     writeRow(row, rowMap, isWriteKeyAsHead);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeRow(XWPFTableRow row, Map<?, ?> rowMap, boolean isWriteKeyAsHead) {
/* 112 */     if (MapUtil.isEmpty(rowMap)) {
/*     */       return;
/*     */     }
/*     */     
/* 116 */     if (isWriteKeyAsHead) {
/* 117 */       writeRow(row, rowMap.keySet());
/* 118 */       row = row.getTable().createRow();
/*     */     } 
/* 120 */     writeRow(row, rowMap.values());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeRow(XWPFTableRow row, Iterable<?> rowData) {
/* 131 */     int index = 0;
/* 132 */     for (Object cellData : rowData) {
/* 133 */       XWPFTableCell cell = getOrCreateCell(row, index);
/* 134 */       cell.setText(Convert.toStr(cellData));
/* 135 */       index++;
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
/*     */   public static XWPFTableRow getOrCreateRow(XWPFTable table, int index) {
/* 148 */     XWPFTableRow row = table.getRow(index);
/* 149 */     if (null == row) {
/* 150 */       row = table.createRow();
/*     */     }
/*     */     
/* 153 */     return row;
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
/*     */   public static XWPFTableCell getOrCreateCell(XWPFTableRow row, int index) {
/* 165 */     XWPFTableCell cell = row.getCell(index);
/* 166 */     if (null == cell) {
/* 167 */       cell = row.createCell();
/*     */     }
/* 169 */     return cell;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\word\TableUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */