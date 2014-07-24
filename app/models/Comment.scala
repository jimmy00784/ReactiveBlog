package models

import reactivemongo.bson.{BSONDateTime, BSONObjectID, BSONDocument, BSONDocumentWriter}

/**
 * Created by karim on 7/23/14.
 */
case class Comment (date:java.util.Date, user:String, email:String, comment:String, response:Option[(java.util.Date,String)])

object Comment {
  implicit object CommentWriter extends BSONDocumentWriter[Comment]{
    def write(comment: Comment):BSONDocument = comment.response match {
        case Some(response) => BSONDocument(
          "date" -> BSONDateTime(comment.date.getTime),
          "user" -> comment.user,
          "email" -> comment.email,
          "comment" -> comment.comment,
          "response" -> BSONDocument(
            "date"  -> BSONDateTime(response._1.getTime),
            "comment" -> response._2
          ))
        case None => BSONDocument(
          "date" -> BSONDateTime(comment.date.getTime),
          "user" -> comment.user,
          "email" -> comment.email,
          "comment" -> comment.comment)
      }
  }
}
