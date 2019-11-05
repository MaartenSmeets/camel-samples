package nl.amis.smeetsm.dataservice.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
@PropertySource("classpath:application.properties")
public class DSHelper {

    private List<DBProcCall> dbproccall;

    public List<DBProcCall> getDbproccalls() {
        return dbproccall;
    }

    public void setDbproccall(List<DBProcCall> dbproccall) {
        this.dbproccall = dbproccall;
    }

    public static class DBProcCall {
        private String identifier;
        private String url;
        private String package_name;
        private Map<String, String> datasource;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPackage_name() {
            return package_name;
        }

        public void setPackage_name(String package_name) {
            this.package_name = package_name;
        }

        public DataSourceProperties getDatasourceProperties() {
            DataSourceProperties dsProps = new DataSourceProperties();
            dsProps.setDriverClassName(datasource.get("driver-class-name"));
            dsProps.setUsername(datasource.get("username"));
            dsProps.setPassword(datasource.get("password"));
            dsProps.setUrl(datasource.get("url"));
            return dsProps;
        }

        public void setDatasource(Map<String, String> datasource) {
            this.datasource = datasource;
        }

        @Override
        public String toString() {
            return "DBProcCall{" +
                    "identifier='" + identifier + '\'' +
                    ", url='" + url + '\'' +
                    ", package_name='" + package_name + '\'' +
                    ", datasource=" + datasource +
                    '}';
        }
    }
}

