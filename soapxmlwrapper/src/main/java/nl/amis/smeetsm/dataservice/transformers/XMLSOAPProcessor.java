package nl.amis.smeetsm.dataservice.transformers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;

@Component
public class XMLSOAPProcessor implements Processor {

    private SOAPMessage XMLtoSOAP(Document doc,String protocol) throws SOAPException {
        MessageFactory myMessageFactory = MessageFactory.newInstance(protocol);
        SOAPMessage soapMessage = myMessageFactory.createMessage();
        SOAPBody soapBody = soapMessage.getSOAPBody();
        SOAPBodyElement docElement = soapBody.addDocument(doc);
        return soapMessage;
    }

    private static String soapMessageToString(SOAPMessage soapmessage) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapmessage.writeTo(out);
            return new String(out.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Document myDoc = exchange.getIn().getBody(Document.class);
        String protocol= (String) exchange.getIn().getHeader("X-MS-SOAPPROTOCOL");
        if (protocol == null) {
            protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
        }
        exchange.getIn().setBody(soapMessageToString(XMLtoSOAP(myDoc,protocol)));
    }
}
