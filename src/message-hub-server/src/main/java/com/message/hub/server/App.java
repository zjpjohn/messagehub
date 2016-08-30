package com.message.hub.server;


import com.message.hub.server.channel.RedisChannel;
import com.message.hub.server.command.CommandProcesser;
import com.message.hub.server.config.AppConfig;
import com.message.hub.server.network.WanderServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;


/**
 * Hello world!
 */
@SpringBootApplication
//@ImportResource(value = {"classpath:/applicationContext.xml"}) //, "classpath:/spring-hub-provider.xml"
public class App extends SpringBootServletInitializer {
    public static void main(String[] args)   {
        SpringApplication.run(App.class);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }

    @Autowired
    AppConfig appConfig;
    @Autowired
    RedisChannel redisChannel;

    @Bean
    CommandLineRunner runner(){
        return args->{
//            wanderServer.start();
            CommandProcesser.injection(redisChannel);
            WanderServer.start(appConfig.getPort());
        };
    }
}
