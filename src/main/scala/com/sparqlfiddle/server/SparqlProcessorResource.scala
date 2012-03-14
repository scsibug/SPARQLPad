package com.sparqlfiddle.server

import org.restlet.data.{Protocol,Form,Status,MediaType}
import org.restlet.resource.{ServerResource,Get,Post}
import org.restlet.representation.{Representation,StringRepresentation}
import org.restlet.ext.jackson.{JacksonRepresentation}
import org.slf4j._

import scala.collection.JavaConversions._

import java.io.ByteArrayInputStream

import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.query._

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
      result = new JacksonRepresentation(processQuery(sparqlraw, triplesraw, "Turtle"))
    } else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST)
      result = new StringRepresentation("Missing form values", MediaType.TEXT_PLAIN)
    }
    result
  }

  def processQuery(sparql:String, triples:String, format:String):DraftResponse = {
    var response:DraftResponse = null
    // Create a model with the triples.
    val model = ModelFactory.createDefaultModel()
    // TODO: Wrap in an ontology model for inferencing
    val in = new ByteArrayInputStream(triples.getBytes("UTF-8"));
    model.read(in, null, format)
    val query = QueryFactory.create(sparql)
    val queryExec = QueryExecutionFactory.create(query,model)
    try {
      val results = queryExec.execSelect()
      val query_variables = results.getResultVars()
      response = DraftResponse(query_variables.mkString(", "))
    } catch {
      case e:Exception => logger.error("Could not run query",e)
    }
    response
  }
}
