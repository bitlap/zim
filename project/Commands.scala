import sbt.Command

/**
 * @author 梦境迷离
 * @since 2022/1/15
 * @version 1.0
 */
object Commands {

  val FmtSbtCommand = Command.command("fmt")(state => "scalafmtSbt" :: "scalafmt" :: "test:scalafmt" :: state)

  val FmtSbtCheckCommand =
    Command.command("check")(state => "scalafmtSbtCheck" :: "scalafmtCheck" :: "test:scalafmtCheck" :: state)

  val value = Seq(
    FmtSbtCommand,
    FmtSbtCheckCommand
  )

}
