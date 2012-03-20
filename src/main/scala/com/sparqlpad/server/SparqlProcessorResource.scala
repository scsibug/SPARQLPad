package com.sparqlpad.server

import org.restlet.data.{Protocol,Form,Status,MediaType}
import org.restlet.resource.{ServerResource,Get,Post}
import org.restlet.representation.{Representation,StringRepresentation}
import org.restlet.ext.jackson.{JacksonRepresentation}
import java.util.List
import java.util.Vector
import org.slf4j._

import scala.collection.JavaConversions._

import java.io.ByteArrayInputStream

import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.query.Query._

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
      logger.info(result.toString())
    } else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST)
      result = new StringRepresentation("Missing form values", MediaType.TEXT_PLAIN)
    }
    result
  }

  def processQuery(sparql:String, triples:String, format:String):DraftResponse = {
    var dr:DraftResponse = new DraftResponse()
    // Create a model with the triples.
    val model = ModelFactory.createDefaultModel()
    // TODO: Wrap in an ontology model for inferencing
    val in = new ByteArrayInputStream(triples.getBytes("UTF-8"));
    try {
    model.read(in, null, format)
    val query = QueryFactory.create(sparql)
    val queryExec = QueryExecutionFactory.create(query,model)
      val queryType = query.getQueryType()
      dr = queryType match {
        case QueryTypeAsk => {dr.error = "Ask queries not supported yet"; dr.queryType = "Ask";dr}
        case QueryTypeConstruct => {dr.error = "Construct queries not supported yet"; dr.queryType = "Construct";dr}
        case QueryTypeDescribe => {dr.error = "Describe queries not supported yet"; dr.queryType = "Describe";dr}
        case QueryTypeSelect => {processSelectQuery(queryExec)}
        case QueryTypeUnknown => {dr.error = "Could not determine query type"; dr.queryType = "Unknown";dr}
      }
    } catch {
      // TODO: distinguish SPARQL errors from Triples errors
      case e:QueryParseException => {logger.info("Could not parse SPARQL",e); dr.error = e.getMessage()}
      case e:com.hp.hpl.jena.n3.turtle.TurtleParseException => {logger.info("Could not parse triples (Turtle)",e); dr.error = e.getMessage()}
      case e:com.hp.hpl.jena.shared.JenaException => {logger.info("Could not parse triples",e); dr.error = e.getMessage()}
      case e:Exception => {logger.error("Could not run query",e); dr.error = "Could not execute query"}
    }
    dr
  }

  def processSelectQuery(queryExec:QueryExecution):DraftResponse = {
    val dr = new DraftResponse()
    dr.queryType = "Select"
    val startQueryTime = System.nanoTime()
    val results = queryExec.execSelect()
    dr.queryExecutionTime = (System.nanoTime() - startQueryTime)/1000000l
    val startResultsTime = System.nanoTime()
    val result_vars = results.getResultVars()
    dr.variables = result_vars
    logger.info("variables: " + result_vars.mkString(", "))
    while (results.hasNext()) {
      val qs = results.next() // get a query solution
      val varsIter = qs.varNames()
      val vres = new Vector[String](result_vars.size())
      vres.setSize(result_vars.size())
      while (varsIter.hasNext()) {
        val thisVar = varsIter.next()
        val nodeRep = qs.get(thisVar).toString()
        logger.info("Found result: "+nodeRep+" for variable "+thisVar)
        vres.setElementAt(nodeRep,result_vars.indexOf(thisVar))
      }
      dr.addResult(vres)
    }
    dr.resultsExecutionTime = (System.nanoTime() - startResultsTime)/1000000l
    dr
  }
}