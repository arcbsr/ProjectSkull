package com.hope.main_ui.activities

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.hope.common.log.Log
import com.hope.db_libs.dbmanager.DatabaseManager
import com.hope.firebase.auth.GoogleAuthProviderStrategy
import com.hope.firebase.auth.LoginHelper
import com.hope.firebase.database.FirebaseDB
import com.hope.firebase.database.UserFirebase
import com.hope.firebase.database.aicreator.Models
import com.hope.lib_mvvm.MainViewModel
import com.hope.lib_mvvm.activity.BaseVmDbActivity
import com.hope.main_ui.R
import com.hope.main_ui.adapters.AIProfilesAdapter
import com.hope.main_ui.adapters.ViewPagerAdapter
import com.hope.main_ui.databinding.LayoutMainactivityBinding
import com.hope.main_ui.dialogs.DialogUtils
import com.hope.main_ui.fragments.MainFragment
import com.hope.main_ui.fragments.PreviousChatFragment
import com.hope.main_ui.utils.GlideEngine
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
@Route(path = "/test/activity")
class MainActivity : BaseVmDbActivity<MainViewModel, LayoutMainactivityBinding>() {
    //    private val viewModel2: MovieListViewModel by viewModels()
    override fun initObservers() {

    }


    private lateinit var loginHelper: LoginHelper
    private lateinit var tabLayouts: List<LinearLayout>
    private lateinit var tabIcons: List<AppCompatImageView>
    private lateinit var tabTexts: List<TextView>

    private fun setupTabs() {
        tabLayouts = listOf(
            findViewById<LinearLayout>(R.id.tab_home),
            findViewById<LinearLayout>(R.id.tab_recent),
            findViewById<LinearLayout>(R.id.tab_profile),
            findViewById<LinearLayout>(R.id.tab_settings)
        )

        tabIcons = listOf(
            findViewById<LinearLayout>(R.id.tab_home).getChildAt(0) as AppCompatImageView,
            findViewById<LinearLayout>(R.id.tab_recent).getChildAt(0) as AppCompatImageView,
            findViewById<LinearLayout>(R.id.tab_profile).getChildAt(0) as AppCompatImageView,
            findViewById<LinearLayout>(R.id.tab_settings).getChildAt(0) as AppCompatImageView
        )

        tabTexts = listOf(
            findViewById<LinearLayout>(R.id.tab_home).getChildAt(1) as TextView,
            findViewById<LinearLayout>(R.id.tab_recent).getChildAt(1) as TextView,
            findViewById<LinearLayout>(R.id.tab_profile).getChildAt(1) as TextView,
            findViewById<LinearLayout>(R.id.tab_settings).getChildAt(1) as TextView
        )

        tabLayouts.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                mDatabind.viewPager.currentItem = index
            }
        }

        mDatabind.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                highlightTab(position)
            }
        })

        highlightTab(0) // default
    }

    private fun highlightTab(selectedIndex: Int) {
        tabIcons.forEachIndexed { index, imageView ->
            imageView.setColorFilter(
                ContextCompat.getColor(
                    this,
                    if (index == selectedIndex) com.hope.resources.R.color.red else com.hope.resources.R.color.gray
                )
            )
        }

        tabTexts.forEachIndexed { index, textView ->
            textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (index == selectedIndex) com.hope.resources.R.color.red else com.hope.resources.R.color.gray
                )
            )
        }
    }


    private val titles = listOf("Home", "History", "Profile", "Settings")
    private val viewPagerAdapter: ViewPagerAdapter by lazy {
        ViewPagerAdapter(this, titles)
    }

    override fun initViews() {

        initData()
        DatabaseManager.initialize(this)

        Log.w("Rafiur>>>", "MainActivity")
//        // Use FragmentTransaction to load MainFragment
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//
//        // Add the fragment to the container (replace if already exists)
//        fragmentTransaction.replace(R.id.fragment_container, MainFragment())
//
//        // Commit the transaction
//        fragmentTransaction.commit()

        mDatabind.viewPager.adapter = viewPagerAdapter
        setGreetingMessage()
        mDatabind.tvMainHello.setOnClickListener {
//            ARouter.getInstance().build(RoutePath.Home.HOME).navigation(this)
//            XPopup.Builder(this)
//                .hasShadowBg(true) // Removes background dimming
//                .dismissOnTouchOutside(true) // So it doesn't close on outside tap
//                .isClickThrough(false) // Allows clicks to go through to background views
//                .isDestroyOnDismiss(true)
//                .asCustom(InviteUserDialog(this))
//                .show()

        }
        setupTabs()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginHelper.handleLoginResult(requestCode, data)
    }

    private fun initData() {

        loginHelper = LoginHelper(
            GoogleAuthProviderStrategy(
                context = this,
                clientId = "1080800805976-1fd1tf93ejitmllp6e8nug3tqs58bjeh.apps.googleusercontent.com",
                onLoginSuccess = {
                    // ✅ Handle successful login
                    Log.d("Rafiur>>>", "Login Success")
                    setupForUserData()
                    val userDb = FirebaseDB("users", UserFirebase::class.java)
                    val newUser = UserFirebase(
                        name = loginHelper.getCurrentUserDisplayName()!!,
                        age = 25,
                        profilePicture = loginHelper.getCurrentUserPhotoUrl()!!
                    )
                    loginHelper.getCurrentUserID()?.let { it ->
                        userDb.create(
                            id = "$it",
                            data = newUser,
                            onSuccess = {
                                Log.d("Rafiur>>>", "User successfully added!")

                            },
                            onError = {
                                Log.d("Rafiur>>>", "Failed to add user: $it")
                            }
                        )

                        mDatabind.aiProfiles.visibility = View.GONE
                    }
                },
                onLoginError = { error ->
                    Log.d("Rafiur>>>", "Login error: ${error.message}")

                    setupForUserData()
                }
            )
        )

        mDatabind.loginOut.root.setOnClickListener {
            loginHelper.startLogin()
        }
        setupForUserData()
    }

    private fun setupForUserData() {
        val userId = loginHelper.getCurrentUserID()
        Log.e("UserID: $userId")
        if (userId != null) {
            fetchAllAiProfiles()
            mDatabind.loginOut.root.setOnClickListener {
                loginHelper.signOut {
                    setupForUserData()
                }
            }

            val db = FirebaseDB("ai_profiles", Models.AiProfile::class.java)
            db.readAll(
                onSuccess = {
                    if (it.isEmpty()) {
                        createAiProfile()
                    }
                }, onError = {
                    Log.e("Rafiur>>>", "Error: $it")
                }
            )
        } else {
            mDatabind.aiProfiles.visibility = View.VISIBLE
            mDatabind.loginOut.root.setOnClickListener {
                loginHelper.startLogin()
            }
        }
    }

    private fun createAiProfile() {

        val db = FirebaseDB("ai_profiles", Models.AiProfile::class.java)

        Models.getProfiles().forEach { profile ->
            db.createAuto(
                profile,
                onSuccess = {
                    fetchAllAiProfiles()
                    println("Profile '${profile.name}' created successfully.")
                    Log.d("Rafiur>>>", "Profile '${profile.name}' created successfully.")
                },
                onError = { e ->
                    println("Error creating profile '${profile.name}': ${e.message}")
                    Log.e("Rafiur>>>", "Error creating profile '${profile.name}': ${e.message}")
                }
            )
        }
    }

    val adapter = AIProfilesAdapter()
    private fun fetchAllAiProfiles() {
        Log.d("Rafiur33>>", "Fetching AI profiles...")
        val db = FirebaseDB("ai_profiles", Models.AiProfile::class.java)
        db.readAll(
            onSuccess = { profiles ->
                mDatabind.horizontalRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                mDatabind.horizontalRecyclerView.adapter = adapter
                adapter.setNewInstance(profiles.toMutableList())
                adapter.setOnInviteClickListener(object : AIProfilesAdapter.OnEventClickListener {
                    override fun onEventClickReload(position: Int, item: Models.AiProfile) {

                    }

                    override fun onEventClickDelete(position: Int, item: Models.AiProfile) {

                    }

                    override fun onEventClick(position: Int, item: Models.AiProfile) {
                        aiSelector(selectedAiProfile = item)
                    }
                })
                aiSelector()
            },
            onError = { e ->
                Log.e("Rafiur33>>", "Error fetching AI profiles: ${e.message}")
            }
        )
    }

    private fun aiSelector(
        selectedAiProfile: Models.AiProfile? = null
    ) {

        mDatabind.chattingHeader.root.visibility = View.GONE
        mDatabind.aiProfiles.visibility = View.VISIBLE
        Log.d("Rafiur33>>", "Selected AI Profile: $selectedAiProfile")
        if(selectedAiProfile == null){
            Log.d("Rafiur33>>", "Selected AI Profile: null")
            adapter.setDefaultAI()
            return
        }
        if (selectedAiProfile != null) {
            mDatabind.chattingHeader.root.visibility = View.VISIBLE
            mDatabind.aiProfiles.visibility = View.GONE
            selectedAiProfile?.imageUrl?.let {
                GlideEngine.createGlideEngine().loadImage(
                    this, it,
                    mDatabind.chattingHeader.profileImage
                )
            }
            mDatabind.chattingHeader.btnInfo.setOnClickListener {
                DialogUtils.showBubbleText(
                    context = this,
                    hintText = DialogUtils.generateAiIntroduction(selectedAiProfile),
                    anchor = mDatabind.chattingHeader.btnInfo
                ) {

                }
            }
            mDatabind.chattingHeader.userName.text = selectedAiProfile.name
            mDatabind.chattingHeader.userStatus.text = selectedAiProfile.personality
            mDatabind.chattingHeader.btnBack.setOnClickListener {
                if (mDatabind.viewPager.currentItem == 0) {
                    aiSelector()
                } else {
                    mDatabind.viewPager.currentItem = 0
                }

            }
        } else {
            adapter.removeSelection()
        }
        mDatabind.chattingHeader.btnGallery.setOnClickListener {
            mDatabind.viewPager.currentItem = 1
        }

        for (fragment in viewPagerAdapter.getAllFragments()) {
            if (fragment is MainFragment) {
                fragment.setAiProfile(selectedAiProfile)
            } else if (fragment is PreviousChatFragment) {
                fragment.setAiProfile(selectedAiProfile)
            }
        }
    }

    private fun setGreetingMessage() {

        val calendar = Calendar.getInstance()
        val greeting = when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..4 -> "Good Night \uD83C\uDF19"
            in 5..11 -> "Good Morning \u2600️"
            in 12..16 -> "Good Afternoon \uD83C\uDF24️"
            in 17..20 -> "Good Evening \uD83C\uDF06"
            else -> "Good Night \uD83C\uDF19"
        }

        mDatabind.tvMainHello.text = greeting
    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

}