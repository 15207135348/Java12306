//package com.whut.config;
//
//import org.apache.catalina.Context;
//import org.apache.catalina.connector.Connector;
//import org.apache.tomcat.util.descriptor.web.SecurityCollection;
//import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class PortMappingConfig {
//
//    @Value("${server.http.port}")
//    private Integer httpPort;
//    @Value("${server.port}")
//    private Integer httpsPort;
//
//    @Bean
//    public Connector connector(){
//        Connector connector=new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        connector.setPort(httpPort);
//        connector.setSecure(false);
//        connector.setRedirectPort(httpsPort);
//        return connector;
//    }
//
//    @Bean
//    public EmbeddedServletContainerFactory tomcatServletWebServerFactory(Connector connector){
//        TomcatEmbeddedServletContainerFactory tomcat=new TomcatEmbeddedServletContainerFactory(){
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint=new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection=new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(connector);
//        return tomcat;
//    }
//}
