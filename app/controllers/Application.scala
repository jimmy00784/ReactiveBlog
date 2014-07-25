package controllers

import models.Comment.CommentWriter
import models.Post.PostWriter
import models.{Post, Comment, Author, Response}
import models.Author.AuthorWriter
import play.api._
import play.api.mvc._
import reactivemongo.bson.{BSONObjectID, BSONDocument}

object Application extends Controller {

  def index = Action {
    //val author = Author(None,"Karim Lalani","jimmy00784@gmail.com","Software Developer")
    //val authorBson = BSONDocument.pretty(AuthorWriter.write(author))
    //val noresponse = Comment(new java.util.Date(), "Karim","jimmy00784@gmail.com","This is my comment no response",None)
    val comment = Comment(new java.util.Date(),"Karim","jimmy00784@gmail.com","This is my comment",Some(Response(new java.util.Date(), "This is my response")))
    //val post = Post(None,new java.util.Date(),"New Post","This is my new post",BSONObjectID.generate,BSONObjectID.generate,List("Linux","Open Source"),List())
    val commentBson = BSONDocument.pretty(CommentWriter.write(comment))

    Ok(views.html.index(commentBson))
  }

}
