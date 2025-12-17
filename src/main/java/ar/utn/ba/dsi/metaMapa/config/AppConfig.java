package ar.utn.ba.dsi.metaMapa.config;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableScheduling
@Configuration
public class AppConfig implements SchedulingConfigurer {

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());
  }

  private Executor taskExecutor() {
    return Executors.newScheduledThreadPool(2); // Si tuvieras muchas tareas programadas (@Scheduled) que tardan en ejecutarse, esto permite que no se bloqueen entre sí.
  }
}