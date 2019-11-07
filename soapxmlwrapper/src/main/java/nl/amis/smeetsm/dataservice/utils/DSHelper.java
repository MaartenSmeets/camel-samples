package nl.amis.smeetsm.dataservice.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Component
@ConfigurationProperties(prefix = "app")
@PropertySource("classpath:datasources.properties")
public class DSHelper {
    private List<MyDS> datasource;

    public List<MyDS> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<MyDS> datasource) {
        this.datasource = datasource;
    }

    @Override
    public String toString() {
        return "DSHelper{" +
                "myDS=" + datasource +
                '}';
    }

    public static class MyDS implements ApplicationContextAware {
        private String ref;
        private String url;
        private String driverClassName;
        private String username;
        private String password;
        private ApplicationContext applicationContext;

        public DataSourceProperties getDatasourceProperties() {
            DataSourceProperties dsProps = new DataSourceProperties();
            dsProps.setDriverClassName(driverClassName);
            dsProps.setUsername(username);
            dsProps.setPassword(password);
            dsProps.setUrl(url);
            return dsProps;
        }

        @Override
        public String toString() {
            return "MyDS{" +
                    "ref='" + ref + '\'' +
                    ", url='" + url + '\'' +
                    ", driverClassName='" + driverClassName + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        public void registerBean() {
            AutowireCapableBeanFactory beanFactory  = applicationContext.getAutowireCapableBeanFactory();
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            if (!registry.containsBeanDefinition(ref)) {
                GenericBeanDefinition myBeanDefinition = new GenericBeanDefinition();
                myBeanDefinition.setBeanClass(HikariDataSource.class);
                myBeanDefinition.setScope(SCOPE_SINGLETON);
                registry.registerBeanDefinition(ref, myBeanDefinition);
                HikariDataSource myDS = (HikariDataSource) applicationContext.getBean(ref);
                myDS.setDriverClassName(this.getDatasourceProperties().getDriverClassName());
                myDS.setUsername(username);
                myDS.setPassword(password);
                myDS.setJdbcUrl(url);
            }
        }
    }
}

