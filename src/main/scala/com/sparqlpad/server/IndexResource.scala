package com.sparqlpad.server

import org.restlet.data.{Protocol,Form,Status,MediaType}
import org.restlet.resource.{ServerResource,Get}
import org.restlet.representation.{Representation,FileRepresentation}
import org.slf4j._

class IndexResource extends ServerResource {
  @Get
  def home(entity:Representation):Representation = {
    new FileRepresentation(new java.io.File("static_root/index.html"), MediaType.TEXT_HTML)
  }
}
