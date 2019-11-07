package nl.amis.smeetsm.dataservice.processors;

import nl.amis.smeetsm.dataservice.utils.WSHelper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import javax.xml.soap.SOAPMessage;

@Component
public class SOAPXMLProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object doc = exchange.getIn().getBody();
        if (!(doc instanceof SOAPMessage)) {
            throw new UnsupportedOperationException("Input is of class: " + doc.getClass().getName() + ". Only SOAPMesage is supported!");
        } else {
            SOAPMessage myMsg = (SOAPMessage) doc;

            exchange.getIn().setBody(WSHelper.SOAPtoXML(myMsg));
        }
    }
}
