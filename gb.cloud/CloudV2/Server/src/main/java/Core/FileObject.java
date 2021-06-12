package Core;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class FileObject implements Message {

    private long len;
    private String name;
    private byte[] data;

    public FileObject(Path path) throws IOException {
        len = Files.size(path);
        name = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }

    @Override
    public MessageType getType() {
        return MessageType.FILE;
    }
}
