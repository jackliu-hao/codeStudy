/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.clone.CloneSupport;
/*     */ import cn.hutool.core.collection.ArrayIter;
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Spliterator;
/*     */ import java.util.Spliterators;
/*     */ import java.util.stream.Stream;
/*     */ import java.util.stream.StreamSupport;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Tuple
/*     */   extends CloneSupport<Tuple>
/*     */   implements Iterable<Object>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -7689304393482182157L;
/*     */   private final Object[] members;
/*     */   private int hashCode;
/*     */   private boolean cacheHash;
/*     */   
/*     */   public Tuple(Object... members) {
/*  36 */     this.members = members;
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
/*     */   public <T> T get(int index) {
/*  48 */     return (T)this.members[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] getMembers() {
/*  57 */     return this.members;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final List<Object> toList() {
/*  67 */     return ListUtil.toList(this.members);
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
/*     */   public Tuple setCacheHash(boolean cacheHash) {
/*  79 */     this.cacheHash = cacheHash;
/*  80 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  90 */     return this.members.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Object value) {
/* 101 */     return ArrayUtil.contains(this.members, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Stream<Object> stream() {
/* 111 */     return Arrays.stream(this.members);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Stream<Object> parallelStream() {
/* 121 */     return StreamSupport.stream(spliterator(), true);
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
/*     */   public final Tuple sub(int start, int end) {
/* 133 */     return new Tuple(ArrayUtil.sub(this.members, start, end));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 138 */     if (this.cacheHash && 0 != this.hashCode) {
/* 139 */       return this.hashCode;
/*     */     }
/* 141 */     int prime = 31;
/* 142 */     int result = 1;
/* 143 */     result = 31 * result + Arrays.deepHashCode(this.members);
/* 144 */     if (this.cacheHash) {
/* 145 */       this.hashCode = result;
/*     */     }
/* 147 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 152 */     if (this == obj) {
/* 153 */       return true;
/*     */     }
/* 155 */     if (obj == null) {
/* 156 */       return false;
/*     */     }
/* 158 */     if (getClass() != obj.getClass()) {
/* 159 */       return false;
/*     */     }
/* 161 */     Tuple other = (Tuple)obj;
/* 162 */     return (false != Arrays.deepEquals(this.members, other.members));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 167 */     return Arrays.toString(this.members);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Object> iterator() {
/* 172 */     return (Iterator<Object>)new ArrayIter(this.members);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Spliterator<Object> spliterator() {
/* 177 */     return Spliterators.spliterator(this.members, 16);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Tuple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */