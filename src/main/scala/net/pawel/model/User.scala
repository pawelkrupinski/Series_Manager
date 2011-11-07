package net.pawel.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import org.openid4java.discovery.DiscoveryInformation
import org.openid4java.message.AuthRequest
import net.liftweb.openid._

object ExtSession extends ExtSession with MetaProtoExtendedSession[ExtSession] {
  override def dbTableName = "ext_session" // define the DB table name

  def logUserIdIn(uid: String): Unit = User.logUserIdIn(uid)

  def recoverUserId: Box[String] = User.currentUserId

  type UserType = User
}

class ExtSession extends ProtoExtendedSession[ExtSession] {
  def getSingleton = ExtSession // what's the "meta" server
}

object User extends User with MetaMegaProtoUser[User] {
//  def openIDVendor = MyVendor
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content"><lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email, locale, timezone, password)
  // comment this line out to require email validations
  override def skipEmailValidation = true

  onLogIn = List(ExtSession.userDidLogin(_))
  onLogOut = List(ExtSession.userDidLogout(_))
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server


}
