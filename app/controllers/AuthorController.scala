package controllers

import models.Author
import play.api.data.Form
import play.api.mvc._
import play.modules.reactivemongo._
import reactivemongo.api._
import reactivemongo.api.collections.default._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AuthorController extends Controller with MongoController {

	lazy val collAuthor = db("author")

  def index = Action.async { implicit request =>

		val authorhtmlfut = collAuthor.find(BSONDocument()).cursor[Author].collect[List]().map{
			list =>
				views.html.author(list)
		}

		for{
			authorpage <- authorhtmlfut
			page <- Application.generatePage(request,authorpage)
		} yield page
		//Ok(views.html.author(List()))
  }

	def get(authorid: String) = Action.async { implicit request =>
		val authorhtmlfut = collAuthor.find(BSONDocument("_id" -> BSONObjectID(authorid))).cursor[Author].collect[List]().map{
			list =>
				views.html.author(list)
		}

		for{
			authorpage <- authorhtmlfut
			page <- Application.generatePage(request,authorpage)
		} yield page

	}

	def delete(authorid: String) = Action.async { implicit request =>
		Future.successful(Ok)
	}

	def submit = Action.async { implicit request =>
		Author.form.bindFromRequest.fold(
			hasErrors => Future.successful(Redirect(routes.AuthorController.index())),
			author => {
				collAuthor.save(author)
				Future.successful(Redirect(routes.AuthorController.index()))
			}
		)

	}

}
