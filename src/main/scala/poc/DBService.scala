package poc

import java.time.Instant

import poc.Model._
import zio.{IO, UIO}

object DBService {

  trait EventService {
    def get(id: Int): UIO[Event]

    def getAll(ids: List[EventId]): UIO[List[Event]]

    def create(): UIO[Event]

    //    def filterEventByType(eventType: EventType): UIO[List[Event]]
  }

  trait InvestigationService {
    def get(id: Int): UIO[Investigation]

    def getAll(ids: List[InvestigationId]): UIO[List[Investigation]]

    def create(): UIO[Investigation]
  }


  trait PersonService {
    def get(id: Int): UIO[Person]

    def getAll(ids: List[PersonId]): UIO[List[Person]]

    def create(): UIO[Person]
  }


  object EventServiceImpl extends EventService {
    var events = Map[EventId, Event]()

    override def get(id: EventId): UIO[Event] = IO.succeed(events(id))

    override def getAll(ids: List[EventId]): UIO[List[Event]] = IO.succeed(ids.map(events(_)))

    override def create(): UIO[Event] = {
      val newEvent = Event(events.size, Instant.now(), 1, 1)
      events = events + (events.size -> newEvent)

      IO.succeed(newEvent)
    }

  }

  object PersonServiceImpl extends PersonService {
    var items = Map[PersonId, Person]()

    override def get(id: PersonId): UIO[Person] = IO.succeed(items(id))

    override def getAll(ids: List[PersonId]): UIO[List[Person]] = IO.succeed(ids.map(items(_)))

    override def create(): UIO[Person] = {
      val newPerson = Person(items.size, Instant.now(), "hola amigo", 1, List(1))
      items = items + (items.size -> newPerson)

      IO.succeed(newPerson)
    }

  }

  object InvestigationServiceImpl extends InvestigationService {
    var items = Map[InvestigationId, Investigation]()

    override def get(id: InvestigationId): UIO[Investigation] = IO.succeed(items(id))

    override def getAll(ids: List[InvestigationId]): UIO[List[Investigation]] = IO.succeed(ids.map(items(_)))

    override def create(): UIO[Investigation] = {
      val newInvestigation = Investigation(items.size, Instant.now(), List(1, 2, 3))
      items = items + (items.size -> newInvestigation)
      IO.succeed(newInvestigation)
    }

  }

}
