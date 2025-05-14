package com.hope.main_ui.viewmodels


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.hope.common.log.Log
import com.hope.db_libs.dbmanager.DatabaseManager
import com.hope.db_libs.dbmanager.ImageItem
import com.hope.lib_mvvm.viewmodel.BaseViewModel
import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.network_libs.monsterlab.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val useCase: UseCase) : BaseViewModel() {
    //    private val token = "Bearer $tokenText"
//    private val prompt = RequestBody.create(MediaType.parse("text/plain"), promptText)
    private val _state = MutableStateFlow<HomePageState>(HomePageState.Ideal)
    val mState: StateFlow<HomePageState> = _state

    fun generateImage(bitmap: Bitmap, promptText: String, context: Context) {
        val tempFile = createTempFile(bitmap, context)
        val imagePart = MultipartBody.Part.createFormData(
            "init_image_url",
            tempFile.name,
            RequestBody.create(MediaType.parse(MEDIA_TYPE_IMAGE), tempFile)
        )
        val prompt = RequestBody.create(MediaType.parse("text/plain"), promptText)
        viewModelScope.launch {

            _state.value = HomePageState.Loading
            useCase.generateImage(imagePart, token, prompt).onStart {}.catch {
                _state.value = HomePageState.Error("Unknown")
            }.collect {
                when (it) {
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = HomePageState.Error("${it.code} : $msg")
                            Log.w("Rafiur>>", "Error: ${it.code} : $msg")
                        }
                    }

                    ResponseWrapper.NetworkError -> {
                        _state.value = HomePageState.Error("Network Error")
                        Log.w("Rafiur>>", "Network Error")
                    }

                    is ResponseWrapper.Success -> {
                        delay(600)
                        if (it.value?.process_id == null || it.value.process_id.isEmpty()) {
                            _state.value = HomePageState.EndOfSearch
                            Log.w("Rafiur>>", "process_id: ${it.value.process_id}")
                        } else {
                            //TODO: handle the response
                            Log.w("Rafiur>>", "process_id: ${it.value.process_id}")
                            _state.value = HomePageState.Success(it.value.process_id)
                        }

                    }
                }
            }
        }
    }

    private val token =
        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IjM5ZmJhYTJmNzA4ZTc3NjEwNDc2NzU3MjYwNGM0ZjU2IiwiY3JlYXRlZF9hdCI6IjIwMjQtMTEtMDRUMTU6MDE6NDguMjMyOTIzIn0.QZtSSUEfdBYTMed9kQRwDbXnJ33j8IxC9rbn1r76TpQ"

    fun retrieveImage(processId: String, item: ImageItem) {

        viewModelScope.launch {

            _state.value = HomePageState.Loading
            useCase.getImageProcessingResponse(processId = processId, authorization = token)
                .onStart {}.catch {
                    _state.value = HomePageState.Error("Unknown")
                }.collect {
                    when (it) {
                        is ResponseWrapper.GenericError -> {
                            it.error?.let { msg ->
                                _state.value = HomePageState.Error("${it.code} : $msg")
                                Log.w("Rafiur333>>", "Error: ${it.code} : $msg")
                            }
                        }

                        ResponseWrapper.NetworkError -> {
                            _state.value = HomePageState.Error("Network Error")
                            Log.w("Rafiur>>", "Network Error")
                        }

                        is ResponseWrapper.Success -> {
                            delay(600)
                            if (it.value?.process_id == null || it.value.process_id.isEmpty()) {
                                _state.value = HomePageState.EndOfSearch
                                Log.w("Rafiur>>", "process_id: ${it.value.process_id}")
                            } else {
                                //TODO: handle the response
                                Log.w("Rafiur>>", "process_id: ${it.value.process_id}")
                                if (it.value.result == null || it.value.result.output == null || it.value.result.output.isEmpty()) {
                                    _state.value = HomePageState.EndOfSearch
                                } else {
                                    Log.w("Rafiur>>", "process_id: ${it.value.result.output}")
                                    _state.value =
                                        HomePageState.ImageData(it.value.result.output[0], item)
                                }
                            }

                        }
                    }
                }
        }
    }

    private fun createTempFile(bitmap: Bitmap, context: Context): File {
        val tempFile = File.createTempFile("upload_image", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return tempFile
    }

    fun generateTxtToImage(promptText: String) {

        val prompt = RequestBody.create(MediaType.parse("text/plain"), promptText)
        viewModelScope.launch {
            _state.value = HomePageState.Loading
            useCase.generateTextImage(token, prompt).onStart {}.catch {
                _state.value = HomePageState.Error("Unknown")
            }.collect {
                when (it) {
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = HomePageState.Error("${it.code} : $msg")
                            Log.w("Rafiur>>", "Error: ${it.code} : $msg")
                        }
                    }

                    ResponseWrapper.NetworkError -> {
                        _state.value = HomePageState.Error("Network Error")
                        Log.w("Rafiur>>", "Network Error")
                    }

                    is ResponseWrapper.Success -> {
                        delay(600)
                        if (it.value?.process_id == null || it.value.process_id.isEmpty()) {
                            _state.value = HomePageState.EndOfSearch
                            Log.w("Rafiur>>", "process_id: ${it.value.process_id}")
                        } else {
                            //TODO: handle the response
                            Log.w("Rafiur>>", "process_id: ${it.value.process_id}")
                            _state.value = HomePageState.Success(it.value.process_id)
                        }

                    }
                }
            }
        }
    }

    companion object {
        const val MAX_RETRIES = 5
        const val RETRY_DELAY = 5000L
        const val INITIAL_DELAY = 2000L
        private const val MEDIA_TYPE_IMAGE = "image/jpeg"
    }
}

sealed class HomePageState {
    object Loading : HomePageState()
    object EndOfSearch : HomePageState()
    data class Success(val processID: String) : HomePageState()
    data class ImageData(val imageLink: String, val item: ImageItem) : HomePageState()
    data class Error(val message: String) : HomePageState()
    object Ideal : HomePageState()
}
