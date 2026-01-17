package de.nils.conntest.model;

import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.repo.ClientMessagesRepo;
import de.nils.conntest.model.repo.ServerMessagesRepo;
import de.nils.conntest.model.repo.SettingsRepo;
import de.nils.conntest.model.services.*;

public class Model
{
    public static Model instance = null;

    private final ServerService serverService;
    private final ClientService clientService;
    private final ConnectionService connectionService;
    private final SettingsService settingsService;
    private final PortScannerService portScannerService;

    private final ServerMessagesRepo serverMessagesRepo;
    private final ClientMessagesRepo clientMessagesRepo;
    private final SettingsRepo settingsRepo;

    public static Model getModel()
    {
        if(instance == null)
        {
            instance = new Model();
        }

        return instance;
    }

    private Model()
    {
        serverService = new ServerService(this);
        clientService = new ClientService(this);
        connectionService = new ConnectionService(this);
        settingsService = new SettingsService(this);
        portScannerService = new PortScannerService(this);

        serverMessagesRepo = new ServerMessagesRepo();
        clientMessagesRepo = new ClientMessagesRepo();
        settingsRepo = new SettingsRepo();

        EventQueue.getInstance().addListener(serverService);
        EventQueue.getInstance().addListener(clientService);
        EventQueue.getInstance().addListener(connectionService);
        EventQueue.getInstance().addListener(settingsService);
    }

    public ServerService getServerService()
    {
        return serverService;
    }

    public ClientService getClientService()
    {
        return clientService;
    }

    public ConnectionService getConnectionService()
    {
        return connectionService;
    }

    public SettingsService getSettingsService()
    {
        return settingsService;
    }

    public PortScannerService getPortScannerService()
    {
        return portScannerService;
    }

    public ServerMessagesRepo getServerMessagesRepo()
    {
        return serverMessagesRepo;
    }

    public ClientMessagesRepo getClientMessagesRepo()
    {
        return clientMessagesRepo;
    }

    public SettingsRepo getSettingsRepo()
    {
        return settingsRepo;
    }
}
