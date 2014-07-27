package controllers

import play.api.mvc._

object AuthorController extends Controller {

  def index = Action {
	Ok(views.html.author(List()))
  }

	def submit = Action { implicit request =>
		Ok
	}

}
