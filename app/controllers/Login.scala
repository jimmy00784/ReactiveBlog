package controllers

//import models.Author
//import models.Author.{AuthorReader, AuthorWriter}
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import play.api.mvc.{Action,Controller}
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * Created by karim on 7/26/14.
 */
object Login extends Controller with MongoController {

  lazy val collLogin = db[BSONCollection]("users")
  lazy val collAuthor = db[BSONCollection]("author")

  val loginForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    )
    {(name,password) => (name -> password)}
    {(loginInfo) => Some((loginInfo._1,loginInfo._2))}
  )
  def authenticate = Action.async { implicit  request =>
    loginForm.bindFromRequest.fold(
      errors => Future.successful(Redirect(routes.Application.index)),
      loginInfo => {
        val futLog = collLogin.find(BSONDocument("$query" ->
          BSONDocument(
            "user" -> loginInfo._1,
            "password" -> loginInfo._2
          ))).one
        val futAut = (email: String) => collAuthor.find(BSONDocument("$query" ->
              BSONDocument("email" -> email))).one
        for {
          log <- futLog
          aut <- futAut(log.map { case l => l.getAs[String]("user").getOrElse("") }.getOrElse(""))
        } yield log match {
          case Some(l) => {
            aut match {
              case Some(a) => {
                val user = models.Author.AuthorReader.read(a)
                Ok(views.html.index(views.html.authenticated(user.name))).withSession("bsonid" -> user.id.get.stringify)
              }
              case None => Ok(views.html.index(views.html.badlogin("Invalid credentials."))).withNewSession
            }
          }
          case None => Ok(views.html.index(views.html.badlogin("Invalid credentials."))).withNewSession
        }
      }
    )
  }
  def logout = Action {
    Redirect("/").withNewSession
  }
}
