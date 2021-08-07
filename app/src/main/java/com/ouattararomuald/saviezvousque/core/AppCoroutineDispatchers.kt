package com.ouattararomuald.saviezvousque.core

import kotlinx.coroutines.CoroutineDispatcher

data class AppCoroutineDispatchers(
  val io: CoroutineDispatcher,
  val main: CoroutineDispatcher,
  val computation: CoroutineDispatcher,
)