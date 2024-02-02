package com.mysql.cj.jdbc.result;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.protocol.a.result.ResultsetRowsCursor;
import java.sql.SQLException;

public class ResultSetFactory implements ProtocolEntityFactory<ResultSetImpl, NativePacketPayload> {
   private JdbcConnection conn;
   private StatementImpl stmt;
   private Resultset.Type type;
   private Resultset.Concurrency concurrency;

   public ResultSetFactory(JdbcConnection connection, StatementImpl creatorStmt) throws SQLException {
      this.type = Resultset.Type.FORWARD_ONLY;
      this.concurrency = Resultset.Concurrency.READ_ONLY;
      this.conn = connection;
      this.stmt = creatorStmt;
      if (creatorStmt != null) {
         this.type = Resultset.Type.fromValue(creatorStmt.getResultSetType(), Resultset.Type.FORWARD_ONLY);
         this.concurrency = Resultset.Concurrency.fromValue(creatorStmt.getResultSetConcurrency(), Resultset.Concurrency.READ_ONLY);
      }

   }

   public Resultset.Type getResultSetType() {
      return this.type;
   }

   public Resultset.Concurrency getResultSetConcurrency() {
      return this.concurrency;
   }

   public int getFetchSize() {
      try {
         return this.stmt.getFetchSize();
      } catch (SQLException var2) {
         throw ExceptionFactory.createException((String)var2.getMessage(), (Throwable)var2);
      }
   }

   public ResultSetImpl createFromProtocolEntity(ProtocolEntity protocolEntity) {
      try {
         if (protocolEntity instanceof OkPacket) {
            return new ResultSetImpl((OkPacket)protocolEntity, this.conn, this.stmt);
         } else if (protocolEntity instanceof ResultsetRows) {
            int resultSetConcurrency = this.getResultSetConcurrency().getIntValue();
            int resultSetType = this.getResultSetType().getIntValue();
            return this.createFromResultsetRows(resultSetConcurrency, resultSetType, (ResultsetRows)protocolEntity);
         } else {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unknown ProtocolEntity class " + protocolEntity);
         }
      } catch (SQLException var4) {
         throw ExceptionFactory.createException((String)var4.getMessage(), (Throwable)var4);
      }
   }

   public ResultSetImpl createFromResultsetRows(int resultSetConcurrency, int resultSetType, ResultsetRows rows) throws SQLException {
      StatementImpl st = this.stmt;
      if (rows.getOwner() != null) {
         st = ((ResultSetImpl)rows.getOwner()).getOwningStatement();
      }

      Object rs;
      switch (resultSetConcurrency) {
         case 1008:
            rs = new UpdatableResultSet(rows, this.conn, st);
            break;
         default:
            rs = new ResultSetImpl(rows, this.conn, st);
      }

      ((ResultSetImpl)rs).setResultSetType(resultSetType);
      ((ResultSetImpl)rs).setResultSetConcurrency(resultSetConcurrency);
      if (rows instanceof ResultsetRowsCursor && st != null) {
         ((ResultSetImpl)rs).setFetchSize(st.getFetchSize());
      }

      return (ResultSetImpl)rs;
   }
}
