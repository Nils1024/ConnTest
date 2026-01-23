package de.nils.conntest.model.services;

import com.google.gson.Gson;
import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.daos.Port;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PortScannerService implements EventListener
{
        private static final Logger log = LoggerFactory.getLogger(PortScannerService.class);

    private final Model model;
    private final Map<Integer, String> knownPorts;

    public PortScannerService(Model model)
    {
        this.model = model;
        knownPorts = new HashMap<>();

        Gson gson = new Gson();
        Port[] portsReadFromJson = gson.fromJson(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getResourceAsStream("/ports.json"))), Port[].class);

        for(Port port : portsReadFromJson)
        {
            knownPorts.put(port.port(), port.service());
        }
    }

    private void scan(String address, int start, int end)
    {
        log.debug("Start port scanner for address <{}> from port <{}> to port <{}>", address, start, end);

        if(start > end)
        {
            EventQueue.getInstance().addEvent(
                    new Event(EventType.ERROR,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.ERROR_TEXT, "The start port is bigger than the end port")));
            EventQueue.getInstance().addEvent(
                    new Event(EventType.PORT_SCANNER_FINISHED,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.OPEN_PORTS_KEY,
                                    Map.of())));
            return;
        }
        else if(end > 65535)
        {
            EventQueue.getInstance().addEvent(
                    new Event(EventType.ERROR,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.ERROR_TEXT, "The end port is bigger than 65535")));
            EventQueue.getInstance().addEvent(
                    new Event(EventType.PORT_SCANNER_FINISHED,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.OPEN_PORTS_KEY,
                                    Map.of())));
            return;
        }
        else if(start < 1)
        {
            EventQueue.getInstance().addEvent(
                    new Event(EventType.ERROR,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.ERROR_TEXT, "The start port is smaller than 1")));
            EventQueue.getInstance().addEvent(
                    new Event(EventType.PORT_SCANNER_FINISHED,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.OPEN_PORTS_KEY,
                                    Map.of())));
            return;
        }

        int timeOut = 1000;

        ConcurrentLinkedQueue<Integer> openPorts = new ConcurrentLinkedQueue<>();

        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
            ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor())
        {
            AtomicInteger port = new AtomicInteger(start);

            timer.scheduleAtFixedRate(() ->
            {
                float percentage = 1.23f;
                EventQueue.getInstance().addEvent(new Event(
                        EventType.PORT_SCANNER_RESULT,
                        System.currentTimeMillis(),
                        Map.of(Const.Event.PORT_SCANNER_PROGRESS_KEY, percentage)));
            }, 1000, 2000, TimeUnit.MILLISECONDS);

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
                        log.trace("Port {} is open", currentPort);
                    }
                    catch (IOException e)
                    {
                        log.trace("Port {} is closed", currentPort);
                    }
                });
            }

            timer.shutdown();
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            log.error("Error while scanning ports", e);
        }

        Map<Integer, String> resultMap = new HashMap<>();

        for(Integer port : openPorts)
        {
            resultMap.put(port, knownPorts.get(port));
        }

        EventQueue.getInstance().addEvent(new Event(EventType.PORT_SCANNER_FINISHED, System.currentTimeMillis(), Map.of(Const.Event.OPEN_PORTS_KEY, resultMap)));
    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case START_PORT_SCANNER ->
            {
                try
                {
                    scan(event.getData(Const.Event.CLIENT_ADDRESS_KEY), Integer.parseInt(event.getData(Const.Event.START_PORT_KEY)), Integer.parseInt(event.getData(Const.Event.END_PORT_KEY)));
                }
                catch(NumberFormatException e)
                {
                    EventQueue.getInstance().addEvent(
                            new Event(EventType.ERROR,
                                    System.currentTimeMillis(),
                                    Map.of(Const.Event.ERROR_TEXT, "Please fill everything correctly")));
                    EventQueue.getInstance().addEvent(
                            new Event(EventType.PORT_SCANNER_FINISHED,
                                    System.currentTimeMillis(),
                                    Map.of(Const.Event.OPEN_PORTS_KEY,
                                            Map.of())));
                }
            }
        }
    }
}
