package models

import reactivemongo.bson.{BSONDateTime, BSONObjectID, BSONDocumentWriter, BSONDocumentReader, BSONDocument}
import models.BSONProducers._
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
  val fldId = "_id"
  val fldDate = "date"
  val fldTitle = "title"
  val fldContent = "content"
  val fldBlogId = "blogid"
  val fldAuthorId = "authorid"
  val fldTags = "tags"
  val fldComments = "comments"

  implicit object PostWriter extends BSONDocumentWriter[Post] {
	def write(post:Post):BSONDocument = BSONDocument(
	  fldId -> post.id.getOrElse(BSONObjectID.generate),
	  fldDate -> post.date,
	  fldTitle -> post.title,
	  fldContent -> post.content,
	  fldBlogId -> post.blogid,
	  fldAuthorId -> post.authorid,
	  fldTags -> post.tags,
	  fldComments -> post.comments
	)
  }

  implicit object PostReader extends BSONDocumentReader[Post] {
	def read(doc:BSONDocument):Post = Post(
	  doc.getAs[BSONObjectID](fldId),
	  doc.getAs[java.util.Date](fldDate).get,
	  doc.getAs[String](fldTitle).get,
	  doc.getAs[String](fldContent).get,
	  doc.getAs[BSONObjectID](fldBlogId).get,
	  doc.getAs[BSONObjectID](fldAuthorId).get,
	  doc.getAs[List[String]](fldTags).getOrElse(List()),
	  doc.getAs[List[Comment]](fldComments).getOrElse(List())
	)
  }
}
