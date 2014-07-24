package models

import reactivemongo.bson.{BSONDateTime, BSONObjectID, BSONDocumentWriter, BSONDocument}

/**
 * Created by karim on 7/23/14.
 */
case class Post (id:Option[BSONObjectID],
                 date:java.util.Date,
                 title:String,
                 content:String,
                 blogid:BSONObjectID,
                 authorid:BSONObjectID,
                 tags:List[String],
                 comments:List[Comment])

object Post {
  implicit object PostWriter extends BSONDocumentWriter[Post] {
    def write(post:Post):BSONDocument = BSONDocument(
      "_id" -> post.id.getOrElse(BSONObjectID.generate),
      "date" -> BSONDateTime(post.date.getTime),
      "title" -> post.title,
      "content" -> post.content,
      "blog" -> post.blogid,
      "author" -> post.authorid,
      "tags" -> post.tags,
      "comments" -> post.comments
    )
  }
}