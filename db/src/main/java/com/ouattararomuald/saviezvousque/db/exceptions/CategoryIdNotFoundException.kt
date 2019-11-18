package com.ouattararomuald.saviezvousque.db.exceptions

import java.lang.Exception

class CategoryIdNotFoundException(categoryId: Int) : Exception("Category with id: $categoryId has not been found.")
