package controllers

import play.api.data.Form
import play.api.mvc._
import play.modules.reactivemongo._
import reactivemongo.api._
import reactivemongo.api.collections.default._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Author extends Controller with MongoController {

	lazy val collAuthor = db("author")

  def index = Action.async { implicit request =>

		val authorhtmlfut = collAuthor.find(BSONDocument()).cursor[models.Author].collect[List]().map{
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
		val authorhtmlfut = collAuthor.find(BSONDocument("_id" -> BSONObjectID(authorid))).cursor[models.Author].collect[List]().map{
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
		models.Author.form.bindFromRequest.fold(
			hasErrors => Future.successful(Redirect(routes.Author.index())),
			author => {
				collAuthor.save(author)
				Future.successful(Redirect(routes.Author.index()))
			}
		)

	}

}
