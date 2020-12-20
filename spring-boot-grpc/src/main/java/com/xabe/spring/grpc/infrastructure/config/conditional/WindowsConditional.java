package com.xabe.spring.grpc.infrastructure.config.conditional;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class WindowsConditional implements Condition {

  @Override
  public boolean matches(final ConditionContext conditionContext, final AnnotatedTypeMetadata annotatedTypeMetadata) {
    return SystemUtils.IS_OS_WINDOWS;
  }
}
