package base

import net.pawel.services.Series_Service._
import net.pawel.injection.Integration_Configuration
import net.pawel.model.{Episode, Series}
import org.junit.Assert._
import org.hamcrest.CoreMatchers._
import org.junit.{Before, Test}
import collection.immutable.List
import bootstrap.liftweb.Boot

trait Prepare_Orm {
  Boot.set_up_orm(_ => Unit)
}