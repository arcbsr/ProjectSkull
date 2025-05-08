package com.hope.main_ui.viewmodels


import androidx.lifecycle.viewModelScope
import com.hope.lib_mvvm.viewmodel.BaseViewModel
import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.models.models.Movie
import com.hope.network_libs.domain_omdb.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(private val useCase: UseCase) :  BaseViewModel() {

    private val _state = MutableStateFlow<MovieState>(MovieState.Loading)
    val mState: StateFlow<MovieState> = _state

    private val _movies: MutableStateFlow<List<Movie>> = MutableStateFlow(emptyList())
    val movies: StateFlow<List<Movie>> get() = _movies

    private val _year: MutableStateFlow<Int> = MutableStateFlow(2025) // Initial page number
    val pageNumber: StateFlow<Int> get() = _year

    private val _blockLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val blockLoading: StateFlow<Boolean> get() = _blockLoading

    fun fetchMovieList(searchTxt: String) {
        if (_blockLoading.value) {
            return
        }
        _state.value = MovieState.Loading
        _blockLoading.value = true
        viewModelScope.launch {
            useCase.getMovies(searchTxt, page = 1, _year.value.toString()).onStart {}.catch {
                _state.value = MovieState.Error("Unknown")
            }.collect {
                when (it) {
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = MovieState.Error("${it.code} : $msg")
                        }
                        _blockLoading.value = false
                    }

                    ResponseWrapper.NetworkError -> {
                        _state.value = MovieState.Error("Network Error")
                        _blockLoading.value = false
                    }

                    is ResponseWrapper.Success -> {
                        delay(600)
                        if (it.value?.search == null || it.value.search.isEmpty()) {
                            _state.value = MovieState.EndOfSearch
                        } else {
                            _year.value++
                            addNewMovies(it.value.search)
                        }
                        _blockLoading.value = false
                    }
                }
            }
        }
    }

    private fun fetchMovieDetail() {
        viewModelScope.launch {

            useCase.getMovieDetail("tt1570728").onStart {}.catch {

            }.collect {
                when (it) {
                    is ResponseWrapper.GenericError -> {
                        it.error?.let {

                        }
                    }

                    ResponseWrapper.NetworkError -> {

                    }

                    is ResponseWrapper.Success -> {
//                        Log.w("get data", it.value.Title)
                    }
                }
            }
        }
    }

    private fun addNewMovies(newMovies: List<Movie>) {
        val currentMovies = _movies.value.toMutableList()
        currentMovies.addAll(newMovies)
        _movies.value = currentMovies.toList()
        _state.value =
//            MovieState.Success(_movies.value.sortedByDescending { it.year })
            MovieState.Success(_movies.value)
    }

    fun sortByNameAscending() {
        val currentState = _state.value
        if (currentState is MovieState.Success) {
            val currentData = currentState.movies.toMutableList()
            val sortedMovies = currentData.sortedBy { it.title }
            _state.value = MovieState.Success(sortedMovies)
        }
    }

    private fun addNewData(search: List<Movie>) {
        val currentState = _state.value
        if (currentState is MovieState.Success) {
            val currentData = currentState.movies.toMutableList()
            currentData.addAll(search.sortedByDescending { it.year })
        }


    }

    fun sortByNameDescending() {
        val currentState = _state.value
        if (currentState is MovieState.Success) {
            val currentData = currentState.movies.toMutableList()
            val sortedMovies = currentData.sortedByDescending { it.title }
            _state.value = MovieState.Success(sortedMovies)
        }
    }

    fun sortByYearAscending() {
        val currentState = _state.value
        if (currentState is MovieState.Success) {
            val currentData = currentState.movies.toMutableList()
            val sortedMovies = currentData.sortedBy { it.year }
            _state.value = MovieState.Success(sortedMovies)
        }
    }

    fun sortByYearDescending() {
        val currentState = _state.value
        if (currentState is MovieState.Success) {
            val currentData = currentState.movies.toMutableList()
            val sortedMovies = currentData.sortedByDescending { it.year }
            _state.value = MovieState.Success(sortedMovies)
        }
    }
}

sealed class MovieState {
    object Loading : MovieState()
    object EndOfSearch : MovieState()
    data class Success(val movies: List<Movie>) : MovieState()
    data class Error(val message: String) : MovieState()
}
