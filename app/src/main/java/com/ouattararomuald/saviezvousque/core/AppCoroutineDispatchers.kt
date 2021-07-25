package com.ouattararomuald.saviezvousque.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

data class AppCoroutineDispatchers @Inject constructor(
  val io: CoroutineDispatcher = Dispatchers.IO,
  val main: CoroutineDispatcher = Dispatchers.Main.immediate,
  val computation: CoroutineDispatcher = Dispatchers.Default,
)