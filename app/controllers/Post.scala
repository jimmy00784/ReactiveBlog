package controllers

import play.modules.reactivemongo._
import play.api.mvc._
import reactivemongo.api.collections.default._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by karim on 4/8/15.
 */
object Post extends Controller with MongoController{

  lazy val collPosts = db("posts")

  def index = Action.async { implicit request =>
    search(BSONDocument())
  }

  def submit = Action.async { implicit request =>

    models.Post.form.bindFromRequest.fold(
      hasErrors => Future.successful(Redirect(routes.Post.index())),
      post => {

        collPosts.save(post)
        Future.successful(Redirect(routes.Post.index()))
      }
    )
  }

  def get(postid:String) = Action.async { implicit request =>
    search(BSONDocument("_id" -> BSONObjectID(postid)))
  }

  def newpost = Action {
    Ok(views.html.newpost())
  }

  def bytag(tag:String) = Action.async { implicit request =>
    search(BSONDocument("tags" -> tag))
  }

  def byauthor(authorid:String) = Action.async { implicit request =>
    search(BSONDocument("authorid" -> BSONObjectID(authorid)))
  }

  def search(selector:BSONDocument) = {
    collPosts.find(selector).cursor[models.Post].collect[List]().map{
      posts =>
        Ok(views.html.posts(posts))
    }
  }

}