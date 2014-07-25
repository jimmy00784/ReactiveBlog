package controllers

import models.Comment.CommentWriter
import models.Post.PostWriter
import models.{Post, Comment, Author, Blog}
import models.Blog.{BlogReader,BlogWriter}
import models.Author.AuthorWriter
import play.api._
import play.api.mvc._
import reactivemongo.bson.{BSONObjectID, BSONDocument}

object AuthorController extends Controller {

  def index = Action {
	Ok(views.html.author(List()))
  }

	def submit = Action { implicit request =>
		Ok
	}

}
