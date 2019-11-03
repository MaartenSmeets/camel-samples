package nl.amis.smeetsm.dataservice.processors;

import nl.amis.smeetsm.dataservice.utils.WSHelper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.soap.*;

@Component
public class XMLSOAPProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Document myDoc = exchange.getIn().getBody(Document.class);
        String protocol= (String) exchange.getIn().getHeader("X-MS-SOAPPROTOCOL");
        if (protocol == null) {
            protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
        }
        exchange.getIn().setBody(WSHelper.soapMessageToString(WSHelper.XMLtoSOAP(myDoc,protocol)));
    }
}
