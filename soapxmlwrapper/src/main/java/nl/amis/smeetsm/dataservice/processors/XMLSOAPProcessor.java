package nl.amis.smeetsm.dataservice.processors;

import nl.amis.smeetsm.dataservice.utils.WSHelper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPConstants;

@Component
public class XMLSOAPProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object doc = exchange.getIn().getBody();
        String protocol = (String) exchange.getIn().getHeader("X-MS-SOAPPROTOCOL");
        if (protocol == null) {
            protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
        }
        if (doc instanceof Document) {
            Document myDoc = (Document) doc;
            exchange.getIn().setBody(WSHelper.XMLtoSOAP(myDoc, protocol));
        } else if (doc instanceof String) {
            Document myDoc = WSHelper.newDocumentFromString((String) doc);
            exchange.getIn().setBody(WSHelper.XMLtoSOAP(myDoc, protocol));
        } else {
            throw new UnsupportedOperationException("Input is of class: " + doc.getClass().getName() + ". Only String and Document is supported!");
        }
    }
}
