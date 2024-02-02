package org.h2.index;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.table.TableLink;
import org.h2.value.ValueToObjectConverter2;

public class LinkedCursor implements Cursor {
   private final TableLink tableLink;
   private final PreparedStatement prep;
   private final String sql;
   private final SessionLocal session;
   private final ResultSet rs;
   private Row current;

   LinkedCursor(TableLink var1, ResultSet var2, SessionLocal var3, String var4, PreparedStatement var5) {
      this.session = var3;
      this.tableLink = var1;
      this.rs = var2;
      this.sql = var4;
      this.prep = var5;
   }

   public Row get() {
      return this.current;
   }

   public SearchRow getSearchRow() {
      return this.current;
   }

   public boolean next() {
      try {
         boolean var1 = this.rs.next();
         if (!var1) {
            this.rs.close();
            this.tableLink.reusePreparedStatement(this.prep, this.sql);
            this.current = null;
            return false;
         }
      } catch (SQLException var2) {
         throw DbException.convert(var2);
      }

      this.current = this.tableLink.getTemplateRow();

      for(int var3 = 0; var3 < this.current.getColumnCount(); ++var3) {
         this.current.setValue(var3, ValueToObjectConverter2.readValue(this.session, this.rs, var3 + 1, this.tableLink.getColumn(var3).getType().getValueType()));
      }

      return true;
   }

   public boolean previous() {
      throw DbException.getInternalError(this.toString());
   }
}
