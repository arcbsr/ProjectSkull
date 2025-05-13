package com.hope.main_ui.fragments

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.text.TextUtils
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
        })
    }

    private fun loadInitialData() {
        lifecycleScope.launch {
            val imageItems = DatabaseManager.imageItemDao().getRecentData()
            adapter.setNewInstance(imageItems.toMutableList())
        }
    }

    private fun setupListeners() {
        mDatabind.btnPickImage.setOnClickListener { requestImagePermissionAndPick() }

        mDatabind.btnSend.setOnClickListener {
            val bitmap = BitmapFactory.decodeFile(selectedImages)
            if (bitmap != null) {
                viewModel.generateImage(
                    promptText = "Create cartoon effect",
                    context = requireContext(),
                    bitmap = bitmap
                )
            } else {
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
                            GlideEngine.createGlideEngine()
                                .loadImage(requireContext(), path, mDatabind.imageviewD)
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
                            Log.d("MainFragment", "Loading...")
                        }

                        is HomePageState.Success -> {
                            processID = state.processID
                            insertImageItem()
                        }

                        is HomePageState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            Log.e("MainFragment", "Error: ${state.message}")
                        }

                        is HomePageState.EndOfSearch -> {
                            Log.d("MainFragment", "End of search")
                        }

                        is HomePageState.ImageData -> {
                            updateImageData(state.imageLink, state.item)
                        }
                    }
                }
            }
        }
    }

    private fun insertImageItem() {
        if (selectedImages.isNullOrEmpty()) {
            Toast.makeText(context, "Image not selected!", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val item = ImageItem(
                prompt = mDatabind.editTextMessage.text.toString(),
                imageUrl = "",
                localPath = selectedImages.orEmpty(),
                savedPath = "",
                createdAt = Date(),
                isCreated = false,
                isError = false,
                details = "This is a sample image",
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

    }
}
