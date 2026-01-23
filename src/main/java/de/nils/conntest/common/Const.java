package de.nils.conntest.common;

public class Const
{
    public static class GUI
    {
        public static final String FXML_FILE_PATH = "/fxml/GUI.fxml";
    }

    public static class Event
    {
        public static final String SERVER_PORT_KEY = "Port";
        public static final String CLIENT_PORT_KEY = "Port";
        public static final String CLIENT_ADDRESS_KEY = "Address";
        public static final String START_PORT_KEY = "StartPort";
        public static final String END_PORT_KEY = "EndPort";
        public static final String OPEN_PORTS_KEY = "OpenPorts";

        public static final String PORT_SCANNER_PROGRESS_KEY = "Progress";

        public static final String EXPORT_DEST_FILE_KEY = "ExportFile";

        public static final String MESSAGE_KEY = "Message";
        public static final String ALL_MESSAGES_KEY = "AllMessages";

        public static final String CONNECTION_KEY = "NewConnection";
        
        public static final String ERROR_TEXT = "ErrorText";

        public static final String SETTINGS_VALUE = "SettingsValue";
    }

    public static class Settings
    {
        public static final String THEME_KEY = "THEME";
        public static final String ENCODING_KEY = "ENCODING";
    }
}
