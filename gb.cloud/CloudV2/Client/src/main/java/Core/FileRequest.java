package Core;

import java.nio.file.Path;

public class FileRequest implements Message {

    private String path;

    public FileRequest(Path sourcePath) {
        this.path = sourcePath.toString();
    }

    public String getPath() {
        return path;
    }

    @Override
    public MessageType getType() {
        return MessageType.FILE_REQUEST;
    }

}
