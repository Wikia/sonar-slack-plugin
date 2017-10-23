package com.wikia.sonar.slack;

import com.github.seratch.jslack.api.model.Attachment;
import com.github.seratch.jslack.api.model.Field;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.Payload.PayloadBuilder;
import java.util.Collections;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.posttask.Project;
import org.sonar.api.ce.posttask.QualityGate.Status;

@ComputeEngineSide
@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class SlackPayloadBuilder {
  private static final String SUCCESS_MSG = "Quality gate <%s|build> passed :green_apple:";
  private static final String WARN_MSG = "Quality gate <%s|build> passed with warning :warning:";
  private static final String ERROR_MSG = "Quality gate <%s|build> failed :exclamation:";

  private static final String COLOR_GOOD = "good";
  private static final String COLOR_WARN = "warning";
  private static final String COLOR_ERROR = "danger";

  private final SlackPluginConfiguration slackPluginConfiguration;

  public SlackPayloadBuilder(
      SlackPluginConfiguration slackPluginConfiguration) {
    this.slackPluginConfiguration = slackPluginConfiguration;
  }

  public Payload buildPayload(Status status, Project project) {
    String serverUrl = slackPluginConfiguration.getServerUrl();

    String projectName = project.getName();
    String projectUrl = serverUrl + "/dashboard?id=" + project.getKey();

    String projectStatusMessage = String.format(getStatusMessage(status), projectUrl);
    String projectStatusColor = getStatusColor(status);

    Field field = Field.builder()
        .title("Project")
        .value(projectName)
        .valueShortEnough(true)
        .build();

    Attachment attachment = Attachment.builder()
        .fallback(projectStatusMessage)
        .text(projectStatusMessage)
        .fields(Collections.singletonList(field))
        .color(projectStatusColor)
        .build();

    PayloadBuilder payload = Payload.builder()
        .attachments(Collections.singletonList(attachment));

    slackPluginConfiguration.getSlackChannelOverride().ifPresent(payload::channel);

    return payload.build();
  }

  private String getStatusMessage(Status status) {
    switch (status) {
      case OK:
        return SUCCESS_MSG;
      case WARN:
        return WARN_MSG;
      case ERROR:
        return ERROR_MSG;
      default:
        throw new IllegalArgumentException("bad status");
    }
  }

  private String getStatusColor(Status status) {
    switch (status) {
      case OK:
        return COLOR_GOOD;
      case WARN:
        return COLOR_WARN;
      case ERROR:
        return COLOR_ERROR;
      default:
        throw new IllegalArgumentException("bad status");
    }
  }
}
