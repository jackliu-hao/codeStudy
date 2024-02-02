/*     */ package org.antlr.v4.runtime.misc;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Interval
/*     */ {
/*     */   public static final int INTERVAL_POOL_MAX_VALUE = 1000;
/*  36 */   public static final Interval INVALID = new Interval(-1, -2);
/*     */   
/*  38 */   static Interval[] cache = new Interval[1001];
/*     */   
/*     */   public int a;
/*     */   
/*     */   public int b;
/*  43 */   public static int creates = 0;
/*  44 */   public static int misses = 0;
/*  45 */   public static int hits = 0;
/*  46 */   public static int outOfRange = 0;
/*     */   public Interval(int a, int b) {
/*  48 */     this.a = a; this.b = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Interval of(int a, int b) {
/*  58 */     if (a != b || a < 0 || a > 1000) {
/*  59 */       return new Interval(a, b);
/*     */     }
/*  61 */     if (cache[a] == null) {
/*  62 */       cache[a] = new Interval(a, a);
/*     */     }
/*  64 */     return cache[a];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/*  71 */     if (this.b < this.a) return 0; 
/*  72 */     return this.b - this.a + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  77 */     if (o == null || !(o instanceof Interval)) {
/*  78 */       return false;
/*     */     }
/*  80 */     Interval other = (Interval)o;
/*  81 */     return (this.a == other.a && this.b == other.b);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  86 */     int hash = 23;
/*  87 */     hash = hash * 31 + this.a;
/*  88 */     hash = hash * 31 + this.b;
/*  89 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean startsBeforeDisjoint(Interval other) {
/*  94 */     return (this.a < other.a && this.b < other.a);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean startsBeforeNonDisjoint(Interval other) {
/*  99 */     return (this.a <= other.a && this.b >= other.a);
/*     */   }
/*     */   
/*     */   public boolean startsAfter(Interval other) {
/* 103 */     return (this.a > other.a);
/*     */   }
/*     */   
/*     */   public boolean startsAfterDisjoint(Interval other) {
/* 107 */     return (this.a > other.b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean startsAfterNonDisjoint(Interval other) {
/* 112 */     return (this.a > other.a && this.a <= other.b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean disjoint(Interval other) {
/* 117 */     return (startsBeforeDisjoint(other) || startsAfterDisjoint(other));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean adjacent(Interval other) {
/* 122 */     return (this.a == other.b + 1 || this.b == other.a - 1);
/*     */   }
/*     */   
/*     */   public boolean properlyContains(Interval other) {
/* 126 */     return (other.a >= this.a && other.b <= this.b);
/*     */   }
/*     */ 
/*     */   
/*     */   public Interval union(Interval other) {
/* 131 */     return of(Math.min(this.a, other.a), Math.max(this.b, other.b));
/*     */   }
/*     */ 
/*     */   
/*     */   public Interval intersection(Interval other) {
/* 136 */     return of(Math.max(this.a, other.a), Math.min(this.b, other.b));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Interval differenceNotProperlyContained(Interval other) {
/* 145 */     Interval diff = null;
/*     */     
/* 147 */     if (other.startsBeforeNonDisjoint(this)) {
/* 148 */       diff = of(Math.max(this.a, other.b + 1), this.b);
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 153 */     else if (other.startsAfterNonDisjoint(this)) {
/* 154 */       diff = of(this.a, other.a - 1);
/*     */     } 
/* 156 */     return diff;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 161 */     return this.a + ".." + this.b;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\Interval.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */