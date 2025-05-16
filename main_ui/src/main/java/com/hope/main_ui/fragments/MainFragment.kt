package com.hope.main_ui.fragments

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.fondesa.kpermissions.isGranted
import com.hope.common.log.Log
import com.hope.db_libs.dbmanager.DatabaseManager
import com.hope.db_libs.dbmanager.ImageItem
import com.hope.firebase.database.aicreator.Models
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.adapters.GridSpacingItemDecoration
import com.hope.main_ui.adapters.ImageGridAdapter
import com.hope.main_ui.databinding.LayoutMainfragmentBinding
import com.hope.main_ui.dialogs.ProgressLoadingDialog
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
import com.lxj.xpopup.XPopup
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

    private fun init() {
        setAiProfile()
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
                val progressDialog = showProgressDialog()
                progressDialog.setProgress(50)
            }
        })

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
                            retryCount = 0
                            updateImageData(state.imageLink, state.item)
                            mDatabind.lottieSave.visibility = View.GONE
                            mDatabind.btnSend.visibility = View.VISIBLE
                        }

                        HomePageState.Ideal -> {
                            mDatabind.lottieSave.visibility = View.GONE
                            mDatabind.btnSend.visibility = View.VISIBLE
                        }

                        is HomePageState.RetryImageLoading -> {
                            retryCount++
                            if (retryCount <= HomeViewModel.MAX_RETRIES) {
                                Log.d("Rafiur>>Retry", "Retrying image loading...")
                                viewModel.retrieveImage(state.processID, state.item)
                            } else {
                                retryCount = 0
                                Log.d("Rafiur>>Retry", "Max retries reached. Stopping retries.")
                                Toast.makeText(
                                    context,
                                    "Failed to load image after 3 retries.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private var retryCount = 0;
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
            val newId = DatabaseManager.imageItemDao().insert(item)
            Log.d("MainFragment", "Inserted item with ID: $newId")
            val newItem = DatabaseManager.imageItemDao().getById(newId.toInt())
            refreshImageList()
            if (newItem != null) {
                viewModel.retrieveImage(processID.orEmpty(), newItem)
            }
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

    private fun showProgressDialog(): ProgressLoadingDialog {
        val progressDialog = ProgressLoadingDialog(requireContext())
        XPopup.Builder(requireContext())
            .hasShadowBg(true) // Removes background dimming
            .dismissOnTouchOutside(false) // So it doesn't close on outside tap
            .isViewMode(true)
            .isClickThrough(false) // Allows clicks to go through to background views
            .isDestroyOnDismiss(false)
            .dismissOnBackPressed(false)
            .asCustom(progressDialog)
            .show()
        return progressDialog
    }

    fun setAiProfile(aiProfile: Models.AiProfile? = null) {
        if (aiProfile != null) {
            Log.d("Rafiur>>AiProfile", "Selected AI Profile: ${aiProfile.name}")
            selectedAiProfile = aiProfile
            GlideEngine.createGlideEngine().loadImage(
                requireContext(),
                aiProfile.imageUrl,
                mDatabind.aiImageView
            )
            mDatabind.aiName.text = generateAiIntroduction(aiProfile)
        } else {
            mDatabind.aiName.text = generateAiIntroduction()
            mDatabind.aiImageView.setImageResource(0)
        }
        mDatabind.aiName.setOnClickListener {
            mDatabind.aiName.maxLines = if (mDatabind.aiName.maxLines >2) 2 else 6
        }
    }

    private fun generateAiIntroduction(aiProfile: Models.AiProfile? = null): String {
        return buildString {
            if (aiProfile != null) {
                append("👋 Hi, I'm *${aiProfile.name}*")
                if (aiProfile.type.isNotEmpty()) append(", your ${aiProfile.type}")
                if (aiProfile.gender.isNotEmpty()) append(" (${aiProfile.gender})")
                if (aiProfile.age > 0) append(", age ${aiProfile.age}")
                append("!\n")

                if (aiProfile.personality.isNotEmpty()) {
                    append("🧠 I'm known to be ${aiProfile.personality.lowercase()}.")
                }

                if (aiProfile.description.isNotEmpty()) {
                    append("💬 ${aiProfile.description}")
                } else {
                    append("Let's chat and get to know each other better!")
                }
            } else {
                append("🚀 Ready to chat?")
                append("\n🤖 Please select an AI agent from above to begin your journey.")
                append("\n🎭 Each one brings a different personality — choose your perfect match!")
            }
        }
    }
}
