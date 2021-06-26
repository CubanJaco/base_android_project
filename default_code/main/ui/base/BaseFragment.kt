package cu.jaco.transito.ui.base

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import cu.jaco.transito.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    private var close = false

    val mParentFragment: Fragment?
        get() {
            val parent = parentFragment
            return if (parent is NavHostFragment)
                parent.parentFragment
            else parent
        }

    open fun getToolbar(): Toolbar? = null

    open fun showHomeAsUp(): Boolean = false

    open fun showDrawer(): Boolean = false

    open fun showTitle(): Boolean = true

    open fun titleRes(): Int = -1

    open fun titleText(): String = findNavController().currentDestination?.label?.toString() ?: ""

    open fun onFragmentClick(view: View) {}

    open fun onBackPressed(): Boolean = false

    open fun onAttachToParentFragment(fragment: Fragment?) {}

    fun requireBaseActivity(): BaseActivity {
        val activity = requireActivity()
        if (activity is BaseActivity)
            return activity
        throw IllegalStateException("Can't get BaseActivity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onAttachToParentFragment(mParentFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getToolbar()?.let { setSupportActionBar(it) }
    }

    private fun setSupportActionBar(toolbar: Toolbar) {
        val activity = requireActivity()
        if (activity !is AppCompatActivity)
            throw IllegalStateException("Activity must be AppCompatActivity")
        activity.setSupportActionBar(toolbar)
        prepareToolbar(toolbar)
    }

    private fun prepareToolbar(toolbar: Toolbar) {

        val title = when {
            !showTitle() -> ""
            titleRes() != -1 -> requireContext().getString(titleRes())
            titleText().isNotBlank() -> titleText()
            else -> ""
        }

        val activity = requireActivity()
        if (activity !is AppCompatActivity)
            throw IllegalStateException("Activity must be AppCompatActivity")

        if (showHomeAsUp()) {
            setHasOptionsMenu(true)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp())
        }
        setupDrawerLayout(toolbar)

        if (title.isNotBlank() || !showTitle())
            activity.supportActionBar?.title = title

    }

    private fun setupDrawerLayout(toolbar: Toolbar) {

        val activity = requireActivity()
        if (activity !is BaseActivity)
            throw IllegalStateException("Activity must be BaseActivity")

        activity.setupDrawerLayout(toolbar, showHomeAsUp() || !showDrawer())

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (!findNavController().popBackStack())
                    onExitPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onExitPressed() {
        val activity = requireActivity()
        if (activity !is AppCompatActivity)
            throw IllegalStateException("Activity must be AppCompatActivity")
        activity.onBackPressed()
    }

    fun hideSoftInput() {

        val inm: InputMethodManager =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        var view: View? = activity?.currentFocus

        if (view == null || view !is EditText) {
            view = EditText(requireActivity())
            view.requestFocus()
        }

        if (view.windowToken == null) {
            requireActivity().window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            )
        } else
            inm.hideSoftInputFromWindow(view.windowToken, 0)

    }

    fun onBackExit() {
        if (close)
            activity?.finish()

        if (!close)
            Toast.makeText(requireContext(), R.string.one_more_time, Toast.LENGTH_SHORT).show()

        close = true

        Handler(Looper.getMainLooper()).postDelayed({ close = false }, 3000)
    }

    protected fun lockDrawerLayout() {
        val activity = requireActivity()
        if (activity is BaseActivity)
            activity.lockDrawerLayout()
        else throw IllegalStateException("Activity must be BaseActivity")
    }
}