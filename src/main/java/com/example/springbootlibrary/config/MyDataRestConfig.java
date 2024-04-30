package com.example.springbootlibrary.config;

import com.example.springbootlibrary.entity.Book;
import com.example.springbootlibrary.entity.History;
import com.example.springbootlibrary.entity.Message;
import com.example.springbootlibrary.entity.Review;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer{

    private String theAllowedOrigins="https://localhost:3000";

    @Override
    public void configureRepositoryRestConfiguration (RepositoryRestConfiguration config, CorsRegistry cors) {

        HttpMethod[] theUnsupportedActions= {
                HttpMethod.DELETE,
                HttpMethod.PUT,
                HttpMethod.PATCH,
                HttpMethod.POST};

        config.exposeIdsFor(Book.class);
        config.exposeIdsFor(Review.class);
        config.exposeIdsFor(Message.class);
        config.exposeIdsFor(History.class);

        disableHttpMethods(Book.class,config,theUnsupportedActions);
        disableHttpMethods(Review.class,config,theUnsupportedActions);
        disableHttpMethods(Message.class,config,theUnsupportedActions);
        disableHttpMethods(History.class,config,theUnsupportedActions);

        //        Configure Cors Mapping
        cors.addMapping(config.getBasePath() + "/**").allowedOrigins(theAllowedOrigins);

    }

    private void disableHttpMethods(Class theclass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {

    config.getExposureConfiguration()
            .forDomainType(theclass)
            .withItemExposure(((metdata, httpMethods) ->
                    httpMethods.disable(theUnsupportedActions)))
            .withCollectionExposure(((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions)));

    }
}
