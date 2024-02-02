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
import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
import com.mysql.cj.protocol.a.result.ResultsetRowsStreaming;
import java.io.IOException;
import java.util.ArrayList;

public class TextResultsetReader implements ProtocolEntityReader<Resultset, NativePacketPayload> {
   protected NativeProtocol protocol;

   public TextResultsetReader(NativeProtocol prot) {
      this.protocol = prot;
   }

   public Resultset read(int maxRows, boolean streamResults, NativePacketPayload resultPacket, ColumnDefinition metadata, ProtocolEntityFactory<Resultset, NativePacketPayload> resultSetFactory) throws IOException {
      Resultset rs = null;
      long columnCount = resultPacket.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
      if (columnCount > 0L) {
         ColumnDefinition cdef = (ColumnDefinition)this.protocol.read(ColumnDefinition.class, new ColumnDefinitionFactory(columnCount, metadata));
         if (!this.protocol.getServerSession().isEOFDeprecated()) {
            this.protocol.skipPacket();
         }

         ResultsetRows rows = null;
         if (streamResults) {
            rows = new ResultsetRowsStreaming(this.protocol, cdef, false, resultSetFactory);
            this.protocol.setStreamingData((ResultsetRows)rows);
         } else {
            TextRowFactory trf = new TextRowFactory(this.protocol, cdef, resultSetFactory.getResultSetConcurrency(), false);
            ArrayList<ResultsetRow> rowList = new ArrayList();
            ResultsetRow row = (ResultsetRow)this.protocol.read(ResultsetRow.class, trf);

            while(true) {
               if (row == null) {
                  rows = new ResultsetRowsStatic(rowList, cdef);
                  break;
               }

               if (maxRows == -1 || rowList.size() < maxRows) {
                  rowList.add(row);
               }

               row = (ResultsetRow)this.protocol.read(ResultsetRow.class, trf);
            }
         }

         rs = (Resultset)resultSetFactory.createFromProtocolEntity((ProtocolEntity)rows);
      } else {
         if (columnCount == -1L) {
            String charEncoding = (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
            String fileName = resultPacket.readString(NativeConstants.StringSelfDataType.STRING_TERM, this.protocol.getServerSession().getCharsetSettings().doesPlatformDbCharsetMatches() ? charEncoding : null);
            resultPacket = this.protocol.sendFileToServer(fileName);
         }

         OkPacket ok = (OkPacket)this.protocol.readServerStatusForResultSets(resultPacket, false);
         rs = (Resultset)resultSetFactory.createFromProtocolEntity(ok);
      }

      return rs;
   }
}
