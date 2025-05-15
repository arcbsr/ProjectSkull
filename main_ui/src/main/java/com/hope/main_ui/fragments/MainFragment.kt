package com.hope.main_ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.fondesa.kpermissions.isGranted
import com.hope.common.log.Log
import com.hope.db_libs.dbmanager.DatabaseManager
import com.hope.db_libs.dbmanager.ImageItem
import com.hope.firebase.auth.GoogleAuthProviderStrategy
import com.hope.firebase.auth.LoginHelper
import com.hope.firebase.database.FirebaseDB
import com.hope.firebase.database.UserFirebase
import com.hope.firebase.database.aicreator.Models
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.adapters.GridSpacingItemDecoration
import com.hope.main_ui.adapters.ImageGridAdapter
import com.hope.main_ui.databinding.LayoutMainfragmentBinding
import com.hope.main_ui.routers.RoutePath
import com.hope.main_ui.utils.CropEngine
import com.hope.main_ui.utils.GetPathFromUri
import com.hope.main_ui.utils.GlideEngine
import com.hope.main_ui.viewmodels.HomePageState
import com.hope.main_ui.viewmodels.HomeViewModel
import com.hope.resources.R
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date

@Route(path = RoutePath.HomeFragment.HOME)
@AndroidEntryPoint
class MainFragment : BaseFragment<HomeViewModel, LayoutMainfragmentBinding>() {

    @Autowired(name = "searchQuery")
    @JvmField
    var searchQuery: String? = null
    private val adapter = ImageGridAdapter()
    private var selectedImages: String? = null
    private var processID: String? = null

    override fun showLoading(message: String) {}
    override fun dismissLoading() {}

    override fun initViews() {
        ARouter.getInstance().inject(this)
        setupRecyclerView()
        loadInitialData()
        setupListeners()
        init()
    }

    private lateinit var loginHelper: LoginHelper
    private fun init() {
        loginHelper = LoginHelper(
            GoogleAuthProviderStrategy(
                fragment = this,
                clientId = "1080800805976-1fd1tf93ejitmllp6e8nug3tqs58bjeh.apps.googleusercontent.com",
                onLoginSuccess = { user ->
                    // ✅ Handle successful login
                    Toast.makeText(requireContext(), "Welcome $user", Toast.LENGTH_SHORT).show()
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
                                println("User successfully added!")

                            },
                            onError = {
                                println("Failed to add user: $it")
                            }
                        )
                    }
                },
                onLoginError = { error ->
                    // ❌ Handle login error
                    Toast.makeText(
                        requireContext(),
                        "Login failed: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    setupForUserData()
                }
            )
        )

        mDatabind.loginOut.cardAi1.setOnClickListener {
            loginHelper.startLogin()
        }
        setupForUserData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginHelper.handleLoginResult(requestCode, data)
    }

    private fun setupForUserData() {
        mDatabind.itemAiProfile1.root.visibility = View.GONE
        mDatabind.itemAiProfile2.root.visibility = View.GONE
        mDatabind.itemAiProfile3.root.visibility = View.GONE
        val userId = loginHelper.getCurrentUserID()
        Log.e("UserID: $userId")
        if (userId != null) {
            fetchAllAiProfiles()
            mDatabind.loginOut.txtAi1.text = loginHelper.getCurrentUserDisplayName()
            GlideEngine.createGlideEngine()
                .loadImage(
                    requireContext(),
                    loginHelper.getCurrentUserPhotoUrl()!!,
                    mDatabind.loginOut.imAi1
                )
            mDatabind.loginOut.cardAi1.setOnClickListener {
                loginHelper.signOut {
                    mDatabind.loginOut.txtAi1.text = "More Agents"
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

            mDatabind.loginOut.cardAi1.setOnClickListener {
                loginHelper.startLogin()
            }
            Glide.with(requireContext())
                .load(R.drawable.ic_profile)
                .into(mDatabind.loginOut.imAi1)
            mDatabind.loginOut.txtAi1.text = "More Agents"
        }
    }

    private fun setupRecyclerView() {
        mDatabind.chatRecyclerView.adapter = adapter
        val spacingInPx = (16 * resources.displayMetrics.density).toInt()
        mDatabind.chatRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(spanCount = 2, spacing = spacingInPx, includeEdge = true)
        )

        adapter.setOnInviteClickListener(object : ImageGridAdapter.OnEventClickListener {
            override fun onEventClickReload(position: Int, item: ImageItem) {
                Log.d("MainFragment", "Image clicked: $item")
                viewModel.retrieveImage(item.processId, item)
            }

            override fun onEventClickDelete(position: Int, item: ImageItem) {
                lifecycleScope.launch {
                    DatabaseManager.imageItemDao().deleteById(item.id)
                    refreshImageList()
                }
            }

            override fun onEventClick(position: Int, item: ImageItem) {
            }
        })

    }

    private fun createAiProfile() {

        val db = FirebaseDB("ai_profiles", Models.AiProfile::class.java)
        val profiles = listOf(
            Models.AiProfile(
                name = "Aurora",
                imageUrl = "https://play-lh.googleusercontent.com/7Ak4Ye7wNUtheIvSKnVgGL_OIZWjGPZNV6TP_3XLxHC-sDHLSE45aDg41dFNmL5COA",
                gender = "Female",
                type = "Assistant",
                personality = "Friendly and insightful",
                age = 5,
                description = "An advanced AI assistant developed to support users with daily tasks."
            ),
            Models.AiProfile(
                name = "Zeus",
                imageUrl = "https://aiavatar.com/globalImages/landingPage/variants/gaming.webp",
                gender = "Male",
                type = "Mentor",
                personality = "Wise and strategic",
                age = 7,
                description = "A guiding AI mentor designed to provide strategic advice."
            ),
            Models.AiProfile(
                name = "Nova",
                imageUrl = "https://aiavatar.com/globalImages/landingPage/variants/gaming.webp",
                gender = "Non-binary",
                type = "Companion",
                personality = "Curious and empathetic",
                age = 3,
                description = "An empathetic AI companion who loves to learn with you."
            )
        )


        profiles.forEach { profile ->
            db.createAuto(
                profile,
                onSuccess = { println("Profile '${profile.name}' created successfully.") },
                onError = { e -> println("Error creating profile '${profile.name}': ${e.message}") }
            )
        }
    }

    private fun fetchAllAiProfiles() {
        Log.d("Rafiur33>>", "Fetching AI profiles...")
        val db = FirebaseDB("ai_profiles", Models.AiProfile::class.java)
        db.readAll(
            onSuccess = { profiles ->
                var count = 0
                profiles.forEach {
                    val aiProfile = it
                    Log.d("Rafiur33>>", "AI Profile: $it")
                    when (count) {
                        0 -> {
                            mDatabind.itemAiProfile1.root.visibility = View.VISIBLE
                            mDatabind.itemAiProfile1.txtAi1.text = it.name
                            GlideEngine.createGlideEngine().loadImage(
                                requireContext(),
                                it.imageUrl,
                                mDatabind.itemAiProfile1.imAi1
                            )
                            mDatabind.itemAiProfile1.root.setOnClickListener {
                                AiSelector(mDatabind.itemAiProfile1.txtAi1, aiProfile)
                            }
                        }

                        1 -> {
                            mDatabind.itemAiProfile2.root.visibility = View.VISIBLE
                            mDatabind.itemAiProfile2.txtAi1.text = it.name
                            GlideEngine.createGlideEngine().loadImage(
                                requireContext(),
                                it.imageUrl,
                                mDatabind.itemAiProfile2.imAi1
                            )
                            mDatabind.itemAiProfile2.root.setOnClickListener {
                                AiSelector(mDatabind.itemAiProfile2.txtAi1, aiProfile)
                            }
                        }

                        2 -> {
                            mDatabind.itemAiProfile3.root.visibility = View.VISIBLE
                            mDatabind.itemAiProfile3.txtAi1.text = it.name
                            GlideEngine.createGlideEngine().loadImage(
                                requireContext(),
                                it.imageUrl,
                                mDatabind.itemAiProfile3.imAi1
                            )
                            mDatabind.itemAiProfile3.root.setOnClickListener {
                                AiSelector(mDatabind.itemAiProfile3.txtAi1, aiProfile)
                            }
                        }

                        else -> {
                            // Handle more profiles if needed
                        }
                    }
                    count++
                }
                AiSelector(null, null)
            },
            onError = { e ->
                Log.e("Rafiur33>>", "Error fetching AI profiles: ${e.message}")
            }
        )
    }

    private fun AiSelector(selectedProfile: TextView?, aiProfile: Models.AiProfile?) {
        mDatabind.itemAiProfile1.txtAi1.setBackgroundColor(resources.getColor(R.color.orange))
        mDatabind.itemAiProfile2.txtAi1.setBackgroundColor(resources.getColor(R.color.orange))
        mDatabind.itemAiProfile3.txtAi1.setBackgroundColor(resources.getColor(R.color.orange))
        selectedProfile?.setBackgroundColor(resources.getColor(R.color.green))
        selectedAiProfile = aiProfile
    }

    private var selectedAiProfile: Models.AiProfile? = null
    private fun loadInitialData() {
        lifecycleScope.launch {
            val imageItems = DatabaseManager.imageItemDao().getRecentData()
            adapter.setNewInstance(imageItems.toMutableList())
        }
    }

    private fun setupListeners() {
        mDatabind.chatBubbleIcon.setOnClickListener { requestImagePermissionAndPick() }

        mDatabind.btnSend.setOnClickListener {
            if (mDatabind.editTextMessage.text?.trim().toString().isEmpty()) {
//                Toast.makeText(requireContext(), "Please enter a prompt!", Toast.LENGTH_SHORT)
//                    .show()
                mDatabind.editTextMessage.text?.clear()
                mDatabind.editTextMessage.setHintTextColor(resources.getColor(R.color.red))
                return@setOnClickListener
            }
            val bitmap = BitmapFactory.decodeFile(selectedImages)
            if (bitmap != null) {
                viewModel.generateImage(
                    promptText = mDatabind.editTextMessage.text.toString(),
                    context = requireContext(),
                    bitmap = bitmap
                )
            } else {
                viewModel.generateTxtToImage(
                    promptText = mDatabind.editTextMessage.text.toString()
                )
//                Toast.makeText(requireContext(), "Image not selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestImagePermissionAndPick() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        permissionsBuilder(permission).build().send { resultList ->
            val granted = resultList.any { it.isGranted() }
            val denied = resultList.filter { !it.isGranted() }

            if (granted) {
                openImagePicker()
            }

            if (denied.isNotEmpty()) {
                Toast.makeText(
                    context,
                    "❌ Permission denied: ${denied.joinToString { it.permission }}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openImagePicker() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCropEngine(CropEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .isPreviewImage(false)
            .isPreviewFullScreenMode(false)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    result.firstOrNull()?.let { media ->
                        val path =
                            if (PictureMimeType.isContent(media.availablePath) && !media.isCut) {
                                GetPathFromUri.getInstance()
                                    .getPath(requireActivity(), Uri.parse(media.availablePath))
                            } else {
                                media.cutPath
                            }

                        if (!TextUtils.isEmpty(path)) {
                            selectedImages = path
//                            GlideEngine.createGlideEngine()
//                                .loadImage(requireContext(), path, mDatabind.imageviewD)
                            GlideEngine.createGlideEngine()
                                .loadImage(requireContext(), path, mDatabind.chatBubbleIcon)
                        }
                    }
                }

                override fun onCancel() {}
            })
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mState.collect { state ->
                    when (state) {
                        is HomePageState.Loading -> {
                            hideSoftKeyboard()
                            Log.d("MainFragment", "Loading...")
                            mDatabind.lottieSave.visibility = View.VISIBLE
                            mDatabind.btnSend.visibility = View.GONE
                        }

                        is HomePageState.Success -> {
                            processID = state.processID
                            insertImageItem()
                            mDatabind.lottieSave.visibility = View.GONE
                            mDatabind.btnSend.visibility = View.VISIBLE
                        }

                        is HomePageState.Error -> {
                            mDatabind.lottieSave.visibility = View.GONE
                            mDatabind.btnSend.visibility = View.VISIBLE
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            Log.e("MainFragment", "Error: ${state.message}")
                        }

                        is HomePageState.EndOfSearch -> {
                            mDatabind.lottieSave.visibility = View.GONE
                            mDatabind.btnSend.visibility = View.VISIBLE
                            Log.d("MainFragment", "End of search")
                        }

                        is HomePageState.ImageData -> {
                            updateImageData(state.imageLink, state.item)
                            mDatabind.lottieSave.visibility = View.GONE
                            mDatabind.btnSend.visibility = View.VISIBLE
                        }

                        HomePageState.Ideal -> {
                            mDatabind.lottieSave.visibility = View.GONE
                            mDatabind.btnSend.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun insertImageItem() {
        if (processID.isNullOrEmpty()) {
            Toast.makeText(context, "Image not selected!", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val item = ImageItem(
                prompt = mDatabind.editTextMessage.text.toString(),
                imageUrl = "",
                localPath = selectedAiProfile?.imageUrl ?: "",
                aiAgent = selectedAiProfile?.name ?: "",
                aiPhoto = selectedAiProfile?.imageUrl ?: "",
                aiDetails = selectedAiProfile?.description ?: "",
                savedPath = "",
                createdAt = Date(),
                isCreated = false,
                isError = false,
                details = mDatabind.editTextMessage.text.toString(),
                processId = processID.orEmpty()
            )
            DatabaseManager.imageItemDao().insert(item)
            refreshImageList()
        }
    }

    private fun updateImageData(imageUrl: String, item: ImageItem) {
        lifecycleScope.launch {
            item.imageUrl = imageUrl
            item.isCreated = true
            DatabaseManager.imageItemDao().update(item)
            refreshImageList()
        }
    }

    private suspend fun refreshImageList() {
        val updatedList = DatabaseManager.imageItemDao().getRecentData()
        adapter.setNewInstance(updatedList.toMutableList())
        mDatabind.editTextMessage.text?.clear()
    }

    private fun hideSoftKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}
