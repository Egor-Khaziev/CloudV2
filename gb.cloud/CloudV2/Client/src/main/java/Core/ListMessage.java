package Core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ListMessage implements Message {

    List<FileInfo> files;

    String dirPath;

    public ListMessage(String serverDir) {
        this.files = listCreate(serverDir);
        this.dirPath = serverDir;
        log.debug("создан лист с файл-инфо");
    }

    private List<FileInfo> listCreate(String serverDir) {
        log.debug("создается лист");
        List<FileInfo> list = new ArrayList<>();

        for (File file : new File(serverDir).listFiles()) {
            list.add(new FileInfo(file.toPath()));
            log.debug("добавлен файл-инфо");
        }
        return list;
    }

    public List<FileInfo> getList(){
        return files;
    };

    public Path getDirPath() {
        return Paths.get(dirPath);
    }

    @Override
    public MessageType getType() {
        return MessageType.LIST;
    }

}
