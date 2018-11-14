import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient extends Application {

    private ChatClient.MessageReader messageReader;
    private TextArea area;
    private TextField field;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private static String userName;
    private static int portNumber;

    @Override
    public void start(Stage stage) {

        BorderPane fieldPane = new BorderPane();
        fieldPane.setPadding(new Insets(5, 5, 5, 5));
        fieldPane.setLeft(new Label("Enter text: "));

        field = new TextField();
        field.setAlignment(Pos.BOTTOM_LEFT);
        fieldPane.setCenter(field);

        BorderPane mainPane = new BorderPane();
        area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefSize(600, 400);
        mainPane.setCenter(new ScrollPane(area));
        mainPane.setBottom(fieldPane);

        Button sendBtn = new Button("Send");
        sendBtn.setOnAction(event -> this.sendMessage());
        Button disconnectBtn = new Button("Disconnect");
        disconnectBtn.setOnAction(event -> this.disconnect());
        ToolBar toolBar = new ToolBar();
        toolBar.setOrientation(Orientation.VERTICAL);
        mainPane.setLeft(toolBar);


        toolBar.getItems().addAll(sendBtn, disconnectBtn);
        Scene scene = new Scene(mainPane, 850, 400);
        stage.setTitle("Client " + userName);
        stage.setScene(scene);
        stage.show();

        connect();

    }

    public static void main(String[] args) {

        userName = "Anonymous";
        portNumber = 4688;

        if (args.length > 0) {
            userName = args[0];
            if (args.length > 1) {
                portNumber = Integer.parseInt(args[1]);
            }
        }

        launch(args);
    }

    void connect() {
        try {
            this.socket = new Socket("localhost", portNumber);
            this.input = new Scanner(this.socket.getInputStream());
            this.output = new PrintWriter(this.socket.getOutputStream(), true);
            this.messageReader = new ChatClient.MessageReader();
            this.messageReader.start();
            this.output.println("connect " + userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void disconnect() {
        this.output.println("disconnect " + userName);
        this.messageReader.disconnect();

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    void sendMessage() {
        String text = this.field.getText();
        this.addMessage("Me: " + text + "\n");
        this.output.println(text);
        this.field.setText("");
    }

    synchronized void addMessage(String msg) {
        this.area.appendText(msg);
    }


    class MessageReader extends Thread {
        boolean done = false;

        void disconnect() {
            this.done = true;
        }

        public void run() {
            while (!this.done) {
                String msg = this.read();
                if (msg != null) {
                    ChatClient.this.addMessage(msg + "\n");
                }
            }

        }

        String read() {
            if (ChatClient.this.input.hasNextLine()) {
                return ChatClient.this.input.nextLine();
            }
            return null;
        }
    }
}


