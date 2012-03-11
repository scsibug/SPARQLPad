package com.sparqlfiddle.server

// Response to a SPARQL "Draft" POST.  Right now we only return a
// string, but we'll eventually serialize an entire SPARQL response.

class DraftResponse {
  @scala.reflect.BeanProperty
  var response: String = null
}

object DraftResponse {
  def apply(res:String):DraftResponse = {
    val dr = new DraftResponse()
    dr.response = res
    dr
  }
}
