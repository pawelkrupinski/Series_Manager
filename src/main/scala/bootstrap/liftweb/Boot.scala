package bootstrap.liftweb

import net.liftweb._
import db.ProtoDBVendor
import common._
import http._
import sitemap._
import Loc._
import mapper._
import net.pawel.model.{Episode, Series, User}
import net.pawel.snippet._
import com.google.inject.Inject
import net.pawel.injection.{Database_Connection_Settings, Injected}
import actors.Actor
import util._

object Boot extends Injected {
  @Inject
  var database_settings: Database_Connection_Settings = _

  def schemify = Schemifier.schemify(true, Schemifier.infoF _, User, Series, Episode)

  def destroy = Schemifier.destroyTables_!!(Schemifier.infoF _, User, Series, Episode)

  def set_up_orm(set_vendor: ProtoDBVendor => Unit) {
    if (!DB.jndiJdbcConnAvailable_?) {
      println("DB Driver: " + Props.get("db.url"))
      val vendor = new StandardDBVendor(
        Props.get("db.driver") openOr database_settings.driver,
        Props.get("db.url") openOr database_settings.url,
        database_settings.user,
        database_settings.password)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
      set_vendor(vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Boot.schemify
  }
}

class Boot extends Injected {

  def boot {
    var vendor: ProtoDBVendor = null
    Boot.set_up_orm(vendor = _)
    try {
      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
    } catch {
      case e: IllegalStateException =>
    }

    new Actor {
      def act() {
        Thread.sleep(1000 * 60 * 60)
        while (true) {
          Update.update
          Thread.sleep(1000 * 60 * 60 * 12)
        }
      }
    }.start


    // where to search snippet
    LiftRules.addToPackages("net.pawel")

    // Build SiteMap
    val menuBuilder = List.newBuilder[Menu]

    val loggedIn = If(() => User.loggedIn_?, () => RedirectResponse("/user_mgt/login"))

    menuBuilder ++= List[Menu](
      Menu.i("Home") / "index" >> loggedIn,
      Menu.i("Find series") / "series" / "search" >> loggedIn,
      Menu.i("List series") / "series" / "list" >> loggedIn,
      Menu(Loc("List episodes", List("episode", "list"), "List episodes", Hidden, loggedIn)),
      Menu(Loc("Results", List("series", "results"), "Results", Hidden, loggedIn)),
      Menu(Loc("Seasons", List("series", "seasons"), "Seasons", Hidden, loggedIn))
    )
    menuBuilder ++= User.sitemap

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(menuBuilder.result: _*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
}
