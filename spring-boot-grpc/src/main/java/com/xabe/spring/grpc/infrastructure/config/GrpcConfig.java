package com.xabe.spring.grpc.infrastructure.config;

import com.google.common.util.concurrent.UncaughtExceptionHandlers;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.EventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.ServerChannel;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import io.grpc.netty.shaded.io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

/*  @Bean
  GrpcServerConfigurer grpcServerConfigurer() {
    return builder -> ((NettyServerBuilder) builder)
        .executor(Executors.newFixedThreadPool(4));
  }*/

/*  @Bean
  GrpcServerConfigurer grpcServerConfigurer() {
    return builder -> ((NettyServerBuilder) builder)
        .workerEventLoopGroup(new EpollEventLoopGroup(4))
        .bossEventLoopGroup(new EpollEventLoopGroup(4))
        .executor(Executors.newFixedThreadPool(4))
        .channelType(EpollServerSocketChannel.class);
  }*/

  @Bean
  GrpcServerConfigurer grpcServerConfigurer() {
    return builder -> {
      final ThreadFactory tf = new DefaultThreadFactory("server-elg-", true);
      // On Linux it can, possibly, be improved by using
      // io.netty.channel.epoll.EpollEventLoopGroup
      // io.netty.channel.epoll.EpollServerSocketChannel
      final EventLoopGroup boss = new NioEventLoopGroup(1, tf);
      final EventLoopGroup worker = new NioEventLoopGroup(0, tf);
      final Class<? extends ServerChannel> channelType = NioServerSocketChannel.class;
      ((NettyServerBuilder) builder)
          .bossEventLoopGroup(boss)
          .workerEventLoopGroup(worker)
          .channelType(channelType)
          .flowControlWindow(NettyChannelBuilder.DEFAULT_FLOW_CONTROL_WINDOW)
          .executor(getAsyncExecutor());
    };
  }

  private Executor getAsyncExecutor() {
    return new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
        new ForkJoinPool.ForkJoinWorkerThreadFactory() {
          final AtomicInteger num = new AtomicInteger();

          @Override
          public ForkJoinWorkerThread newThread(final ForkJoinPool pool) {
            final ForkJoinWorkerThread thread =
                ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setDaemon(true);
            thread.setName("grpc-server-app-" + "-" + this.num.getAndIncrement());
            return thread;
          }
        }, UncaughtExceptionHandlers.systemExit(), true);
  }
}
