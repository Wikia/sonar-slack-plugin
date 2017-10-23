package com.wikia.sonar.slack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.seratch.jslack.api.webhook.Payload;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.sonar.api.ce.posttask.Project;
import org.sonar.api.ce.posttask.QualityGate.Status;

class SlackPayloadBuilderTest {
  private final static String TEST_SONAR_URL = "http://sonar.example.com";
  private final static String TEST_PROJECT_KEY = "test.project";
  private final static String TEST_PROJECT_NAME = "Test Project";

  private final Project project = mock(Project.class);

  private final SlackPluginConfiguration slackPluginConfiguration = mock(SlackPluginConfiguration.class);
  private final SlackPayloadBuilder slackPayloadBuilder = new SlackPayloadBuilder(slackPluginConfiguration);

  @BeforeEach
  void setupSonar() {
    when(slackPluginConfiguration.getServerUrl())
        .thenReturn(TEST_SONAR_URL);
    when(project.getKey())
        .thenReturn(TEST_PROJECT_KEY);
    when(project.getName())
        .thenReturn(TEST_PROJECT_NAME);
  }

  @ParameterizedTest
  @EnumSource(Status.class)
  void testFieldsAreCorrectForAllStatuses(Status status) {
    Map<Status, String> expectedMessageMap = new HashMap<Status, String>(){{
      put(Status.OK, "Quality gate <http://sonar.example.com/dashboard?id=test.project|build> passed :green_apple:");
      put(Status.WARN, "Quality gate <http://sonar.example.com/dashboard?id=test.project|build> passed with warning :warning:");
      put(Status.ERROR, "Quality gate <http://sonar.example.com/dashboard?id=test.project|build> failed :exclamation:");
    }};

    Map<Status, String> expectedColorsMap = new HashMap<Status, String>(){{
      put(Status.OK, "good");
      put(Status.WARN, "warning");
      put(Status.ERROR, "danger");
    }};

    String expectedMessage = expectedMessageMap.get(status);
    String expectedColor = expectedColorsMap.get(status);

    Payload payload = slackPayloadBuilder.buildPayload(status, project);

    assertThat(payload.getAttachments()).hasOnlyOneElementSatisfying(attachment -> {
      assertThat(attachment.getFields()).hasOnlyOneElementSatisfying(field -> {
        assertThat(field.getTitle()).isEqualTo("Project");
        assertThat(field.getValue()).isEqualTo(TEST_PROJECT_NAME);
        assertThat(field.isValueShortEnough()).isTrue();
      });

      assertThat(attachment.getFallback()).isEqualTo(expectedMessage);
      assertThat(attachment.getText()).isEqualTo(expectedMessage);
      assertThat(attachment.getColor()).isEqualTo(expectedColor);
    });

    assertThat(payload.getChannel()).isNull();
  }
}
