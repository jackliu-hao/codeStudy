package org.h2.expression.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.h2.engine.SessionLocal;
import org.h2.expression.BinaryOperation;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.SortOrder;
import org.h2.table.ColumnResolver;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class WindowFrame {
   private final WindowFrameUnits units;
   private final WindowFrameBound starting;
   private final WindowFrameBound following;
   private final WindowFrameExclusion exclusion;

   public static Iterator<Value[]> iterator(Window var0, SessionLocal var1, ArrayList<Value[]> var2, SortOrder var3, int var4, boolean var5) {
      WindowFrame var6 = var0.getWindowFrame();
      if (var6 != null) {
         return var6.iterator(var1, var2, var3, var4, var5);
      } else {
         int var7 = var2.size() - 1;
         return plainIterator(var2, 0, var0.getOrderBy() == null ? var7 : toGroupEnd(var2, var3, var4, var7), var5);
      }
   }

   public static int getEndIndex(Window var0, SessionLocal var1, ArrayList<Value[]> var2, SortOrder var3, int var4) {
      WindowFrame var5 = var0.getWindowFrame();
      if (var5 != null) {
         return var5.getEndIndex(var1, var2, var3, var4);
      } else {
         int var6 = var2.size() - 1;
         return var0.getOrderBy() == null ? var6 : toGroupEnd(var2, var3, var4, var6);
      }
   }

   private static Iterator<Value[]> plainIterator(ArrayList<Value[]> var0, int var1, int var2, boolean var3) {
      if (var2 < var1) {
         return Collections.emptyIterator();
      } else {
         return (Iterator)(var3 ? new PlainReverseItr(var0, var1, var2) : new PlainItr(var0, var1, var2));
      }
   }

   private static Iterator<Value[]> biIterator(ArrayList<Value[]> var0, int var1, int var2, int var3, int var4, boolean var5) {
      return (Iterator)(var5 ? new BiReverseItr(var0, var1, var2, var3, var4) : new BiItr(var0, var1, var2, var3, var4));
   }

   private static Iterator<Value[]> triIterator(ArrayList<Value[]> var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      return (Iterator)(var7 ? new TriReverseItr(var0, var1, var2, var3, var4, var5, var6) : new TriItr(var0, var1, var2, var3, var4, var5, var6));
   }

   private static int toGroupStart(ArrayList<Value[]> var0, SortOrder var1, int var2, int var3) {
      for(Value[] var4 = (Value[])var0.get(var2); var2 > var3 && var1.compare(var4, (Value[])var0.get(var2 - 1)) == 0; --var2) {
      }

      return var2;
   }

   private static int toGroupEnd(ArrayList<Value[]> var0, SortOrder var1, int var2, int var3) {
      for(Value[] var4 = (Value[])var0.get(var2); var2 < var3 && var1.compare(var4, (Value[])var0.get(var2 + 1)) == 0; ++var2) {
      }

      return var2;
   }

   private static int getIntOffset(WindowFrameBound var0, Value[] var1, SessionLocal var2) {
      Value var3 = var0.isVariable() ? var1[var0.getExpressionIndex()] : var0.getValue().getValue(var2);
      int var4;
      if (var3 != ValueNull.INSTANCE && (var4 = var3.getInt()) >= 0) {
         return var4;
      } else {
         throw DbException.get(22013, (String)var3.getTraceSQL());
      }
   }

   private static Value[] getCompareRow(SessionLocal var0, ArrayList<Value[]> var1, SortOrder var2, int var3, WindowFrameBound var4, boolean var5) {
      int var6 = var2.getQueryColumnIndexes()[0];
      Value[] var7 = (Value[])var1.get(var3);
      Value var8 = var7[var6];
      int var9 = var8.getValueType();
      Value var11 = getValueOffset(var4, (Value[])var1.get(var3), var0);
      Object var10;
      switch (var9) {
         case 0:
            var10 = ValueNull.INSTANCE;
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         default:
            throw DbException.getInvalidValueException("unsupported type of sort key for RANGE units", var8.getTraceSQL());
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
            BinaryOperation.OpType var12 = var5 ^ (var2.getSortTypes()[0] & 1) != 0 ? BinaryOperation.OpType.PLUS : BinaryOperation.OpType.MINUS;

            try {
               var10 = (new BinaryOperation(var12, ValueExpression.get(var8), ValueExpression.get(var11))).optimize(var0).getValue(var0).convertTo(var9);
            } catch (DbException var14) {
               switch (var14.getErrorCode()) {
                  case 22003:
                  case 22004:
                     return null;
                  default:
                     throw var14;
               }
            }
      }

      Value[] var15 = (Value[])var7.clone();
      var15[var6] = (Value)var10;
      return var15;
   }

   private static Value getValueOffset(WindowFrameBound var0, Value[] var1, SessionLocal var2) {
      Value var3 = var0.isVariable() ? var1[var0.getExpressionIndex()] : var0.getValue().getValue(var2);
      if (var3 != ValueNull.INSTANCE && var3.getSignum() >= 0) {
         return var3;
      } else {
         throw DbException.get(22013, (String)var3.getTraceSQL());
      }
   }

   public WindowFrame(WindowFrameUnits var1, WindowFrameBound var2, WindowFrameBound var3, WindowFrameExclusion var4) {
      this.units = var1;
      this.starting = var2;
      if (var3 != null && var3.getType() == WindowFrameBoundType.CURRENT_ROW) {
         var3 = null;
      }

      this.following = var3;
      this.exclusion = var4;
   }

   public WindowFrameUnits getUnits() {
      return this.units;
   }

   public WindowFrameBound getStarting() {
      return this.starting;
   }

   public WindowFrameBound getFollowing() {
      return this.following;
   }

   public WindowFrameExclusion getExclusion() {
      return this.exclusion;
   }

   public boolean isValid() {
      WindowFrameBoundType var1 = this.starting.getType();
      WindowFrameBoundType var2 = this.following != null ? this.following.getType() : WindowFrameBoundType.CURRENT_ROW;
      return var1 != WindowFrameBoundType.UNBOUNDED_FOLLOWING && var2 != WindowFrameBoundType.UNBOUNDED_PRECEDING && var1.compareTo(var2) <= 0;
   }

   public boolean isVariableBounds() {
      if (this.starting.isVariable()) {
         return true;
      } else {
         return this.following != null && this.following.isVariable();
      }
   }

   void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.starting.mapColumns(var1, var2, var3);
      if (this.following != null) {
         this.following.mapColumns(var1, var2, var3);
      }

   }

   void optimize(SessionLocal var1) {
      this.starting.optimize(var1);
      if (this.following != null) {
         this.following.optimize(var1);
      }

   }

   void updateAggregate(SessionLocal var1, int var2) {
      this.starting.updateAggregate(var1, var2);
      if (this.following != null) {
         this.following.updateAggregate(var1, var2);
      }

   }

   public Iterator<Value[]> iterator(SessionLocal var1, ArrayList<Value[]> var2, SortOrder var3, int var4, boolean var5) {
      int var6 = this.getIndex(var1, var2, var3, var4, this.starting, false);
      int var7 = this.following != null ? this.getIndex(var1, var2, var3, var4, this.following, true) : (this.units == WindowFrameUnits.ROWS ? var4 : toGroupEnd(var2, var3, var4, var2.size() - 1));
      if (var7 < var6) {
         return Collections.emptyIterator();
      } else {
         int var8 = var2.size();
         if (var6 < var8 && var7 >= 0) {
            if (var6 < 0) {
               var6 = 0;
            }

            if (var7 >= var8) {
               var7 = var8 - 1;
            }

            return this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS ? this.complexIterator(var2, var3, var4, var6, var7, var5) : plainIterator(var2, var6, var7, var5);
         } else {
            return Collections.emptyIterator();
         }
      }
   }

   public int getStartIndex(SessionLocal var1, ArrayList<Value[]> var2, SortOrder var3, int var4) {
      if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
         throw new UnsupportedOperationException();
      } else {
         int var5 = this.getIndex(var1, var2, var3, var4, this.starting, false);
         if (var5 < 0) {
            var5 = 0;
         }

         return var5;
      }
   }

   private int getEndIndex(SessionLocal var1, ArrayList<Value[]> var2, SortOrder var3, int var4) {
      if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
         throw new UnsupportedOperationException();
      } else {
         int var5 = this.following != null ? this.getIndex(var1, var2, var3, var4, this.following, true) : (this.units == WindowFrameUnits.ROWS ? var4 : toGroupEnd(var2, var3, var4, var2.size() - 1));
         int var6 = var2.size();
         if (var5 >= var6) {
            var5 = var6 - 1;
         }

         return var5;
      }
   }

   private int getIndex(SessionLocal var1, ArrayList<Value[]> var2, SortOrder var3, int var4, WindowFrameBound var5, boolean var6) {
      int var7 = var2.size();
      int var8 = var7 - 1;
      int var9;
      Value[] var10;
      int var12;
      switch (var5.getType()) {
         case UNBOUNDED_PRECEDING:
            var9 = -1;
            break;
         case PRECEDING:
            switch (this.units) {
               case ROWS:
                  var12 = getIntOffset(var5, (Value[])var2.get(var4), var1);
                  var9 = var12 > var4 ? -1 : var4 - var12;
                  return var9;
               case GROUPS:
                  var12 = getIntOffset(var5, (Value[])var2.get(var4), var1);
                  if (!var6) {
                     for(var9 = toGroupStart(var2, var3, var4, 0); var12 > 0 && var9 > 0; var9 = toGroupStart(var2, var3, var9 - 1, 0)) {
                        --var12;
                     }

                     if (var12 > 0) {
                        var9 = -1;
                        return var9;
                     }
                  } else if (var12 == 0) {
                     var9 = toGroupEnd(var2, var3, var4, var8);
                  } else {
                     for(var9 = var4; var12 > 0 && var9 >= 0; var9 = toGroupStart(var2, var3, var9, 0) - 1) {
                        --var12;
                     }

                     return var9;
                  }

                  return var9;
               case RANGE:
                  var10 = getCompareRow(var1, var2, var3, var4, var5, false);
                  if (var10 != null) {
                     var9 = Collections.binarySearch(var2, var10, var3);
                     if (var9 >= 0) {
                        if (!var6) {
                           while(var9 > 0 && var3.compare(var10, (Value[])var2.get(var9 - 1)) == 0) {
                              --var9;
                           }

                           return var9;
                        } else {
                           while(var9 < var8 && var3.compare(var10, (Value[])var2.get(var9 + 1)) == 0) {
                              ++var9;
                           }

                           return var9;
                        }
                     } else {
                        var9 = ~var9;
                        if (!var6) {
                           if (var9 == 0) {
                              var9 = -1;
                              return var9;
                           }
                        } else {
                           --var9;
                        }

                        return var9;
                     }
                  } else {
                     var9 = -1;
                     return var9;
                  }
               default:
                  throw DbException.getUnsupportedException("units=" + this.units);
            }
         case CURRENT_ROW:
            switch (this.units) {
               case ROWS:
                  var9 = var4;
                  return var9;
               case GROUPS:
               case RANGE:
                  var9 = var6 ? toGroupEnd(var2, var3, var4, var8) : toGroupStart(var2, var3, var4, 0);
                  return var9;
               default:
                  throw DbException.getUnsupportedException("units=" + this.units);
            }
         case FOLLOWING:
            switch (this.units) {
               case ROWS:
                  var12 = getIntOffset(var5, (Value[])var2.get(var4), var1);
                  int var11 = var8 - var4;
                  var9 = var12 > var11 ? var7 : var4 + var12;
                  return var9;
               case GROUPS:
                  var12 = getIntOffset(var5, (Value[])var2.get(var4), var1);
                  if (var6) {
                     for(var9 = toGroupEnd(var2, var3, var4, var8); var12 > 0 && var9 < var8; var9 = toGroupEnd(var2, var3, var9 + 1, var8)) {
                        --var12;
                     }

                     if (var12 > 0) {
                        var9 = var7;
                        return var9;
                     }
                  } else if (var12 == 0) {
                     var9 = toGroupStart(var2, var3, var4, 0);
                  } else {
                     for(var9 = var4; var12 > 0 && var9 <= var8; var9 = toGroupEnd(var2, var3, var9, var8) + 1) {
                        --var12;
                     }

                     return var9;
                  }

                  return var9;
               case RANGE:
                  var10 = getCompareRow(var1, var2, var3, var4, var5, true);
                  if (var10 != null) {
                     var9 = Collections.binarySearch(var2, var10, var3);
                     if (var9 >= 0) {
                        if (var6) {
                           while(var9 < var8 && var3.compare(var10, (Value[])var2.get(var9 + 1)) == 0) {
                              ++var9;
                           }

                           return var9;
                        } else {
                           while(var9 > 0 && var3.compare(var10, (Value[])var2.get(var9 - 1)) == 0) {
                              --var9;
                           }

                           return var9;
                        }
                     } else {
                        var9 = ~var9;
                        if (var6 && var9 != var7) {
                           --var9;
                           return var9;
                        }
                     }
                  } else {
                     var9 = var7;
                  }

                  return var9;
               default:
                  throw DbException.getUnsupportedException("units=" + this.units);
            }
         case UNBOUNDED_FOLLOWING:
            var9 = var7;
            break;
         default:
            throw DbException.getUnsupportedException("window frame bound type=" + var5.getType());
      }

      return var9;
   }

   private Iterator<Value[]> complexIterator(ArrayList<Value[]> var1, SortOrder var2, int var3, int var4, int var5, boolean var6) {
      if (this.exclusion == WindowFrameExclusion.EXCLUDE_CURRENT_ROW) {
         if (var3 >= var4 && var3 <= var5) {
            if (var3 == var4) {
               ++var4;
            } else {
               if (var3 != var5) {
                  return biIterator(var1, var4, var3 - 1, var3 + 1, var5, var6);
               }

               --var5;
            }
         }
      } else {
         int var7 = toGroupStart(var1, var2, var3, var4);
         int var8 = toGroupEnd(var1, var2, var3, var5);
         boolean var9 = this.exclusion == WindowFrameExclusion.EXCLUDE_TIES;
         if (var9) {
            if (var3 == var7) {
               ++var7;
               var9 = false;
            } else if (var3 == var8) {
               --var8;
               var9 = false;
            }
         }

         if (var7 <= var8 && var8 >= var4 && var7 <= var5) {
            if (var9) {
               if (var4 == var7) {
                  if (var5 == var8) {
                     return Collections.singleton(var1.get(var3)).iterator();
                  }

                  return biIterator(var1, var3, var3, var8 + 1, var5, var6);
               }

               if (var5 == var8) {
                  return biIterator(var1, var4, var7 - 1, var3, var3, var6);
               }

               return triIterator(var1, var4, var7 - 1, var3, var3, var8 + 1, var5, var6);
            }

            if (var4 >= var7) {
               var4 = var8 + 1;
            } else {
               if (var5 > var8) {
                  return biIterator(var1, var4, var7 - 1, var8 + 1, var5, var6);
               }

               var5 = var7 - 1;
            }
         }
      }

      return plainIterator(var1, var4, var5, var6);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append(this.units.getSQL());
      if (this.following == null) {
         var1.append(' ');
         this.starting.getSQL(var1, false, var2);
      } else {
         var1.append(" BETWEEN ");
         this.starting.getSQL(var1, false, var2).append(" AND ");
         this.following.getSQL(var1, true, var2);
      }

      if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
         var1.append(' ').append(this.exclusion.getSQL());
      }

      return var1;
   }

   private static final class TriReverseItr extends BiReverseItr {
      private final int end2;
      private final int start2;

      TriReverseItr(ArrayList<Value[]> var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         super(var1, var2, var3, var4, var7);
         this.end2 = var5;
         this.start2 = var6;
      }

      public Value[] next() {
         if (this.cursor < this.startIndex) {
            throw new NoSuchElementException();
         } else {
            Value[] var1 = (Value[])this.orderedRows.get(this.cursor);
            this.cursor = this.cursor != this.start1 ? (this.cursor != this.start2 ? this.cursor - 1 : this.end2) : this.end1;
            return var1;
         }
      }
   }

   private static final class TriItr extends BiItr {
      private final int end2;
      private final int start2;

      TriItr(ArrayList<Value[]> var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         super(var1, var2, var3, var4, var7);
         this.end2 = var5;
         this.start2 = var6;
      }

      public Value[] next() {
         if (this.cursor > this.endIndex) {
            throw new NoSuchElementException();
         } else {
            Value[] var1 = (Value[])this.orderedRows.get(this.cursor);
            this.cursor = this.cursor != this.end1 ? (this.cursor != this.end2 ? this.cursor + 1 : this.start2) : this.start1;
            return var1;
         }
      }
   }

   private static class BiReverseItr extends PlainReverseItr {
      final int end1;
      final int start1;

      BiReverseItr(ArrayList<Value[]> var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var5);
         this.end1 = var3;
         this.start1 = var4;
      }

      public Value[] next() {
         if (this.cursor < this.startIndex) {
            throw new NoSuchElementException();
         } else {
            Value[] var1 = (Value[])this.orderedRows.get(this.cursor);
            this.cursor = this.cursor != this.start1 ? this.cursor - 1 : this.end1;
            return var1;
         }
      }
   }

   private static class BiItr extends PlainItr {
      final int end1;
      final int start1;

      BiItr(ArrayList<Value[]> var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var5);
         this.end1 = var3;
         this.start1 = var4;
      }

      public Value[] next() {
         if (this.cursor > this.endIndex) {
            throw new NoSuchElementException();
         } else {
            Value[] var1 = (Value[])this.orderedRows.get(this.cursor);
            this.cursor = this.cursor != this.end1 ? this.cursor + 1 : this.start1;
            return var1;
         }
      }
   }

   private static class PlainReverseItr extends Itr {
      final int startIndex;

      PlainReverseItr(ArrayList<Value[]> var1, int var2, int var3) {
         super(var1);
         this.startIndex = var2;
         this.cursor = var3;
      }

      public boolean hasNext() {
         return this.cursor >= this.startIndex;
      }

      public Value[] next() {
         if (this.cursor < this.startIndex) {
            throw new NoSuchElementException();
         } else {
            return (Value[])this.orderedRows.get(this.cursor--);
         }
      }
   }

   private static class PlainItr extends Itr {
      final int endIndex;

      PlainItr(ArrayList<Value[]> var1, int var2, int var3) {
         super(var1);
         this.endIndex = var3;
         this.cursor = var2;
      }

      public boolean hasNext() {
         return this.cursor <= this.endIndex;
      }

      public Value[] next() {
         if (this.cursor > this.endIndex) {
            throw new NoSuchElementException();
         } else {
            return (Value[])this.orderedRows.get(this.cursor++);
         }
      }
   }

   private abstract static class Itr implements Iterator<Value[]> {
      final ArrayList<Value[]> orderedRows;
      int cursor;

      Itr(ArrayList<Value[]> var1) {
         this.orderedRows = var1;
      }
   }
}
