package Core;

public class FileRequest implements Message {




    @Override
    public MessageType getType() {
        return MessageType.FILE_REQUEST;
    }
}
