package org.h2.expression.aggregate;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.index.Index;
import org.h2.mvstore.db.MVSpatialIndex;
import org.h2.table.Column;
import org.h2.table.TableFilter;
import org.h2.util.geometry.GeometryUtils;
import org.h2.value.ExtTypeInfoGeometry;
import org.h2.value.Value;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueNull;

final class AggregateDataEnvelope extends AggregateData {
   private double[] envelope;

   static Index getGeometryColumnIndex(Expression var0) {
      if (var0 instanceof ExpressionColumn) {
         ExpressionColumn var1 = (ExpressionColumn)var0;
         Column var2 = var1.getColumn();
         if (var2.getType().getValueType() == 37) {
            TableFilter var3 = var1.getTableFilter();
            if (var3 != null) {
               ArrayList var4 = var3.getTable().getIndexes();
               if (var4 != null) {
                  int var5 = 1;

                  for(int var6 = var4.size(); var5 < var6; ++var5) {
                     Index var7 = (Index)var4.get(var5);
                     if (var7 instanceof MVSpatialIndex && var7.isFirstColumn(var2)) {
                        return var7;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   void add(SessionLocal var1, Value var2) {
      if (var2 != ValueNull.INSTANCE) {
         this.envelope = GeometryUtils.union(this.envelope, var2.convertToGeometry((ExtTypeInfoGeometry)null).getEnvelopeNoCopy());
      }
   }

   Value getValue(SessionLocal var1) {
      return ValueGeometry.fromEnvelope(this.envelope);
   }
}
