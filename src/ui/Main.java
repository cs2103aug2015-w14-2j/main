package ui;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ui.view.OverviewController; 

//@@author A0133888N
public class Main extends Application {
	
    private Stage primaryStage;
    private BorderPane rootLayout;// empty
    private AnchorPane overview;

	@Override
	public void start(Stage primaryStage) {
		assert primaryStage != null;
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Flexi-List");

        initRootLayout();

        showOverview();
		
	}
	
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            assert rootLayout != null;

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setHeight(705);
            primaryStage.setWidth(600);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Shows the overview inside the root layout.
     */
    public void showOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/Overview.fxml"));// change to ui.class
            overview = (AnchorPane) loader.load();
            // Give the controller access to the main app
            OverviewController controller = loader.getController();
            controller.setMainApp(this);
            controller.initialize();
            
            rootLayout.setCenter(overview);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

	public static void main(String[] args) {
		launch(args);
	}
}
