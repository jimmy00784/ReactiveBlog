package controllers

import play.api.data.Form
import play.api.mvc._
import play.modules.reactivemongo._
import play.twirl.api.Html
import reactivemongo.api.collections.default._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends Controller with MongoController{

  trait User
  case class InvalidUser() extends User
  case class LoggedInUser(userid:String,username:String) extends User
  case class NoLoggedInUser() extends User

	lazy val collAuthor = db("author")

	/*def index = Action.async { implicit request =>
		val form = Author.form.bindFromRequest
    partialIndex(form)
  }*/

  def generatePage(request:Request[AnyContent],content:Html,authNeeded:Boolean = true): Future[Result] = {
    generatePage(request,() => {Future.successful(content)},authNeeded)
  }

  def generatePage(request:Request[AnyContent],func:(() => Future[Html]),authNeeded:Boolean): Future[Result] = {
    val step1 = for {
      user <- getLoggedInUser(request)
      loginHtml <- getLoginHtmlFuture(request)
      sidebarHtml <- getSidebarHtmlFuture(request,user)
    } yield {
      val result = views.html.index(loginHtml)(sidebarHtml) _
      (user -> result)
    }

    for {
      partial <- step1
      content <- func()
    } yield {
      val (user,result) = partial
      if(authNeeded) {
        user match {
          case LoggedInUser(userid, username) => Ok(result(content))
          case _ => Ok(result(Html("Authentication Required!!!"))).withNewSession
        }
      }
      else {
        Ok(result(content))
      }
    }
  }

  /*def generatePage(request:Request[AnyContent],func:(() => Html),authNeeded:Boolean): Future[Result] = {
    for {
      user <- getLoggedInUser(request)
      loginHtml <- getLoginHtmlFuture(request)
      sidebarHtml <- getSidebarHtmlFuture(request,user)
    } yield {
      val result = views.html.index(loginHtml)(sidebarHtml)_
      lazy val content = func()
      if(authNeeded) {
        user match {
          case LoggedInUser(userid, username) => Ok(result(content))
          case _ => Ok(result(Html("Authentication Required!!!"))).withNewSession
        }
      }
      else {
        Ok(result(content))
      }
    }
  }*/

  def index = Action.async { implicit request =>
    generatePage(request,Html(""),false)
  }

  def getLoggedInUser = { request:Request[AnyContent] =>
    request.session.get("bsonid").map {
      bsonid =>
        if(bsonid == "badid") {
          Future successful InvalidUser()
        }
        else {
          collAuthor.find(BSONDocument("$query" -> BSONDocument("_id" -> BSONObjectID(bsonid)))).
            one[models.Author].map {
            author =>
              author match {
                case Some(a) => LoggedInUser(bsonid,a.name)
                case None => InvalidUser()
              }
          }
        }
    }.getOrElse {
      Future successful NoLoggedInUser()
    }
  }

  def getLoginHtmlFuture = { request:Request[AnyContent] =>

    getLoggedInUser(request).map {
     user =>
      user match
      {
        case InvalidUser() => views.html.badlogin("Invalid credentials.")
        case NoLoggedInUser() => views.html.loginform()
        case LoggedInUser(userid,username) => views.html.authenticated(username)
      }
    }
  }

  def getSidebarHtmlFuture = { (request:Request[AnyContent],user:User) =>
    user match {
      case LoggedInUser(userid,username) => Future successful views.html.sidebar()
      case _ => Future successful views.html.sidebar()
    }

  }

  def partialIndex(form:Form[models.Author]) =  {
    val found = collAuthor.find(BSONDocument(
			"$query" -> BSONDocument()
			)).cursor[models.Author]
		
		found.collect[List]().map{
		f => Ok //(views.html.index(f)(form))
		}.recover {
			case e =>
				e.printStackTrace
				BadRequest(e.getMessage)
		}
	}

	def add = Action.async { implicit request =>
		models.Author.form.bindFromRequest.fold(
			errors => Future.successful(Redirect(routes.Application.index)),
			author => 
      collAuthor.insert(author).zip( partialIndex(models.Author.form.fill(author))).map(_._2)
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
