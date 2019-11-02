package nl.amis.smeetsm.dataservice.utils;

import org.springframework.stereotype.Component;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WSHelper {
    public static String createSOAPFaultServerError(final Exception cause) throws SOAPException, IOException {
        String result = null;
        SOAPMessage message = MessageFactory.newInstance().createMessage();
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

    private static String soapMessage2String(final SOAPMessage message) throws SOAPException, IOException {
        String result = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        message.writeTo(outStream);
        result = new String(outStream.toByteArray(), StandardCharsets.UTF_8);
        return result;
    }
}
