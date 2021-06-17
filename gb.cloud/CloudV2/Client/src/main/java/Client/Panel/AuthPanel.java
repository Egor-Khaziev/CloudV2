package Client.Panel;

import Client.ClientConnect;
import Core.AuthenticationRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static java.lang.Thread.sleep;

@Slf4j
public class AuthPanel  {
    public TextField usernameTF;
    public PasswordField passwordPF;
    public Button connectBtn;

    static boolean auth = false;

    Stage stage;

    static AuthPanel authPanel;

    public void start() throws IOException, InterruptedException {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);


        Parent root = FXMLLoader.load(getClass().getResource("/Client/Panel/AuthWindows.fxml"));
        stage.setTitle("Cloud Authentification");
        stage.setScene(new Scene(root));
        stage.showAndWait();




    }

    public static AuthPanel getAuthPanel() {
        if(authPanel==null){
            authPanel = new AuthPanel();
        }
        return authPanel;
    }

    private void authentication(String name, String pass) throws IOException {

        log.debug("отправка запроса на авторизацию");
        ClientConnect.getOs().writeObject(new AuthenticationRequest(name,pass));

        ClientConnect.getOs().flush();
    }

    public void btnConnectAction(ActionEvent actionEvent) throws IOException, InterruptedException {

        log.debug("создание запроса на авторизацию");
        authentication(usernameTF.getText(), passwordPF.getText());

        while (true){

            if(auth){
                log.debug("авторизация ободрена");
                stage.close();
                break;}

        }

    }

    public void setAuth(boolean auth){
        this.auth=auth;
        if (auth)
            log.debug("изменение булеан авторизации");
    }

    public boolean getAuth() {
        return auth;
    }
}
