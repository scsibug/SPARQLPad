package com.sparqlfiddle.server
import org.restlet.{Restlet,Server,Application,Component}
import org.restlet.routing.Router
import org.restlet.data.{Protocol,Form,Status,MediaType}
import org.restlet.resource.{ServerResource,Get,Post}
import org.restlet.representation.{Representation,StringRepresentation}
import org.restlet.ext.jackson.{JacksonRepresentation}
import org.slf4j._

class SparqlProcessorResource extends ServerResource {
  val logger = LoggerFactory.getLogger(classOf[SparqlProcessorResource])
  logger.info("Resource created")

  @Get
  override def toString():String = {  
    return "You've hit the SPARQL processor, using the GET method.";  
  }

  @Post  
  def process(entity:Representation):Representation = {
    var result:Representation = null
    val form = new Form(entity)
    // expect "sparql", "triples", and "triple-format" entries.
    val sparqlraw = form.getFirstValue("sparql")
    val triplesraw = form.getFirstValue("triples")
    val tripleformatraw = form.getFirstValue("tripleformat")
    logger.info("sparql was "+sparqlraw)
    logger.info("triples was "+triplesraw)
    logger.info("triples-format was "+tripleformatraw)
    if (sparqlraw != null && triplesraw != null && tripleformatraw != null) {
      setStatus(Status.SUCCESS_OK)
      result = new JacksonRepresentation(DraftResponse("a sample response"))
    } else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST)
      result = new StringRepresentation("Missing form values", MediaType.TEXT_PLAIN)
    }
    result
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
