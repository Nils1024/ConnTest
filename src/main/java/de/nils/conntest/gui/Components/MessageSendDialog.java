package de.nils.conntest.gui.Components;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class MessageSendDialog extends Dialog<String>
{
    public MessageSendDialog()
    {
        super();

        setTitle("New Message");
        setHeaderText("Send a new message");

        ButtonType sendFileButtonType = new ButtonType("Send File", ButtonBar.ButtonData.LEFT);
        ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.FINISH);
        getDialogPane().getButtonTypes().addAll(sendFileButtonType, sendButtonType, ButtonType.CANCEL);

        Node sendButton = getDialogPane().lookupButton(sendButtonType);
        sendButton.setDisable(true);

        TextArea textArea = new TextArea();

        textArea.textProperty().addListener((observable, oldValue, newValue) ->
                sendButton.setDisable(textArea.getText().isEmpty()));

        getDialogPane().setContent(textArea);

        Platform.runLater(textArea::requestFocus);

        setResultConverter(dialogBtn ->
        {
            if(dialogBtn == sendButtonType)
            {
                return textArea.getText();
            }
            else if(dialogBtn == sendFileButtonType)
            {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(getOwner());

                if(selectedFile == null)
                {
                    return null;
                }

                try
                {
                    return new String(Files.readAllBytes(selectedFile.toPath()));
                }
                catch (IOException e)
                {
                    return null;
                }
            }

            return null;
        });
    }
}
