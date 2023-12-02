package application.Admin;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class AdminMainUI {
	public Scene getScene() {
        Parent root;
        Scene s = null;
		try {
			root = FXMLLoader.load(getClass().getResource("AdminMainUI.fxml"));
			s = new Scene(root, 1280, 720);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return s;
      
    }

}
