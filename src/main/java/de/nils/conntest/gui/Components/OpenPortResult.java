package de.nils.conntest.gui.Components;

public class OpenPortResult
{
    private int port;
    private String service; // z.B. "Offen" oder "Geschlossen"

    public OpenPortResult(int port, String service)
    {
        this.port = port;
        this.service = service;
    }

    public int getPort()
    {
        return port;
    }

    public String getService()
    {
        return service;
    }
}
