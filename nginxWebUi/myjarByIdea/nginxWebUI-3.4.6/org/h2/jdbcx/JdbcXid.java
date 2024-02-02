package org.h2.jdbcx;

import java.util.Base64;
import javax.transaction.xa.Xid;
import org.h2.message.DbException;
import org.h2.message.TraceObject;

public final class JdbcXid extends TraceObject implements Xid {
   private static final String PREFIX = "XID";
   private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
   private final int formatId;
   private final byte[] branchQualifier;
   private final byte[] globalTransactionId;

   JdbcXid(JdbcDataSourceFactory var1, int var2, String var3) {
      this.setTrace(var1.getTrace(), 15, var2);

      try {
         String[] var4 = var3.split("\\|");
         if (var4.length == 4 && "XID".equals(var4[0])) {
            this.formatId = Integer.parseInt(var4[1]);
            Base64.Decoder var5 = Base64.getUrlDecoder();
            this.branchQualifier = var5.decode(var4[2]);
            this.globalTransactionId = var5.decode(var4[3]);
            return;
         }
      } catch (IllegalArgumentException var6) {
      }

      throw DbException.get(90101, var3);
   }

   static StringBuilder toString(StringBuilder var0, Xid var1) {
      return var0.append("XID").append('|').append(var1.getFormatId()).append('|').append(ENCODER.encodeToString(var1.getBranchQualifier())).append('|').append(ENCODER.encodeToString(var1.getGlobalTransactionId()));
   }

   public int getFormatId() {
      this.debugCodeCall("getFormatId");
      return this.formatId;
   }

   public byte[] getBranchQualifier() {
      this.debugCodeCall("getBranchQualifier");
      return this.branchQualifier;
   }

   public byte[] getGlobalTransactionId() {
      this.debugCodeCall("getGlobalTransactionId");
      return this.globalTransactionId;
   }
}
