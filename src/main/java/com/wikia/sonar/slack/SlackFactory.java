package com.wikia.sonar.slack;

import com.github.seratch.jslack.Slack;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.ce.ComputeEngineSide;

@ComputeEngineSide
@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class SlackFactory {
  public Slack getSlackClient() {
    return Slack.getInstance();
  }
}
