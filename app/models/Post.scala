package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDateTime, BSONObjectID, BSONDocumentWriter, BSONDocumentReader, BSONDocument}
import models.BSONProducers._
/**
 * Created by karim on 7/23/14.
 */
case class Post (id:Option[BSONObjectID],
                 date:Option[java.util.Date],
                 title:String,
                 content:String,
                 //blogid:BSONObjectID,
                 authorid:BSONObjectID,
								 authorname:String,
                 tags:List[String],
                 comments:List[Comment])

object Post {
  val fldId = "_id"
  val fldDate = "date"
  val fldTitle = "title"
  val fldContent = "content"
  //val fldBlogId = "blogid"
  val fldAuthorId = "authorid"
	val fldAuthorName = "authorname"
  val fldTags = "tags"
  val fldComments = "comments"

  implicit object PostWriter extends BSONDocumentWriter[Post] {
	def write(post:Post):BSONDocument = BSONDocument(
	  fldId -> post.id.getOrElse(BSONObjectID.generate),
	  fldDate -> post.date.getOrElse(new java.util.Date()),
	  fldTitle -> post.title,
	  fldContent -> post.content,
	  //fldBlogId -> post.blogid,
	  fldAuthorId -> post.authorid,
		fldAuthorName -> post.authorname,
	  fldTags -> post.tags,
	  fldComments -> post.comments
	)
  }

  implicit object PostReader extends BSONDocumentReader[Post] {
	def read(doc:BSONDocument):Post = Post(
	  doc.getAs[BSONObjectID](fldId),
	  doc.getAs[java.util.Date](fldDate),
	  doc.getAs[String](fldTitle).get,
	  doc.getAs[String](fldContent).get,
	  //doc.getAs[BSONObjectID](fldBlogId).get,
	  doc.getAs[BSONObjectID](fldAuthorId).get,
		doc.getAs[String](fldAuthorName).get,
	  doc.getAs[List[String]](fldTags).getOrElse(List()),
	  doc.getAs[List[Comment]](fldComments).getOrElse(List())
	)
  }

  val form = Form(
		mapping(
			fldId -> optional(of[String] verifying pattern(
				Common.objectIdRegEx,
        "constraint.objectId",
        "error.objectId")),
			fldDate -> optional(of[java.util.Date]),
			fldTitle -> nonEmptyText,
			fldContent -> nonEmptyText,
			/*fldBlogId -> nonEmptyText.verifying(pattern(
        Common.objectIdRegEx,
				"constraint.blogId",
				"error.blogId")),*/
			//fldAuthorId -> text,
			fldTags -> text
			)
			{ (id,date,title,content/*,blogId,authorId*/,tags) => Post(
				id.map(BSONObjectID(_)),
				date,
				title,
				content,
				//BSONObjectID(blogId),
				BSONObjectID.generate,
				"",
				tags.split(",").foldLeft(List[String]()){(c,e) => e.trim :: c},
				List())
			}
			{
				post => Some(
					(post.id.map(_.stringify),post.date,post.title,post.content/*,post.blogid.stringify,post.authorid.stringify*/,post.tags.mkString(", "))
				)
			}
	)
}
