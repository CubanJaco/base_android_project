package cu.jaco.transito

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import cu.jaco.transito.databinding.ActivityMainBinding
import cu.jaco.transito.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun fragmentContainer(): Int = R.id.nav_host_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

}