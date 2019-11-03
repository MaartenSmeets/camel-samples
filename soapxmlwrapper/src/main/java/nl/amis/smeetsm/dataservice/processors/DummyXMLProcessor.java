package nl.amis.smeetsm.dataservice.processors;

import nl.amis.smeetsm.dataservice.utils.WSHelper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.stream.InputStreamCache;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Component
public class DummyXMLProcessor implements Processor {
    private Document getDocWithRootElem(Document inputDoc) {
        Element root = inputDoc.getDocumentElement();
        String resulttag = root.getNodeName();

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();

            // root element
            Element rootElem = doc.createElement("dummyXML");
            doc.appendChild(rootElem);

            // greeting element
            Element greeting = doc.createElement("greeting");
            greeting.setTextContent("Root element of request: " + resulttag);
            rootElem.appendChild(greeting);
            return doc;
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        InputStreamCache is = new InputStreamCache(((String) exchange.getIn().getBody()).getBytes());
        Document doc = WSHelper.newDocumentFromInputStream(is);
        exchange.getIn().setBody(WSHelper.docToString(getDocWithRootElem(doc)));
    }
}
