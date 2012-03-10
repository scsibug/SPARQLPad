import org.restlet.{Restlet,Server,Application,Component}
import org.restlet.routing.Router
import org.restlet.data.Protocol
import org.restlet.resource.{ServerResource,Get}


class DefRes extends ServerResource {
  @Get
  override def toString():String = {  
    return "You've hit the default resource.";  
  }
}

class SparqlProcessorResource extends ServerResource {
  @Get
  override def toString():String = {  
    return "You've hit the SPARQL processor, using the GET method.";  
  }
}

object MainApp extends Application {

  override def createRoot():Restlet = {
    println("MainApp starting");
    val router = new Router(getContext())
    router.attachDefault(classOf[DefRes])
    router.attach("/sparqlDraft",classOf[SparqlProcessorResource])
    router
  }

}

object Main {
  def main(args: Array[String]) {
    try {
      val component = new Component()
      component.getServers().add(Protocol.HTTP,8080)
      component.getDefaultHost().attach(MainApp)
      println("MainComponent starting");
      component.start()
    } catch {
      case e:Exception => println("exception caught: "+e)
    }
  }
}
