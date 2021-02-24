name := "caliban-blog-series"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "com.github.ghostdogpr"        %% "caliban"                       % "0.9.5",
  "com.github.ghostdogpr"        %% "caliban-http4s"                % "0.9.5",
  "com.github.ghostdogpr"        %% "caliban-client"                % "0.9.5",
  "dev.zio"                      %% "zio-query"                     % "0.2.6",
  "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.2.1"
)

enablePlugins(CodegenPlugin)
