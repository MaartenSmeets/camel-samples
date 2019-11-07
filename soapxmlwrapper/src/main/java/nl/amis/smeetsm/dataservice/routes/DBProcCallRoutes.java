package nl.amis.smeetsm.dataservice.routes;

import nl.amis.smeetsm.dataservice.processors.*;
import nl.amis.smeetsm.dataservice.utils.DBProcCallHelper;
import nl.amis.smeetsm.dataservice.utils.DSHelper;
import nl.amis.smeetsm.dataservice.utils.WSHelper;
import oracle.sql.CLOB;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

@Component
public class DBProcCallRoutes extends RouteBuilder {

    @Autowired
    DBProcCallHelper dbProcCallHelper;
    @Autowired
    DSHelper dsHelper;
    @Autowired
    private ApplicationContext ctx;

    @Override
    public void configure() throws Exception {
        for (DSHelper.MyDS myds : dsHelper.getDatasource()) {
            myds.setApplicationContext(ctx);
            myds.registerBean();
        }

        for (DBProcCallHelper.DBProcCall dbProcCall : dbProcCallHelper.getDbproccall()) {
            String dbcall_template = "sql-stored:" + dbProcCall.getProcedure_name() + "(CLOB ${body},OUT CLOB response_clob)?dataSource=#" + dbProcCall.getDatasource_ref();

            from("direct:" + dbProcCall.getIdentifier()).id("direct_" + dbProcCall.getIdentifier())
                    .tracing()
                    .to(dbcall_template)
                    .tracing()
                    .transform(simple("${body['response_clob']}"))
                    .process(new StringProcessor());

            rest("/services")
                    .post(dbProcCall.getUrl())
                    .route()
                    .process(new SOAPProcessor())
                    .choice()
                    .when(header("SOAPAction").isEqualTo(dbProcCall.getSoapoperation()))
                    .process(new SOAPXMLProcessor())
                    .process(new XMLStringProcessor())
                    .to("direct:" + dbProcCall.getIdentifier())
                    .process(new XMLSOAPProcessor())
                    .process(new StringProcessor());

        }
    }
}
