/*    */ package cn.hutool.bloomfilter;
/*    */ 
/*    */ import cn.hutool.bloomfilter.filter.DefaultFilter;
/*    */ import cn.hutool.bloomfilter.filter.ELFFilter;
/*    */ import cn.hutool.bloomfilter.filter.JSFilter;
/*    */ import cn.hutool.bloomfilter.filter.PJWFilter;
/*    */ import cn.hutool.bloomfilter.filter.SDBMFilter;
/*    */ import cn.hutool.core.util.NumberUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BitMapBloomFilter
/*    */   implements BloomFilter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private BloomFilter[] filters;
/*    */   
/*    */   public BitMapBloomFilter(int m) {
/* 30 */     long mNum = NumberUtil.div(String.valueOf(m), String.valueOf(5)).longValue();
/* 31 */     long size = mNum * 1024L * 1024L * 8L;
/*    */     
/* 33 */     this.filters = new BloomFilter[] { (BloomFilter)new DefaultFilter(size), (BloomFilter)new ELFFilter(size), (BloomFilter)new JSFilter(size), (BloomFilter)new PJWFilter(size), (BloomFilter)new SDBMFilter(size) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BitMapBloomFilter(int m, BloomFilter... filters) {
/* 49 */     this(m);
/* 50 */     this.filters = filters;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean add(String str) {
/* 60 */     boolean flag = false;
/* 61 */     for (BloomFilter filter : this.filters) {
/* 62 */       flag |= filter.add(str);
/*    */     }
/* 64 */     return flag;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean contains(String str) {
/* 75 */     for (BloomFilter filter : this.filters) {
/* 76 */       if (!filter.contains(str)) {
/* 77 */         return false;
/*    */       }
/*    */     } 
/* 80 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\BitMapBloomFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */