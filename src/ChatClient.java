import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Chat Client Class.
 */
public class ChatClient extends Application {

    /**
     * Message Reader for Chat Client.
     */
    private ChatClient.MessageReader mr;

    /**
     * Text Area Initializer.
     */
    private TextArea area;

    /**
     * Text Field Initializer.
     */
    private TextField field;

    /**
     * Socket Initializer.
     */
    private Socket socket;

    /**
     * Scanner Initializer.
     */
    private Scanner input;

    /**
     * Print Writer Initializer.
     */
    private PrintWriter output;

    /**
     * Username Initializer.
     */
    private static String userName;

    /**
     * Port number Initializer.
     */
    private static int portNumber;

    /**
     * Padding number.
     */
    private static final int PADDING = 5;

    /**
     * Preferred width number.
     */
    private static final int PREF_WIDTH = 600;

    /**
     * Preferred height number.
     */
    private static final int PREF_HEIGHT = 400;

    /**
     * scene width number.
     */
    private static final int SCENE_WIDTH = 850;

    /**
     * scene height number.
     */
    private static final int SCENE_HEIGHT = 400;

    /**
     * Default port number.
     */
    private static final int DEFAULT_PORT = 4688;

    @Override
    /**
     * start method to start JavaFX
     */
    public void start(final Stage stage) {

        BorderPane fieldPane = new BorderPane();
        fieldPane.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        fieldPane.setLeft(new Label("Enter text: "));

        field = new TextField();
        field.setAlignment(Pos.BOTTOM_LEFT);
        fieldPane.setCenter(field);

        BorderPane mainPane = new BorderPane();
        area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
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
        Scene scene = new Scene(mainPane, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setTitle("Client " + userName);
        stage.setScene(scene);
        stage.show();

        connect();

    }

    /**
     * Main function.
     * @param args args
     */
    public static void main(final String[] args) {

        userName = "Anonymous";
        portNumber = DEFAULT_PORT;

        if (args.length > 0) {
            userName = args[0];
            if (args.length > 1) {
                portNumber = Integer.parseInt(args[1]);
            }
        }

        launch(args);
    }


    /**
     * connect method for connecting to server.
     */
    void connect() {
        try {
            this.socket = new Socket("localhost", portNumber);
            this.input = new Scanner(this.socket.getInputStream());
            this.output = new PrintWriter(this.socket.getOutputStream(), true);
            this.mr = new ChatClient.MessageReader();
            this.mr.start();
            this.output.println("connect " + userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * disconnect method for disconnecting from server.
     */
    void disconnect() {
        this.output.println("disconnect " + userName);
        this.mr.disconnect();

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    /**
     * send message to server.
     */
    void sendMessage() {
        String text = this.field.getText();
        this.addMessage("Me: " + text + "\n");
        this.output.println(text);
        this.field.setText("");
    }

    /**
     * add message to text field.
     * @param msg msg
     */
    synchronized void addMessage(final String msg) {
        this.area.appendText(msg);
    }

    /**
     * Message reader that extends the thread class.
     */
    class MessageReader extends Thread {
        /**
         * done initializer.
         */
        private boolean done = false;

        /**
         * disconnect from server.
         */
        void disconnect() {
            this.done = true;
        }

        /**
         * run method.
         */
        public void run() {
            while (!this.done) {
                String msg = this.read();
                if (msg != null) {
                    ChatClient.this.addMessage(msg + "\n");
                }
            }

        }
        /**
         * read method.
         * @return null null
         */
        String read() {
            if (ChatClient.this.input.hasNextLine()) {
                return ChatClient.this.input.nextLine();
            }
            return null;
        }
    }
}


