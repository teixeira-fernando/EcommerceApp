package com.ecommerceapp.inventory;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker.ReachedState;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

public class EmbeddedMongoInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  @Override
  public void initialize(ConfigurableApplicationContext context) {
    ReachedState<RunningMongodProcess> mongo = Mongod.instance().start(Version.Main.V7_0);
    context.addApplicationListener(
        event -> {
          if (event instanceof ContextClosedEvent && event.getSource() == context) {
            mongo.close();
          }
        });
    TestPropertyValues.of(
            "spring.data.mongodb.host=localhost",
            "spring.data.mongodb.port=" + mongo.current().getServerAddress().getPort())
        .applyTo(context);
  }
}
