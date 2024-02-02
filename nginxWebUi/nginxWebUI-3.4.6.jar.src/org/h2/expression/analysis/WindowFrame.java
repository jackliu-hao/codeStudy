/*     */ package org.h2.expression.analysis;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.BinaryOperation;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class WindowFrame
/*     */ {
/*     */   private final WindowFrameUnits units;
/*     */   private final WindowFrameBound starting;
/*     */   private final WindowFrameBound following;
/*     */   private final WindowFrameExclusion exclusion;
/*     */   
/*     */   private static abstract class Itr
/*     */     implements Iterator<Value[]>
/*     */   {
/*     */     final ArrayList<Value[]> orderedRows;
/*     */     int cursor;
/*     */     
/*     */     Itr(ArrayList<Value[]> param1ArrayList) {
/*  37 */       this.orderedRows = param1ArrayList;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PlainItr
/*     */     extends Itr
/*     */   {
/*     */     final int endIndex;
/*     */     
/*     */     PlainItr(ArrayList<Value[]> param1ArrayList, int param1Int1, int param1Int2) {
/*  47 */       super(param1ArrayList);
/*  48 */       this.endIndex = param1Int2;
/*  49 */       this.cursor = param1Int1;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/*  54 */       return (this.cursor <= this.endIndex);
/*     */     }
/*     */ 
/*     */     
/*     */     public Value[] next() {
/*  59 */       if (this.cursor > this.endIndex) {
/*  60 */         throw new NoSuchElementException();
/*     */       }
/*  62 */       return this.orderedRows.get(this.cursor++);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PlainReverseItr
/*     */     extends Itr
/*     */   {
/*     */     final int startIndex;
/*     */     
/*     */     PlainReverseItr(ArrayList<Value[]> param1ArrayList, int param1Int1, int param1Int2) {
/*  72 */       super(param1ArrayList);
/*  73 */       this.startIndex = param1Int1;
/*  74 */       this.cursor = param1Int2;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/*  79 */       return (this.cursor >= this.startIndex);
/*     */     }
/*     */ 
/*     */     
/*     */     public Value[] next() {
/*  84 */       if (this.cursor < this.startIndex) {
/*  85 */         throw new NoSuchElementException();
/*     */       }
/*  87 */       return this.orderedRows.get(this.cursor--);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BiItr
/*     */     extends PlainItr {
/*     */     final int end1;
/*     */     final int start1;
/*     */     
/*     */     BiItr(ArrayList<Value[]> param1ArrayList, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
/*  97 */       super(param1ArrayList, param1Int1, param1Int4);
/*  98 */       this.end1 = param1Int2;
/*  99 */       this.start1 = param1Int3;
/*     */     }
/*     */ 
/*     */     
/*     */     public Value[] next() {
/* 104 */       if (this.cursor > this.endIndex) {
/* 105 */         throw new NoSuchElementException();
/*     */       }
/* 107 */       Value[] arrayOfValue = this.orderedRows.get(this.cursor);
/* 108 */       this.cursor = (this.cursor != this.end1) ? (this.cursor + 1) : this.start1;
/* 109 */       return arrayOfValue;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BiReverseItr
/*     */     extends PlainReverseItr {
/*     */     final int end1;
/*     */     final int start1;
/*     */     
/*     */     BiReverseItr(ArrayList<Value[]> param1ArrayList, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
/* 119 */       super(param1ArrayList, param1Int1, param1Int4);
/* 120 */       this.end1 = param1Int2;
/* 121 */       this.start1 = param1Int3;
/*     */     }
/*     */ 
/*     */     
/*     */     public Value[] next() {
/* 126 */       if (this.cursor < this.startIndex) {
/* 127 */         throw new NoSuchElementException();
/*     */       }
/* 129 */       Value[] arrayOfValue = this.orderedRows.get(this.cursor);
/* 130 */       this.cursor = (this.cursor != this.start1) ? (this.cursor - 1) : this.end1;
/* 131 */       return arrayOfValue;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class TriItr
/*     */     extends BiItr
/*     */   {
/*     */     private final int end2;
/*     */     private final int start2;
/*     */     
/*     */     TriItr(ArrayList<Value[]> param1ArrayList, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
/* 142 */       super(param1ArrayList, param1Int1, param1Int2, param1Int3, param1Int6);
/* 143 */       this.end2 = param1Int4;
/* 144 */       this.start2 = param1Int5;
/*     */     }
/*     */ 
/*     */     
/*     */     public Value[] next() {
/* 149 */       if (this.cursor > this.endIndex) {
/* 150 */         throw new NoSuchElementException();
/*     */       }
/* 152 */       Value[] arrayOfValue = this.orderedRows.get(this.cursor);
/* 153 */       this.cursor = (this.cursor != this.end1) ? ((this.cursor != this.end2) ? (this.cursor + 1) : this.start2) : this.start1;
/* 154 */       return arrayOfValue;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class TriReverseItr
/*     */     extends BiReverseItr
/*     */   {
/*     */     private final int end2;
/*     */     private final int start2;
/*     */     
/*     */     TriReverseItr(ArrayList<Value[]> param1ArrayList, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
/* 165 */       super(param1ArrayList, param1Int1, param1Int2, param1Int3, param1Int6);
/* 166 */       this.end2 = param1Int4;
/* 167 */       this.start2 = param1Int5;
/*     */     }
/*     */ 
/*     */     
/*     */     public Value[] next() {
/* 172 */       if (this.cursor < this.startIndex) {
/* 173 */         throw new NoSuchElementException();
/*     */       }
/* 175 */       Value[] arrayOfValue = this.orderedRows.get(this.cursor);
/* 176 */       this.cursor = (this.cursor != this.start1) ? ((this.cursor != this.start2) ? (this.cursor - 1) : this.end2) : this.end1;
/* 177 */       return arrayOfValue;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Iterator<Value[]> iterator(Window paramWindow, SessionLocal paramSessionLocal, ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt, boolean paramBoolean) {
/* 210 */     WindowFrame windowFrame = paramWindow.getWindowFrame();
/* 211 */     if (windowFrame != null) {
/* 212 */       return windowFrame.iterator(paramSessionLocal, paramArrayList, paramSortOrder, paramInt, paramBoolean);
/*     */     }
/* 214 */     int i = paramArrayList.size() - 1;
/* 215 */     return plainIterator(paramArrayList, 0, 
/* 216 */         (paramWindow.getOrderBy() == null) ? i : toGroupEnd(paramArrayList, paramSortOrder, paramInt, i), paramBoolean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getEndIndex(Window paramWindow, SessionLocal paramSessionLocal, ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt) {
/* 241 */     WindowFrame windowFrame = paramWindow.getWindowFrame();
/* 242 */     if (windowFrame != null) {
/* 243 */       return windowFrame.getEndIndex(paramSessionLocal, paramArrayList, paramSortOrder, paramInt);
/*     */     }
/* 245 */     int i = paramArrayList.size() - 1;
/* 246 */     return (paramWindow.getOrderBy() == null) ? i : toGroupEnd(paramArrayList, paramSortOrder, paramInt, i);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Iterator<Value[]> plainIterator(ArrayList<Value[]> paramArrayList, int paramInt1, int paramInt2, boolean paramBoolean) {
/* 251 */     if (paramInt2 < paramInt1) {
/* 252 */       return (Iterator)Collections.emptyIterator();
/*     */     }
/* 254 */     return paramBoolean ? new PlainReverseItr(paramArrayList, paramInt1, paramInt2) : new PlainItr(paramArrayList, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Iterator<Value[]> biIterator(ArrayList<Value[]> paramArrayList, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
/* 260 */     return paramBoolean ? new BiReverseItr(paramArrayList, paramInt1, paramInt2, paramInt3, paramInt4) : new BiItr(paramArrayList, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Iterator<Value[]> triIterator(ArrayList<Value[]> paramArrayList, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
/* 266 */     return paramBoolean ? new TriReverseItr(paramArrayList, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6) : new TriItr(paramArrayList, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static int toGroupStart(ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt1, int paramInt2) {
/* 272 */     Value[] arrayOfValue = paramArrayList.get(paramInt1);
/* 273 */     while (paramInt1 > paramInt2 && paramSortOrder.compare(arrayOfValue, paramArrayList.get(paramInt1 - 1)) == 0) {
/* 274 */       paramInt1--;
/*     */     }
/* 276 */     return paramInt1;
/*     */   }
/*     */   
/*     */   private static int toGroupEnd(ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt1, int paramInt2) {
/* 280 */     Value[] arrayOfValue = paramArrayList.get(paramInt1);
/* 281 */     while (paramInt1 < paramInt2 && paramSortOrder.compare(arrayOfValue, paramArrayList.get(paramInt1 + 1)) == 0) {
/* 282 */       paramInt1++;
/*     */     }
/* 284 */     return paramInt1;
/*     */   }
/*     */   
/*     */   private static int getIntOffset(WindowFrameBound paramWindowFrameBound, Value[] paramArrayOfValue, SessionLocal paramSessionLocal) {
/* 288 */     Value value = paramWindowFrameBound.isVariable() ? paramArrayOfValue[paramWindowFrameBound.getExpressionIndex()] : paramWindowFrameBound.getValue().getValue(paramSessionLocal);
/*     */     int i;
/* 290 */     if (value == ValueNull.INSTANCE || (i = value.getInt()) < 0) {
/* 291 */       throw DbException.get(22013, value.getTraceSQL());
/*     */     }
/* 293 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Value[] getCompareRow(SessionLocal paramSessionLocal, ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt, WindowFrameBound paramWindowFrameBound, boolean paramBoolean) {
/*     */     ValueNull valueNull;
/*     */     Value value2, arrayOfValue3[];
/*     */     BinaryOperation.OpType opType;
/*     */     Value[] arrayOfValue2;
/* 317 */     int i = paramSortOrder.getQueryColumnIndexes()[0];
/* 318 */     Value[] arrayOfValue1 = paramArrayList.get(paramInt);
/* 319 */     Value value1 = arrayOfValue1[i];
/* 320 */     int j = value1.getValueType();
/*     */     
/* 322 */     Value value3 = getValueOffset(paramWindowFrameBound, paramArrayList.get(paramInt), paramSessionLocal);
/* 323 */     switch (j) {
/*     */       case 0:
/* 325 */         valueNull = ValueNull.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 371 */         arrayOfValue3 = (Value[])arrayOfValue1.clone();
/* 372 */         arrayOfValue3[i] = (Value)valueNull;
/* 373 */         return arrayOfValue3;case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 34: opType = ((paramBoolean ^ (((paramSortOrder.getSortTypes()[0] & 0x1) != 0) ? 1 : 0)) != 0) ? BinaryOperation.OpType.PLUS : BinaryOperation.OpType.MINUS; try { value2 = (new BinaryOperation(opType, (Expression)ValueExpression.get(value1), (Expression)ValueExpression.get(value3))).optimize(paramSessionLocal).getValue(paramSessionLocal).convertTo(j); } catch (DbException dbException) { switch (dbException.getErrorCode()) { case 22003: case 22004: return null; }  throw dbException; }  arrayOfValue2 = (Value[])arrayOfValue1.clone(); arrayOfValue2[i] = value2; return arrayOfValue2;
/*     */     } 
/*     */     throw DbException.getInvalidValueException("unsupported type of sort key for RANGE units", value1.getTraceSQL());
/*     */   } private static Value getValueOffset(WindowFrameBound paramWindowFrameBound, Value[] paramArrayOfValue, SessionLocal paramSessionLocal) {
/* 377 */     Value value = paramWindowFrameBound.isVariable() ? paramArrayOfValue[paramWindowFrameBound.getExpressionIndex()] : paramWindowFrameBound.getValue().getValue(paramSessionLocal);
/* 378 */     if (value == ValueNull.INSTANCE || value.getSignum() < 0) {
/* 379 */       throw DbException.get(22013, value.getTraceSQL());
/*     */     }
/* 381 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFrame(WindowFrameUnits paramWindowFrameUnits, WindowFrameBound paramWindowFrameBound1, WindowFrameBound paramWindowFrameBound2, WindowFrameExclusion paramWindowFrameExclusion) {
/* 398 */     this.units = paramWindowFrameUnits;
/* 399 */     this.starting = paramWindowFrameBound1;
/* 400 */     if (paramWindowFrameBound2 != null && paramWindowFrameBound2.getType() == WindowFrameBoundType.CURRENT_ROW) {
/* 401 */       paramWindowFrameBound2 = null;
/*     */     }
/* 403 */     this.following = paramWindowFrameBound2;
/* 404 */     this.exclusion = paramWindowFrameExclusion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFrameUnits getUnits() {
/* 413 */     return this.units;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFrameBound getStarting() {
/* 422 */     return this.starting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFrameBound getFollowing() {
/* 431 */     return this.following;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFrameExclusion getExclusion() {
/* 440 */     return this.exclusion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isValid() {
/* 449 */     WindowFrameBoundType windowFrameBoundType1 = this.starting.getType();
/* 450 */     WindowFrameBoundType windowFrameBoundType2 = (this.following != null) ? this.following.getType() : WindowFrameBoundType.CURRENT_ROW;
/* 451 */     return (windowFrameBoundType1 != WindowFrameBoundType.UNBOUNDED_FOLLOWING && windowFrameBoundType2 != WindowFrameBoundType.UNBOUNDED_PRECEDING && windowFrameBoundType1
/* 452 */       .compareTo(windowFrameBoundType2) <= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isVariableBounds() {
/* 462 */     if (this.starting.isVariable()) {
/* 463 */       return true;
/*     */     }
/* 465 */     if (this.following != null && this.following.isVariable()) {
/* 466 */       return true;
/*     */     }
/* 468 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 482 */     this.starting.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 483 */     if (this.following != null) {
/* 484 */       this.following.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void optimize(SessionLocal paramSessionLocal) {
/* 495 */     this.starting.optimize(paramSessionLocal);
/* 496 */     if (this.following != null) {
/* 497 */       this.following.optimize(paramSessionLocal);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 511 */     this.starting.updateAggregate(paramSessionLocal, paramInt);
/* 512 */     if (this.following != null) {
/* 513 */       this.following.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<Value[]> iterator(SessionLocal paramSessionLocal, ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt, boolean paramBoolean) {
/* 534 */     int i = getIndex(paramSessionLocal, paramArrayList, paramSortOrder, paramInt, this.starting, false);
/*     */ 
/*     */     
/* 537 */     int j = (this.following != null) ? getIndex(paramSessionLocal, paramArrayList, paramSortOrder, paramInt, this.following, true) : ((this.units == WindowFrameUnits.ROWS) ? paramInt : toGroupEnd(paramArrayList, paramSortOrder, paramInt, paramArrayList.size() - 1));
/* 538 */     if (j < i) {
/* 539 */       return (Iterator)Collections.emptyIterator();
/*     */     }
/* 541 */     int k = paramArrayList.size();
/* 542 */     if (i >= k || j < 0) {
/* 543 */       return (Iterator)Collections.emptyIterator();
/*     */     }
/* 545 */     if (i < 0) {
/* 546 */       i = 0;
/*     */     }
/* 548 */     if (j >= k) {
/* 549 */       j = k - 1;
/*     */     }
/* 551 */     return (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) ? 
/* 552 */       complexIterator(paramArrayList, paramSortOrder, paramInt, i, j, paramBoolean) : 
/* 553 */       plainIterator(paramArrayList, i, j, paramBoolean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStartIndex(SessionLocal paramSessionLocal, ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt) {
/* 573 */     if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
/* 574 */       throw new UnsupportedOperationException();
/*     */     }
/* 576 */     int i = getIndex(paramSessionLocal, paramArrayList, paramSortOrder, paramInt, this.starting, false);
/* 577 */     if (i < 0) {
/* 578 */       i = 0;
/*     */     }
/* 580 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getEndIndex(SessionLocal paramSessionLocal, ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt) {
/* 600 */     if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
/* 601 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/* 605 */     int i = (this.following != null) ? getIndex(paramSessionLocal, paramArrayList, paramSortOrder, paramInt, this.following, true) : ((this.units == WindowFrameUnits.ROWS) ? paramInt : toGroupEnd(paramArrayList, paramSortOrder, paramInt, paramArrayList.size() - 1));
/* 606 */     int j = paramArrayList.size();
/* 607 */     if (i >= j) {
/* 608 */       i = j - 1;
/*     */     }
/* 610 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getIndex(SessionLocal paramSessionLocal, ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt, WindowFrameBound paramWindowFrameBound, boolean paramBoolean) {
/*     */     int k, n;
/*     */     Value[] arrayOfValue2;
/*     */     int m;
/*     */     Value[] arrayOfValue1;
/* 634 */     int i1, i = paramArrayList.size();
/* 635 */     int j = i - 1;
/*     */     
/* 637 */     switch (paramWindowFrameBound.getType()) {
/*     */       case UNBOUNDED_PRECEDING:
/* 639 */         k = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 790 */         return k;case PRECEDING: switch (this.units) { case UNBOUNDED_PRECEDING: n = getIntOffset(paramWindowFrameBound, paramArrayList.get(paramInt), paramSessionLocal); k = (n > paramInt) ? -1 : (paramInt - n); return k;case PRECEDING: n = getIntOffset(paramWindowFrameBound, paramArrayList.get(paramInt), paramSessionLocal); if (!paramBoolean) { k = toGroupStart(paramArrayList, paramSortOrder, paramInt, 0); while (n > 0 && k > 0) { n--; k = toGroupStart(paramArrayList, paramSortOrder, k - 1, 0); }  if (n > 0) k = -1;  } else if (n == 0) { k = toGroupEnd(paramArrayList, paramSortOrder, paramInt, j); } else { k = paramInt; while (n > 0 && k >= 0) { n--; k = toGroupStart(paramArrayList, paramSortOrder, k, 0) - 1; }  }  return k;case CURRENT_ROW: k = paramInt; arrayOfValue2 = getCompareRow(paramSessionLocal, paramArrayList, paramSortOrder, k, paramWindowFrameBound, false); if (arrayOfValue2 != null) { k = Collections.binarySearch(paramArrayList, arrayOfValue2, (Comparator<? super Value>)paramSortOrder); if (k >= 0) { if (!paramBoolean) { while (k > 0 && paramSortOrder.compare(arrayOfValue2, paramArrayList.get(k - 1)) == 0) k--;  } else { while (k < j && paramSortOrder.compare(arrayOfValue2, paramArrayList.get(k + 1)) == 0) k++;  }  } else { k ^= 0xFFFFFFFF; if (!paramBoolean) { if (k == 0) k = -1;  } else { k--; }  }  } else { k = -1; }  return k; }  throw DbException.getUnsupportedException("units=" + this.units);case CURRENT_ROW: switch (this.units) { case UNBOUNDED_PRECEDING: k = paramInt; return k;case PRECEDING: case CURRENT_ROW: k = paramBoolean ? toGroupEnd(paramArrayList, paramSortOrder, paramInt, j) : toGroupStart(paramArrayList, paramSortOrder, paramInt, 0); return k; }  throw DbException.getUnsupportedException("units=" + this.units);case FOLLOWING: switch (this.units) { case UNBOUNDED_PRECEDING: m = getIntOffset(paramWindowFrameBound, paramArrayList.get(paramInt), paramSessionLocal); i1 = j - paramInt; k = (m > i1) ? i : (paramInt + m); return k;case PRECEDING: m = getIntOffset(paramWindowFrameBound, paramArrayList.get(paramInt), paramSessionLocal); if (paramBoolean) { k = toGroupEnd(paramArrayList, paramSortOrder, paramInt, j); while (m > 0 && k < j) { m--; k = toGroupEnd(paramArrayList, paramSortOrder, k + 1, j); }  if (m > 0) k = i;  } else if (m == 0) { k = toGroupStart(paramArrayList, paramSortOrder, paramInt, 0); } else { k = paramInt; while (m > 0 && k <= j) { m--; k = toGroupEnd(paramArrayList, paramSortOrder, k, j) + 1; }  }  return k;case CURRENT_ROW: k = paramInt; arrayOfValue1 = getCompareRow(paramSessionLocal, paramArrayList, paramSortOrder, k, paramWindowFrameBound, true); if (arrayOfValue1 != null) { k = Collections.binarySearch(paramArrayList, arrayOfValue1, (Comparator<? super Value>)paramSortOrder); if (k >= 0) { if (paramBoolean) { while (k < j && paramSortOrder.compare(arrayOfValue1, paramArrayList.get(k + 1)) == 0) k++;  } else { while (k > 0 && paramSortOrder.compare(arrayOfValue1, paramArrayList.get(k - 1)) == 0) k--;  }  } else { k ^= 0xFFFFFFFF; if (paramBoolean && k != i) k--;  }  } else { k = i; }  return k; }  throw DbException.getUnsupportedException("units=" + this.units);case UNBOUNDED_FOLLOWING: k = i; return k;
/*     */     } 
/*     */     throw DbException.getUnsupportedException("window frame bound type=" + paramWindowFrameBound.getType());
/*     */   }
/*     */   private Iterator<Value[]> complexIterator(ArrayList<Value[]> paramArrayList, SortOrder paramSortOrder, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
/* 795 */     if (this.exclusion == WindowFrameExclusion.EXCLUDE_CURRENT_ROW) {
/* 796 */       if (paramInt1 >= paramInt2 && paramInt1 <= paramInt3)
/*     */       {
/* 798 */         if (paramInt1 == paramInt2) {
/* 799 */           paramInt2++;
/* 800 */         } else if (paramInt1 == paramInt3) {
/* 801 */           paramInt3--;
/*     */         } else {
/* 803 */           return biIterator(paramArrayList, paramInt2, paramInt1 - 1, paramInt1 + 1, paramInt3, paramBoolean);
/*     */         } 
/*     */       }
/*     */     } else {
/* 807 */       int i = toGroupStart(paramArrayList, paramSortOrder, paramInt1, paramInt2);
/*     */       
/* 809 */       int j = toGroupEnd(paramArrayList, paramSortOrder, paramInt1, paramInt3);
/* 810 */       boolean bool = (this.exclusion == WindowFrameExclusion.EXCLUDE_TIES) ? true : false;
/* 811 */       if (bool)
/*     */       {
/* 813 */         if (paramInt1 == i) {
/* 814 */           i++;
/* 815 */           bool = false;
/* 816 */         } else if (paramInt1 == j) {
/* 817 */           j--;
/* 818 */           bool = false;
/*     */         } 
/*     */       }
/* 821 */       if (i <= j && j >= paramInt2 && i <= paramInt3) {
/*     */         
/* 823 */         if (bool) {
/* 824 */           if (paramInt2 == i) {
/* 825 */             if (paramInt3 == j) {
/* 826 */               return (Iterator)Collections.<Value[]>singleton(paramArrayList.get(paramInt1)).iterator();
/*     */             }
/* 828 */             return biIterator(paramArrayList, paramInt1, paramInt1, j + 1, paramInt3, paramBoolean);
/*     */           } 
/*     */           
/* 831 */           if (paramInt3 == j) {
/* 832 */             return biIterator(paramArrayList, paramInt2, i - 1, paramInt1, paramInt1, paramBoolean);
/*     */           }
/* 834 */           return triIterator(paramArrayList, paramInt2, i - 1, paramInt1, paramInt1, j + 1, paramInt3, paramBoolean);
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 839 */         if (paramInt2 >= i) {
/* 840 */           paramInt2 = j + 1;
/* 841 */         } else if (paramInt3 <= j) {
/* 842 */           paramInt3 = i - 1;
/*     */         } else {
/* 844 */           return biIterator(paramArrayList, paramInt2, i - 1, j + 1, paramInt3, paramBoolean);
/*     */         } 
/*     */       } 
/*     */     } 
/* 848 */     return plainIterator(paramArrayList, paramInt2, paramInt3, paramBoolean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 862 */     paramStringBuilder.append(this.units.getSQL());
/* 863 */     if (this.following == null) {
/* 864 */       paramStringBuilder.append(' ');
/* 865 */       this.starting.getSQL(paramStringBuilder, false, paramInt);
/*     */     } else {
/* 867 */       paramStringBuilder.append(" BETWEEN ");
/* 868 */       this.starting.getSQL(paramStringBuilder, false, paramInt).append(" AND ");
/* 869 */       this.following.getSQL(paramStringBuilder, true, paramInt);
/*     */     } 
/* 871 */     if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
/* 872 */       paramStringBuilder.append(' ').append(this.exclusion.getSQL());
/*     */     }
/* 874 */     return paramStringBuilder;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\WindowFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */