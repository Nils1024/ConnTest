package de.nils.conntest.model.services;

import de.nils.conntest.model.Model;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortScannerService implements EventListener
{
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final Model model;

    public PortScannerService(Model model)
    {
        this.model = model;
    }

    @Override
    public void handleEvent(Event event)
    {

    }
}
