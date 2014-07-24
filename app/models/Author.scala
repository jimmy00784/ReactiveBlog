package models

/**
 * Created by karim on 7/23/14.
 */

import reactivemongo.bson._

case class Author (id: Option[BSONObjectID], name: String, email: String, bio: String)

object Author {
  val fldId = "_id"
  val fldName = "name"
  val fldEmail = "email"
  val fldBio = "bio"

  implicit object AuthorWriter extends BSONDocumentWriter[Author] {
    def write(author: Author):BSONDocument = BSONDocument(
      fldId -> author.id.getOrElse(BSONObjectID.generate),
      fldName -> author.name,
      fldEmail -> author.email,
      fldBio -> author.bio
    )
  }
  implicit object AuthorReader extends BSONDocumentReader[Author] {
    def read(doc: BSONDocument):Author = Author(
      doc.getAs[BSONObjectID](fldId),
      doc.getAs[String](fldName).getOrElse(""),
      doc.getAs[String](fldEmail).getOrElse(""),
      doc.getAs[String](fldBio).getOrElse("")
    )
  }
}
