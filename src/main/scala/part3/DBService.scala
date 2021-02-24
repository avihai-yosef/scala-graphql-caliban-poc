package part3

import java.time.Instant

import part3.Data.{Event, EventId, EventType, Participant, ParticipantId}
import part3.Data.Event._
import part3.Data.EventType.MESSAGE
import zio.{IO, UIO}

object DBService {

  trait EventService {
    def getEvent(id: Int): UIO[Event]

    def getEvents(ids: List[EventId]): UIO[List[Event]]

    def filterEventByType(eventType: EventType): UIO[List[Event]]
  }


  object EventServiceImpl extends EventService {
    var events = Map(
      1 -> MessageEvent(1, Instant.now(), "Message content", List(1, 2, 3)),
      2 -> CallEvent(1, Instant.now().minusSeconds(1000), duration = 1000, List()),
    )

    override def getEvent(id: Int): UIO[Event] = IO.succeed(events(id))

    override def getEvents(ids: List[EventId]): UIO[List[Event]] = IO.succeed(events.values.toList)

    override def filterEventByType(eventType: EventType): UIO[List[Event]] = {

      val result = eventType match {
        case MESSAGE => events.values.collect({ case a: MessageEvent => a.widen }).toList
        case _ => events.values.collect({ case a: CallEvent => a.widen }).toList
      }

      IO.succeed(result)
    }


  }

  object PersonService {
    var persons = Map(
      1 -> Participant(1, "Person 1"),
      2 -> Participant(2, "Person 2"),
      3 -> Participant(3, "Person 3"),
    )

    def getParticipant(id: Int): UIO[Participant] = IO.succeed(persons(id))

    def getParticipants(ids: List[ParticipantId]): UIO[List[Participant]] = IO.succeed(persons.values.toList)


  }

}
