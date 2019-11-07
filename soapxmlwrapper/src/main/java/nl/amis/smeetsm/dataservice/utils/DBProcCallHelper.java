package nl.amis.smeetsm.dataservice.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
@PropertySource("classpath:dbproccall.properties")
public class DBProcCallHelper {
    private List<DBProcCall> dbproccall;

    public List<DBProcCall> getDbproccall() {
        return dbproccall;
    }

    public void setDbproccall(List<DBProcCall> dbproccall) {
        this.dbproccall = dbproccall;
    }

    @Override
    public String toString() {
        return "DBProcCallHelper{" +
                "dbproccall=" + dbproccall +
                '}';
    }

    public static class DBProcCall {
        private String identifier;
        private String url;
        private String soapoperation;
        private String procedure_name;
        private String datasource_ref;

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

        public String getSoapoperation() {
            return soapoperation;
        }

        public void setSoapoperation(String soapoperation) {
            this.soapoperation = soapoperation;
        }

        public String getProcedure_name() {
            return procedure_name;
        }

        public void setProcedure_name(String procedure_name) {
            this.procedure_name = procedure_name;
        }

        public String getDatasource_ref() {
            return datasource_ref;
        }

        public void setDatasource_ref(String datasource_ref) {
            this.datasource_ref = datasource_ref;
        }

        @Override
        public String toString() {
            return "DBProcCall{" +
                    "identifier='" + identifier + '\'' +
                    ", url='" + url + '\'' +
                    ", soapoperation='" + soapoperation + '\'' +
                    ", procedure_name='" + procedure_name + '\'' +
                    ", datasource_ref='" + datasource_ref + '\'' +
                    '}';
        }
    }
}
