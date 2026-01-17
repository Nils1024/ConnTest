package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
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

    private void scan(String address, int start, int end)
    {
        log.debug("Start port scanner for address <{}> from port <{}> to port <{}>", address, start, end);
    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case START_PORT_SCANNER -> scan(event.getData(Const.Event.CLIENT_ADDRESS_KEY), Integer.parseInt(event.getData(Const.Event.START_PORT_KEY)), Integer.parseInt(event.getData(Const.Event.END_PORT_KEY)));
        }
    }
}
