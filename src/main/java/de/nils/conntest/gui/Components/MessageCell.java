package de.nils.conntest.gui.Components;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.communication.Message;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MessageCell extends ListCell<Message>
{
    public MessageCell()
    {
        super();
    }

    public String getEncodedString(byte[] bytes)
    {
        return new String(bytes, Charset.availableCharsets().getOrDefault(Model.instance.getSettingsRepo().get(Const.Settings.ENCODING_KEY).getValue(), StandardCharsets.US_ASCII));
    }

    @Override
    public void updateItem(Message message, boolean empty)
    {
        super.updateItem(message, empty);

        if(empty || message == null)
        {
            setText(null);
            setGraphic(null);
        }
        else
        {
            switch(message.messageType())
            {
                case RECEIVED ->
                {
                    HBox hBox = new HBox();
                    Label messageBox = new Label();

                    messageBox.setText("-> " + getEncodedString(message.message()));
                    messageBox.setTooltip(new Tooltip("Source: " + message.source() + System.lineSeparator() + "Type: " + message.messageType()));

                    hBox.getChildren().add(messageBox);
                    setGraphic(hBox);
                }
                case SENT ->
                {
                    HBox hBox = new HBox();
                    Label messageBox = new Label();

                    messageBox.setText("<- " + getEncodedString(message.message()));
                    messageBox.setTooltip(new Tooltip("Source: " + message.source() + System.lineSeparator() + "Type: " + message.messageType()));

                    hBox.getChildren().add(messageBox);
                    setGraphic(hBox);
                }
                case INFORMATION ->
                {
                    BorderPane borderPane = new BorderPane();

                    borderPane.setCenter(new Label(getEncodedString(message.message())));

                    setGraphic(borderPane);
                }
            }

            setText(null);
        }
    }
}
