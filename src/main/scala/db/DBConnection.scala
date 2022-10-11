package db

import akka.actor.ActorSystem
import com.couchbase.client.scala.env.{ClusterEnvironment, TimeoutConfig}
import com.couchbase.client.scala.manager.collection.CollectionManager
import com.couchbase.client.scala.{Bucket, Cluster, ClusterOptions}

import scala.concurrent.duration.DurationInt

object DBConnection {

  implicit val actor: ActorSystem = ActorSystem("bet-service")
  val username = "Administrator"
  val password = "mypassword"

  val env: ClusterEnvironment = ClusterEnvironment.builder
    .timeoutConfig(
      TimeoutConfig()
        .kvTimeout(10.seconds)
    )
    .build
    .get

  val cluster: Cluster = Cluster
    .connect(
      "couchbase://localhost",
      ClusterOptions
        .create(username, password)
        .environment(env)
    )
    .get




  cluster.bucket("").defaultCollection
}
