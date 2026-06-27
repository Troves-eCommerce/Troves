package com.example.data.repository

import com.example.data.source.remote.RemoteDatasource
import com.example.domain.TrovesRepository

class TrovesRepositoryImpl(
    private val remoteDataSource: RemoteDatasource
) : TrovesRepository