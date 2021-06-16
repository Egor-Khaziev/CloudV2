package Core;


public class ListRequest implements Message {

    String dirPath;

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }
    public ListRequest() {}

    public ListRequest(String dirPath) {
        this.dirPath = dirPath;
    }

    @Override
    public MessageType getType() {
        return MessageType.LIST_REQUEST;
    }
}

