package poc

import java.time.Instant

object Model {
  type InvestigationId = Int
  type PersonId = Int
  type EventId = Int

  case class Investigation(id: InvestigationId, createdAt: Instant, persons: List[PersonId])

  case class Person(id: PersonId, createdAt: Instant, name: String, investigationId: InvestigationId, events: List[EventId])

  case class Event(id: EventId, createdAt: Instant, investigationId: InvestigationId, person: PersonId)

}
