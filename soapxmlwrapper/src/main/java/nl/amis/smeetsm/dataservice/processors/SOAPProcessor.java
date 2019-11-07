package nl.amis.smeetsm.dataservice.processors;

import nl.amis.smeetsm.dataservice.utils.WSHelper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.stream.InputStreamCache;
import org.apache.camel.util.MessageHelper;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;

@Component
public class SOAPProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object input = exchange.getIn().getBody();
        if (!(input instanceof InputStreamCache)) {
            throw new UnsupportedOperationException("Input of class is: "+input.getClass().getName()+". Only InputStreamCache is supported by this processor");
        }
        InputStreamCache is = (InputStreamCache) input;
        Document doc = WSHelper.newDocumentFromInputStream(is);
        Element root = doc.getDocumentElement();
        String envelopNS = root.getNamespaceURI();
        String protocol;
        if (envelopNS.equals(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE)) {
            protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
        } else if (envelopNS.equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
            protocol = SOAPConstants.SOAP_1_2_PROTOCOL;
        } else {
            throw new UnsupportedOperationException("SOAP Envelop namespace " + envelopNS + " did not equal the SOAP 1.1 namespace: " + SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE + " or the SOAP 1.2 namespace: " + SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        }
        MessageHelper.resetStreamCache(exchange.getIn());
        SOAPMessage request = WSHelper.initSOAPMessage(is, protocol);
        exchange.getIn().setHeader("X-MS-SOAPPROTOCOL",protocol);
        if (exchange.getIn().getHeader("SOAPAction") == null) {
            String SOAPOperationFromHeader=WSHelper.getOperationFromSOAPHdr(request.getSOAPHeader());
            if (SOAPOperationFromHeader != null) {
                exchange.getIn().setHeader("SOAPAction",SOAPOperationFromHeader);
            } else {
                throw new UnsupportedOperationException("SOAPOperation not present in SOAPAction HTTP header and not in SOAPHeader in message. The SOAPAction is required for routing purposes");
            }
        }
        exchange.getIn().setBody(request);
    }
}
