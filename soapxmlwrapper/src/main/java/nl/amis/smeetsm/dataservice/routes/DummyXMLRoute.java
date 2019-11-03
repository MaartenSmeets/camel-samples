package nl.amis.smeetsm.dataservice.routes;

import nl.amis.smeetsm.dataservice.processors.DummyXMLProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class DummyXMLRoute  extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        rest("/services").id("dummyxml-restservice").produces(MediaType.APPLICATION_XML.toString()).get("helloxml").to("direct:greetingxml");
        from("direct:greetingxml").id("dummyxml-direct").process(new DummyXMLProcessor()).tracing().log(">>> triggered");
    }
}
