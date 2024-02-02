package com.mysql.cj.protocol.a;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.ProtocolEntityReader;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ResultsetRow;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.protocol.a.result.ResultsetRowsCursor;
import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
import com.mysql.cj.protocol.a.result.ResultsetRowsStreaming;
import java.io.IOException;
import java.util.ArrayList;

public class BinaryResultsetReader implements ProtocolEntityReader<Resultset, NativePacketPayload> {
   protected NativeProtocol protocol;

   public BinaryResultsetReader(NativeProtocol prot) {
      this.protocol = prot;
   }

   public Resultset read(int maxRows, boolean streamResults, NativePacketPayload resultPacket, ColumnDefinition metadata, ProtocolEntityFactory<Resultset, NativePacketPayload> resultSetFactory) throws IOException {
      Resultset rs = null;
      long columnCount = resultPacket.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
      if (columnCount > 0L) {
         ColumnDefinition cdef = (ColumnDefinition)this.protocol.read(ColumnDefinition.class, new MergingColumnDefinitionFactory(columnCount, metadata));
         boolean isCursorPossible = (Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.useCursorFetch).getValue() && resultSetFactory.getResultSetType() == Resultset.Type.FORWARD_ONLY && resultSetFactory.getFetchSize() > 0;
         NativePacketPayload rowPacket;
         if (isCursorPossible || !this.protocol.getServerSession().isEOFDeprecated()) {
            rowPacket = this.protocol.probeMessage(this.protocol.getReusablePacket());
            this.protocol.checkErrorMessage(rowPacket);
            if (!rowPacket.isResultSetOKPacket() && !rowPacket.isEOFPacket()) {
               isCursorPossible = false;
            } else {
               rowPacket = this.protocol.readMessage(this.protocol.getReusablePacket());
               this.protocol.readServerStatusForResultSets(rowPacket, true);
            }
         }

         rowPacket = null;
         Object rows;
         if (isCursorPossible && this.protocol.getServerSession().cursorExists()) {
            rows = new ResultsetRowsCursor(this.protocol, cdef);
         } else if (streamResults) {
            rows = new ResultsetRowsStreaming(this.protocol, cdef, true, resultSetFactory);
            this.protocol.setStreamingData((ResultsetRows)rows);
         } else {
            BinaryRowFactory brf = new BinaryRowFactory(this.protocol, cdef, resultSetFactory.getResultSetConcurrency(), false);
            ArrayList<ResultsetRow> rowList = new ArrayList();
            ResultsetRow row = (ResultsetRow)this.protocol.read(ResultsetRow.class, brf);

            while(true) {
               if (row == null) {
                  rows = new ResultsetRowsStatic(rowList, cdef);
                  break;
               }

               if (maxRows == -1 || rowList.size() < maxRows) {
                  rowList.add(row);
               }

               row = (ResultsetRow)this.protocol.read(ResultsetRow.class, brf);
            }
         }

         rs = (Resultset)resultSetFactory.createFromProtocolEntity((ProtocolEntity)rows);
      } else {
         if (columnCount == -1L) {
            String charEncoding = (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
            String fileName = resultPacket.readString(NativeConstants.StringSelfDataType.STRING_TERM, this.protocol.getServerSession().getCharsetSettings().doesPlatformDbCharsetMatches() ? null : charEncoding);
            resultPacket = this.protocol.sendFileToServer(fileName);
         }

         OkPacket ok = (OkPacket)this.protocol.readServerStatusForResultSets(resultPacket, false);
         rs = (Resultset)resultSetFactory.createFromProtocolEntity(ok);
      }

      return rs;
   }
}
