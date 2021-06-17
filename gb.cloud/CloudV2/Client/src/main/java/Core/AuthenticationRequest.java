package Core;


public class AuthenticationRequest implements Message {

    private String name;
    private String pass;

    public AuthenticationRequest(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    @Override
    public MessageType getType() {
        return MessageType.AUTH_REQUEST;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }
}
