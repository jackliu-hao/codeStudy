package cn.hutool.extra.tokenizer;

import java.io.Serializable;

public interface Word extends Serializable {
  String getText();
  
  int getStartOffset();
  
  int getEndOffset();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\Word.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */