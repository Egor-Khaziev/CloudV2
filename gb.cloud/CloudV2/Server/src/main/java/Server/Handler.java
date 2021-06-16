package Server;

import Core.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class Handler implements Runnable, Closeable {

    private final Socket socket;
    private String serverDir = "serverDir/";

    public Handler(Socket socket) {
        this.socket = socket;
        log.debug("handler runing");
    }

    @Override
    public void run() {
        try (ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream())) {
            while (true) {
                Message msg = (Message) is.readObject();
                log.debug("Получено сообщение");
                switch (msg.getType()) {
                    case FILE_REQUEST:
                        log.debug("сообщение - файл-запрос");
                        handlerFileRequest(msg, os);
                        break;
                    case FILE:
                        log.debug("сообщение - файл: " + ((FileObject)msg).getName() + " path: " + ((FileObject)msg).getPath());
                        handlerFileMessage(msg);
                        break;
                    case LIST_REQUEST:
                        log.debug("сообщение - лист-запрос");
                        if (((ListRequest) msg).getDirPath() != null) {
                            serverDir = ((ListRequest) msg).getDirPath()+"/";
                        }

                        os.writeObject((new ListMessage(serverDir)));
                        log.debug("отправлен - лист");
                        os.flush();
                        break;

                }
            }
        } catch (Exception e) {
            log.error("e=", e);
        }
    }

    private void handlerFileRequest(Message msg, ObjectOutputStream os) throws IOException {
        FileObject file = new FileObject(Paths.get(((FileRequest) msg).getPath()));
        os.writeObject(file);
        log.debug("отправлен файл");
    }

    private void handlerFileMessage(Message msg) throws Exception {
        FileObject file = (FileObject) msg;
        Files.write(Paths.get(serverDir + file.getName()), file.getData());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
