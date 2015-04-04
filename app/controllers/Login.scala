package controllers

//import models.Author
//import models.Author.{AuthorReader, AuthorWriter}
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

  val loginForm:Form[(String,String)] = Form(
    mapping(
      "name"->nonEmptyText,
      "password"->nonEmptyText
      )
      {(name:String,password:String) => (name -> password)}
      {(loginInfo:(String, String)) => Some((loginInfo._1,loginInfo._2))}
  )
  def authenticate = Action.async { implicit  request =>
    val futlogin:Future[Option[String]] = loginForm.bindFromRequest.fold(
      errors => Future.successful(None),
      loginInfo => {
        val futLog = collLogin.find(BSONDocument( "$query" ->
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
      login =>
        val result = Redirect("/",null)
        login match {
          case Some(userid) => result.withSession("bsonid" -> userid)
          case None => result.withSession("bsonid" -> "badid")
        }
    }
  }
  def logout = Action {
    Redirect("/",null).withNewSession
  }
}
