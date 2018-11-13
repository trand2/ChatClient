import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class ChatClient extends Application {

    /**
     * IO Stream.
     */
    static DataOutputStream toServer = null;

    /**
     * IO Stream.
     */
    DataInputStream fromServer = null;


    private static int portNumber;
    private static String userName;

    @Override
    public void start(Stage stage) {

        BorderPane fieldPane = new BorderPane();
        fieldPane.setPadding(new Insets(5, 5, 5, 5));
        fieldPane.setLeft(new Label("Enter text: "));

        TextField field = new TextField();
        field.setAlignment(Pos.BOTTOM_LEFT);
        fieldPane.setCenter(field);

        BorderPane mainPane = new BorderPane();
        TextArea area = new TextArea();
        area.setEditable(false);
        mainPane.setCenter(new ScrollPane(area));
        mainPane.setTop(fieldPane);

        Scene scene = new Scene(mainPane, 450, 200);
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();


        try {
            Socket socket = new Socket("localhost", portNumber);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
            toServer.writeUTF("connect" + userName);
        } catch (Exception e) {
            area.appendText(e.toString() + '\n');
        }

        field.setOnAction(e -> {
            try {
                String normal = field.getText();

                toServer.writeUTF(userName + ": " + normal);
                toServer.flush();

                String response = fromServer.readUTF();

                area.appendText(response);
            } catch (Exception e2) {
                area.appendText(e2.toString() + '\n');
            }
        });

    }

    public static void main(String[] args) {

        userName = "Anonymous";
        portNumber = 4688;

        if(args.length > 0) {
            userName = args[0];
            if (args.length > 1) {
                portNumber = Integer.parseInt(args[1]);
            }
        }

        Application.launch(args);
    }


}
