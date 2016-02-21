package org.homonoia.eris.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;

/**
 * Created by alexparlett on 14/02/2016.
 */
@Configuration
public class JsonConfiguration {

    @Bean
    public GsonBuilder gsonBuilder() {
        return new GsonBuilder();
    }

    @Bean
    public Gson gson(GsonBuilder gsonBuilder) {
        return gsonBuilder.setVersion(1.0)
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .create();
    }

}
