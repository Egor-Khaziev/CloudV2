package Client.Panel;


import Client.ClientConnect;
import Core.FileObject;
import Core.FileRequest;
import Core.ListRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

@Slf4j
public class ControllerMainPanel implements Initializable {

    @FXML
    public VBox ClientPanel, ServerPanel;

    public ControllerPCPanel PanelPC;
    public ControllerServerPanel PanelServer;




    static ControllerMainPanel controllerMP;
    private static ClientConnect clientConnect;

    {
        clientConnect = ClientConnect.getClientConnect();
    }


    private static ControllerMainPanel getControllerMP() {
        if (controllerMP == null) {
            controllerMP = new ControllerMainPanel();
        }
        return controllerMP;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        PanelPC = (ControllerPCPanel) ClientPanel.getProperties().get("ctrl");
        PanelServer = (ControllerServerPanel) ServerPanel.getProperties().get("ctrl");

    }


    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void copyBtnAction(ActionEvent actionEvent) {

        Path sourcePath = null;
        Path destinationPath = null;

        if (PanelPC.getSelectedFilename() == null && PanelServer.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No file selected", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        ControllerPanel source = null, destination = null;
        if (PanelPC.getSelectedFilename() != null) {
            source = PanelPC;
            destination = PanelServer;
        }
        if (PanelServer.getSelectedFilename() != null) {
            source = PanelServer;
            destination = PanelPC;
        }

         sourcePath = Paths.get(source.getCurrentPath(), source.getSelectedFilename());
         destinationPath = Paths.get(destination.getCurrentPath()).resolve(sourcePath.getFileName().toString());

        try {
            fileCopy(destination, sourcePath, destinationPath);
        } catch (IOException | InterruptedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't copy file", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void fileCopy(ControllerPanel destination, Path sourcePath, Path destinationPath) throws IOException, InterruptedException {

        if(destination == PanelPC){
            FileRequest fileRequest = new FileRequest(sourcePath);
            ClientConnect.getOs().writeObject(fileRequest);
            log.debug("отправлен файл-реквест");
        }
        if(destination == PanelServer){
            FileObject file = new FileObject(sourcePath);
            ClientConnect.getOs().writeObject(file);
            log.debug("отправлен файл");
            PanelServer.sendListRequest  (Paths.get(destination.getCurrentPath()).toAbsolutePath());
        }

        destination.updateList(Paths.get(destination.getCurrentPath()).toAbsolutePath());


    }

    public void btnAboutAction(ActionEvent actionEvent) {
    }


    public void btnDeleteAction(ActionEvent actionEvent) {
    }

    public void btnCutAction(ActionEvent actionEvent) {
    }


}