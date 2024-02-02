package cn.hutool.poi.excel.reader;

import org.apache.poi.ss.usermodel.Sheet;

@FunctionalInterface
public interface SheetReader<T> {
   T read(Sheet var1);
}
