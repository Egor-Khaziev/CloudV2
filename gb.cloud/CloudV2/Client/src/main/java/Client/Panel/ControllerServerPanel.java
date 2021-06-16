package Client.Panel;


import Client.ClientConnect;
import Core.FileInfo;
import Core.ListRequest;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

@Slf4j
public class ControllerServerPanel implements Initializable, ControllerPanel {

    static List<FileInfo> fileList;
    static int hashFileList;
    static Path path;
    static Path pathRoot;

    public static void setFileList(List<FileInfo> fileList) {
        ControllerServerPanel.fileList = fileList;
    }

    public static void setPath(Path path) {
        ControllerServerPanel.path = path.toAbsolutePath();
        setPathRoot(path);
    }

    public static Path getPathRoot() {
        return pathRoot;
    }

    public static void setPathRoot(Path path) {
        if(pathRoot==null) {
            pathRoot = path.toAbsolutePath();
        }
    }


    @FXML
    public TableView<FileInfo> filesTable;

    @FXML
    public TextField pathField;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(180);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);

        filesTable.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn); //, fileDateColumn);
        filesTable.getSortOrder().add(fileTypeColumn);

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @SneakyThrows
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {

                    Path path = Paths.get(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        sendListRequest(path);
                        updateList(path);
                    }
                }
            }
        });

        hashFileList = fileList.hashCode();

        updateList(path);
    }


    @SneakyThrows
    public void updateList(Path path)  {
        log.debug("updateList run");
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(fileList);

            filesTable.sort();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }

    }
    @SneakyThrows
    public void btnPathUpAction(ActionEvent actionEvent)  {

        if (path.getParent().toAbsolutePath().equals(pathRoot.getParent().toAbsolutePath())){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Это корневой каталог вашего облака", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            log.debug("button UP pushed");
            Path upperPath = Paths.get(pathField.getText()).getParent();

            sendListRequest(upperPath);


            updateList(upperPath);
        }
    }

    protected void sendListRequest(Path path) throws IOException, InterruptedException {

        int i = 0;
        ListRequest listRequest = new ListRequest(path.toString());

        log.debug("List-request created");

        log.debug("List-request send");
        ClientConnect.getOs().writeObject(listRequest);
        ClientConnect.getOs().flush();


        while (hashFileList==fileList.hashCode()&&i<20){
            i++;
            sleep(100);
            if (i>=20){
                log.error("превышено время ожидания ЛИСТА");
            }
        }
        if (hashFileList!=fileList.hashCode()){
            hashFileList=fileList.hashCode();
        }
    }


    public String getSelectedFilename() {
        if (!filesTable.isFocused()) {
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getFilename();
    }

    public String getCurrentPath() {
        return pathField.getText();
    }






}
