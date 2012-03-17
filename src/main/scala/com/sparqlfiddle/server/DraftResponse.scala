package com.sparqlfiddle.server

import java.util.List
import java.util.Vector
import org.slf4j._
import scala.collection.JavaConversions._

// Response to a SPARQL "Draft" POST.  Right now we only return a
// string, but we'll eventually serialize an entire SPARQL response.

class DraftResponse {
  val logger = LoggerFactory.getLogger(classOf[DraftResponse])
  logger.info("Created DraftResponse")

  @scala.reflect.BeanProperty
  var variables: List[String] = null

  @scala.reflect.BeanProperty
  var queryType: String = null

  @scala.reflect.BeanProperty
  var error: String = null

  // Result order matches that of 'variables'
  @scala.reflect.BeanProperty
  var results: List[List[String]] = new Vector[List[String]](50)
  
  def addResult(r:List[String]) = results.add(r)
}

/**
object DraftResponse {
  def apply(res:String):DraftResponse = {
    val dr = new DraftResponse()
    dr.response = res
    dr
  }
}
**/
