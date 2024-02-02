package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.result.Field;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlobFromLocator implements java.sql.Blob {
   private List<String> primaryKeyColumns = null;
   private List<String> primaryKeyValues = null;
   private ResultSetImpl creatorResultSet;
   private String blobColumnName = null;
   private String tableName = null;
   private int numColsInResultSet = 0;
   private int numPrimaryKeys = 0;
   private String quotedId;
   private ExceptionInterceptor exceptionInterceptor;

   public BlobFromLocator(ResultSetImpl creatorResultSetToSet, int blobColumnIndex, ExceptionInterceptor exceptionInterceptor) throws SQLException {
      this.exceptionInterceptor = exceptionInterceptor;
      this.creatorResultSet = creatorResultSetToSet;
      Field[] fields = this.creatorResultSet.getMetadata().getFields();
      this.numColsInResultSet = fields.length;
      this.quotedId = this.creatorResultSet.getSession().getIdentifierQuoteString();
      if (this.numColsInResultSet > 1) {
         this.primaryKeyColumns = new ArrayList();
         this.primaryKeyValues = new ArrayList();

         for(int i = 0; i < this.numColsInResultSet; ++i) {
            if (fields[i].isPrimaryKey()) {
               StringBuilder keyName = new StringBuilder();
               keyName.append(this.quotedId);
               String originalColumnName = fields[i].getOriginalName();
               if (originalColumnName != null && originalColumnName.length() > 0) {
                  keyName.append(originalColumnName);
               } else {
                  keyName.append(fields[i].getName());
               }

               keyName.append(this.quotedId);
               this.primaryKeyColumns.add(keyName.toString());
               this.primaryKeyValues.add(this.creatorResultSet.getString(i + 1));
            }
         }
      } else {
         this.notEnoughInformationInQuery();
      }

      this.numPrimaryKeys = this.primaryKeyColumns.size();
      if (this.numPrimaryKeys == 0) {
         this.notEnoughInformationInQuery();
      }

      StringBuilder tableNameBuffer;
      if (fields[0].getOriginalTableName() != null) {
         tableNameBuffer = new StringBuilder();
         String databaseName = fields[0].getDatabaseName();
         if (databaseName != null && databaseName.length() > 0) {
            tableNameBuffer.append(this.quotedId);
            tableNameBuffer.append(databaseName);
            tableNameBuffer.append(this.quotedId);
            tableNameBuffer.append('.');
         }

         tableNameBuffer.append(this.quotedId);
         tableNameBuffer.append(fields[0].getOriginalTableName());
         tableNameBuffer.append(this.quotedId);
         this.tableName = tableNameBuffer.toString();
      } else {
         tableNameBuffer = new StringBuilder();
         tableNameBuffer.append(this.quotedId);
         tableNameBuffer.append(fields[0].getTableName());
         tableNameBuffer.append(this.quotedId);
         this.tableName = tableNameBuffer.toString();
      }

      this.blobColumnName = this.quotedId + this.creatorResultSet.getString(blobColumnIndex) + this.quotedId;
   }

   private void notEnoughInformationInQuery() throws SQLException {
      throw SQLError.createSQLException(Messages.getString("Blob.8"), "S1000", this.exceptionInterceptor);
   }

   public OutputStream setBinaryStream(long indexToWriteAt) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public InputStream getBinaryStream() throws SQLException {
      try {
         return new BufferedInputStream(new LocatorInputStream(), (Integer)this.creatorResultSet.getSession().getPropertySet().getMemorySizeProperty(PropertyKey.locatorFetchBufferSize).getValue());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   public int setBytes(long writeAt, byte[] bytes, int offset, int length) throws SQLException {
      try {
         PreparedStatement pStmt = null;
         if (offset + length > bytes.length) {
            length = bytes.length - offset;
         }

         byte[] bytesToWrite = new byte[length];
         System.arraycopy(bytes, offset, bytesToWrite, 0, length);
         StringBuilder query = new StringBuilder("UPDATE ");
         query.append(this.tableName);
         query.append(" SET ");
         query.append(this.blobColumnName);
         query.append(" = INSERT(");
         query.append(this.blobColumnName);
         query.append(", ");
         query.append(writeAt);
         query.append(", ");
         query.append(length);
         query.append(", ?) WHERE ");
         query.append((String)this.primaryKeyColumns.get(0));
         query.append(" = ?");

         int rowsUpdated;
         for(rowsUpdated = 1; rowsUpdated < this.numPrimaryKeys; ++rowsUpdated) {
            query.append(" AND ");
            query.append((String)this.primaryKeyColumns.get(rowsUpdated));
            query.append(" = ?");
         }

         try {
            pStmt = this.creatorResultSet.getConnection().prepareStatement(query.toString());
            pStmt.setBytes(1, bytesToWrite);

            for(rowsUpdated = 0; rowsUpdated < this.numPrimaryKeys; ++rowsUpdated) {
               pStmt.setString(rowsUpdated + 2, (String)this.primaryKeyValues.get(rowsUpdated));
            }

            rowsUpdated = pStmt.executeUpdate();
            if (rowsUpdated != 1) {
               throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
            }
         } finally {
            if (pStmt != null) {
               try {
                  pStmt.close();
               } catch (SQLException var17) {
               }

               pStmt = null;
            }

         }

         return (int)this.length();
      } catch (CJException var19) {
         throw SQLExceptionsMapping.translateException(var19, this.exceptionInterceptor);
      }
   }

   public int setBytes(long writeAt, byte[] bytes) throws SQLException {
      try {
         return this.setBytes(writeAt, bytes, 0, bytes.length);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }

   public byte[] getBytes(long pos, int length) throws SQLException {
      try {
         PreparedStatement pStmt = null;

         byte[] var5;
         try {
            pStmt = this.createGetBytesStatement();
            var5 = this.getBytesInternal(pStmt, pos, length);
         } finally {
            if (pStmt != null) {
               try {
                  pStmt.close();
               } catch (SQLException var14) {
               }

               pStmt = null;
            }

         }

         return var5;
      } catch (CJException var16) {
         throw SQLExceptionsMapping.translateException(var16, this.exceptionInterceptor);
      }
   }

   public long length() throws SQLException {
      try {
         ResultSet blobRs = null;
         PreparedStatement pStmt = null;
         StringBuilder query = new StringBuilder("SELECT LENGTH(");
         query.append(this.blobColumnName);
         query.append(") FROM ");
         query.append(this.tableName);
         query.append(" WHERE ");
         query.append((String)this.primaryKeyColumns.get(0));
         query.append(" = ?");

         int i;
         for(i = 1; i < this.numPrimaryKeys; ++i) {
            query.append(" AND ");
            query.append((String)this.primaryKeyColumns.get(i));
            query.append(" = ?");
         }

         long var20;
         try {
            pStmt = this.creatorResultSet.getConnection().prepareStatement(query.toString());

            for(i = 0; i < this.numPrimaryKeys; ++i) {
               pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
            }

            blobRs = pStmt.executeQuery();
            if (!blobRs.next()) {
               throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
            }

            var20 = blobRs.getLong(1);
         } finally {
            if (blobRs != null) {
               try {
                  blobRs.close();
               } catch (SQLException var17) {
               }

               blobRs = null;
            }

            if (pStmt != null) {
               try {
                  pStmt.close();
               } catch (SQLException var16) {
               }

               pStmt = null;
            }

         }

         return var20;
      } catch (CJException var19) {
         throw SQLExceptionsMapping.translateException(var19, this.exceptionInterceptor);
      }
   }

   public long position(java.sql.Blob pattern, long start) throws SQLException {
      try {
         return this.position(pattern.getBytes(0L, (int)pattern.length()), start);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }

   public long position(byte[] pattern, long start) throws SQLException {
      try {
         ResultSet blobRs = null;
         PreparedStatement pStmt = null;
         StringBuilder query = new StringBuilder("SELECT LOCATE(");
         query.append("?, ");
         query.append(this.blobColumnName);
         query.append(", ");
         query.append(start);
         query.append(") FROM ");
         query.append(this.tableName);
         query.append(" WHERE ");
         query.append((String)this.primaryKeyColumns.get(0));
         query.append(" = ?");

         int i;
         for(i = 1; i < this.numPrimaryKeys; ++i) {
            query.append(" AND ");
            query.append((String)this.primaryKeyColumns.get(i));
            query.append(" = ?");
         }

         long var23;
         try {
            pStmt = this.creatorResultSet.getConnection().prepareStatement(query.toString());
            pStmt.setBytes(1, pattern);

            for(i = 0; i < this.numPrimaryKeys; ++i) {
               pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
            }

            blobRs = pStmt.executeQuery();
            if (!blobRs.next()) {
               throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
            }

            var23 = blobRs.getLong(1);
         } finally {
            if (blobRs != null) {
               try {
                  blobRs.close();
               } catch (SQLException var20) {
               }

               blobRs = null;
            }

            if (pStmt != null) {
               try {
                  pStmt.close();
               } catch (SQLException var19) {
               }

               pStmt = null;
            }

         }

         return var23;
      } catch (CJException var22) {
         throw SQLExceptionsMapping.translateException(var22, this.exceptionInterceptor);
      }
   }

   public void truncate(long length) throws SQLException {
      try {
         PreparedStatement pStmt = null;
         StringBuilder query = new StringBuilder("UPDATE ");
         query.append(this.tableName);
         query.append(" SET ");
         query.append(this.blobColumnName);
         query.append(" = LEFT(");
         query.append(this.blobColumnName);
         query.append(", ");
         query.append(length);
         query.append(") WHERE ");
         query.append((String)this.primaryKeyColumns.get(0));
         query.append(" = ?");

         int rowsUpdated;
         for(rowsUpdated = 1; rowsUpdated < this.numPrimaryKeys; ++rowsUpdated) {
            query.append(" AND ");
            query.append((String)this.primaryKeyColumns.get(rowsUpdated));
            query.append(" = ?");
         }

         try {
            pStmt = this.creatorResultSet.getConnection().prepareStatement(query.toString());

            for(rowsUpdated = 0; rowsUpdated < this.numPrimaryKeys; ++rowsUpdated) {
               pStmt.setString(rowsUpdated + 1, (String)this.primaryKeyValues.get(rowsUpdated));
            }

            rowsUpdated = pStmt.executeUpdate();
            if (rowsUpdated != 1) {
               throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
            }
         } finally {
            if (pStmt != null) {
               try {
                  pStmt.close();
               } catch (SQLException var13) {
               }

               pStmt = null;
            }

         }

      } catch (CJException var15) {
         throw SQLExceptionsMapping.translateException(var15, this.exceptionInterceptor);
      }
   }

   PreparedStatement createGetBytesStatement() throws SQLException {
      StringBuilder query = new StringBuilder("SELECT SUBSTRING(");
      query.append(this.blobColumnName);
      query.append(", ");
      query.append("?");
      query.append(", ");
      query.append("?");
      query.append(") FROM ");
      query.append(this.tableName);
      query.append(" WHERE ");
      query.append((String)this.primaryKeyColumns.get(0));
      query.append(" = ?");

      for(int i = 1; i < this.numPrimaryKeys; ++i) {
         query.append(" AND ");
         query.append((String)this.primaryKeyColumns.get(i));
         query.append(" = ?");
      }

      return this.creatorResultSet.getConnection().prepareStatement(query.toString());
   }

   byte[] getBytesInternal(PreparedStatement pStmt, long pos, int length) throws SQLException {
      ResultSet blobRs = null;

      byte[] var15;
      try {
         pStmt.setLong(1, pos);
         pStmt.setInt(2, length);

         for(int i = 0; i < this.numPrimaryKeys; ++i) {
            pStmt.setString(i + 3, (String)this.primaryKeyValues.get(i));
         }

         blobRs = pStmt.executeQuery();
         if (!blobRs.next()) {
            throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
         }

         var15 = blobRs.getBytes(1);
      } finally {
         if (blobRs != null) {
            try {
               blobRs.close();
            } catch (SQLException var13) {
            }

            blobRs = null;
         }

      }

      return var15;
   }

   public void free() throws SQLException {
      try {
         this.creatorResultSet = null;
         this.primaryKeyColumns = null;
         this.primaryKeyValues = null;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   public InputStream getBinaryStream(long pos, long length) throws SQLException {
      try {
         return new LocatorInputStream(pos, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.exceptionInterceptor);
      }
   }

   class LocatorInputStream extends InputStream {
      long currentPositionInBlob = 0L;
      long length = 0L;
      PreparedStatement pStmt = null;

      LocatorInputStream() throws SQLException {
         this.length = BlobFromLocator.this.length();
         this.pStmt = BlobFromLocator.this.createGetBytesStatement();
      }

      LocatorInputStream(long pos, long len) throws SQLException {
         this.length = pos + len;
         this.currentPositionInBlob = pos;
         long blobLength = BlobFromLocator.this.length();
         if (pos + len > blobLength) {
            throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamLength", new Object[]{blobLength, pos, len}), "S1009", BlobFromLocator.this.exceptionInterceptor);
         } else if (pos < 1L) {
            throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this.exceptionInterceptor);
         } else if (pos > blobLength) {
            throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this.exceptionInterceptor);
         }
      }

      public int read() throws IOException {
         if (this.currentPositionInBlob + 1L > this.length) {
            return -1;
         } else {
            try {
               byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob++ + 1L, 1);
               return asBytes == null ? -1 : asBytes[0];
            } catch (SQLException var2) {
               throw new IOException(var2.toString());
            }
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if (this.currentPositionInBlob + 1L > this.length) {
            return -1;
         } else {
            try {
               byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, len);
               if (asBytes == null) {
                  return -1;
               } else {
                  System.arraycopy(asBytes, 0, b, off, asBytes.length);
                  this.currentPositionInBlob += (long)asBytes.length;
                  return asBytes.length;
               }
            } catch (SQLException var5) {
               throw new IOException(var5.toString());
            }
         }
      }

      public int read(byte[] b) throws IOException {
         if (this.currentPositionInBlob + 1L > this.length) {
            return -1;
         } else {
            try {
               byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, b.length);
               if (asBytes == null) {
                  return -1;
               } else {
                  System.arraycopy(asBytes, 0, b, 0, asBytes.length);
                  this.currentPositionInBlob += (long)asBytes.length;
                  return asBytes.length;
               }
            } catch (SQLException var3) {
               throw new IOException(var3.toString());
            }
         }
      }

      public void close() throws IOException {
         if (this.pStmt != null) {
            try {
               this.pStmt.close();
            } catch (SQLException var2) {
               throw new IOException(var2.toString());
            }
         }

         super.close();
      }
   }
}
