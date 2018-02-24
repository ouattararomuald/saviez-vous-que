package com.ouattararomuald.saviezvousque.posts

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.ouattararomuald.saviezvousque.R


class HomeActivity : AppCompatActivity() {

  private lateinit var drawerLayout: DrawerLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.home_activity)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    val actionbar = supportActionBar!!
    actionbar.apply {
      setDisplayHomeAsUpEnabled(true)
      actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    drawerLayout = findViewById(R.id.drawer_layout)

    val navigationView = findViewById<NavigationView>(R.id.nav_view)
    navigationView.setNavigationItemSelectedListener { menuItem ->
      menuItem.isChecked = true
      drawerLayout.closeDrawers()
      true
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        drawerLayout.openDrawer(GravityCompat.START)
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }
}