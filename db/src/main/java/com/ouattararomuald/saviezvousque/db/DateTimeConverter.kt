package com.ouattararomuald.saviezvousque.db

import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * This [DateTimeConverter] provides utility methods to convert ThreeTen's [OffsetDateTime] to [String]
 * and from [String] to [OffsetDateTime].
 */
object DateTimeConverter {
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  @JvmStatic
  fun toLocalDateTime(value: String): LocalDateTime = formatter.parse(value, LocalDateTime::from)

  @JvmStatic
  fun fromLocalDateTime(date: LocalDateTime): String = date.format(formatter)
}