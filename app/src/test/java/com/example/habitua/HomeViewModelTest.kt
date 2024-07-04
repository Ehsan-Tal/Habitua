package com.example.habitua

import com.example.habitua.data.AppRepository
import com.example.habitua.data.UserPreferencesRepository
import com.example.habitua.ui.home.HomeViewModel
import org.junit.Before
import org.mockito.kotlin.mock

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockAppRepository: AppRepository
    private lateinit var mockUserPreferencesRepository: UserPreferencesRepository

    @Before
    fun setup() {
        mockAppRepository = mock()
        viewModel = HomeViewModel(mockAppRepository)
    }
    // HomeViewModel collects from the repository

    // there are a lot of private functions that we can't directly test
    // well, we can test their after effects by keeping track of data
    // and ensuring that, e.g., a habit of X streak days must be marked acquired
}