package Client.Panel;

import Client.ClientConnect;
import Core.AuthenticationRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class AuthPanel implements Initializable {
    public TextField usernameTF;
    public PasswordField passwordPF;
    public Button connectBtn;

    public static boolean auth = false;

    public static Stage stage;

    static AuthPanel authPanel;

    public void start() throws IOException, InterruptedException {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);


        Parent root = FXMLLoader.load(getClass().getResource("/Client/Panel/AuthWindows.fxml"));
        stage.setTitle("Cloud Authentification");
        stage.setScene(new Scene(root));
        stage.show();

    }

    public static AuthPanel getAuthPanel() {
        if (authPanel == null) {
            authPanel = new AuthPanel();
        }
        return authPanel;
    }

    private void authentication(String name, String pass) throws IOException {

        log.debug("отправка запроса на авторизацию");
        ClientConnect.getOs().writeObject(new AuthenticationRequest(name, pass));

        ClientConnect.getOs().flush();
    }

    public void btnConnectAction(ActionEvent actionEvent) throws IOException, InterruptedException {

        log.debug("создание запроса на авторизацию");
        authentication(usernameTF.getText(), passwordPF.getText());


    }

    public void setAuth(boolean auth) {
        this.auth = auth;
        if (auth) {
            log.debug("изменение булеан авторизации на true");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.close();
                }
            });
        } else {
            log.debug("изменение булеан авторизации на false");
        }
    }

    public boolean getAuth() {
        return auth;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
