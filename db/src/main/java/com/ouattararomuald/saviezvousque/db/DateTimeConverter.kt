package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * This [DateTimeConverter] provides utility methods to convert ThreeTen's [OffsetDateTime] to [String]
 * and from [String] to [OffsetDateTime].
 */
object DateTimeConverter {
  private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  @JvmStatic
  @TypeConverter
  fun toOffsetDateTime(value: String): OffsetDateTime {
    return formatter.parse(value, OffsetDateTime::from)
  }

  @JvmStatic
  @TypeConverter
  fun fromOffsetDateTime(date: OffsetDateTime): String {
    return date.format(formatter)
  }
}