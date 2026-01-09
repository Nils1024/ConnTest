package de.nils.conntest.model.communication;

import java.util.List;

public record Message(MessageType messageType, byte[] message, long time, Connection source, byte[] rawData) implements Comparable<Message>
{
    @Override
    public int compareTo(Message o)
    {
        return Long.compare(time, o.time());
    }
}
