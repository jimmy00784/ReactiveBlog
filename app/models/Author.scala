package models

/**
 * Created by karim on 7/23/14.
 */

import reactivemongo.bson._

case class Author (id: Option[BSONObjectID], name: String, email: String, bio: String)

object Author {
  implicit object AuthorWriter extends BSONDocumentWriter[Author] {
    def write(author: Author):BSONDocument = BSONDocument(
      "_id" -> author.id.getOrElse(BSONObjectID.generate),
      "name" -> author.name,
      "email" -> author.email,
      "bio" -> author.bio
    )
  }
  implicit object AuthorReader extends BSONDocumentReader[Author] {
    def read(doc: BSONDocument):Author = Author(
      doc.getAs[BSONObjectID]("_id"),
      doc.getAs[String]("name").getOrElse(""),
      doc.getAs[String]("email").getOrElse(""),
      doc.getAs[String]("bio").getOrElse("")
    )
  }
}
