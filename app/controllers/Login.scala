package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import play.api.mvc.{Action,Controller}
import play.modules.reactivemongo._
import play.twirl.api.Html
import reactivemongo.api.collections.default._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * Created by karim on 7/26/14.
 */
object Login extends Controller with MongoController {

  lazy val collLogin = db("users")
  lazy val collAuthor = db("author")

  def authenticate = Action.async { implicit  request =>
    val futlogin = models.User.form.bindFromRequest.fold(
      errors => {
        Future.successful(None)
      },
      login => {
        val futLog = collLogin.find(BSONDocument("user" -> login.user,"password" -> login.password)).one
        val futAut = (email: String) => collAuthor.find(BSONDocument("email" -> email)).one
        for {
          log <- futLog
          aut <- futAut(log.map { case l => l.getAs[String]("user").getOrElse("") }.getOrElse(""))
        } yield log match {
          case Some(l) => {
            aut match {
              case Some(a) => {
                val user = models.Author.AuthorReader.read(a)
                Some(user.id.get.stringify)
              }
              case None => None
            }
          }
          case None => None
        }
      }
    )
    futlogin.map{
      login => {
        val result = Redirect("/", null)
        login match {
          case Some(userid) => result.withSession("bsonid" -> userid)
          case None => result.withSession("bsonid" -> "badid")
        }
      }
    }
  }
  def logout = Action {
    Redirect("/",null).withNewSession
  }
}
