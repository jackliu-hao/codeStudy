package cn.hutool.extra.tokenizer.engine.analysis;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;

public class SmartcnEngine extends AnalysisEngine {
   public SmartcnEngine() {
      super(new SmartChineseAnalyzer());
   }
}
