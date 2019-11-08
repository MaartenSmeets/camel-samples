package nl.amis.smeetsm.dataservice.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
@PropertySource("classpath:dbproccall_procedure.properties")
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
        private String id;
        private String uri;
        private String procedure_name;
        private String datasource_ref;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            return "DBProcCall{" +
                    "id='" + id + '\'' +
                    ", uri='" + uri + '\'' +
                    ", procedure_name='" + procedure_name + '\'' +
                    ", datasource_ref='" + datasource_ref + '\'' +
                    '}';
        }
    }

}
