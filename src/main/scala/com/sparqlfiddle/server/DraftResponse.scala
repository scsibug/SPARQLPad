package com.sparqlfiddle.server

import java.util.List
import java.util.Map
import java.util.Vector
import org.slf4j._
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
  var results: List[Map[String,String]] = new Vector[Map[String,String]]
  
  def addResult(r:Map[String,String]) {
    results.add(r)
  }
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
