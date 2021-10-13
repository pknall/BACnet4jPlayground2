package com.github.pknall;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;

// https://forum.mango-os.com/topic/167/simple-examples/6

public class ApplicationTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {

        IpNetworkBuilder ipNetworkBuilder = new IpNetworkBuilder();
        ipNetworkBuilder.withLocalNetworkNumber(2500);
        ipNetworkBuilder.withPort(47808);
        ipNetworkBuilder.withReuseAddress(false);
        ipNetworkBuilder.withLocalBindAddress("192.168.168.153");     // This is the IP Address of the machine you're on (don't do this on the host machine)
        ipNetworkBuilder.withSubnet("255.255.255.0", 24);
        ipNetworkBuilder.withBroadcast("192.168.168.254", 24);

        Network network = ipNetworkBuilder.build();
        Transport transport = new DefaultTransport(network);

        LocalDevice localDevice = new LocalDevice(1234, transport);
        localDevice.initialize();
        try {
            localDevice.sendGlobalBroadcast(new WhoIsRequest(0, 4000000));

            Thread.sleep(1000);

            System.out.println("Results: ");
            for (RemoteDevice d : localDevice.getRemoteDevices()) {
                System.out.println(d.getName() +  " : " + d.getAddress());
            }
        }
        catch (Exception ex) {

        }
        finally {
            //localDevice.terminate();
        }

        System.out.println("Done.");
    }

}

