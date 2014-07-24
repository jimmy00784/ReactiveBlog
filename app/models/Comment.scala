package models

import reactivemongo.bson._

/**
 * Created by karim on 7/23/14.
 */
case class Comment (date:java.util.Date, user:String, email:String, comment:String, response:Option[(java.util.Date,String)])

object Comment {
  /*implicit object DateWriter extends BSONDocumentWriter[java.util.Date] {
    def write(dt:java.util.Date) : BSONDateTime = BSONDateTime(dt.getTime)
  }*/
  /*implicit object DateReader extends BSONDocumentReader[BSONDateTime] {
    def read(dt:BSONDateTime) : java.util.Date = new java.util.Date(dt.value)
  }*/
  implicit object ResponseWriter extends BSONDocumentWriter[(java.util.Date,String)]{
    def write(resp:(java.util.Date,String)):BSONDocument = BSONDocument(
      "date" -> BSONDateTime(resp._1.getTime),
      "comment" -> resp._2
    )
  }
  implicit object ResponseReader extends BSONDocumentReader[(java.util.Date,String)]{
    def read(doc:BSONDocument):(java.util.Date,String) = {
      val d = doc.getAs[BSONDateTime]("date").map(dt => new java.util.Date(dt.value)).get
      val s = doc.getAs[String]("comment").getOrElse("")
      d -> s
    }
  }
  implicit object CommentWriter extends BSONDocumentWriter[Comment]{
    def write(comment: Comment):BSONDocument = //comment.response match {
        /*case Some(response) =>*/ BSONDocument(
          "date" -> BSONDateTime(comment.date.getTime),
          "user" -> comment.user,
          "email" -> comment.email,
          "comment" -> comment.comment,
          "response" -> comment.response
          )//)
        /*case None => BSONDocument(
          "date" -> BSONDateTime(comment.date.getTime),
          "user" -> comment.user,
          "email" -> comment.email,
          "comment" -> comment.comment)
      }*/
  }
  implicit object CommentReader extends BSONDocumentReader[Comment]{
    def read(doc: BSONDocument): Comment = {
      val comment = Comment(
        //doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONDateTime]("date").map(dt => new java.util.Date(dt.value)).get,
        doc.getAs[String]("user").getOrElse(""),
        doc.getAs[String]("email").getOrElse(""),
        doc.getAs[String]("comment").getOrElse(""),
        doc.getAs[(java.util.Date,String)]("response")
      )
      comment
    }
  }
}
