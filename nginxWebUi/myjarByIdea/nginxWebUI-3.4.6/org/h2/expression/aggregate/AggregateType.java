package org.h2.expression.aggregate;

public enum AggregateType {
   COUNT_ALL,
   COUNT,
   SUM,
   MIN,
   MAX,
   AVG,
   STDDEV_POP,
   STDDEV_SAMP,
   VAR_POP,
   VAR_SAMP,
   ANY,
   EVERY,
   BIT_AND_AGG,
   BIT_OR_AGG,
   BIT_XOR_AGG,
   BIT_NAND_AGG,
   BIT_NOR_AGG,
   BIT_XNOR_AGG,
   HISTOGRAM,
   COVAR_POP,
   COVAR_SAMP,
   CORR,
   REGR_SLOPE,
   REGR_INTERCEPT,
   REGR_COUNT,
   REGR_R2,
   REGR_AVGX,
   REGR_AVGY,
   REGR_SXX,
   REGR_SYY,
   REGR_SXY,
   RANK,
   DENSE_RANK,
   PERCENT_RANK,
   CUME_DIST,
   PERCENTILE_CONT,
   PERCENTILE_DISC,
   MEDIAN,
   LISTAGG,
   ARRAY_AGG,
   MODE,
   ENVELOPE,
   JSON_OBJECTAGG,
   JSON_ARRAYAGG;
}
