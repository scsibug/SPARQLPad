import org.restlet.Server
import org.restlet.data.Protocol
import org.restlet.resource.{ServerResource,Get}


class Main extends ServerResource {  

  @Get
  override def toString():String = {  
    return "hello, world";  
  }

}

object Main {

  def main(args: Array[String]) {
    println("Server starting");
    new Server(Protocol.HTTP, 8080, classOf[Main]).start();
  }

}
