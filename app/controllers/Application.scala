package controllers

import models.Author
import play.api.data.Form
import play.api.mvc._
import play.modules.reactivemongo._
import reactivemongo.api.collections.default._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends Controller with MongoController{

	lazy val collAuthor = db("author")

	/*def index = Action.async { implicit request =>
		val form = Author.form.bindFromRequest
    partialIndex(form)
  }*/

  def index = Action.async { implicit request =>
    request.session.get("bsonid").map {
      bsonid =>
        collAuthor.find(BSONDocument("$query" -> BSONDocument("_id" -> BSONObjectID(bsonid)))).
          one[Author].map{
            author =>
              author match {
                case Some(a) => Ok(views.html.index(views.html.authenticated(a.name)))
                case None => Ok(views.html.index(views.html.badlogin("Invalid credentials."))).withNewSession
              }

        }
    }.getOrElse {
      Future.successful(Ok(views.html.index(views.html.loginform())))
    }
  }

  def partialIndex(form:Form[Author]) =  {
    val found = collAuthor.find(BSONDocument(
			"$query" -> BSONDocument()
			)).cursor[Author]
		
		found.collect[List]().map{
		f => Ok //(views.html.index(f)(form))
		}.recover {
			case e =>
				e.printStackTrace
				BadRequest(e.getMessage)
		}
	}

	def add = Action.async { implicit request =>
		Author.form.bindFromRequest.fold(
			errors => Future.successful(Redirect(routes.Application.index)),
			author => 
      collAuthor.insert(author).zip( partialIndex(Author.form.fill(author))).map(_._2)
			)
	}

 /* def test = Action {
    //val author = Author(None,"Karim Lalani","jimmy00784@gmail.com","Software Developer")
    //val authorBson = BSONDocument.pretty(AuthorWriter.write(author))
    //val noresponse = Comment(new java.util.Date(), "Karim","jimmy00784@gmail.com","This is my comment no response",None)
    val comment = Comment(new java.util.Date(),"Karim","jimmy00784@gmail.com","This is my comment",Some(Response(new java.util.Date(), "This is my response")))
    //val post = Post(None,new java.util.Date(),"New Post","This is my new post",BSONObjectID.generate,BSONObjectID.generate,List("Linux","Open Source"),List())
    val commentBson = BSONDocument.pretty(CommentWriter.write(comment))

    Ok(views.html.index(commentBson))
  }*/

}
