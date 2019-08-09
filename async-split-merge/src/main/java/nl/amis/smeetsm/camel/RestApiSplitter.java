package nl.amis.smeetsm.camel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.Future;

@Component
class RestApiSplitter extends RouteBuilder {

    ObjectMapper mapper = new ObjectMapper();

    @Value("${server.port}")
    String serverPort;

    @Value("${simplerouter.api.path}")
    String contextPath;

    @Override
    public void configure() {

        CamelContext context = new DefaultCamelContext();
        // http://localhost:8080/camel/api-doc
        restConfiguration().contextPath(contextPath) //
                .port(serverPort)
                .enableCORS(true)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Test REST API")
                .apiProperty("api.version", "v1")
                .apiProperty("cors", "true") // cross-site
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        from("direct:invokeRestApi1").description("Test REST Service").id("api-route1")
                .to("http://localhost:8080/camel/api/bean");

        from("direct:invokeRestApi2").description("Test REST Service").id("api-route2")
                .to("http://localhost:8080/camel/api/bean");

        from("direct:splitter")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        CamelContext context = exchange.getContext();
                        ProducerTemplate producerTemplate = context.createProducerTemplate();

                        // Asynchronous call to internal route
                        String body = exchange.getIn().getBody(String.class);
                        Future<String> api1 = producerTemplate.asyncRequestBody("direct:invokeRestApi1", body, String.class);
                        Future<String> api2 = producerTemplate.asyncRequestBody("direct:invokeRestApi2", body, String.class);
                        ArrayNode arrayNode = mapper.createArrayNode();
                        JsonNode actualObj1 = mapper.readTree(api1.get());
                        JsonNode actualObj2 = mapper.readTree(api2.get());
                        arrayNode.add(actualObj1);
                        arrayNode.add(actualObj2);

                        // Do rest of the work
                        exchange.getOut().setBody(arrayNode.toString());
                    }
                });

        rest("/api-splitter/").description("Test REST Service").id("splitter-caller").post().type(HelloBean.class).bindingMode(RestBindingMode.off).to("direct:splitter");

        rest("/api/").description("Test REST Service")
                .id("api-route")
                .post("/bean")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .bindingMode(RestBindingMode.auto)
                .type(HelloBean.class)
                .enableCORS(true)
                .to("direct:remoteService");

        from("direct:remoteService")
                .routeId("direct-route")
                .tracing()
                .log(">>> ${body.id}")
                .log(">>> ${body.name}")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        HelloBean bodyIn = (HelloBean) exchange.getIn().getBody();
                        HelloService.getGreeting(bodyIn);
                        exchange.getIn().setBody(bodyIn);
                    }
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
    }
}
