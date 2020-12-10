package com.xabe.spring.grpc.infrastructure.config;

import io.grpc.netty.NettyServerBuilder;
import java.util.concurrent.Executors;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

  @Bean
  GrpcServerConfigurer grpcServerConfigurer() {
    return builder -> ((NettyServerBuilder) builder)
        .executor(Executors.newFixedThreadPool(4));
  }

}
