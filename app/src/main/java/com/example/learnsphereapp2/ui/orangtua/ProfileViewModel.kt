//package com.example.learnsphereapp2.ui.orangtua
//
//import android.content.Context
//import android.net.Uri
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.learnsphereapp2.util.PreferencesHelper
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import java.io.File
//import java.io.FileOutputStream
//import java.io.InputStream
//
//class ProfileViewModel(
//    private val context: Context,
//    private val profileDao: ProfileDao
//) : ViewModel() {
//
//    private val _profilePicturePath = MutableStateFlow<String?>(null)
//    val profilePicturePath: StateFlow<String?> = _profilePicturePath
//
//    init {
//        viewModelScope.launch {
//            val userId = getUserIdFromPreferences(context)
//            val profile = profileDao.getProfile(userId)
//            _profilePicturePath.value = profile?.profilePicturePath
//        }
//    }
//
//    fun uploadPhoto(uri: Uri) {
//        viewModelScope.launch {
//            val userId = getUserIdFromPreferences(context)
//            val file = saveImageToInternalStorage(context, uri)
//            val profile = ProfileEntity(userId = userId, profilePicturePath = file.absolutePath)
//            profileDao.insert(profile)
//            _profilePicturePath.value = file.absolutePath
//        }
//    }
//
//    fun updatePhoto(uri: Uri) {
//        viewModelScope.launch {
//            val userId = getUserIdFromPreferences(context)
//            val file = saveImageToInternalStorage(context, uri)
//            val profile = ProfileEntity(userId = userId, profilePicturePath = file.absolutePath)
//            profileDao.update(profile)
//            _profilePicturePath.value = file.absolutePath
//        }
//    }
//
//    fun deletePhoto() {
//        viewModelScope.launch {
//            val userId = getUserIdFromPreferences(context)
//            profileDao.delete(userId)
//            _profilePicturePath.value = null
//        }
//    }
//
//    private fun saveImageToInternalStorage(context: Context, uri: Uri): File {
//        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//        val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.jpg")
//        inputStream?.use { input ->
//            FileOutputStream(file).use { output ->
//                input.copyTo(output)
//            }
//        }
//        return file
//    }
//
//    private fun getUserIdFromPreferences(context: Context): String {
//        val preferencesHelper = PreferencesHelper(context)
//        return preferencesHelper.getUserId() ?: "Unknown"
//    }
//}
//
//class ProfileViewModelFactory(
//    private val context: Context,
//    private val profileDao: ProfileDao
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ProfileViewModel(context, profileDao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}