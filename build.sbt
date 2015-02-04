name := "scala-ssh-shell"

organization := "scala-ssh-shell"

version := "0.0.1-SNAPSHOT"

compileOrder := CompileOrder.JavaThenScala

libraryDependencies <+= scalaVersion { sv =>
  "com.dongxiguo" %% "zero-log" % "0.3.2"
}

libraryDependencies += "commons-codec" % "commons-codec" % "1.8"

crossScalaVersions :=
  Seq("2.10.1", "2.10.1-SNAPSHOT")

scalacOptions ++= Vector("-unchecked", "-deprecation", "-Ywarn-all")

javacOptions ++= Vector("-encoding", "UTF-8")

libraryDependencies <++= (scalaVersion) {
	(scala) => Seq(
	"org.scala-lang" % "scala-compiler" % scala,
	"org.scala-lang" % "jline" % scala,
	"org.bouncycastle" % "bcprov-jdk16" % "1.46",
	"org.apache.sshd" % "sshd-core" % "0.8.0",
	"org.slf4j" % "slf4j-api" % "1.6.4"
	)}

pomExtra <<= scalaVersion { sv =>
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <scala.version>{sv}</scala.version>
  </properties>
  <build>
    <plugins>
      <plugin>
        <groupId>org.scala-tools</groupId>
        <artifactId>maven-scala-plugin</artifactId>
        <version>2.15.2</version>
        <executions>
          <execution>
            <goals>
              <goal>compile</goal>
              <goal>testCompile</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <recompileMode>modified-only</recompileMode>
          <args>
            <arg>-Xelide-below</arg>
            <arg>FINEST</arg>
            <arg>-deprecation</arg>
          </args>
        </configuration>
      </plugin>
    </plugins>
  </build>
}
