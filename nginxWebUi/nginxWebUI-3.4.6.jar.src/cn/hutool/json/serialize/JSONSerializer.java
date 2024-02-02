package cn.hutool.json.serialize;

@FunctionalInterface
public interface JSONSerializer<T extends cn.hutool.json.JSON, V> {
  void serialize(T paramT, V paramV);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\serialize\JSONSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */