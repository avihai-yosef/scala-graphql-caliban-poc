package part3

import java.time.Instant

import caliban.schema.Annotations.GQLInterface

object Data {

  type ParticipantId = Int
  type EventId = Int

  sealed trait EventType

  object EventType {

    case object MESSAGE extends EventType

    case object CALL extends EventType

  }


  @GQLInterface // this should convert it to interface instead of union
  sealed trait Event {
    val id: EventId
    val createdAt: Instant
    val participants: List[ParticipantId]
    def widen = this
  }

  object Event {

    case class MessageEvent(id: EventId, createdAt: Instant, content: String, participants: List[ParticipantId]) extends Event

    case class CallEvent(id: EventId, createdAt: Instant, duration: Int, participants: List[ParticipantId]) extends Event

  }


  case class Participant(id: ParticipantId, name: String)

  case class EventNotFound(id: Int)

}
