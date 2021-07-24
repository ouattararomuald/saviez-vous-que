package com.ouattararomuald.saviezvousque.db.adapters

import com.squareup.sqldelight.ColumnAdapter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class LocalDateTimeAdapter: ColumnAdapter<LocalDateTime, String> {
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  override fun decode(databaseValue: String): LocalDateTime = formatter.parse(databaseValue, LocalDateTime::from)

  override fun encode(value: LocalDateTime): String = value.format(formatter)
}
