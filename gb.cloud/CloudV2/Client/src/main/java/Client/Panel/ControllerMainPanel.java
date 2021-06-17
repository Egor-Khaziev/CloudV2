package Client.Panel;


import Client.ClientConnect;
import Core.AuthenticationRequest;
import Core.DeleteRequest;
import Core.FileObject;
import Core.FileRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

@Slf4j
public class ControllerMainPanel implements Initializable {

    @FXML
    public VBox ClientPanel, ServerPanel;

    public ControllerPCPanel PanelPC;
    public ControllerServerPanel PanelServer;
    ContextMenu contextMenu = new ContextMenu();

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        PanelPC = (ControllerPCPanel) ClientPanel.getProperties().get("ctrl");
        PanelServer = (ControllerServerPanel) ServerPanel.getProperties().get("ctrl");

        PanelPC.filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                    Path path = Paths.get(PanelPC.pathField.getText()).resolve(PanelPC.filesTable.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        PanelPC.updateList(path);
                    }
                }

                if (event.getButton() == MouseButton.SECONDARY)
                {
                contextMenu(event, PanelPC);
                }
            }
        });
        PanelServer.filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @SneakyThrows
            @Override
            public void handle(MouseEvent event) {
        Path path = Paths.get(PanelServer.pathField.getText()).resolve(PanelServer.filesTable.getSelectionModel().getSelectedItem().getFilename());
        if (Files.isDirectory(path)) {
            PanelServer.sendListRequest(path);
            PanelServer.updateList(path);


                }
                if (event.getButton() == MouseButton.SECONDARY)
                {
                    contextMenu(event, PanelServer);
                }

            }
        });

    }



    private void contextMenu(MouseEvent event, ControllerPanel panel) {


            log.debug("Вызов контекстного меню");

            // Create ContextMenu


            MenuItem copy = new MenuItem("Copy");
            copy.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    copyBtnAction(event);
                }
            });
            MenuItem cut = new MenuItem("Cut and paste");
            cut.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    btnCutAction(event);
                }
            });
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    btnDeleteAction(event);
                }
            });
            contextMenu.getItems().clear();
            contextMenu.getItems().addAll(copy, cut, delete);

            if(panel==PanelServer){
                contextMenu.show(PanelPC.filesTable, event.getScreenX(), event.getScreenY());
            }
            if(panel==PanelPC){
                contextMenu.show(PanelServer.filesTable, event.getScreenX(), event.getScreenY());
            }




        event.consume();
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
            fileCopy(destination, sourcePath);
        } catch (IOException | InterruptedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't copy file", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void fileCopy(ControllerPanel destination, Path sourcePath) throws IOException, InterruptedException {

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

        sleep(100);
        destination.updateList(Paths.get(destination.getCurrentPath()).toAbsolutePath());


    }

    public void btnAboutAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cloud manager [1.0.0.1]", ButtonType.OK);        alert.showAndWait();
    }


    public void btnDeleteAction(ActionEvent actionEvent) {


        Path sourcePath = null;


        if (PanelPC.getSelectedFilename() == null && PanelServer.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No file selected", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        ControllerPanel source = null, destination = null;
        if (PanelPC.getSelectedFilename() != null) {
            source = PanelPC;

        }
        if (PanelServer.getSelectedFilename() != null) {
            source = PanelServer;

        }

        sourcePath = Paths.get(source.getCurrentPath(), source.getSelectedFilename());

        try {
            fileDelete(source, sourcePath);
        } catch (IOException | InterruptedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't delete file", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void fileDelete(ControllerPanel source, Path sourcePath) throws IOException, InterruptedException {

        if(source == PanelPC){
            Files.delete(sourcePath);
            log.debug("файл " + sourcePath + " удален");

        }
        if(source == PanelServer){
            DeleteRequest delFile = new DeleteRequest(sourcePath);
            ClientConnect.getOs().writeObject(delFile);
            log.debug("отправлен запрос на удаление");
            PanelServer.sendListRequest  (Paths.get(source.getCurrentPath()).toAbsolutePath());
        }

        source.updateList(Paths.get(source.getCurrentPath()).toAbsolutePath());

    }

    public void btnCutAction(ActionEvent actionEvent) {
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
            fileCut(destination, source, sourcePath);
        } catch (IOException | InterruptedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't cut file", ButtonType.OK);
            alert.showAndWait();
        }

    }

    private void fileCut(ControllerPanel destination, ControllerPanel source, Path sourcePath) throws IOException, InterruptedException {

        fileCopy(destination, sourcePath);

        fileDelete(source, sourcePath);

        sleep(100);
        destination.updateList(Paths.get(destination.getCurrentPath()).toAbsolutePath());


    }


}