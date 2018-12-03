package program;

import java.net.URL;
import java.util.ResourceBundle;

import windows.AlertBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable{

    LoginModel loginModel = new LoginModel();

    @FXML
    private Label dbstatus;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginButton;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(this.loginModel.isDatabaseConnected()) {
            this.dbstatus.setText("Connected");
        }
        else this.dbstatus.setText("Disconnected");
    }

    @FXML
    public void Login(ActionEvent event) {
        try {
            if(this.loginModel.isLogin(this.username.getText(), this.password.getText())) {
                Display.username = this.username.getText().toLowerCase();
                Display.password = this.password.getText().toLowerCase();
                Stage stage = (Stage)this.loginButton.getScene().getWindow();
                stage.close();
            }

            else {
                AlertBox.display("Invalid Credentials", "Invalid Username or Password", 300, 150);
            }
        }
        catch(Exception localException){

        }
    }

    public void loginSuccess() {

    }

}
