package part3

import java.time.Instant

import caliban.GraphQL.graphQL
import caliban.RootResolver
import part3.DBService.EventServiceImpl
import part3.Data._
import zio.query.{DataSource, Request, ZQuery}
import zio.{Chunk, UIO}

object MyApi {

  // API definition

  type MyQuery[+A] = ZQuery[Any, Nothing, A] // kind like Resolvable?


  case class GetEventArgs(id: EventId)

  case class GetEventsArgs(ids: List[EventId])

  case class FilterEventByTypeArgs(eventType: EventType)

  case class EventView(id: EventId, createdAt: Instant, participants: MyQuery[List[Participant]])

  case class Queries(
                      getEvent: GetEventArgs => MyQuery[EventView],
                      getEvents: GetEventsArgs => MyQuery[List[EventView]],
                      filterEventByType: FilterEventByTypeArgs => UIO[List[Event]]
                    )


  def resolver(): Queries = {


    case class GetPerson(id: ParticipantId) extends Request[Nothing, Participant]

    val PersonDataSource: DataSource[Any, GetPerson] =
      DataSource.fromFunctionBatchedM("PersonDataSource")(
        requests => DBService.PersonService.getParticipants(requests.map(_.id).toList).map(Chunk.fromIterable)
      )

    def getPerson(id: ParticipantId): MyQuery[Participant] = ZQuery.fromRequest(GetPerson(id))(PersonDataSource)

    def getPersons(ids: List[ParticipantId]): MyQuery[List[Participant]] = ZQuery.foreachPar(ids)(getPerson)

    def getEvent(id: EventId): MyQuery[EventView] = {

      ZQuery
        .fromEffect(EventServiceImpl
          .getEvent(id))
        .map(event => EventView(event.id, event.createdAt, getPersons(event.participants)))

    }

    def getEvents(ids: List[EventId]): MyQuery[List[EventView]] = {
      ZQuery
        .fromEffect(DBService.EventServiceImpl.getEvents(ids))
        .map(_.map(event => EventView(event.id, event.createdAt, getPersons(event.participants))))
    }

    Queries(
      args => getEvent(args.id),
      args => getEvents(args.ids),
      // TODO: currently sum type is not supported as inputValue, should check for a workaround
      //  https://ghostdogpr.github.io/caliban/faq/#can-i-use-a-union-as-input
      args => DBService.EventServiceImpl.filterEventByType(args.eventType))
  }


  // interpreter
  val interpreter = graphQL(RootResolver(resolver())).interpreter
}


/*
Things to consider:
1. its use magnolia, we should learn how to help this lib to generate less code and reduce compile time.

Checklist:
1. enum type as input - DONE.
2. Sum type - DONE.
3. batching
4. deduplicate queries
5. query only selected fields from DB - need additional effort to implement it.
6. learn how to write schemas by hand - NOW
7. generate case classes from existing graphql schema.
8. union type as input - need to try to define our scalar.
9.  subscriptions.
10. mutations.
11. integrate with DB, like postgres or something.
12. handle authorization.
13. middlewares.
14. understand ZIO.
15. federation
16. join graphql queries definitions.(semigroup)
17. Error handling, schema error/resolving error.
18. Unit test?



Data will be:
* We need 3 level nesting
* We need 2 entities that has foreign key to the same resource
* we sum type.
* we need sum type as input argument.
* we need users? with role based authorization

 */