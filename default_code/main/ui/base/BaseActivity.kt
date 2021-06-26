package cu.jaco.transito.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import cu.jaco.transito.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    private val navController by lazy {
        Navigation.findNavController(this, fragmentContainer())
    }

    private lateinit var mAppBarConfiguration: AppBarConfiguration

    open fun fragmentContainer(): Int = -1

    open fun getDrawerLayout(): DrawerLayout? = null

    open fun getToolbar(): Toolbar? = null

    open fun getNavView(): NavigationView? = null

    private fun visibleFragment(): Fragment? {

        var f = supportFragmentManager.findFragmentById(fragmentContainer())

        if (f is NavHostFragment)
            f = f.childFragmentManager.fragments.firstOrNull()

        return f

    }

    /**
     * Recibir onBackPressed y enviarlo al fragment actualmente visible
     */
    override fun onBackPressed() {

        //close drawer layout on back pressed
        if (getDrawerLayout()?.isDrawerOpen(GravityCompat.START) == true) {
            getDrawerLayout()?.closeDrawer(GravityCompat.START)
            return
        }

        val processed = if (fragmentContainer() != -1) {
            val f = visibleFragment()
            if (f is BaseFragment?)
                f?.onBackPressed() ?: false
            else false
        } else false

        if (!processed)
            super.onBackPressed()

    }

    fun lockDrawerLayout() {
        getDrawerLayout()?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    protected fun unlockDrawerLayout() {
        getDrawerLayout()?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    fun setupDrawerLayout(toolbar: Toolbar, locked: Boolean) {

        getNavView()?.setupWithNavController(navController)
        getDrawerLayout()?.let {
            NavigationUI.setupActionBarWithNavController(this, navController, it)

            if (locked) {
                it.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                val toggle = ActionBarDrawerToggle(
                    this, it, toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
                )

                it.addDrawerListener(toggle)
                it.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                toggle.syncState()
            }

        }

    }

    fun setupDrawerLayout(locked: Boolean = false) =
        getToolbar()?.let { setupDrawerLayout(it, locked) }

    override fun onSupportNavigateUp(): Boolean {
        return getDrawerLayout()?.let {
            NavigationUI.navigateUp(navController, it)
        } ?: super.onSupportNavigateUp()
    }

    fun hideSoftInput() {

        val inm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        var view: View? = currentFocus

        if (view == null) {
            view = EditText(this)
            view.requestFocus()
        }

        if (view.windowToken == null) {
            window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            )
        } else
            inm.hideSoftInputFromWindow(view.windowToken, 0)

    }

    /**
     * Recibir onFragmentClick desde el onClick del xml y enviarlo al fragment actualmente visible
     */
    open fun onFragmentClick(view: View) {
        val f = visibleFragment()
        if (f is BaseFragment)
            f.onFragmentClick(view)
    }

    fun onNavDestinationSelected(
        item: MenuItem,
        args: Bundle,
        navController: NavController
    ): Boolean = onNavDestinationSelected(item.itemId, args, navController)

    fun onNavDestinationSelected(
        itemId: Int,
        args: Bundle?,
        navController: NavController
    ): Boolean {

        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .build()

        return try {
            navController.navigate(itemId, args, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }

    }

}