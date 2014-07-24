package models

import reactivemongo.bson._
import models.BSONProducers._
/**
 * Created by karim on 7/23/14.
 */
case class Comment (date:java.util.Date, user:String, email:String, comment:String, response:Option[(java.util.Date,String)])

object Comment {
  val fldDate = "date"
  val fldUser = "user"
  val fldEmail = "email"
  val fldComment = "comment"
  val fldResponse = "response"
  
  implicit object ResponseWriter extends BSONDocumentWriter[(java.util.Date,String)]{
	def write(resp:(java.util.Date,String)):BSONDocument = BSONDocument(
	  fldDate -> resp._1,
	  fldComment -> resp._2
	)
  }
  
  implicit object ResponseReader extends BSONDocumentReader[(java.util.Date,String)]{
	def read(doc:BSONDocument):(java.util.Date,String) = {
	  val d = doc.getAs[java.util.Date](fldDate).get
	  val s = doc.getAs[String](fldComment).getOrElse("")
	  d -> s
	}
  }
  
  implicit object CommentWriter extends BSONDocumentWriter[Comment]{
	def write(comment: Comment):BSONDocument = BSONDocument(
	  fldDate -> comment.date,
	  fldUser -> comment.user,
	  fldEmail -> comment.email,
	  fldComment -> comment.comment,
	  fldResponse -> comment.response
	)
  }
  
  implicit object CommentReader extends BSONDocumentReader[Comment]{
	def read(doc: BSONDocument): Comment = Comment(
	  doc.getAs[java.util.Date](fldDate).get,
	  doc.getAs[String](fldUser).getOrElse(""),
	  doc.getAs[String](fldEmail).getOrElse(""),
	  doc.getAs[String](fldComment).getOrElse(""),
	  doc.getAs[(java.util.Date,String)](fldResponse)
	)
  }
}
