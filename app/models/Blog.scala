package models

import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader, BSONObjectID}

/**
 * Created by karim on 7/23/14.
 */
case class Blog (id:Option[BSONObjectID],
                 title:String,
                 caption:String,
                 contributors:List[BSONObjectID],
                 tags:List[String])

object Blog {
  val fldId = "_id"
  val fldTitle = "title"
  val fldCaption = "caption"
  val fldContributors = "contributors"
  val fldTags = "tags"
  
  implicit object BlogWriter extends BSONDocumentWriter[Blog] {
    def write(blog: Blog): BSONDocument = BSONDocument(
      fldId -> blog.id.getOrElse(BSONObjectID.generate),
      fldTitle -> blog.title,
      fldCaption -> blog.caption,
      fldContributors -> blog.contributors,
      fldTags -> blog.tags
    )
  }
  
  implicit object BlogReader extends BSONDocumentReader[Blog] {
    def read(doc: BSONDocument): Blog = Blog(
      doc.getAs[BSONObjectID](fldId),
      doc.getAs[String](fldTitle).getOrElse(""),
      doc.getAs[String](fldCaption).getOrElse(""),
      doc.getAs[List[BSONObjectID]](fldContributors).getOrElse(List()),
      doc.getAs[List[String]](fldTags).getOrElse(List())
    )
  }
}
