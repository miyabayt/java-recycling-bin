package com.bigtreetc.recycling.aws.client.ecs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

@RequiredArgsConstructor
@Slf4j
public class AwsEcsClient {

  @NonNull final EcsClient ecsClient;

  /**
   * ECSタスクを実行します。
   *
   * @param ecsClusterArn
   * @param ecsTaskDefinition
   * @param containerName
   * @param subnetId
   * @param securityGroupId
   * @param command
   * @return
   */
  public RunTaskResponse runTask(
      String ecsClusterArn,
      String ecsTaskDefinition,
      String containerName,
      String subnetId,
      String securityGroupId,
      String... command) {
    val containerOverride =
        ContainerOverride.builder().name(containerName).command(command).build();
    val taskOverride = TaskOverride.builder().containerOverrides(containerOverride).build();
    val awsvpcConfiguration =
        AwsVpcConfiguration.builder()
            .subnets(subnetId)
            .securityGroups(securityGroupId)
            .assignPublicIp(AssignPublicIp.DISABLED)
            .build();
    val networkConfiguration =
        NetworkConfiguration.builder().awsvpcConfiguration(awsvpcConfiguration).build();
    val request =
        RunTaskRequest.builder()
            .cluster(ecsClusterArn)
            .taskDefinition(ecsTaskDefinition)
            .networkConfiguration(networkConfiguration)
            .launchType(LaunchType.FARGATE)
            .overrides(taskOverride)
            .build();
    return ecsClient.runTask(request);
  }
}
