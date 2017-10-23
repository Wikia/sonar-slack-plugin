package com.wikia.sonar.slack;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.github.seratch.jslack.Slack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask.ProjectAnalysis;

class SlackBuildStatusPublisherTaskTest {
  private final Slack slack = mock(Slack.class);
  private final SlackFactory slackFactory = mock(SlackFactory.class);
  private final SlackPluginConfiguration slackPluginConfiguration = mock(SlackPluginConfiguration.class);
  private final SlackPayloadBuilder slackPayloadBuilder = mock(SlackPayloadBuilder.class);

  private final ProjectAnalysis projectAnalysis = mock(ProjectAnalysis.class);

  private final SlackBuildStatusPublisherTask slackBuildStatusPublisherTask =
      new SlackBuildStatusPublisherTask(slackFactory, slackPluginConfiguration, slackPayloadBuilder);

  @BeforeEach
  void configureSlackFactory() {
    when(slackFactory.getSlackClient())
        .thenReturn(slack);
  }

  @Test
  void noNotificationIsSentWhenQualityGateIsNull() {
    when(projectAnalysis.getQualityGate())
        .thenReturn(null);

    slackBuildStatusPublisherTask.finished(projectAnalysis);

    verifyZeroInteractions(slack);
  }
}
