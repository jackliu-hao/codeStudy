package cn.hutool.extra.tokenizer;

import java.io.Serializable;

public interface Word extends Serializable {
   String getText();

   int getStartOffset();

   int getEndOffset();
}
