import sbtassembly.AssemblyPlugin.autoImport.assembly

lazy val commonSettings = Seq(
  name := "PartialSorting",
  version := "1.0",
  scalaVersion := "2.13.7",

  Compile / PB.targets := Seq(
    scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
  ),

  libraryDependencies ++= Seq(
    "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
  ),

    assemblyMergeStrategy in assembly := {
    case x if x.contains("io.netty.versions.properties") => MergeStrategy.discard
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

lazy val root = (project in file (".")).aggregate(master, worker, common)

lazy val master = project
  .settings(
    assembly / mainClass := Some("master.master"),
    assembly / assemblyJarName := "master.jar",
    commonSettings,
  ).dependsOn(common)

lazy val worker = project
  .settings(
    assembly / assemblyJarName := "worker.jar",
    commonSettings
  ).dependsOn(common)

lazy val common = project
  .settings(
    commonSettings
  )
