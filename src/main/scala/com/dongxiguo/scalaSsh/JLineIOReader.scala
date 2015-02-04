/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author Stepan Koltsov
 */
package com.dongxiguo.scalaSshShell
import scala.tools.nsc._
import scala.tools.nsc.interpreter._

import scala.tools.jline.console.ConsoleReader
import scala.tools.jline.console.completer._
import session._
import scala.collection.JavaConverters._
import Completion._
import io.Streamable.slurp

/**
 *  Reads from the console using JLine.
 */
class JLineIOReader(in: java.io.InputStream,
                    out: java.io.OutputStream,
                    _completion: => Completion) extends InteractiveReader {
  override val interactive = true
  val consoleReader = new JLineConsoleReader()

  override lazy val completion = _completion
  override lazy val history: JLineHistory = JLineHistory()

  private def term = consoleReader.getTerminal()
  override def reset() = term.reset()
  override def init()  = term.init()

  def scalaToJline(tc: ScalaCompleter): Completer = new Completer {
    override def complete(_buf: String, cursor: Int, candidates: JList[CharSequence]): Int = {
      val buf   = if (_buf == null) "" else _buf
      val Candidates(newCursor, newCandidates) = tc.complete(buf, cursor)
      newCandidates foreach (candidates add _)
      newCursor
    }
  }

  class JLineConsoleReader
  extends ConsoleReader(in, out, null, new SshTerminal)
  with ConsoleReaderHelper {
    // working around protected/trait/java insufficiencies.
    override def goBack(num: Int): Unit = back(num)
    override def readOneKey(prompt: String) = {
      this.print(prompt)
      this.flush()
      this.readVirtualKey()
    }
    def eraseLine() = consoleReader.resetPromptLine("", "", 0)
    def redrawLineAndFlush(): Unit = { flush() ; drawLine() ; flush() }
    // override def readLine(prompt: String): String

    // A hook for running code after the repl is done initializing.
    //lazy val postInit: Unit = {
      this setBellEnabled false
      if (history ne NoHistory)
        this setHistory history

      if (completion ne NoCompletion) {
        val argCompletor: ArgumentCompleter =
          new ArgumentCompleter(new JLineDelimiter, scalaToJline(completion.completer()))
        argCompletor setStrict false

        this addCompleter argCompletor
        this setAutoprintThreshold 400 // max completion candidates without warning
      }
    //}
  }

  def currentLine = consoleReader.getCursorBuffer.buffer.toString
  def redrawLine() = consoleReader.redrawLineAndFlush()
  def eraseLine() = consoleReader.eraseLine()
  // Alternate implementation, not sure if/when I need this.
  // def eraseLine() = while (consoleReader.delete()) { }
  def readOneLine(prompt: String) = consoleReader readLine prompt
  def readOneKey(prompt: String)  = consoleReader readOneKey prompt
}

object JLineIOReader {
  def apply(intp: IMain,
            in: java.io.InputStream,
            out: java.io.OutputStream): JLineIOReader =
              apply(new JLineCompletion(intp), in, out)
  def apply(comp: Completion,
            in: java.io.InputStream,
            out: java.io.OutputStream): JLineIOReader =
              new JLineIOReader(in, out, comp)
}
