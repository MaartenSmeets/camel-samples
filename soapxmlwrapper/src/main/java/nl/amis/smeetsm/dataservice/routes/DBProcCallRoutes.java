package nl.amis.smeetsm.dataservice.routes;

import com.zaxxer.hikari.HikariDataSource;
import nl.amis.smeetsm.dataservice.processors.DummyXMLProcessor;
import nl.amis.smeetsm.dataservice.utils.DSHelper;
import nl.amis.smeetsm.dataservice.utils.WSHelper;
import oracle.sql.CLOB;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.spi.ApplicationContextRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.apache.commons.io.IOUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.StringWriter;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Component
public class DBProcCallRoutes extends RouteBuilder implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    DSHelper dsHelper;

    @Override
    public void configure() throws Exception {
        for (DSHelper.DBProcCall dbProcCall : dsHelper.getDbproccalls()) {

            AutowireCapableBeanFactory beanFactory  = applicationContext.getAutowireCapableBeanFactory();
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            GenericBeanDefinition myBeanDefinition = new GenericBeanDefinition();
            myBeanDefinition.setBeanClass(HikariDataSource.class);
            myBeanDefinition.setScope(SCOPE_SINGLETON);
            registry.registerBeanDefinition(dbProcCall.getIdentifier()+"_bean", myBeanDefinition);
            HikariDataSource myDS = (HikariDataSource) applicationContext.getBean(dbProcCall.getIdentifier()+"_bean");
            myDS.setDriverClassName(dbProcCall.getDatasourceProperties().getDriverClassName());
            myDS.setUsername(dbProcCall.getDatasourceProperties().getUsername());
            myDS.setPassword(dbProcCall.getDatasourceProperties().getPassword());
            myDS.setJdbcUrl(dbProcCall.getDatasourceProperties().getUrl());

            String dbcall_template="sql-stored:"+dbProcCall.getPackage_name()+"(CLOB ${body[0]},OUT CLOB response_clob)?dataSource=#"+dbProcCall.getIdentifier()+"_bean";

            from("direct:"+dbProcCall.getIdentifier()).id("direct_"+dbProcCall.getIdentifier())
                    .tracing()
                    .to(dbcall_template)
                    //.transform(body().convertToString())
                    .transform(simple("${body['response_clob']}"))
                    .process(exchange -> {
                        CLOB body = exchange.getIn().getBody(CLOB.class);
                        InputStream in = body.getAsciiStream();
                        StringWriter w = new StringWriter();
                        IOUtils.copy(in, w);
                        exchange.getOut().setBody(w.toString());
                        System.out.println(body);
                    });
        }
    }


}
