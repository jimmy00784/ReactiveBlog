package models

import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONObjectID}

/**
 * Created by karim on 7/23/14.
 */
case class Blog (id:Option[BSONObjectID],
                 title:String,
                 caption:String,
                 contributors:List[BSONObjectID],
                 tags:List[String])

object Blog {
  implicit object BlogWriter extends BSONDocumentWriter[Blog] {
    def write(blog: Blog): BSONDocument = BSONDocument(
      "_id" -> blog.id.getOrElse(BSONObjectID.generate),
      "title" -> blog.title,
      "caption" -> blog.caption,
      "contributors" -> blog.contributors,
      "tags" -> blog.tags
    )
  }
}