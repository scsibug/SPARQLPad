package com.sparqlpad.server
import org.restlet.{Restlet,Server,Application,Component}
import org.restlet.routing.Router
import org.restlet.resource.Directory
import org.restlet.data.{Protocol,Form,Status,MediaType}
import org.restlet.representation.{Representation,StringRepresentation}
import org.restlet.ext.jackson.{JacksonRepresentation}
import org.slf4j._
import scala.collection.JavaConversions._

object MainApp extends Application {
  val logger = LoggerFactory.getLogger("MainApp")
  override def createRoot():Restlet = {
    // Support FILE for Directory restlet
    getConnectorService().getClientProtocols().add(Protocol.FILE);
    val directory = new Directory(getContext(), staticPath())
    // wrap subordinate apps
    val router = new Router(getContext())
    router.attach("/sparqlDraft",classOf[SparqlProcessorResource])
    router.attach("/", classOf[IndexResource])
    router.attach("/static", directory)
    router
  }

  def staticPath():String = new java.io.File("static_root").toURI().toString()
}

object Main {
  val logger = LoggerFactory.getLogger("Main")
  def main(args: Array[String]) {
    try {
      System.setProperty("org.restlet.engine.loggerFacadeClass","org.restlet.ext.slf4j.Slf4jLoggerFacade")
      val component = new Component()
      component.getServers().add(Protocol.HTTP,10001)
      component.getClients().add(Protocol.FILE)
      component.getDefaultHost().attach(MainApp)
      logger.info("MainComponent starting")
      component.start()
    } catch {
      case e:Exception => logger.error("exception caught",e)
    }
  }
}
