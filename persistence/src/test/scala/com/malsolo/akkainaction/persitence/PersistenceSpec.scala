package com.malsolo.akkainaction.persitence

import java.io.File

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.Config
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.util.Try

abstract class PersistenceSpec(system: ActorSystem) extends TestKit(system)
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll with PersitenceCleanup {

  def this(name: String, config: Config) = this(ActorSystem(name, config))

  override protected def beforeAll(): Unit = deleteStorageLocations()

  override protected def afterAll(): Unit = {
    deleteStorageLocations()
    TestKit.shutdownActorSystem(system)
  }

  def killActors(actors: ActorRef*) = {
    actors.foreach { actor =>
      watch(actor)
      system.stop(actor)
      expectTerminated(actor)
      Thread.sleep(1000)
    }
  }

}

trait PersitenceCleanup {
  def system: ActorSystem

  val storageLocations = List(
    "akka.persistence.journal.leveldb.dir",
    "akka.persistence.journal.leveldb-shared.store.dir",
    "akka.persistence.snapshot-store.local.dir"
  ).map {s =>
    new File(system.settings.config.getString(s))
  }

  def deleteStorageLocations(): Unit = {
    storageLocations.foreach { dir =>
      println(s"Deleting directory ${dir.getAbsolutePath}")
      Try(FileUtils.deleteDirectory(dir))
    }
  }
}
