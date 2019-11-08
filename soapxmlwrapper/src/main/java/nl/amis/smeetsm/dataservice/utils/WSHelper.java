package nl.amis.smeetsm.dataservice.utils;

import org.apache.camel.Exchange;
import org.w3c.dom.Document;
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
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class WSHelper {
    public static String createSOAPFaultServerError(final Exception cause, Exchange exchange) throws SOAPException, IOException {
        String protocol;
        try {
            protocol = (String) exchange.getIn().getHeader("X-MS-SOAPPROTOCOL");
        } catch (Exception e) {
            protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
        }
        if (protocol == null) {
            protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
        }

        String result = null;
        SOAPMessage message = MessageFactory.newInstance(protocol).createMessage();
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody body = message.getSOAPBody();
        SOAPFault fault = body.addFault();
        fault.setFaultCode("Server");
        fault.setFaultString("Unexpected server error.");
        Detail detail = fault.addDetail();
        Name entryName = envelope.createName("message");
        DetailEntry entry = detail.addDetailEntry(entryName);
        try {
            entry.addTextNode(cause.getMessage());
        } catch (NullPointerException e) {
            entry.addTextNode("The server is not able to complete the request. Internal error.");
        }

        result = soapMessage2String(message);
        return result;
    }

    public static String soapMessage2String(final SOAPMessage message) throws SOAPException, IOException {
        String result = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        message.writeTo(outStream);
        result = new String(outStream.toByteArray(), StandardCharsets.UTF_8);
        return result;
    }

    public static Document newDocumentFromInputStream(InputStream in) {
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
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Document newDocumentFromString(String in) {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try {
            factory.setNamespaceAware(true);
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(in)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String docToString(Document doc) {
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

    public static InputStream docToInputStream(Document doc) throws TransformerException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(doc);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static String soapMessageToString(SOAPMessage soapmessage) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapmessage.writeTo(out);
            return new String(out.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    public static SOAPMessage XMLtoSOAP(Document doc, String protocol) throws SOAPException {
        MessageFactory myMessageFactory = MessageFactory.newInstance(protocol);
        SOAPMessage soapMessage = myMessageFactory.createMessage();
        SOAPBody soapBody = soapMessage.getSOAPBody();

        SOAPBodyElement docElement = soapBody.addDocument(doc);

        return soapMessage;
    }

    public static Document SOAPtoXML(SOAPMessage soapMessage) throws SOAPException {
        //System.out.println(soapMessageToString(soapMessage));
        //Envelop namespace should be http://schemas.xmlsoap.org/soap/envelope/
        SOAPBody soapBody = soapMessage.getSOAPBody();
        return soapBody.extractContentAsDocument();
    }

    public static SOAPMessage initSOAPMessage(InputStream is, String protocol) throws SOAPException, IOException {
        SOAPMessage msg;
        if (is != null) {
            msg = MessageFactory.newInstance(protocol).createMessage(null, is);
        } else {
            msg = MessageFactory.newInstance(protocol).createMessage();
        }
        return msg;
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        InputStreamReader isReader = new InputStreamReader(is);
        //Creating a BufferedReader object
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String getOperationFromSOAPHdr(SOAPHeader soapHeader) {
        @SuppressWarnings("unchecked")
        Iterator<SOAPHeaderElement> it = soapHeader.examineAllHeaderElements();

        SOAPHeaderElement opHdr = null;

        while (it.hasNext()) {
            opHdr = it.next();

            if ("operation".equals(opHdr.getLocalName())) {
                System.out.println("Found operation in SOAP header");

                return opHdr.getTextContent();
            }
        }

        return null;
    }

}
