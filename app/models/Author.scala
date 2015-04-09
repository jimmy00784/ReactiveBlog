package models

/**
 * Created by karim on 7/23/14.
 */

import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._
import reactivemongo.bson._

case class Author (id: Option[BSONObjectID], name: String, email: String, bio: String, pic: String = "")

object Author {
  val fldId = "_id"
  val fldName = "name"
  val fldEmail = "email"
  val fldBio = "bio"
  val fldPic = "pic"

  implicit object AuthorWriter extends BSONDocumentWriter[Author] {
    def write(author: Author):BSONDocument = BSONDocument(
      fldId -> author.id.getOrElse(BSONObjectID.generate),
      fldName -> author.name,
      fldEmail -> author.email,
      fldBio -> author.bio,
      fldPic -> author.pic
    )
  }
  implicit object AuthorReader extends BSONDocumentReader[Author] {
    def read(doc: BSONDocument):Author = Author(
      doc.getAs[BSONObjectID](fldId),
      doc.getAs[String](fldName).getOrElse(""),
      doc.getAs[String](fldEmail).getOrElse(""),
      doc.getAs[String](fldBio).getOrElse(""),
      doc.getAs[String](fldPic).getOrElse("")
    )
  }

  val form = Form(
    mapping(
      fldId -> optional(of[String] verifying pattern(
        """[a-fA-F0-9]{24}""".r,
        "constraint.objectId",
        "error.objectId")),
      fldName -> nonEmptyText,
      fldEmail -> nonEmptyText,
      fldBio -> text,
      fldPic -> text) { (id, name, email, bio, pic) =>
      Author(
        id.map(BSONObjectID(_)),
        name,
        email,
        bio,
        pic
      )
    }{ author => Some(author.id.map(_.stringify),author.name, author.email,author.bio,author.pic)}
  )
}
