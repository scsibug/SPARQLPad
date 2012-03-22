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
import com.hp.hpl.jena.ontology.OntModelSpec._

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
    val inferencingSpec = form.getFirstValue("inferencingSpec")
    val tripleformatraw = form.getFirstValue("tripleformat")
    logger.info("sparql was "+sparqlraw)
    logger.info("triples was "+triplesraw)
    logger.info("triples-format was "+tripleformatraw)
    if (sparqlraw != null && triplesraw != null && tripleformatraw != null) {
      setStatus(Status.SUCCESS_OK)
      result = new JacksonRepresentation(processQuery(sparqlraw, triplesraw, inferencingSpec, "Turtle"))
      logger.info(result.toString())
    } else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST)
      result = new StringRepresentation("Missing form values", MediaType.TEXT_PLAIN)
    }
    result
  }

  def processQuery(sparql:String, triples:String, inferencingSpec: String, format:String):DraftResponse = {
    var dr:DraftResponse = new DraftResponse()
    // Create a model with the triples.
    //val model = ModelFactory.createDefaultModel()
    val model = inferencingSpec match {
      case "Transitive" => ModelFactory.createOntologyModel(RDFS_MEM_TRANS_INF) // only subClassOf/subPropertyOf transitive/reflexive relations
      case "RDFS" => ModelFactory.createOntologyModel(RDFS_MEM_RDFS_INF) // (subset of) RDFS entailments
      case "OWL" => ModelFactory.createOntologyModel(OWL_DL_MEM_RDFS_INF) // OWL DL
      case _ => ModelFactory.createDefaultModel() // None
    }
    // TODO: Wrap in an ontology model for inferencing
    val in = new ByteArrayInputStream(triples.getBytes("UTF-8"));
    try {
      model.read(in, "http://sparqlpad.com/relative-uri/", format) // make up a relative URI so local file paths do not show through.
      val query = QueryFactory.create(sparql)
      val queryExec = QueryExecutionFactory.create(query,model)
      val queryType = query.getQueryType()
      dr = queryType match {
        case QueryTypeAsk => {processAskQuery(queryExec)}
        case QueryTypeConstruct => {processConstructQuery(queryExec)}
        case QueryTypeDescribe => {processDescribeQuery(queryExec)}
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

  def processAskQuery(queryExec:QueryExecution):DraftResponse = {
    val dr = new DraftResponse()
    dr.queryType = "Ask"
    val startQueryTime = System.nanoTime()
    val askresult = queryExec.execAsk()
    dr.queryExecutionTime = (System.nanoTime() - startQueryTime)/1000000l
    val vars = new Vector[String](3)
    vars.add("Result")
    dr.variables = vars
    val vres = new Vector[String](1)
    vres.add(askresult.toString())
    dr.addResult(vres)
    dr.resultsExecutionTime = 0
    dr
  }

  def processConstructQuery(queryExec:QueryExecution):DraftResponse = {
    val dr = new DraftResponse()
    dr.queryType = "Construct"
    val startQueryTime = System.nanoTime()
    val resultmodel = queryExec.execConstruct()
    dr.queryExecutionTime = (System.nanoTime() - startQueryTime)/1000000l
    val startResultsTime = System.nanoTime()
    val stmtiter = resultmodel.listStatements()
    val vars = new Vector[String](3)
    vars.add("Subject")
    vars.add("Predicate")
    vars.add("Object")
    dr.variables = vars
    while (stmtiter.hasNext()) {
      val stmt = stmtiter.nextStatement() // get a statement in the model
      val vres = new Vector[String](3)
      vres.add(stmt.getSubject().getURI())
      vres.add(stmt.getPredicate().getURI())
      vres.add(stmt.getObject().toString())
      dr.addResult(vres)
    }
    dr.resultsExecutionTime = (System.nanoTime() - startResultsTime)/1000000l
    dr
  }

  def processDescribeQuery(queryExec:QueryExecution):DraftResponse = {
    val dr = new DraftResponse()
    dr.queryType = "Describe"
    val startQueryTime = System.nanoTime()
    val resultmodel = queryExec.execDescribe()
    dr.queryExecutionTime = (System.nanoTime() - startQueryTime)/1000000l
    val startResultsTime = System.nanoTime()
    val stmtiter = resultmodel.listStatements()
    val vars = new Vector[String](3)
    vars.add("Subject")
    vars.add("Predicate")
    vars.add("Object")
    dr.variables = vars
    while (stmtiter.hasNext()) {
      val stmt = stmtiter.nextStatement() // get a statement in the model
      val vres = new Vector[String](3)
      vres.add(stmt.getSubject().getURI())
      vres.add(stmt.getPredicate().getURI())
      vres.add(stmt.getObject().toString())
      dr.addResult(vres)
    }
    dr.resultsExecutionTime = (System.nanoTime() - startResultsTime)/1000000l
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
