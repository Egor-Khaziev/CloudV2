package Client.Panel;

import java.nio.file.Path;

public interface ControllerPanel {

    String getCurrentPath();

    String getSelectedFilename();

    void updateList(Path path);

}
