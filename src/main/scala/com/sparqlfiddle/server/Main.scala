import org.restlet.{Restlet,Server,Application,Component}
import org.restlet.routing.Router
import org.restlet.data.Protocol
import org.restlet.resource.{ServerResource,Get}

import org.slf4j._

class SparqlProcessorResource extends ServerResource {
  val logger = LoggerFactory.getLogger(classOf[SparqlProcessorResource])
  logger.info("Resource created")
  @Get
  override def toString():String = {  
    return "You've hit the SPARQL processor, using the GET method.";  
  }
}

object MainApp extends Application {
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
      component.getDefaultHost().attach(MainApp)
      logger.info("MainComponent starting")
      component.start()
    } catch {
      case e:Exception => println("exception caught: "+e)
    }
  }
}
