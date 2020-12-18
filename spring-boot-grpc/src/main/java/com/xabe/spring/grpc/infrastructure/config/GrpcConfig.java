package com.xabe.spring.grpc.infrastructure.config;

import com.google.common.util.concurrent.UncaughtExceptionHandlers;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

  private final boolean EPOLL_IS_AVAILABLE = Epoll.isAvailable();

  private final int DEFAULT_EVENT_LOOP_THREADS = NettyRuntime.availableProcessors() * 2;

  private final boolean KQUEUE_IS_AVAILABLE = KQueue.isAvailable();

  private final Logger LOG = LoggerFactory.getLogger(GrpcConfig.class);

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

  /*@Bean
  GrpcServerConfigurer grpcServerConfigurerNettyShaded() {
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
  }*/

  /*@Bean
  GrpcServerConfigurer grpcServerConfigurerLinux() {
    return builder -> {
        if (this.EPOLL_IS_AVAILABLE) {
      this.LOG.info("System supports native epoll netty transport");
    } else {
      this.LOG.info("System does not support native epoll netty transport");
    }
      final ThreadFactory tf = new DefaultThreadFactory("server-elg-", true);
      final EventLoopGroup boss = new EpollEventLoopGroup(1, tf);
      final EventLoopGroup worker = new EpollEventLoopGroup(0, tf);
      final Class<? extends ServerChannel> channelType = EpollServerSocketChannel.class;
      ((NettyServerBuilder) builder)
          .bossEventLoopGroup(boss)
          .workerEventLoopGroup(worker)
          .channelType(channelType)
          .flowControlWindow(NettyChannelBuilder.DEFAULT_FLOW_CONTROL_WINDOW)
          .executor(getAsyncExecutor());
    };
  }*/

  @Bean
  GrpcServerConfigurer grpcServerConfigurerOsx() {
    if (this.KQUEUE_IS_AVAILABLE) {
      this.LOG.info("System supports native kqueue netty transport");
    } else {
      this.LOG.info("System does not support native kqueue netty transport");
    }
    return builder -> {
      final ThreadFactory tf = new DefaultThreadFactory("server-elg-", true);
      final EventLoopGroup boss = new KQueueEventLoopGroup(1, tf);
      final EventLoopGroup worker = new KQueueEventLoopGroup(0, tf);
      final Class<? extends ServerChannel> channelType = KQueueServerSocketChannel.class;
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
