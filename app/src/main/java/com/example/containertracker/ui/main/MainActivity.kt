package com.example.containertracker.ui.main

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.location.models.Location
import com.example.containertracker.data.user.models.User
import com.example.containertracker.databinding.ActivityMainBinding
import com.example.containertracker.ui.selectlocation.SelectLocationBottomSheet
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.PreferenceUtils.USER_PREFERENCE
import com.example.containertracker.widget.DropDownWidget
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.containertracker.BuildConfig
import com.example.containertracker.databinding.LogoutDialogBinding
import com.example.containertracker.ui.login.LoginActivity
import com.example.containertracker.utils.enums.RoleAccessEnum
import com.example.containertracker.utils.extension.observeNonNull
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.response.GenericErrorResponse
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), NavController.OnDestinationChangedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private val user by lazy {
        PreferenceUtils.get<User>(USER_PREFERENCE)
    }
    private lateinit var placementDropDown: DropDownWidget
    private var isFirstInit: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getLocations(user?.id.orEmpty())

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        placementDropDown = headerView.findViewById<DropDownWidget>(R.id.placementDropDownWidget)
        val username = headerView.findViewById<TextView>(R.id.username)
        val btnLogOut = headerView.findViewById<TextView>(R.id.button_logout)
        val tvVersion = binding.tvVersion
        tvVersion.text =
            getString(R.string.version_format, BuildConfig.VERSION_NAME)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_container,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_scan_local_sales,
                R.id.nav_scan_flexi,
                R.id.nav_scan_marking,
                R.id.nav_scan_seal,
                R.id.nav_scan_isotank,
                R.id.nav_container_repair,
                R.id.nav_container_tally
            ), drawerLayout
        )
        setNavDrawer(navView)
        setNavController()
        placementDropDown.setTitle("Location")
        viewModel.userData.value?.let { user ->
            when (user.departmentId) {
                RoleAccessEnum.CY.value, RoleAccessEnum.INFINITY.value -> placementDropDown.isVisible =
                    false

                else -> placementDropDown.isVisible = true
            }
        }
        username.text = user?.name
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        btnLogOut.setOnClickListener {
            drawerLayout.closeDrawers()
            logout()
        }

        observer()
        listener()
    }

    private fun listener() {
        placementDropDown.onClickDropDownListener {
            selectLocation()
        }
    }

    private fun observer() {
        viewModel.apply {
            locationLiveData.observe(this@MainActivity, ::handleUpdateLocation)
            locationListLiveData.observe(this@MainActivity, ::handleUpdateLocationList)
            loadingWidgetLiveData.observeNonNull(this@MainActivity) { handleLoadingWidget(it) }
            isContainerLaden.observe(this@MainActivity) {
                isFirstInit = true
                viewModel.getLocations(user?.id.orEmpty())
            }
            serverErrorState.observe(this@MainActivity) {
                handleErrorWidget(it)
            }
        }
    }

    private fun handleErrorWidget(genericErrorResponse: GenericErrorResponse?) {
        showErrorMessage(genericErrorResponse?.status ?: "Terjadi masalah pada server")
    }

    private fun handleUpdateLocationList(list: List<Location>) {
        if (isFirstInit) {
            if (list.size > 1) {
                placementDropDown.setEnabledClick(true)
                viewModel.userData.value?.let { user ->
                    when (user.departmentId) {
                        RoleAccessEnum.CY.value, RoleAccessEnum.INFINITY.value, RoleAccessEnum.TALLY.value -> {}
                        else -> selectLocation()
                    }
                }
            } else {
                placementDropDown.setEnabledClick(false)
                if (viewModel.locationListLiveData.value.isNullOrEmpty()) {
                    viewModel.locationLiveData.value =
                        Location(id = "", name = getString(R.string.general_action_select))
                } else {
                    viewModel.locationLiveData.value = viewModel.locationListLiveData.value?.first()
                    viewModel.locationListLiveData.value?.let { handleUpdateLocation(it.first()) }
                }
            }
            isFirstInit = false
        }
    }

    private fun handleUpdateLocation(location: Location) {
        if (location.id.isNotEmpty())
            placementDropDown.setText(location.name)
        navController.addOnDestinationChangedListener(this)
    }

    private fun selectLocation() {
        val selectPostBottomSheet = SelectLocationBottomSheet(
            isContainerLaden = viewModel.isContainerLaden.value.orFalse()
        ) { location, list ->
            viewModel.locationListLiveData.value = list
            viewModel.locationLiveData.value = location
        }
        selectPostBottomSheet.isCancelable = false
        selectPostBottomSheet.show(supportFragmentManager, selectPostBottomSheet.tag)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun logout() {
        val dialog = Dialog(this)

        val bindingToast: LogoutDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.logout_dialog, null, false
        )

        bindingToast.lifecycleOwner = this

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(bindingToast.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingToast.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        bindingToast.btnYes.setOnClickListener {
            PreferenceUtils.put(null, USER_PREFERENCE)
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_left, R.anim.nav_default_enter_anim)
            finish()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.nav_home ->
                binding.appBarMain.toolbar.title = getString(
                    R.string.scanner_title_toolbar,
                    viewModel.locationLiveData.value?.name.toString()
                )

            R.id.nav_scan_local_sales ->
                binding.appBarMain.toolbar.title = getString(
                    R.string.scanner_title_toolbar,
                    viewModel.locationLiveData.value?.name.toString()
                )
        }
    }

    private fun setNavDrawer(navView: NavigationView) {
        when (viewModel.userData.value?.departmentId.orEmpty()) {
            RoleAccessEnum.CY.value -> {
                navView.menu.removeItem(R.id.nav_home)
                navView.menu.removeItem(R.id.nav_container)
                navView.menu.removeItem(R.id.nav_gallery)
                navView.menu.removeItem(R.id.nav_slideshow)
                navView.menu.removeItem(R.id.nav_scan_local_sales)
                navView.menu.removeItem(R.id.nav_scan_flexi)
                navView.menu.removeItem(R.id.nav_container_tally)
                navView.menu.removeItem(R.id.nav_scan_marking_local_sales)
                navView.menu.removeItem(R.id.nav_scan_seal_local_sales)
            }

            RoleAccessEnum.INFINITY.value -> {
                navView.menu.removeItem(R.id.nav_home)
                navView.menu.removeItem(R.id.nav_container)
                navView.menu.removeItem(R.id.nav_gallery)
                navView.menu.removeItem(R.id.nav_slideshow)
                navView.menu.removeItem(R.id.nav_scan_local_sales)
                navView.menu.removeItem(R.id.nav_scan_marking)
                navView.menu.removeItem(R.id.nav_scan_marking_local_sales)
                navView.menu.removeItem(R.id.nav_scan_seal_local_sales)
                navView.menu.removeItem(R.id.nav_scan_seal)
                navView.menu.removeItem(R.id.nav_scan_isotank)
                navView.menu.removeItem(R.id.nav_container_repair)
                navView.menu.removeItem(R.id.nav_container_tally)
            }

            RoleAccessEnum.LOCALSALES.value -> {
                navView.menu.removeItem(R.id.nav_home)
                navView.menu.removeItem(R.id.nav_container)
                navView.menu.removeItem(R.id.nav_gallery)
                navView.menu.removeItem(R.id.nav_slideshow)
                navView.menu.removeItem(R.id.nav_scan_isotank)
                navView.menu.removeItem(R.id.nav_container_repair)
                navView.menu.removeItem(R.id.nav_container_tally)
                navView.menu.removeItem(R.id.nav_scan_marking)
                navView.menu.removeItem(R.id.nav_scan_seal)
            }

            RoleAccessEnum.TALLY.value -> {
                navView.menu.removeItem(R.id.nav_home)
                navView.menu.removeItem(R.id.nav_container)
                navView.menu.removeItem(R.id.nav_gallery)
                navView.menu.removeItem(R.id.nav_slideshow)
                navView.menu.removeItem(R.id.nav_scan_flexi)
                navView.menu.removeItem(R.id.nav_scan_isotank)
                navView.menu.removeItem(R.id.nav_container_repair)
            }

            else -> {
                navView.menu.removeItem(R.id.nav_scan_local_sales)
                navView.menu.removeItem(R.id.nav_scan_flexi)
                navView.menu.removeItem(R.id.nav_scan_marking)
                navView.menu.removeItem(R.id.nav_scan_marking_local_sales)
                navView.menu.removeItem(R.id.nav_scan_seal_local_sales)
                navView.menu.removeItem(R.id.nav_scan_seal)
                navView.menu.removeItem(R.id.nav_scan_isotank)
                navView.menu.removeItem(R.id.nav_container_repair)
                navView.menu.removeItem(R.id.nav_container_tally)
            }
        }
    }

    private fun setNavController() {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.setStartDestination(
            when (viewModel.userData.value?.departmentId.orEmpty()) {
                RoleAccessEnum.CY.value -> R.id.nav_scan_marking
                RoleAccessEnum.INFINITY.value -> R.id.nav_scan_flexi
                RoleAccessEnum.LOCALSALES.value -> R.id.nav_scan_local_sales
                RoleAccessEnum.TALLY.value -> R.id.nav_container_tally
                else -> R.id.nav_home
            }
        )
        navController.graph = navGraph
    }
}