package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.log.Log;
import com.mysql.cj.util.StringUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class MysqlXAConnection extends MysqlPooledConnection implements XAConnection, XAResource {
   private static final int MAX_COMMAND_LENGTH = 300;
   private JdbcConnection underlyingConnection;
   private static final Map<Integer, Integer> MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
   private Log log;
   protected boolean logXaCommands;

   protected static MysqlXAConnection getInstance(JdbcConnection mysqlConnection, boolean logXaCommands) throws SQLException {
      return new MysqlXAConnection(mysqlConnection, logXaCommands);
   }

   public MysqlXAConnection(JdbcConnection connection, boolean logXaCommands) {
      super(connection);
      this.underlyingConnection = connection;
      this.log = connection.getSession().getLog();
      this.logXaCommands = logXaCommands;
   }

   public XAResource getXAResource() throws SQLException {
      try {
         return this;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2);
      }
   }

   public int getTransactionTimeout() throws XAException {
      return 0;
   }

   public boolean setTransactionTimeout(int arg0) throws XAException {
      return false;
   }

   public boolean isSameRM(XAResource xares) throws XAException {
      return xares instanceof MysqlXAConnection ? this.underlyingConnection.isSameResource(((MysqlXAConnection)xares).underlyingConnection) : false;
   }

   public Xid[] recover(int flag) throws XAException {
      return recover(this.underlyingConnection, flag);
   }

   protected static Xid[] recover(Connection c, int flag) throws XAException {
      boolean startRscan = (flag & 16777216) > 0;
      boolean endRscan = (flag & 8388608) > 0;
      if (!startRscan && !endRscan && flag != 0) {
         throw new MysqlXAException(-5, Messages.getString("MysqlXAConnection.001"), (String)null);
      } else if (!startRscan) {
         return new Xid[0];
      } else {
         ResultSet rs = null;
         Statement stmt = null;
         List<MysqlXid> recoveredXidList = new ArrayList();

         int numXids;
         try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("XA RECOVER");

            while(rs.next()) {
               numXids = rs.getInt(1);
               int gtridLength = rs.getInt(2);
               int bqualLength = rs.getInt(3);
               byte[] gtridAndBqual = rs.getBytes(4);
               byte[] gtrid = new byte[gtridLength];
               byte[] bqual = new byte[bqualLength];
               if (gtridAndBqual.length != gtridLength + bqualLength) {
                  throw new MysqlXAException(105, Messages.getString("MysqlXAConnection.002"), (String)null);
               }

               System.arraycopy(gtridAndBqual, 0, gtrid, 0, gtridLength);
               System.arraycopy(gtridAndBqual, gtridLength, bqual, 0, bqualLength);
               recoveredXidList.add(new MysqlXid(gtrid, bqual, numXids));
            }
         } catch (SQLException var23) {
            throw mapXAExceptionFromSQLException(var23);
         } finally {
            if (rs != null) {
               try {
                  rs.close();
               } catch (SQLException var22) {
                  throw mapXAExceptionFromSQLException(var22);
               }
            }

            if (stmt != null) {
               try {
                  stmt.close();
               } catch (SQLException var21) {
                  throw mapXAExceptionFromSQLException(var21);
               }
            }

         }

         numXids = recoveredXidList.size();
         Xid[] asXids = new Xid[numXids];
         Object[] asObjects = recoveredXidList.toArray();

         for(int i = 0; i < numXids; ++i) {
            asXids[i] = (Xid)asObjects[i];
         }

         return asXids;
      }
   }

   public int prepare(Xid xid) throws XAException {
      StringBuilder commandBuf = new StringBuilder(300);
      commandBuf.append("XA PREPARE ");
      appendXid(commandBuf, xid);
      this.dispatchCommand(commandBuf.toString());
      return 0;
   }

   public void forget(Xid xid) throws XAException {
   }

   public void rollback(Xid xid) throws XAException {
      StringBuilder commandBuf = new StringBuilder(300);
      commandBuf.append("XA ROLLBACK ");
      appendXid(commandBuf, xid);

      try {
         this.dispatchCommand(commandBuf.toString());
      } finally {
         this.underlyingConnection.setInGlobalTx(false);
      }

   }

   public void end(Xid xid, int flags) throws XAException {
      StringBuilder commandBuf = new StringBuilder(300);
      commandBuf.append("XA END ");
      appendXid(commandBuf, xid);
      switch (flags) {
         case 33554432:
            commandBuf.append(" SUSPEND");
         case 67108864:
         case 536870912:
            this.dispatchCommand(commandBuf.toString());
            return;
         default:
            throw new XAException(-5);
      }
   }

   public void start(Xid xid, int flags) throws XAException {
      StringBuilder commandBuf = new StringBuilder(300);
      commandBuf.append("XA START ");
      appendXid(commandBuf, xid);
      switch (flags) {
         case 0:
            break;
         case 2097152:
            commandBuf.append(" JOIN");
            break;
         case 134217728:
            commandBuf.append(" RESUME");
            break;
         default:
            throw new XAException(-5);
      }

      this.dispatchCommand(commandBuf.toString());
      this.underlyingConnection.setInGlobalTx(true);
   }

   public void commit(Xid xid, boolean onePhase) throws XAException {
      StringBuilder commandBuf = new StringBuilder(300);
      commandBuf.append("XA COMMIT ");
      appendXid(commandBuf, xid);
      if (onePhase) {
         commandBuf.append(" ONE PHASE");
      }

      try {
         this.dispatchCommand(commandBuf.toString());
      } finally {
         this.underlyingConnection.setInGlobalTx(false);
      }

   }

   private ResultSet dispatchCommand(String command) throws XAException {
      Statement stmt = null;

      ResultSet var4;
      try {
         if (this.logXaCommands) {
            this.log.logDebug("Executing XA statement: " + command);
         }

         stmt = this.underlyingConnection.createStatement();
         stmt.execute(command);
         ResultSet rs = stmt.getResultSet();
         var4 = rs;
      } catch (SQLException var13) {
         throw mapXAExceptionFromSQLException(var13);
      } finally {
         if (stmt != null) {
            try {
               stmt.close();
            } catch (SQLException var12) {
            }
         }

      }

      return var4;
   }

   protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
      Integer xaCode = (Integer)MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get(sqlEx.getErrorCode());
      return xaCode != null ? (XAException)(new MysqlXAException(xaCode, sqlEx.getMessage(), (String)null)).initCause(sqlEx) : (XAException)(new MysqlXAException(-7, Messages.getString("MysqlXAConnection.003"), (String)null)).initCause(sqlEx);
   }

   private static void appendXid(StringBuilder builder, Xid xid) {
      byte[] gtrid = xid.getGlobalTransactionId();
      byte[] btrid = xid.getBranchQualifier();
      if (gtrid != null) {
         StringUtils.appendAsHex(builder, gtrid);
      }

      builder.append(',');
      if (btrid != null) {
         StringUtils.appendAsHex(builder, btrid);
      }

      builder.append(',');
      StringUtils.appendAsHex(builder, xid.getFormatId());
   }

   public synchronized Connection getConnection() throws SQLException {
      try {
         Connection connToWrap = this.getConnection(false, true);
         return connToWrap;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   static {
      HashMap<Integer, Integer> temp = new HashMap();
      temp.put(1397, -4);
      temp.put(1398, -5);
      temp.put(1399, -7);
      temp.put(1400, -9);
      temp.put(1401, -3);
      temp.put(1402, 100);
      temp.put(1440, -8);
      temp.put(1613, 106);
      temp.put(1614, 102);
      MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
   }
}
