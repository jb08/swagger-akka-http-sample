package com.example.akka.add

import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import io.swagger.annotations._

import com.example.akka.DefaultJsonFormats
import com.example.akka.add.AddActor._

@Api(value = "/multiply", produces = "application/json")
@Path("/multiply")
class AddService(addActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)

  implicit val requestFormat = jsonFormat1(AddRequest)
  implicit val responseFormat = jsonFormat1(AddResponse)

  val route = add

  @ApiOperation(value = "Multiply integers", nickname = "addIntegers", httpMethod = "POST", response = classOf[AddResponse])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "\"numbers\" to multiply", required = true,
        dataTypeClass = classOf[AddRequest], paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error by Barry")
  ))
  def add =
    path("multiply") {
      post {
        entity(as[AddRequest]) { request =>
          complete { (addActor ? request).mapTo[AddResponse] }
        }
      }
    }

}
