package com.wikia.sonar.slack;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

@Properties({
    @Property(
        key = SlackPlugin.SLACK_WEB_HOOK_URL,
        name = "Slack web hook URL",
        description = "Slack web hook URL to notify on quality gate build completion"
    ),
    @Property(
        key = SlackPlugin.SLACK_CHANNEL,
        name = "Slack channel override",
        description = "Allows to override the default Slack channel for current project",
        global = false,
        project = true
    )
})
public class SlackPlugin implements Plugin {
  static final String SLACK_WEB_HOOK_URL = "sonar.slack.webHookUrl";
  static final String SLACK_CHANNEL = "sonar.slack.channel";

  @Override
  public void define(Context context) {
    context.addExtensions(
        SlackBuildStatusPublisherTask.class,
        SlackPayloadBuilder.class,
        SlackPluginConfiguration.class,
        SlackFactory.class
    );
  }
}
