package com.breens.foodappadmin.di


import com.breens.foodappadmin.data.repositories.Repository
import com.breens.foodappadmin.data.repositories.RepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton



    fun provideNoteRepository(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        firebaseDatabase: FirebaseDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): Repository {
        return RepositoryImpl(
            foodDB = firebaseFirestore,
            firebaseAuth = firebaseAuth,
            firebaseDatabase = firebaseDatabase,
            ioDispatcher = ioDispatcher,
        )
    }


}

