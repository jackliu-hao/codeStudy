package cn.hutool.poi.excel.sax;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;

public class Excel03SaxReader implements HSSFListener, ExcelSaxReader<Excel03SaxReader> {
   private final boolean isOutputFormulaValues = true;
   private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
   private HSSFWorkbook stubWorkbook;
   private SSTRecord sstRecord;
   private FormatTrackingHSSFListener formatListener;
   private final List<BoundSheetRecord> boundSheetRecords = new ArrayList();
   private boolean isOutputNextStringRecord;
   private List<Object> rowCellList = new ArrayList();
   private int rid = -1;
   private String sheetName;
   private int curRid = -1;
   private final RowHandler rowHandler;

   public Excel03SaxReader(RowHandler rowHandler) {
      this.rowHandler = rowHandler;
   }

   public Excel03SaxReader read(File file, String idOrRidOrSheetName) throws POIException {
      try {
         POIFSFileSystem poifsFileSystem = new POIFSFileSystem(file, true);
         Throwable var4 = null;

         Excel03SaxReader var5;
         try {
            var5 = this.read(poifsFileSystem, idOrRidOrSheetName);
         } catch (Throwable var15) {
            var4 = var15;
            throw var15;
         } finally {
            if (poifsFileSystem != null) {
               if (var4 != null) {
                  try {
                     poifsFileSystem.close();
                  } catch (Throwable var14) {
                     var4.addSuppressed(var14);
                  }
               } else {
                  poifsFileSystem.close();
               }
            }

         }

         return var5;
      } catch (IOException var17) {
         throw new POIException(var17);
      }
   }

   public Excel03SaxReader read(InputStream excelStream, String idOrRidOrSheetName) throws POIException {
      try {
         return this.read(new POIFSFileSystem(excelStream), idOrRidOrSheetName);
      } catch (IOException var4) {
         throw new POIException(var4);
      }
   }

   public Excel03SaxReader read(POIFSFileSystem fs, String idOrRidOrSheetName) throws POIException {
      this.rid = this.getSheetIndex(idOrRidOrSheetName);
      this.formatListener = new FormatTrackingHSSFListener(new MissingRecordAwareHSSFListener(this));
      HSSFRequest request = new HSSFRequest();
      request.addListenerForAllRecords(this.formatListener);
      HSSFEventFactory factory = new HSSFEventFactory();

      try {
         factory.processWorkbookEvents(request, fs);
      } catch (IOException var9) {
         throw new POIException(var9);
      } finally {
         IoUtil.close(fs);
      }

      return this;
   }

   public int getSheetIndex() {
      return this.rid;
   }

   public String getSheetName() {
      if (null != this.sheetName) {
         return this.sheetName;
      } else {
         return this.boundSheetRecords.size() > this.rid ? ((BoundSheetRecord)this.boundSheetRecords.get(this.rid > -1 ? this.rid : this.curRid)).getSheetname() : null;
      }
   }

   public void processRecord(Record record) {
      if (this.rid <= -1 || this.curRid <= this.rid) {
         if (record instanceof BoundSheetRecord) {
            BoundSheetRecord boundSheetRecord = (BoundSheetRecord)record;
            this.boundSheetRecords.add(boundSheetRecord);
            String currentSheetName = boundSheetRecord.getSheetname();
            if (null != this.sheetName && StrUtil.equals(this.sheetName, currentSheetName)) {
               this.rid = this.boundSheetRecords.size() - 1;
            }
         } else if (record instanceof SSTRecord) {
            this.sstRecord = (SSTRecord)record;
         } else if (record instanceof BOFRecord) {
            BOFRecord bofRecord = (BOFRecord)record;
            if (bofRecord.getType() == 16) {
               if (this.workbookBuildingListener != null && this.stubWorkbook == null) {
                  this.stubWorkbook = this.workbookBuildingListener.getStubHSSFWorkbook();
               }

               ++this.curRid;
            }
         } else if (record instanceof EOFRecord) {
            if (this.rid < 0 && null != this.sheetName) {
               throw new POIException("Sheet [{}] not exist!", new Object[]{this.sheetName});
            }

            this.processLastCellSheet();
         } else if (this.isProcessCurrentSheet()) {
            if (record instanceof MissingCellDummyRecord) {
               MissingCellDummyRecord mc = (MissingCellDummyRecord)record;
               this.addToRowCellList(mc);
            } else if (record instanceof LastCellOfRowDummyRecord) {
               this.processLastCell((LastCellOfRowDummyRecord)record);
            } else {
               this.processCellValue(record);
            }
         }

      }
   }

   private void addToRowCellList(MissingCellDummyRecord record) {
      this.addToRowCellList(record.getRow(), record.getColumn(), "");
   }

   private void addToRowCellList(CellValueRecordInterface record, Object value) {
      this.addToRowCellList(record.getRow(), record.getColumn(), value);
   }

   private void addToRowCellList(int row, int column, Object value) {
      while(column > this.rowCellList.size()) {
         this.rowCellList.add("");
         this.rowHandler.handleCell(this.curRid, (long)row, this.rowCellList.size() - 1, value, (CellStyle)null);
      }

      this.rowCellList.add(column, value);
      this.rowHandler.handleCell(this.curRid, (long)row, column, value, (CellStyle)null);
   }

   private void processCellValue(Record record) {
      Object value = null;
      switch (record.getSid()) {
         case 6:
            FormulaRecord formulaRec = (FormulaRecord)record;
            if (Double.isNaN(formulaRec.getValue())) {
               this.isOutputNextStringRecord = true;
            } else {
               value = ExcelSaxUtil.getNumberOrDateValue(formulaRec, formulaRec.getValue(), this.formatListener);
            }

            this.addToRowCellList(formulaRec, value);
            break;
         case 253:
            LabelSSTRecord lsrec = (LabelSSTRecord)record;
            if (null != this.sstRecord) {
               value = this.sstRecord.getString(lsrec.getSSTIndex()).toString();
            }

            this.addToRowCellList(lsrec, ObjectUtil.defaultIfNull(value, (Object)""));
            break;
         case 513:
            this.addToRowCellList((BlankRecord)record, "");
            break;
         case 515:
            NumberRecord numrec = (NumberRecord)record;
            value = ExcelSaxUtil.getNumberOrDateValue(numrec, numrec.getValue(), this.formatListener);
            this.addToRowCellList(numrec, value);
            break;
         case 516:
            LabelRecord lrec = (LabelRecord)record;
            Object value = lrec.getValue();
            this.addToRowCellList(lrec, value);
            break;
         case 517:
            BoolErrRecord berec = (BoolErrRecord)record;
            this.addToRowCellList(berec, berec.getBooleanValue());
            break;
         case 519:
            if (this.isOutputNextStringRecord) {
               this.isOutputNextStringRecord = false;
            }
      }

   }

   private void processLastCell(LastCellOfRowDummyRecord lastCell) {
      this.rowHandler.handle(this.curRid, (long)lastCell.getRow(), this.rowCellList);
      this.rowCellList = new ArrayList(this.rowCellList.size());
   }

   private void processLastCellSheet() {
      this.rowHandler.doAfterAllAnalysed();
   }

   private boolean isProcessCurrentSheet() {
      return this.rid < 0 && null == this.sheetName || this.rid == this.curRid;
   }

   private int getSheetIndex(String idOrRidOrSheetName) {
      Assert.notBlank(idOrRidOrSheetName, "id or rid or sheetName must be not blank!");
      if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, "rId")) {
         return Integer.parseInt(StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, "rId"));
      } else {
         if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, "sheetName:")) {
            this.sheetName = StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, "sheetName:");
         } else {
            try {
               return Integer.parseInt(idOrRidOrSheetName);
            } catch (NumberFormatException var3) {
               this.sheetName = idOrRidOrSheetName;
            }
         }

         return -1;
      }
   }
}
