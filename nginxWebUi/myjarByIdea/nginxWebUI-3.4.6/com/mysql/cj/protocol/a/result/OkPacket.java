package com.mysql.cj.protocol.a.result;

import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.NativeServerSessionStateController;

public class OkPacket implements ProtocolEntity {
   private long updateCount = -1L;
   private long updateID = -1L;
   private int statusFlags = 0;
   private int warningCount = 0;
   private String info = null;
   private NativeServerSessionStateController.NativeServerSessionStateChanges sessionStateChanges = new NativeServerSessionStateController.NativeServerSessionStateChanges();

   private OkPacket() {
   }

   public static OkPacket parse(NativePacketPayload buf, String errorMessageEncoding) {
      OkPacket ok = new OkPacket();
      buf.setPosition(1);
      ok.setUpdateCount(buf.readInteger(NativeConstants.IntegerDataType.INT_LENENC));
      ok.setUpdateID(buf.readInteger(NativeConstants.IntegerDataType.INT_LENENC));
      ok.setStatusFlags((int)buf.readInteger(NativeConstants.IntegerDataType.INT2));
      ok.setWarningCount((int)buf.readInteger(NativeConstants.IntegerDataType.INT2));
      ok.setInfo(buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, errorMessageEncoding));
      if ((ok.getStatusFlags() & 16384) > 0) {
         ok.sessionStateChanges.init(buf, errorMessageEncoding);
      }

      return ok;
   }

   public long getUpdateCount() {
      return this.updateCount;
   }

   public void setUpdateCount(long updateCount) {
      this.updateCount = updateCount;
   }

   public long getUpdateID() {
      return this.updateID;
   }

   public void setUpdateID(long updateID) {
      this.updateID = updateID;
   }

   public String getInfo() {
      return this.info;
   }

   public void setInfo(String info) {
      this.info = info;
   }

   public int getStatusFlags() {
      return this.statusFlags;
   }

   public void setStatusFlags(int statusFlags) {
      this.statusFlags = statusFlags;
   }

   public int getWarningCount() {
      return this.warningCount;
   }

   public void setWarningCount(int warningCount) {
      this.warningCount = warningCount;
   }

   public NativeServerSessionStateController.NativeServerSessionStateChanges getSessionStateChanges() {
      return this.sessionStateChanges;
   }
}
