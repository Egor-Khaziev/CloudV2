package Core;

import lombok.Data;

@Data
public class Authentification implements Message {

    boolean auth = false;

    @Override
    public MessageType getType() {
        return MessageType.AUTH;
    }
}
