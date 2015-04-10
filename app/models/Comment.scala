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
case class Comment (date:Option[java.util.Date], user:String, email:String, comment:String/*, response:Option[(Response)]*/)

object Comment {
  val fldDate = "date"
  val fldUser = "user"
  val fldEmail = "email"
  val fldComment = "comment"
  //val fldResponse = "response"
  
 
  implicit object CommentWriter extends BSONDocumentWriter[Comment]{
	def write(comment: Comment):BSONDocument = BSONDocument(
	  fldDate -> comment.date.getOrElse(new java.util.Date()),
	  fldUser -> comment.user,
	  fldEmail -> comment.email,
	  fldComment -> comment.comment//,
	  //fldResponse -> comment.response
	)
  }
  
  implicit object CommentReader extends BSONDocumentReader[Comment]{
	def read(doc: BSONDocument): Comment = Comment(
	  doc.getAs[java.util.Date](fldDate),
	  doc.getAs[String](fldUser).getOrElse(""),
	  doc.getAs[String](fldEmail).getOrElse(""),
	  doc.getAs[String](fldComment).getOrElse("")//,
	  //doc.getAs[Response](fldResponse)
	)
  }

  val form = Form(
    mapping(
      fldDate -> optional(of[java.util.Date]),
      fldUser -> nonEmptyText,
      fldEmail -> nonEmptyText,
      fldComment -> nonEmptyText
    ) {(d,u,e,c) => Comment(d,u,e,c/*,None*/)}
  {c => Some((c.date,c.user,c.user,c.comment))}
  )
}
