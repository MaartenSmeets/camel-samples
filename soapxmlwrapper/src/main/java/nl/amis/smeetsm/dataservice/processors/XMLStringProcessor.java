package nl.amis.smeetsm.dataservice.processors;

import nl.amis.smeetsm.dataservice.utils.WSHelper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;

public class XMLStringProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Object doc = exchange.getIn().getBody();
        if (!(doc instanceof Document)) {
            throw new UnsupportedOperationException("Input is of class: " + doc.getClass().getName() + ". Only Document is supported!");
        } else {
            Document myMsg = (Document) doc;
            exchange.getIn().setBody(WSHelper.docToString(myMsg));
        }
    }
}
