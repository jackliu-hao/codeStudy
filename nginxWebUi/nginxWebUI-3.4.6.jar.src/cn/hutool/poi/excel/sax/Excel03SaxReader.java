/*     */ package cn.hutool.poi.excel.sax;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.excel.sax.handler.RowHandler;
/*     */ import cn.hutool.poi.exceptions.POIException;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
/*     */ import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
/*     */ import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
/*     */ import org.apache.poi.hssf.eventusermodel.HSSFListener;
/*     */ import org.apache.poi.hssf.eventusermodel.HSSFRequest;
/*     */ import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
/*     */ import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
/*     */ import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
/*     */ import org.apache.poi.hssf.record.BOFRecord;
/*     */ import org.apache.poi.hssf.record.BoolErrRecord;
/*     */ import org.apache.poi.hssf.record.BoundSheetRecord;
/*     */ import org.apache.poi.hssf.record.CellValueRecordInterface;
/*     */ import org.apache.poi.hssf.record.FormulaRecord;
/*     */ import org.apache.poi.hssf.record.LabelRecord;
/*     */ import org.apache.poi.hssf.record.LabelSSTRecord;
/*     */ import org.apache.poi.hssf.record.NumberRecord;
/*     */ import org.apache.poi.hssf.record.Record;
/*     */ import org.apache.poi.hssf.record.SSTRecord;
/*     */ import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/*     */ import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
/*     */ public class Excel03SaxReader
/*     */   implements HSSFListener, ExcelSaxReader<Excel03SaxReader>
/*     */ {
/*     */   private final boolean isOutputFormulaValues = true;
/*     */   private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
/*     */   private HSSFWorkbook stubWorkbook;
/*     */   private SSTRecord sstRecord;
/*     */   private FormatTrackingHSSFListener formatListener;
/*  72 */   private final List<BoundSheetRecord> boundSheetRecords = new ArrayList<>();
/*     */ 
/*     */   
/*     */   private boolean isOutputNextStringRecord;
/*     */   
/*  77 */   private List<Object> rowCellList = new ArrayList();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private int rid = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String sheetName;
/*     */ 
/*     */ 
/*     */   
/*  91 */   private int curRid = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final RowHandler rowHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Excel03SaxReader(RowHandler rowHandler) {
/* 104 */     this.rowHandler = rowHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Excel03SaxReader read(File file, String idOrRidOrSheetName) throws POIException {
/* 110 */     try (POIFSFileSystem poifsFileSystem = new POIFSFileSystem(file, true)) {
/* 111 */       return read(poifsFileSystem, idOrRidOrSheetName);
/* 112 */     } catch (IOException e) {
/* 113 */       throw new POIException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Excel03SaxReader read(InputStream excelStream, String idOrRidOrSheetName) throws POIException {
/*     */     try {
/* 120 */       return read(new POIFSFileSystem(excelStream), idOrRidOrSheetName);
/* 121 */     } catch (IOException e) {
/* 122 */       throw new POIException(e);
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
/*     */   public Excel03SaxReader read(POIFSFileSystem fs, String idOrRidOrSheetName) throws POIException {
/* 135 */     this.rid = getSheetIndex(idOrRidOrSheetName);
/*     */     
/* 137 */     this.formatListener = new FormatTrackingHSSFListener((HSSFListener)new MissingRecordAwareHSSFListener(this));
/* 138 */     HSSFRequest request = new HSSFRequest();
/*     */     
/* 140 */     request.addListenerForAllRecords((HSSFListener)this.formatListener);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     HSSFEventFactory factory = new HSSFEventFactory();
/*     */     try {
/* 147 */       factory.processWorkbookEvents(request, fs);
/* 148 */     } catch (IOException e) {
/* 149 */       throw new POIException(e);
/*     */     } finally {
/* 151 */       IoUtil.close((Closeable)fs);
/*     */     } 
/* 153 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSheetIndex() {
/* 163 */     return this.rid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSheetName() {
/* 172 */     if (null != this.sheetName) {
/* 173 */       return this.sheetName;
/*     */     }
/*     */     
/* 176 */     if (this.boundSheetRecords.size() > this.rid) {
/* 177 */       return ((BoundSheetRecord)this.boundSheetRecords.get((this.rid > -1) ? this.rid : this.curRid)).getSheetname();
/*     */     }
/*     */     
/* 180 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void processRecord(Record record) {
/* 190 */     if (this.rid > -1 && this.curRid > this.rid) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 195 */     if (record instanceof BoundSheetRecord) {
/*     */       
/* 197 */       BoundSheetRecord boundSheetRecord = (BoundSheetRecord)record;
/* 198 */       this.boundSheetRecords.add(boundSheetRecord);
/* 199 */       String currentSheetName = boundSheetRecord.getSheetname();
/* 200 */       if (null != this.sheetName && StrUtil.equals(this.sheetName, currentSheetName)) {
/* 201 */         this.rid = this.boundSheetRecords.size() - 1;
/*     */       }
/* 203 */     } else if (record instanceof SSTRecord) {
/*     */       
/* 205 */       this.sstRecord = (SSTRecord)record;
/* 206 */     } else if (record instanceof BOFRecord) {
/* 207 */       BOFRecord bofRecord = (BOFRecord)record;
/* 208 */       if (bofRecord.getType() == 16) {
/*     */         
/* 210 */         if (this.workbookBuildingListener != null && this.stubWorkbook == null) {
/* 211 */           this.stubWorkbook = this.workbookBuildingListener.getStubHSSFWorkbook();
/*     */         }
/* 213 */         this.curRid++;
/*     */       } 
/* 215 */     } else if (record instanceof org.apache.poi.hssf.record.EOFRecord) {
/* 216 */       if (this.rid < 0 && null != this.sheetName) {
/* 217 */         throw new POIException("Sheet [{}] not exist!", new Object[] { this.sheetName });
/*     */       }
/* 219 */       processLastCellSheet();
/* 220 */     } else if (isProcessCurrentSheet()) {
/* 221 */       if (record instanceof MissingCellDummyRecord) {
/*     */         
/* 223 */         MissingCellDummyRecord mc = (MissingCellDummyRecord)record;
/* 224 */         addToRowCellList(mc);
/* 225 */       } else if (record instanceof LastCellOfRowDummyRecord) {
/*     */         
/* 227 */         processLastCell((LastCellOfRowDummyRecord)record);
/*     */       } else {
/*     */         
/* 230 */         processCellValue(record);
/*     */       } 
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
/*     */   private void addToRowCellList(MissingCellDummyRecord record) {
/* 244 */     addToRowCellList(record.getRow(), record.getColumn(), "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addToRowCellList(CellValueRecordInterface record, Object value) {
/* 254 */     addToRowCellList(record.getRow(), record.getColumn(), value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addToRowCellList(int row, int column, Object value) {
/* 265 */     while (column > this.rowCellList.size()) {
/*     */       
/* 267 */       this.rowCellList.add("");
/* 268 */       this.rowHandler.handleCell(this.curRid, row, this.rowCellList.size() - 1, value, null);
/*     */     } 
/*     */     
/* 271 */     this.rowCellList.add(column, value);
/* 272 */     this.rowHandler.handleCell(this.curRid, row, column, value, null);
/*     */   }
/*     */   
/*     */   private void processCellValue(Record record) {
/*     */     BoolErrRecord berec;
/*     */     FormulaRecord formulaRec;
/*     */     LabelRecord lrec;
/*     */     LabelSSTRecord lsrec;
/*     */     NumberRecord numrec;
/* 281 */     Object value = null;
/*     */     
/* 283 */     switch (record.getSid()) {
/*     */       
/*     */       case 513:
/* 286 */         addToRowCellList((CellValueRecordInterface)record, "");
/*     */         break;
/*     */       
/*     */       case 517:
/* 290 */         berec = (BoolErrRecord)record;
/* 291 */         addToRowCellList((CellValueRecordInterface)berec, Boolean.valueOf(berec.getBooleanValue()));
/*     */         break;
/*     */       
/*     */       case 6:
/* 295 */         formulaRec = (FormulaRecord)record;
/*     */         
/* 297 */         if (Double.isNaN(formulaRec.getValue())) {
/*     */ 
/*     */           
/* 300 */           this.isOutputNextStringRecord = true;
/*     */         } else {
/* 302 */           value = ExcelSaxUtil.getNumberOrDateValue((CellValueRecordInterface)formulaRec, formulaRec.getValue(), this.formatListener);
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 307 */         addToRowCellList((CellValueRecordInterface)formulaRec, value);
/*     */         break;
/*     */       
/*     */       case 519:
/* 311 */         if (this.isOutputNextStringRecord)
/*     */         {
/*     */           
/* 314 */           this.isOutputNextStringRecord = false;
/*     */         }
/*     */         break;
/*     */       case 516:
/* 318 */         lrec = (LabelRecord)record;
/* 319 */         value = lrec.getValue();
/* 320 */         addToRowCellList((CellValueRecordInterface)lrec, value);
/*     */         break;
/*     */       
/*     */       case 253:
/* 324 */         lsrec = (LabelSSTRecord)record;
/* 325 */         if (null != this.sstRecord) {
/* 326 */           value = this.sstRecord.getString(lsrec.getSSTIndex()).toString();
/*     */         }
/* 328 */         addToRowCellList((CellValueRecordInterface)lsrec, ObjectUtil.defaultIfNull(value, ""));
/*     */         break;
/*     */       case 515:
/* 331 */         numrec = (NumberRecord)record;
/* 332 */         value = ExcelSaxUtil.getNumberOrDateValue((CellValueRecordInterface)numrec, numrec.getValue(), this.formatListener);
/*     */         
/* 334 */         addToRowCellList((CellValueRecordInterface)numrec, value);
/*     */         break;
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
/*     */   private void processLastCell(LastCellOfRowDummyRecord lastCell) {
/* 348 */     this.rowHandler.handle(this.curRid, lastCell.getRow(), this.rowCellList);
/*     */     
/* 350 */     this.rowCellList = new ArrayList(this.rowCellList.size());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processLastCellSheet() {
/* 357 */     this.rowHandler.doAfterAllAnalysed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isProcessCurrentSheet() {
/* 367 */     return ((this.rid < 0 && null == this.sheetName) || this.rid == this.curRid);
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
/*     */   private int getSheetIndex(String idOrRidOrSheetName) {
/* 382 */     Assert.notBlank(idOrRidOrSheetName, "id or rid or sheetName must be not blank!", new Object[0]);
/*     */ 
/*     */     
/* 385 */     if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, "rId"))
/* 386 */       return Integer.parseInt(StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, "rId")); 
/* 387 */     if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, "sheetName:")) {
/*     */       
/* 389 */       this.sheetName = StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, "sheetName:");
/*     */     } else {
/*     */       try {
/* 392 */         return Integer.parseInt(idOrRidOrSheetName);
/* 393 */       } catch (NumberFormatException ignore) {
/*     */         
/* 395 */         this.sheetName = idOrRidOrSheetName;
/*     */       } 
/*     */     } 
/*     */     
/* 399 */     return -1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\Excel03SaxReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */