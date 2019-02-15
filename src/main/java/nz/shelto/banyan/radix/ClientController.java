package nz.shelto.banyan.radix;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
public class ClientController {

    private Random random = new Random();

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientWaitingRepository clientWaitingRepository;


    @GetMapping(path="/register")
    public @ResponseBody Client register(final HttpServletRequest request) {
        final Client client = new Client();
        final int firstWordIndex = random.nextInt((int)Main.LINE_NUM - 1) + 1;
        final int suffixNumber = (int)(Math.random() * 100);
        final String handle = String.format("%s%s%d", Main.FILE_CONTENTS.get(firstWordIndex), Main.FILE_CONTENTS.get(random.nextInt(firstWordIndex -1)), suffixNumber);
        client.setHandle(handle);
        client.setSecret(UUID.randomUUID().toString());
        client.setIpAddr(createAddrFromRequest(request));
        clientRepository.save(client);
        return client;
    }

    @GetMapping(path="/ping")
    public @ResponseBody Client ping(@RequestParam("handle") final String handle, @RequestParam("secret") final String secret, final HttpServletRequest request) {
        final Client client = clientRepository.findById(handle).get();
        if(!secret.equals(client.getSecret())) {
            return new Client();
        }
        final String addr = createAddrFromRequest(request);
        if(client.getIpAddr().equals(addr)) {
            return client;
        }
        client.setIpAddr(addr);
        clientRepository.save(client);
        return client;
    }

    @GetMapping(path="/request")
    public @ResponseBody Client request(@RequestParam("requesterHandle") final String requesterHandle, @RequestParam("requestingHandle") final String requestingHandle, @RequestParam("secret") final String secret, final HttpServletRequest request) {
        final Client requesterClient = clientRepository.findById(requesterHandle).get();
        if(!requesterClient.getSecret().equals(secret)){
            return new Client();
        }
        final Client requestingClient = copyClient(clientRepository.findById(requestingHandle).get());
        requestingClient.setSecret("NO_SECRET");
        if(!requestingClient.isBroadcasting()) {
            requestingClient.setIpAddr("NO_BROAD");
        }
        final ClientWaiting clientWaiting = new ClientWaiting();
        clientWaiting.setRequesterHandle(requesterHandle);
        clientWaiting.setRequestingHandle(requestingHandle);
        clientWaitingRepository.save(clientWaiting);
        return requestingClient;
    }


    @GetMapping(path="/broadcast")
    public @ResponseBody List<Client> broadcast(@RequestParam("handle") final String handle, @RequestParam("secret") final String secret, final HttpServletRequest request) {
        final Client client = clientRepository.findById(handle).get();
        if(!secret.equals(client.getSecret())) {
            return new ArrayList<>();
        }
        client.setBroadcasting(true);
        clientRepository.save(client);
        final Iterable<ClientWaiting> clientsWaiting = clientWaitingRepository.findAll();
        final List<Client> clientWaitingList = new ArrayList<>();
        for(final ClientWaiting c : clientsWaiting) {
            if(!c.getRequestingHandle().equals(handle)){
                continue;
            }
            final Client waitingClient = copyClient(clientRepository.findById(c.getRequesterHandle()).get());
            waitingClient.setSecret("NO_SECRET");
            clientWaitingList.add(waitingClient);
        }
        return clientWaitingList;
    }

    private String createAddrFromRequest(final HttpServletRequest request) {
        return String.format("%s:%s", request.getRemoteAddr(), request.getRemotePort());
    }

    private Client copyClient(final Client client) {
        final Client emptyClient = new Client();
        emptyClient.setSecret(client.getSecret());
        emptyClient.setHandle(client.getHandle());
        emptyClient.setBroadcasting(client.isBroadcasting());
        emptyClient.setIpAddr(client.getIpAddr());
        return emptyClient;
    }




}
