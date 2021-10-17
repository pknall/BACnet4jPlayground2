package com.github.pknall;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.event.DeviceEventHandler;
import com.serotonin.bacnet4j.event.DeviceEventListener;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.Service;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.*;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.MessagePriority;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;

// https://forum.mango-os.com/topic/167/simple-examples/6

public class ApplicationTest {

    static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        // TODO:  Needed Arguments (5): Network Number, Port, IP Address, Subnet Mask, Device ID
        Hashtable<Long, String> ObjectIDs = new Hashtable<Long, String>();

        IpNetworkBuilder ipNetworkBuilder = new IpNetworkBuilder();
        ipNetworkBuilder.withLocalNetworkNumber(2500);
        ipNetworkBuilder.withPort(47808);

        // https://stackoverflow.com/questions/3229860/what-is-the-meaning-of-so-reuseaddr-setsockopt-option-linux
        // https://docs.oracle.com/javase/6/docs/api/java/net/ServerSocket.html#setReuseAddress%28boolean%29
        ipNetworkBuilder.withReuseAddress(false);

        // This is the IP Address of the machine you're on (don't do this on the host machine)
        ipNetworkBuilder.withLocalBindAddress("192.168.168.153");

        // Only use one of the next two
        //ipNetworkBuilder.withSubnet("192.168.1.0", 24);

        ipNetworkBuilder.withBroadcast("192.168.168.255", 24);

        Network network = ipNetworkBuilder.build();
        Transport transport = new DefaultTransport(network);

        LocalDevice localDevice = new LocalDevice(1234, transport);

        localDevice.initialize();


        try {
            localDevice.getEventHandler().addListener(new DeviceEventListener() {
                @Override
                public void listenerException(Throwable throwable) {

                }

                @Override
                public void iAmReceived(RemoteDevice remoteDevice) {
                    //LOG.debug("I-AM Received: Adding {} to queue: {}", remoteDevice.toString(), localDevice.getRemoteDevices().size());

                    if (ObjectIDs.containsKey(new Long(remoteDevice.getInstanceNumber()))) {
                        System.out.print("DUPLICATE: ");
                    }
                    else
                    {
                        System.out.print("---------: ");
                        ObjectIDs.put(new Long(remoteDevice.getInstanceNumber()), remoteDevice.getAddress().toString());
                    }
                    System.out.println(remoteDevice.getInstanceNumber() + ","
                            + remoteDevice.getAddress().getNetworkNumber() + ","
                            + remoteDevice.getAddress().getMacAddress());
                }

                @Override
                public boolean allowPropertyWrite(Address address, BACnetObject baCnetObject, PropertyValue propertyValue) {
                    return false;
                }

                @Override
                public void propertyWritten(Address address, BACnetObject baCnetObject, PropertyValue propertyValue) {

                }

                @Override
                public void iHaveReceived(RemoteDevice remoteDevice, RemoteObject remoteObject) {

                }

                @Override
                public void covNotificationReceived(UnsignedInteger unsignedInteger, ObjectIdentifier objectIdentifier, ObjectIdentifier objectIdentifier1, UnsignedInteger unsignedInteger1, SequenceOf<PropertyValue> sequenceOf) {

                }

                @Override
                public void eventNotificationReceived(UnsignedInteger unsignedInteger, ObjectIdentifier objectIdentifier, ObjectIdentifier objectIdentifier1, TimeStamp timeStamp, UnsignedInteger unsignedInteger1, UnsignedInteger unsignedInteger2, EventType eventType, CharacterString characterString, NotifyType notifyType, Boolean aBoolean, EventState eventState, EventState eventState1, NotificationParameters notificationParameters) {

                }

                @Override
                public void textMessageReceived(ObjectIdentifier objectIdentifier, Choice choice, MessagePriority messagePriority, CharacterString characterString) {

                }

                @Override
                public void synchronizeTime(Address address, DateTime dateTime, boolean b) {

                }

                @Override
                public void requestReceived(Address address, Service service) {

                }
            });
            localDevice.sendGlobalBroadcast(new WhoIsRequest(0, 5000000));

            Thread.sleep(10000);
        }
        catch (Exception ex) {

        }
        finally {
            localDevice.terminate();
        }

    }

}

