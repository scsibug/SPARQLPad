package com.sparqlfiddle.server
import org.restlet.{Restlet,Server,Application,Component}
import org.restlet.routing.Router
import org.restlet.data.{Protocol,Form,Status,MediaType}
import org.restlet.representation.{Representation,StringRepresentation}
import org.restlet.ext.jackson.{JacksonRepresentation}
import org.slf4j._

object SparqlApp extends Application {
  val logger = LoggerFactory.getLogger("MainApp")
  override def createRoot():Restlet = {
    println("MainApp starting");
    val router = new Router(getContext())
    router.attach("/sparqlDraft",classOf[SparqlProcessorResource])
    router
  }
}

object Main {
  val logger = LoggerFactory.getLogger("Main")
  def main(args: Array[String]) {
    try {
      System.setProperty("org.restlet.engine.loggerFacadeClass","org.restlet.ext.slf4j.Slf4jLoggerFacade")
      val component = new Component()
      component.getServers().add(Protocol.HTTP,8080)
      component.getDefaultHost().attach(SparqlApp)
      logger.info("MainComponent starting")
      component.start()
    } catch {
      case e:Exception => logger.error("exception caught",e)
    }
  }
}
