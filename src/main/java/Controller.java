import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Controller {

    @FXML
    TextArea textArea;
    @FXML
    TextField textField;

    private static Client client;

    public Controller() {
        client = new Client(this);
    }

    public void sendBtnClick() {
        if (!textField.getText().isEmpty()) {
            client.sendMessage(textField.getText());
        }
        textField.clear();
        textField.requestFocus();
    }

    public void textFieldEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (!textField.getText().isEmpty()) {
                client.sendMessage(textField.getText());
                textField.clear();
            }
        }
    }

    public void appendToTextArea(String message) {
        if (!message.isEmpty()) {
            textArea.appendText((textArea.getText().isEmpty() ? "" : "\n") + message);
        }
    }

    public static void onStageClose() {
        client.closeConnection();
    }
}
