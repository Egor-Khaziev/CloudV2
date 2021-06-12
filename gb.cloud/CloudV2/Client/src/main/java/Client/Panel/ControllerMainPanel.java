package Client.Panel;


import Client.ClientConnect;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ControllerMainPanel {

    @FXML
    public VBox ClientPanel, ServerPanel;

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


    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void copyBtnAction(ActionEvent actionEvent) {
        ControllerPCPanel leftPC = (ControllerPCPanel) ClientPanel.getProperties().get("ctrl");
        ControllerPCPanel rightPC = (ControllerPCPanel) ServerPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No file selected", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        ControllerPCPanel srcPC = null, dstPC = null;
        if (leftPC.getSelectedFilename() != null) {
            srcPC = leftPC;
            dstPC = rightPC;
        }
        if (rightPC.getSelectedFilename() != null) {
            srcPC = rightPC;
            dstPC = leftPC;
        }

        Path srcPath = Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFilename());
        Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPath.getFileName().toString());

        try {
            Files.copy(srcPath, dstPath);
            dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't copy file", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void btnAboutAction(ActionEvent actionEvent) {
    }


}