package models

import reactivemongo.bson._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._
import models.BSONProducers._
/**
 * Created by karim on 7/23/14.
 */
case class Response (date:java.util.Date, comment:String)

object Response {
  val fldDate = "date"
  val fldComment = "comment"

  implicit object ResponseWriter extends BSONDocumentWriter[Response]{
	def write(resp:Response):BSONDocument = BSONDocument(
    fldDate -> resp.date,
	  fldComment -> resp.comment
	)
  }
  
  implicit object ResponseReader extends BSONDocumentReader[Response]{
	def read(doc:BSONDocument):Response = Response(
	  doc.getAs[java.util.Date](fldDate).get,
	  doc.getAs[String](fldComment).getOrElse("")
  )
  }

  val form = Form(
    mapping(
      fldDate -> date,
      fldComment -> nonEmptyText
    )((date,comment) => Response(date,comment))
    (response => Some(response.date,response.comment))
  )
}
