package org.h2.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.mvstore.DataUtils;
import org.h2.util.IOUtils;
import org.h2.util.Task;
import org.h2.value.Value;

public abstract class JdbcLob extends TraceObject {
   final JdbcConnection conn;
   Value value;
   State state;

   JdbcLob(JdbcConnection var1, Value var2, State var3, int var4, int var5) {
      this.setTrace(var1.getSession().getTrace(), var4, var5);
      this.conn = var1;
      this.value = var2;
      this.state = var3;
   }

   void checkClosed() {
      this.conn.checkClosed();
      if (this.state == JdbcLob.State.CLOSED) {
         throw DbException.get(90007);
      }
   }

   void checkEditable() {
      this.checkClosed();
      if (this.state != JdbcLob.State.NEW) {
         throw DbException.getUnsupportedException("Allocate a new object to set its value.");
      }
   }

   void checkReadable() throws SQLException, IOException {
      this.checkClosed();
      if (this.state == JdbcLob.State.SET_CALLED) {
         throw DbException.getUnsupportedException("Stream setter is not yet closed.");
      }
   }

   void completeWrite(Value var1) {
      this.checkClosed();
      this.state = JdbcLob.State.WITH_VALUE;
      this.value = var1;
   }

   public void free() {
      this.debugCodeCall("free");
      this.state = JdbcLob.State.CLOSED;
      this.value = null;
   }

   InputStream getBinaryStream() throws SQLException {
      try {
         this.debugCodeCall("getBinaryStream");
         this.checkReadable();
         return this.value.getInputStream();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   Reader getCharacterStream() throws SQLException {
      try {
         this.debugCodeCall("getCharacterStream");
         this.checkReadable();
         return this.value.getReader();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   Writer setCharacterStreamImpl() throws IOException {
      return IOUtils.getBufferedWriter(this.setClobOutputStreamImpl());
   }

   LobPipedOutputStream setClobOutputStreamImpl() throws IOException {
      final PipedInputStream var1 = new PipedInputStream();
      Task var2 = new Task() {
         public void call() {
            JdbcLob.this.completeWrite(JdbcLob.this.conn.createClob(IOUtils.getReader(var1), -1L));
         }
      };
      LobPipedOutputStream var3 = new LobPipedOutputStream(var1, var2);
      var2.execute();
      return var3;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder()).append(this.getTraceObjectName()).append(": ");
      if (this.state == JdbcLob.State.SET_CALLED) {
         var1.append("<setter_in_progress>");
      } else if (this.state == JdbcLob.State.CLOSED) {
         var1.append("<closed>");
      } else {
         var1.append(this.value.getTraceSQL());
      }

      return var1.toString();
   }

   public static enum State {
      NEW,
      SET_CALLED,
      WITH_VALUE,
      CLOSED;
   }

   static final class LobPipedOutputStream extends PipedOutputStream {
      private final Task task;

      LobPipedOutputStream(PipedInputStream var1, Task var2) throws IOException {
         super(var1);
         this.task = var2;
      }

      public void close() throws IOException {
         super.close();

         try {
            this.task.get();
         } catch (Exception var2) {
            throw DataUtils.convertToIOException(var2);
         }
      }
   }
}
