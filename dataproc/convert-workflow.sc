// import $ivy.`com.google.cloud:google-cloud-storage:2.4.0`
// import $ivy.`com.google.cloud:google-cloud-dataproc:2.3.2`
// https://mvnrepository.com/artifact/com.google.cloud/libraries-bom
//import $ivy.`com.google.cloud:libraries-bom:24.3.0`
// import $ivy.`io.grpc:grpc-protobuf:1.44.0`
// import $ivy.`com.google.protobuf:protobuf-java:3.19.3`

import com.google.cloud.dataproc.v1.{ClusterConfig, DiskConfig, GceClusterConfig, InstanceGroupConfig, ManagedCluster, OrderedJob, RegionName, SoftwareConfig, SparkJob, WorkflowTemplate, WorkflowTemplatePlacement, WorkflowTemplateServiceClient, WorkflowTemplateServiceSettings}

val projectId = "open-targets-eu-dev"
val region = "europe-west1"

 val configPath = "gs://genetics-portal-dev-data/22.05.2/conf"
 val config = "genetics-l2g-input.conf"

val jarPath = "gs://ot-team/jarrod/jars"
val jar = "spark_converter-assembly-0.1.0-SNAPSHOT.jar"

val gcpUrl = s"$region-dataproc.googleapis.com:443"


// Configure the settings for the workflow template service client.
val workflowTemplateServiceSettings =
  WorkflowTemplateServiceSettings.newBuilder.setEndpoint(gcpUrl).build

val workflowTemplateServiceClient: WorkflowTemplateServiceClient =
  WorkflowTemplateServiceClient.create(workflowTemplateServiceSettings)

// Configure the jobs within the workflow.
def sparkJob(step: String): SparkJob = SparkJob
  .newBuilder
  .setMainJarFileUri(s"$jarPath/$jar")
   .addFileUris(s"$configPath/$config")
   .putProperties("spark.executor.extraJavaOptions", s"-Dconfig.file=$config")
   .putProperties("spark.driver.extraJavaOptions", s"-Dconfig.file=$config")
  .build

val convertJob: OrderedJob = OrderedJob
  .newBuilder
  .setStepId("convert")
  .setSparkJob(sparkJob("convert"))
  .build

// Configure the cluster placement for the workflow.// Configure the cluster placement for the workflow.
val gceClusterConfig = GceClusterConfig
  .newBuilder
  .setZoneUri(s"$region-d")
  .addTags("etl-dev-cluster")
  .build

val clusterConfig: ClusterConfig = {
  val softwareConfig = SoftwareConfig
    .newBuilder
    .setImageVersion("2.0-debian10")
    .putProperties("dataproc:dataproc.allow.zero.workers","true")
    .build

  val disk = DiskConfig
    .newBuilder
    .setBootDiskSizeGb(2000)
    .build

  val sparkMasterConfig = {
    InstanceGroupConfig
      .newBuilder
      .setNumInstances(1)
      .setMachineTypeUri("n1-highmem-96")
      .setDiskConfig(disk)
      .build
  }
  ClusterConfig
    .newBuilder
    .setGceClusterConfig(gceClusterConfig)
    .setSoftwareConfig(softwareConfig)
    .setMasterConfig(sparkMasterConfig)
    .build
}

val managedCluster = ManagedCluster.newBuilder.setClusterName("etl-convert-cluster").setConfig(clusterConfig).build
val workflowTemplatePlacement = WorkflowTemplatePlacement.newBuilder.setManagedCluster(managedCluster).build

// Create the inline workflow template.
val workflowTemplate = WorkflowTemplate
  .newBuilder
  .addJobs(convertJob)
  .setPlacement(workflowTemplatePlacement)
  .build


val parent = RegionName.format(projectId, region)
val instantiateInlineWorkflowTemplateAsync = workflowTemplateServiceClient.instantiateInlineWorkflowTemplateAsync(parent, workflowTemplate)
instantiateInlineWorkflowTemplateAsync.get

// Print out a success message.
println("Workflow ran successfully.")
workflowTemplateServiceClient.close()