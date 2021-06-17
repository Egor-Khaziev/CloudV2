package Client;

import Client.Panel.AuthPanel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Client extends Application {

    String cloudWindow = "/Client/Panel/mainPanel.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientConnect.getClientConnect();


        Parent root = FXMLLoader.load(getClass().getResource(cloudWindow));
        primaryStage.setTitle("Cloud Manager");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


        AuthPanel.getAuthPanel().start();

        log.debug("проверка на аутентификацию при закрытии");
        AuthPanel.stage.setOnCloseRequest(event -> {
            if (!AuthPanel.getAuthPanel().getAuth()) {
                Platform.exit();
            }

        });
    }

    public void openWindow(String winName, Stage primaryStage) throws IOException {
        primaryStage.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(winName));
        Parent root = loader.getRoot();
        primaryStage.setTitle("Cloud Manager");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void Authentication(Stage primaryStage) throws IOException {


        openWindow("/Client/Panel/AuthWindows.fxml", primaryStage);


    }

    public static void main(String[] args) {
        launch(args);
    }
}
