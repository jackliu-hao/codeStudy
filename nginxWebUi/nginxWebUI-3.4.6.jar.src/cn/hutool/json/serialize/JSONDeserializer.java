package cn.hutool.json.serialize;

import cn.hutool.json.JSON;

@FunctionalInterface
public interface JSONDeserializer<T> {
  T deserialize(JSON paramJSON);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\serialize\JSONDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */