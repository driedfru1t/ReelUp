package com.nikol.detail_impl.data.repository

import arrow.core.Either
import arrow.core.left
import com.nikol.detail_impl.data.remote.ext.toDomain
import com.nikol.detail_impl.data.remote.service.DetailService
import com.nikol.detail_impl.domain.errors.DetailsError
import com.nikol.detail_impl.domain.model.ContentDetailDomain
import com.nikol.detail_impl.domain.parameter.DetailParameter
import com.nikol.detail_impl.domain.repository.DetailRepository
import com.nikol.security.model.SessionState
import com.nikol.security.repository.TokenRepository

class DetailRepositoryImpl(
    private val tokenRepository: TokenRepository,
    private val detailService: DetailService
) : DetailRepository {
    override suspend fun getMovie(detailParameter: DetailParameter): Either<DetailsError, ContentDetailDomain> {
        return when (val user = tokenRepository.getCurrentSessionState()) {
            is SessionState.Guest,
            is SessionState.User -> detailService.getDetailAboutMovie(detailParameter.id, user)
                .map { it.toDomain() }

            is SessionState.None -> DetailsError.UserNotAuth.left()
        }
    }

    override suspend fun getTv(detailParameter: DetailParameter): Either<DetailsError, ContentDetailDomain> {
        return when (val user = tokenRepository.getCurrentSessionState()) {
            is SessionState.Guest,
            is SessionState.User -> detailService.getDetailAboutTv(detailParameter.id, user)
                .map { it.toDomain() }

            SessionState.None -> DetailsError.UserNotAuth.left()
        }
    }

    override suspend fun getPerson(detailParameter: DetailParameter): Either<DetailsError, ContentDetailDomain> {
        TODO("Not yet implemented")
    }
}