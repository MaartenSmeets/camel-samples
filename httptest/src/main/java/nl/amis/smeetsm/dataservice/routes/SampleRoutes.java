package nl.amis.smeetsm.dataservice.routes;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.camel.component.jetty.JettyHttpEndpoint;
import org.apache.camel.component.netty.NettyComponent;
import org.apache.camel.component.rest.RestComponent;
import org.apache.camel.component.sparkrest.SparkComponent;
import org.apache.camel.component.sparkrest.SparkConfiguration;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.spi.RestConfiguration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        //both on the same default port
        from("rest:get:/:url1").id("test_route1").to("log:dummylog");
        from("rest:get:/:url2").id("test_route2").to("log:dummylog");

        //both on port 8085
        //restConfiguration().component("netty-http").host("0.0.0.0").port(8084);
        //rest("/url1").get().id("test_route1").to("log:dummylog");
        //restConfiguration().component("netty-http").host("0.0.0.0").port(8085);
        //rest("/url2").get().id("test_route2").to("log:dummylog");

        //Both OK
        //from("netty-http:http://0.0.0.0:8083/url1").id("test_route1").to("log:dummylog");
        //from("netty-http:http://0.0.0.0:8083/url2").id("test_route2").to("log:dummylog");

        //Both OK
        //from("jetty:http://0.0.0.0:8083/url1").id("test_route1").to("log:dummylog");
        //from("jetty:http://0.0.0.0:8083/url2").id("test_route2").to("log:dummylog");

        //Both OK
        //from("undertow:http://0.0.0.0:8083/myapp1").id("test_route1").to("log:dummylog");
        //from("undertow:http://0.0.0.0:8083/myapp2").id("test_route2").to("log:dummylog");

        //Both OK, however path below camel.component.servlet.mapping.context-path and uses the same port
        //from("servlet:/url1").id("test_route1").to("log:dummylog");
        //from("servlet:/url2").id("test_route2").to("log:dummylog");

        //Both OK however need multiple component instances
        /*SparkComponent sc = getContext().getComponent("spark-rest",SparkComponent.class);
        sc.setPort(8083);
        SparkConfiguration sc2config = sc.getSparkConfiguration();
        SparkComponent sc2 = new SparkComponent();
        sc2.setSparkConfiguration(sc2config);
        sc2.setPort(8084);
        getContext().addComponent("spark-rest2",sc2);
        */
        //from("spark-rest://get:url1").id("test_route1").to("log:dummylog");
        //from("spark-rest2://get:url2").id("test_route2").to("log:dummylog");
    }
}

