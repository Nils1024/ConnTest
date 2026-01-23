package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.communication.Message;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.PriorityQueue;

public class FileService implements EventListener
{
    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private final Model model;

    public FileService(Model model)
    {
        this.model = model;
    }

    private void saveMessagesToFile(File file, List<Message> messages)
    {
        try(BufferedWriter writer = Files.newBufferedWriter(file.toPath(), Charset.availableCharsets().get(model.getSettingsRepo().get(Const.Settings.ENCODING_KEY).getValue()), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
        {
            PriorityQueue<Message> orderedMessages = new PriorityQueue<>();
            orderedMessages.addAll(messages);

            for(Message message : orderedMessages)
            {
                writer.write(message.message());
                writer.newLine();
            }

            writer.flush();
        }
        catch (IOException e)
        {
            log.error("Error writing message to file: <{}>", file, e);
        }
    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case CLIENT_EXPORT -> saveMessagesToFile(event.getData(Const.Event.EXPORT_DEST_FILE_KEY), model.getClientMessagesRepo().getAll());
            case SERVER_EXPORT -> saveMessagesToFile(event.getData(Const.Event.EXPORT_DEST_FILE_KEY), model.getServerMessagesRepo().getAll());
        }
    }
}
