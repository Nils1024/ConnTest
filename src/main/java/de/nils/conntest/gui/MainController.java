package de.nils.conntest.gui;

import de.nils.conntest.common.Const;
import de.nils.conntest.gui.Components.*;
import de.nils.conntest.model.communication.Message;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainController implements Initializable, EventListener
{
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label titleLabel;

    @FXML
    private BorderPane clientBorderPaneBtn;
    @FXML
    private BorderPane serverBorderPaneBtn;
    @FXML
    private BorderPane settingsBorderPaneBtn;
    @FXML
    private BorderPane portScannerBorderPaneBtn;

    @FXML
    private TextField serverPort;
    @FXML
    private TextField clientAddress;
    @FXML
    private TextField clientPort;

    @FXML
    private Button serverStartBtn;
    @FXML
    private Button clientConnectBtn;

    @FXML
    private Button serverMessageBtn;
    @FXML
    private Button clientMessageBtn;

    @FXML
    private ListView<Message> serverMessages;
    @FXML
    private ListView<Message> clientMessages;

    // Content Panes
    @FXML
    private AnchorPane serverPane;
    @FXML
    private AnchorPane clientPane;
    @FXML
    private AnchorPane settingsPane;
    @FXML
    private AnchorPane portScannerPane;

    // Port Scanner related components
    @FXML
    private TextField scannerAddress;
    @FXML
    private TextField scannerStartPort;
    @FXML
    private TextField scannerEndPort;
    @FXML
    private Button scanBtn;
    @FXML
    private TableView<OpenPortResult> portScannerResultTableView;
    @FXML
    private Label scannerProgress;

    // Settings related components
    @FXML
    private ChoiceBox<String> settingThemeChBox;
    @FXML
    private ChoiceBox<String> settingEncodingChBox;

    private final String baseCSS = Objects.requireNonNull(getClass().getResource("/fxml/styles/base.css")).toExternalForm();
    private final Map<String, String> themes = new HashMap<>();
    private BorderPane previousSelectedBtn;
    private boolean serverStarted = false;
    private boolean clientConnected = false;

    public MainController()
    {
        EventQueue.getInstance().addListener(this);
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        serverMessages.setCellFactory(param -> new MessageCell());
        clientMessages.setCellFactory(param -> new MessageCell());

        serverMessages.setSelectionModel(new NoSelectionModel<>());
        clientMessages.setSelectionModel(new NoSelectionModel<>());

        serverMessages.setFocusTraversable(false);
        clientMessages.setFocusTraversable(false);

        // Port Scanner Cell Factories
        portScannerResultTableView
                .getColumns().getFirst().setCellValueFactory(new PropertyValueFactory<>("port"));
        portScannerResultTableView
                .getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("service"));

        doSelectServer();

        // Init settings
        try(InputStream in = getClass().getResourceAsStream("/settings.properties"))
        {
            Properties settings = new Properties();
            settings.load(in);

            for(int i = 0; settings.containsKey("de.nils.conntest.theme." + i + ".name"); i++)
            {
                themes.put(settings.getProperty("de.nils.conntest.theme." + i + ".name"),
                        Objects.requireNonNull(getClass().getResource("/fxml/styles/" + settings.getProperty("de.nils.conntest.theme." + i + ".file"))).toExternalForm());
            }
        }
        catch (IOException e)
        {
            log.error("Failed to load settings.properties", e);
        }

        settingThemeChBox.getItems().addAll(themes.keySet());
        settingThemeChBox.setValue(themes.keySet().stream().findFirst().orElse("NONE"));

        settingEncodingChBox.getItems().addAll(Charset.availableCharsets().keySet());
        settingEncodingChBox.setValue(StandardCharsets.US_ASCII.name());
    }

    @FXML
    public void doSelectClient()
    {
        select(clientBorderPaneBtn, clientPane, "Client");
    }

    @FXML
    public void doSelectServer()
    {
        select(serverBorderPaneBtn, serverPane, "Server");
    }

    @FXML
    public void doSelectPortScanner()
    {
        select(portScannerBorderPaneBtn, portScannerPane, "Port Scanner");
    }

    @FXML
    public void doSelectSettings()
    {
        select(settingsBorderPaneBtn, settingsPane, "Settings");
    }

    public void select(BorderPane btn, AnchorPane contentPane, String title)
    {
        if(previousSelectedBtn != null)
        {
            previousSelectedBtn.setId("");
        }

        btn.setId("selectedBtn");
        previousSelectedBtn = btn;

        for(Node node : contentPane.getParent().getChildrenUnmodifiable())
        {
            node.setVisible(node == contentPane);
        }

        titleLabel.setText(title);
    }

    @FXML
    public void doStartServer()
    {
        if(serverStarted)
        {
            EventQueue.getInstance().addEvent(
                    new Event(EventType.STOP_SERVER,
                            System.currentTimeMillis(),
                            null));
        }
        else
        {
            String port = serverPort.getText();

            EventQueue.getInstance().addEvent(
                    new Event(EventType.START_SERVER,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.SERVER_PORT_KEY, port)));
        }
    }

    @FXML
    public void doSendServerMessage()
    {
        log.debug("New server message button clicked");

        MessageSendDialog messageSendDialog = new MessageSendDialog();
        Optional<String> message = messageSendDialog.showAndWait();

        message.ifPresent(s ->
                EventQueue.getInstance().addEvent(
                    new Event(EventType.SERVER_MESSAGE_SENT,
                        System.currentTimeMillis(),
                        Map.of(Const.Event.MESSAGE_KEY, s))));
    }

    @FXML
    public void doExportServerMessage()
    {
        sendExportEvent(EventType.SERVER_EXPORT);
    }

    @FXML
    public void doStartClient()
    {
        if(clientConnected)
        {
            EventQueue.getInstance().addEvent(
                    new Event(EventType.STOP_CLIENT,
                            System.currentTimeMillis(),
                            null));
        }
        else
        {
            String port = clientPort.getText();
            String address = clientAddress.getText();

            EventQueue.getInstance().addEvent(
                    new Event(EventType.START_CLIENT,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.CLIENT_ADDRESS_KEY, address,
                                    Const.Event.CLIENT_PORT_KEY, port)));
        }
    }

    @FXML
    public void doSendClientMessage()
    {
        log.debug("New client message button clicked");

        MessageSendDialog messageSendDialog = new MessageSendDialog();
        Optional<String> message = messageSendDialog.showAndWait();

        message.ifPresent(s ->
                EventQueue.getInstance().addEvent(
                    new Event(EventType.CLIENT_MESSAGE_SENT,
                        System.currentTimeMillis(),
                        Map.of(Const.Event.MESSAGE_KEY, s))));
    }

    @FXML
    public void doExportClientMessage()
    {
        sendExportEvent(EventType.CLIENT_EXPORT);
    }

    @FXML
    public void doStartPortScanner()
    {
        String address = scannerAddress.getText();
        String startPort = scannerStartPort.getText();
        String endPort = scannerEndPort.getText();

        Map<String, String> payload = Map.of(
                Const.Event.CLIENT_ADDRESS_KEY, address,
                Const.Event.START_PORT_KEY, startPort,
                Const.Event.END_PORT_KEY, endPort);

        EventQueue.getInstance().addEvent(new Event(
                EventType.START_PORT_SCANNER,
                System.currentTimeMillis(),
                payload));

        scanBtn.setDisable(true);
        scannerProgress.setVisible(true);
    }

    @FXML
    public void doChangeTheme()
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put(Const.Event.SETTINGS_VALUE, settingThemeChBox.getValue());

        Event event = new Event(EventType.THEME_CHANGED, System.currentTimeMillis(), payload);

        EventQueue.getInstance().addEvent(event);

        mainBorderPane.getStylesheets().clear();
        mainBorderPane.getStylesheets().addAll(baseCSS, themes.getOrDefault(settingThemeChBox.getValue(), ""));
    }

    @FXML
    public void doChangeEncoding()
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put(Const.Event.SETTINGS_VALUE, settingEncodingChBox.getValue());

        Event event = new Event(EventType.ENCODING_CHANGED, System.currentTimeMillis(), payload);

        EventQueue.getInstance().addEvent(event);
    }

    @FXML
    public void doClearChatHistory()
    {
        //TODO: Implement
    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case SERVER_STARTED -> Platform.runLater(() ->
            {
                serverPort.setDisable(true);
                serverStarted = true;
                serverStartBtn.setText("Stop");
                serverStartBtn.setId("cancelBtn");
                serverMessageBtn.setDisable(false);
            });
            case SERVER_STOPPED -> Platform.runLater(() ->
            {
                serverPort.setDisable(false);
                serverStarted = false;
                serverStartBtn.setText("Start");
                serverStartBtn.setId("initiateBtn");
                serverMessageBtn.setDisable(true);
            });
            case SERVER_MESSAGE_RECEIVED -> addMessageToListView(event, serverMessages);
            case CLIENT_STARTED -> Platform.runLater(() ->
            {
                clientAddress.setDisable(true);
                clientPort.setDisable(true);
                clientConnected = true;
                clientConnectBtn.setText("Disconnect");
                clientConnectBtn.setId("cancelBtn");
                clientMessageBtn.setDisable(false);
            });
            case CLIENT_STOPPED -> Platform.runLater(() ->
            {
                clientAddress.setDisable(false);
                clientPort.setDisable(false);
                clientConnected = false;
                clientConnectBtn.setText("Connect");
                clientConnectBtn.setId("initiateBtn");
                clientMessageBtn.setDisable(true);
            });
            case CLIENT_MESSAGE_RECEIVED -> addMessageToListView(event, clientMessages);
            case PORT_SCANNER_RESULT ->
            {

            }
            case PORT_SCANNER_FINISHED ->
            {
                Platform.runLater(() -> scanBtn.setDisable(false));

                Map<Integer, String> openPorts = event.getData(Const.Event.OPEN_PORTS_KEY);
                System.out.println(openPorts);

                Platform.runLater(() ->
                {
                    portScannerResultTableView.getItems().clear();

                    for(Map.Entry<Integer, String> openPort : openPorts.entrySet())
                    {
                        OpenPortResult row = new OpenPortResult(openPort.getKey(), openPort.getValue());
                        portScannerResultTableView.getItems().add(row);
                    }

                    scannerProgress.setVisible(false);
                });
            }
            case ERROR ->
            {
            	event.mustExist(Const.Event.ERROR_TEXT);
            	
            	Platform.runLater(() ->
            	{
            		ErrorAlert alert = new ErrorAlert(event.getData(Const.Event.ERROR_TEXT));
            		alert.showAndWait();
            	});
            }
            default ->
            {
            }
        }
    }

    private void sendExportEvent(EventType eventType)
    {
        FileChooser fileChooser = new FileChooser();

        File destFile = fileChooser.showSaveDialog(mainBorderPane.getScene().getWindow());

        EventQueue.getInstance().addEvent(new Event(
                eventType,
                System.currentTimeMillis(),
                Map.of(Const.Event.EXPORT_DEST_FILE_KEY, destFile)));
    }

    private void addMessageToListView(Event event, ListView<Message> listView)
    {
        event.mustExist(Const.Event.ALL_MESSAGES_KEY);

        PriorityQueue<Message> messages = new PriorityQueue<>();
        messages.addAll(event.getData(Const.Event.ALL_MESSAGES_KEY));

        Platform.runLater(() ->
        {
            listView.getItems().clear();

            while(!messages.isEmpty())
            {
                listView.getItems().add(messages.poll());
            }
        });
    }
}
