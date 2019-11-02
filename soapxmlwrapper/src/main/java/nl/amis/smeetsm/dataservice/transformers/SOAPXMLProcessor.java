package nl.amis.smeetsm.dataservice.transformers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.stream.InputStreamCache;
import org.apache.camel.util.MessageHelper;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

@Component
public class SOAPXMLProcessor implements Processor {

    private Document SOAPtoXML(SOAPMessage soapMessage) throws SOAPException {
        //System.out.println(soapMessageToString(soapMessage));
        //Envelop namespace should be http://schemas.xmlsoap.org/soap/envelope/
        SOAPBody soapBody = soapMessage.getSOAPBody();
        return soapBody.extractContentAsDocument();
    }

    /*
    private static String soapMessageToString(SOAPMessage soapmessage) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapmessage.writeTo(out);
            return new String(out.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }
     */

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

    private SOAPMessage initSOAPMessage(InputStream is,String protocol) throws SOAPException, IOException {
        SOAPMessage msg;
        if (is != null) {
            msg = MessageFactory.newInstance(protocol).createMessage(null, is);
        } else {
            msg = MessageFactory.newInstance(protocol).createMessage();
        }
        return msg;
    }
/*
    private static InputStream docToInputStream(Document doc) throws TransformerException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(doc);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
*/

    private static Document newDocumentFromInputStream(InputStream in) {
        DocumentBuilderFactory factory;
        DocumentBuilder builder = null;
        Document ret = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            ret = builder.parse(new InputSource(in));
        } catch (SAXException|IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        InputStreamCache is = exchange.getIn().getBody(InputStreamCache.class);
        Document doc = newDocumentFromInputStream(is);
        Element root = doc.getDocumentElement();
        String envelopNS=root.getNamespaceURI();
        String protocol;
        if (envelopNS.equals(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE)) {
            protocol=SOAPConstants.SOAP_1_1_PROTOCOL;
        } else if (envelopNS.equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
            protocol=SOAPConstants.SOAP_1_2_PROTOCOL;
        } else {
            throw new UnsupportedOperationException("SOAP Envelop namespace "+envelopNS+" did not equal the SOAP 1.1 namespace: "+SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE+" or the SOAP 1.2 namespace: "+SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        }
        MessageHelper.resetStreamCache(exchange.getIn());
        SOAPMessage request = initSOAPMessage(is,protocol);
        exchange.getIn().setHeader("X-MS-SOAPPROTOCOL",protocol);
        exchange.getIn().setBody(docToString(SOAPtoXML(request)));
    }
}
