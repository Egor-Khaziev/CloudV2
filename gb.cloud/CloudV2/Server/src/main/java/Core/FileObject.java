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
    private String path;



    public FileObject(Path path) throws IOException {
        this.len = Files.size(path);
        this.name = path.getFileName().toString();
        this.data = Files.readAllBytes(path);
        this.path = path.toString();
    }

    @Override
    public MessageType getType() {
        return MessageType.FILE;
    }
}
