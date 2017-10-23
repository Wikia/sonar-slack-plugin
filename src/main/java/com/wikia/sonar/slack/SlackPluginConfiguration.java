package com.wikia.sonar.slack;

import java.util.Optional;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.config.Configuration;
import org.sonar.api.platform.Server;

@ComputeEngineSide
@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class SlackPluginConfiguration {
  private final Configuration configuration;
  private final Server server;

  public SlackPluginConfiguration(Configuration configuration, Server server) {
    this.configuration = configuration;
    this.server = server;
  }

  public Optional<String> getSlackChannelOverride() {
    return configuration.get(SlackPlugin.SLACK_CHANNEL);
  }

  public Optional<String> getSlackWebHookUrl() {
    return configuration.get(SlackPlugin.SLACK_WEB_HOOK_URL);
  }

  public String getServerUrl() {
    return server.getPublicRootUrl();
  }
}
