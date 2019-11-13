package nl.amis.smeetsm.dataservice.routes;

import nl.amis.smeetsm.dataservice.processors.*;
import nl.amis.smeetsm.dataservice.utils.DBProcCallHelper;
import nl.amis.smeetsm.dataservice.utils.DSHelper;
import nl.amis.smeetsm.dataservice.utils.EndpointHelper;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DBProcCallRoutes extends RouteBuilder {

    @Autowired
    DBProcCallHelper dbProcCallHelper;
    @Autowired
    EndpointHelper endpointHelper;
    @Autowired
    DSHelper dsHelper;
    @Autowired
    private ApplicationContext ctx;

    @Override
    public void configure() throws Exception {

        System.out.println(dsHelper.toString());
        System.out.println(endpointHelper.toString());
        System.out.println(dbProcCallHelper.toString());

        //Register the datasources as beans
        for (DSHelper.MyDS myds : dsHelper.getDatasource()) {
            myds.setApplicationContext(ctx);
            myds.registerBean();
        }
        //Process each application from dbproccall_endpoint.properties
        for (EndpointHelper.App app : endpointHelper.getApp()) {
            //Process for each application the defined endpoints
            for (EndpointHelper.App.EndpointDef endpointDef : app.getEndpoint()) {
                String port = endpointDef.getPort();
                if (port == null) {
					port=(new Integer(serverPort)).toString();
				}
                /*
                rest(app.getBase_path() + endpointDef.getUrl()).post().route().id(app.getName() + "_" + endpointDef.getName() + "_route")
                        .process(new SOAPProcessor())
                        .process(new SOAPXMLProcessor())
                        .process(new XMLStringProcessor())
                        .to(endpointDef.getTo_route())
                        .process(new XMLSOAPProcessor())
                        .process(new StringProcessor());
                 */
                 from("netty-http:http://0.0.0.0:"+port+app.getBase_path() + endpointDef.getUrl()).id(app.getName() + "_" + endpointDef.getName() + "_route")
				        .process(new SOAPProcessor())
				        .process(new SOAPXMLProcessor())
				        .process(new XMLStringProcessor())
				        .to(endpointDef.getTo_route())
				        .process(new XMLSOAPProcessor())
				        .process(new StringProcessor());
            }
        }

        for (DBProcCallHelper.DBProcCall dbProcCall : dbProcCallHelper.getDbproccall()) {
            String dbcall_template = "sql-stored:" + dbProcCall.getProcedure_name() + "(CLOB ${body},OUT CLOB response_clob)?dataSource=#" + dbProcCall.getDatasource_ref();

            from(dbProcCall.getUri()).id(dbProcCall.getId())
                    .tracing()
                    .to(dbcall_template)
                    .tracing()
                    .transform(simple("${body['response_clob']}"))
                    .process(new StringProcessor());
        }
    }
}
