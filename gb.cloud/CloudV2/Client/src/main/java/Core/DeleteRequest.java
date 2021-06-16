package Core;

import java.nio.file.Path;

public class DeleteRequest implements Message {

    private String path;

    public DeleteRequest(Path sourcePath) {
        this.path = sourcePath.toString();
    }

    public String getPath() {
        return path;
    }

    @Override
    public MessageType getType() {
        return MessageType.DELETE_REQUEST;
    }

}
