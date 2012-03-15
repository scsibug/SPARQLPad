package com.sparqlfiddle.server

import org.slf4j._
// Response to a SPARQL "Draft" POST.  Right now we only return a
// string, but we'll eventually serialize an entire SPARQL response.

class DraftResponse {
  val logger = LoggerFactory.getLogger(classOf[DraftResponse])
  logger.info("Created DraftResponse")

  @scala.reflect.BeanProperty
  var variables: java.util.List[String] = null

  @scala.reflect.BeanProperty
  var queryType: String = null
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
