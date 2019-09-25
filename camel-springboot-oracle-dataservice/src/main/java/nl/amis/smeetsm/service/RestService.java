package nl.amis.smeetsm.service;

import oracle.sql.CLOB;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.io.StringWriter;

@Component
class RestService extends RouteBuilder {

    @Value("${server.port}")
    String serverPort;

    @Value("${restservice.api.path}")
    String contextPath;

    @Override
    public void configure() {
        CamelContext context = new DefaultCamelContext();

        restConfiguration().contextPath(contextPath) //
                .port(serverPort)
                .enableCORS(true)
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");


        from("direct:proccall").id("proccall")
                .tracing()
                .setHeader("username", simple("TESTUSER"))
                .log(">>> ${header.test}")
                .to("sql-stored:get_tables('p_username' VARCHAR ${headers.username},OUT CLOB result_clob)?dataSource=dataSource")
                //.transform(body().convertToString())
                .transform(simple("${body['result_clob']}"))
                .process(exchange -> {
                    CLOB body = exchange.getIn().getBody(CLOB.class);
                    InputStream in = body.getAsciiStream();
                    StringWriter w = new StringWriter();
                    IOUtils.copy(in, w);
                    exchange.getOut().setBody(w.toString());
                    System.out.println(body);
                });

        // http://localhost:8080/camel/api/in
        rest("/api/").description("Test REST Service")
                .id("api-route")
                .get("/in")
                .produces(MediaType.TEXT_PLAIN)
                .consumes(MediaType.MEDIA_TYPE_WILDCARD)
                .to("direct:proccall");
    }
}
