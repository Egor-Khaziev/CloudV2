package Client.Panel;


import Core.FileInfo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControllerServerPanel implements Initializable {

    static List<FileInfo> fileList;
    static Path path;
    static Path pathRoot;

    public static void setFileList(List<FileInfo> fileList) {
        ControllerServerPanel.fileList = fileList;
    }

    public static void setPath(Path path) {
        ControllerServerPanel.path = path;
    }

    public static Path getPathRoot() {
        return pathRoot;
    }

    public static void setPathRoot(Path pathRoot) {
        ControllerServerPanel.pathRoot = pathRoot;
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
                        updateList(path);
                    }
                }
            }
        });



        updateList(path);
    }


    @SneakyThrows
    public void updateList(Path path)  {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(fileList);


//        filesTable.getItems().addAll(FileInfo.getList().stream().map(FileInfo::new).collect(Collectors.toList()));

            filesTable.sort();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }

    }
    @SneakyThrows
    public void btnPathUpAction(ActionEvent actionEvent)  {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != pathRoot) {
            updateList(upperPath);
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
