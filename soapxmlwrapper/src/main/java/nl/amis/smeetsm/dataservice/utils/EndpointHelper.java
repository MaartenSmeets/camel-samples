package nl.amis.smeetsm.dataservice.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "")
@PropertySource("classpath:dbproccall_endpoint.properties")
public class EndpointHelper {

    private List<App> app;

    public List<App> getApp() {
        return app;
    }

    public void setApp(List<App> app) {
        this.app = app;
    }

    @Override
    public String toString() {
        return "EndpointHelper{" +
                "app=" + app +
                '}';
    }

    public static class App {
        private String name;
        private String base_path;

        private List<EndpointDef> endpoint;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBase_path() {
            return base_path;
        }

        public void setBase_path(String base_path) {
            this.base_path = base_path;
        }

        public List<EndpointDef> getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(List<EndpointDef> endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public String toString() {
            return "App{" +
                    "name='" + name + '\'' +
                    ", base_path='" + base_path + '\'' +
                    ", endpoint=" + endpoint +
                    '}';
        }

        public static class EndpointDef {
            private String name;
            private String url;
            private String to_route;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getTo_route() {
                return to_route;
            }

            public void setTo_route(String to_route) {
                this.to_route = to_route;
            }

            @Override
            public String toString() {
                return "EndpointDef{" +
                        "name='" + name + '\'' +
                        ", url='" + url + '\'' +
                        ", to_route='" + to_route + '\'' +
                        '}';
            }
        }
    }
}
