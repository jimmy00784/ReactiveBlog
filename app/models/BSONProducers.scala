package models

import java.util.Date
import reactivemongo.bson.{BSONDateTime,BSONReader,BSONWriter}

object BSONProducers {
  implicit object DateWriter extends BSONWriter[Date,BSONDateTime] {
    def write(dt:Date) : BSONDateTime = BSONDateTime(dt.getTime)
  }
  implicit object DateReader extends BSONReader[BSONDateTime,Date] {
    def read(dt:BSONDateTime) : Date = new Date(dt.value)
  }
}
