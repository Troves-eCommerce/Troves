package com.example.data.repository

import com.example.data.source.remote.RemoteDatasourceImpl
import com.example.domain.TrovesRepository


class TrovesRepositoryImpl (
    private val remoteDatasourceImpl: RemoteDatasourceImpl
) : TrovesRepository{
}