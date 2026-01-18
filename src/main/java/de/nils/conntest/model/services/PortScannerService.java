package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

        int poolSize = 500;
        int timeOut = 500;

        ConcurrentLinkedQueue<Object> openPorts = new ConcurrentLinkedQueue<>();

        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
            ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor())
        {
            timer.scheduleAtFixedRate(() ->
            {
                log.debug("Open ports: {}", openPorts.size());
            }, 0, 1000, TimeUnit.MILLISECONDS);

            AtomicInteger port = new AtomicInteger(start);
            while(port.get() < end)
            {
                final int currentPort = port.getAndIncrement();
                executorService.submit(() ->
                {
                    try
                    {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(address, currentPort), timeOut);
                        socket.close();
                        openPorts.add(currentPort);
                        log.debug("Port {} is open", currentPort);
                    }
                    catch (IOException e)
                    {
                        log.trace("Port {} is closed", currentPort);
                    }
                });
            }
        }

        EventQueue.getInstance().addEvent(new Event(EventType.PORT_SCANNER_FINISHED, System.currentTimeMillis(), null));
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
