package com.wikia.sonar.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.ce.posttask.Project;
import org.sonar.api.ce.posttask.QualityGate;
import org.sonar.api.ce.posttask.QualityGate.Status;

public class SlackBuildStatusPublisherTask implements PostProjectAnalysisTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(SlackBuildStatusPublisherTask.class);

  private final Slack slackClient;
  private final SlackPluginConfiguration slackPluginConfiguration;
  private final SlackPayloadBuilder slackPayloadBuilder;

  public SlackBuildStatusPublisherTask(SlackFactory slackFactory,
      SlackPluginConfiguration slackPluginConfiguration,
      SlackPayloadBuilder slackPayloadBuilder) {
    this.slackClient = slackFactory.getSlackClient();
    this.slackPluginConfiguration = slackPluginConfiguration;
    this.slackPayloadBuilder = slackPayloadBuilder;
  }

  @Override
  public void finished(ProjectAnalysis projectAnalysis) {
    QualityGate qualityGate = projectAnalysis.getQualityGate();

    if (qualityGate == null) {
      return;
    }

    try {
      Project project = projectAnalysis.getProject();
      Status status = qualityGate.getStatus();

      String webHookUrl = slackPluginConfiguration.getSlackWebHookUrl()
          .orElseThrow(IllegalStateException::new);
      Payload payload = slackPayloadBuilder.buildPayload(status, project);

      slackClient.send(webHookUrl, payload);
    } catch (Exception e) {
      LOGGER.error("Error while sending Slack notification", e);
    }
  }
}
