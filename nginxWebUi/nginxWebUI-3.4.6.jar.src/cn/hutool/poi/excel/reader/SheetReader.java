package cn.hutool.poi.excel.reader;

import org.apache.poi.ss.usermodel.Sheet;

@FunctionalInterface
public interface SheetReader<T> {
  T read(Sheet paramSheet);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\reader\SheetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */