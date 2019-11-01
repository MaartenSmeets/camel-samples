package nl.amis.smeetsm.dataservice.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Component
public class DummyXMLRoute  extends RouteBuilder {

    private static Document doc;

    static {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
            doc = documentBuilder.newDocument();

            // root element
            Element root = doc.createElement("dummyXML");
            doc.appendChild(root);

            // greeting element
            Element greeting = doc.createElement("greeting");
            greeting.setTextContent("Hi there!");
            root.appendChild(greeting);
        } catch (ParserConfigurationException e) {
            doc=null;
        }
    }

    private static String docToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    @Override
    public void configure() throws Exception {
        rest("/services").id("dummyxml").produces(MediaType.APPLICATION_XML.toString()).get("helloxml").to("direct:greetingxml");
        from("direct:greetingxml").tracing().log(">>> triggered").transform().simple(docToString(doc));
    }
}
