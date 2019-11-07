package nl.amis.smeetsm.dataservice.processors;

import nl.amis.smeetsm.dataservice.utils.WSHelper;
import oracle.sql.CLOB;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPMessage;
import java.io.InputStream;

public class StringProcessor  implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Object input = exchange.getIn().getBody();
        if (input instanceof SOAPMessage) {
            exchange.getIn().setBody(WSHelper.soapMessage2String((SOAPMessage) input));
        } else if (input instanceof Document) {
            exchange.getIn().setBody(WSHelper.docToString((Document) input));
        } else if (input instanceof CLOB) {
            CLOB body = (CLOB) input;
            InputStream in = body.getAsciiStream();
            exchange.getIn().setBody(WSHelper.inputStreamToString(in));
        }  else if (input instanceof String) {
            ; //we're good...
        } else {
            throw new UnsupportedOperationException("Input is of class: "+input.getClass().getName()+". Only SOAPMessage, Document, CLOB or String are supported");
        }
    }
}
