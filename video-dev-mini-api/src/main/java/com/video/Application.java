package com.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@MapperScan(basePackages={"com.video.mapper"})
public class Application {

    public static void main(String[] args) throws UnknownHostException {

        System.out.println(InetAddress.getLocalHost().getHostAddress());
        SpringApplication.run(Application.class, args);
    }

}
