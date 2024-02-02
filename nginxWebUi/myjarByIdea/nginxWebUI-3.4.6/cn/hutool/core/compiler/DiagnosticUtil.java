package cn.hutool.core.compiler;

import java.util.List;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;

public class DiagnosticUtil {
   public static String getMessages(DiagnosticCollector<?> collector) {
      List<?> diagnostics = collector.getDiagnostics();
      return (String)diagnostics.stream().map(String::valueOf).collect(Collectors.joining(System.lineSeparator()));
   }
}
