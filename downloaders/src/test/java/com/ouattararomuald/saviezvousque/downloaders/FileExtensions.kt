@file:JvmName("FilesUtil")

package com.ouattararomuald.saviezvousque.downloaders

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

internal fun File.asString(path: Path): String = String(Files.readAllBytes(path))