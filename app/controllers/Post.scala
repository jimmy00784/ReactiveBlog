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
    searchResults(request,BSONDocument())
  }

  def submit = Action.async { implicit request =>

    Application.getLoggedInUser(request).map{
      user =>
        user match {
          case Application.LoggedInUser(userid,username) => {
            models.Post.form.bindFromRequest.fold(
              hasErrors => Redirect(routes.Post.index()),
              post => {
                val postWithAuthorInfo = models.Post(
                  post.id,
                  post.date,
                  post.title,
                  post.content,
                  BSONObjectID(userid),
                  username,
                  post.tags,
                  post.comments
                )
                collPosts.save(postWithAuthorInfo)
                Redirect(routes.Post.index())
              }
            )
          }
          case _ => Redirect(routes.Post.index())
        }
    }
  }

  def get(postid:String) = Action.async { implicit request =>
    searchResults(request,BSONDocument("_id" -> BSONObjectID(postid)))
  }

  def newpost = Action.async { implicit request =>
    Application.generatePage(request,views.html.newpost())
  }

  def bytag(tag:String) = Action.async { implicit request =>
    searchResults(request,BSONDocument("tags" -> tag))
  }

  def byauthor(authorid:String) = Action.async { implicit request =>
    searchResults(request,BSONDocument("authorid" -> BSONObjectID(authorid)))
  }

  def search(selector:BSONDocument) = {
    collPosts.find(selector).cursor[models.Post].collect[List]().map{
      posts =>
        views.html.posts(posts)
    }
  }

  def searchResults(request:Request[AnyContent], selector:BSONDocument) = {
    for{
      posts <- search(selector)
      page <- Application.generatePage(request,posts,false)
    } yield {
      page
    }
  }

}