package net.bytle.email;

import jakarta.mail.event.TransportEvent;
import jakarta.mail.event.TransportListener;

class BMailTransportListener implements TransportListener {
  public static BMailTransportListener create() {
    return new BMailTransportListener();
  }

  @Override
  public void messageDelivered(TransportEvent e) {

    System.out.println("Message Delivered: "+e.getMessage());

  }

  @Override
  public void messageNotDelivered(TransportEvent e) {

    System.out.println("Message Not Delivered: "+e.getMessage());

  }

  @Override
  public void messagePartiallyDelivered(TransportEvent e) {

    System.out.println("Message Partially Delivered: "+ e.getMessage());

  }
}
