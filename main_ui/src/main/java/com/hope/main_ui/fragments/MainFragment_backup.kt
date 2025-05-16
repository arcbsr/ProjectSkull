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
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
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
import com.hope.firebase.database.FirebaseDB
import com.hope.firebase.auth.LoginHelper
import com.hope.firebase.database.UserFirebase
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.adapters.GridSpacingItemDecoration
import com.hope.main_ui.adapters.ImageGridAdapter
import com.hope.main_ui.aimodels.AIData
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

@Route(path = RoutePath.HomeFragment.HOME_BACKUP)
@AndroidEntryPoint
class MainFragmentBackup : BaseFragment<HomeViewModel, LayoutMainfragmentBinding>() {

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
        val aiData = AIData.getRandomThreeAINames()

    }

    private fun loadInitialData() {
        lifecycleScope.launch {
            val imageItems = DatabaseManager.imageItemDao().getRecentData()
            adapter.setNewInstance(imageItems.toMutableList())
        }
    }

    private fun setupListeners() {
        mDatabind.chatBubbleIcon.setOnClickListener { requestImagePermissionAndPick() }

        mDatabind.btnSend.setOnClickListener {
            if (mDatabind.editTextMessage.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a prompt!", Toast.LENGTH_SHORT)
                    .show()
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
                Toast.makeText(requireContext(), "Image not selected!", Toast.LENGTH_SHORT).show()
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

                        else -> {}
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
                localPath = "",
                aiAgent = "",
                aiPhoto = "",
                aiDetails = "",
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
