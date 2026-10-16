package com.lokahe.backend.supabase.mapper

import com.lokahe.auth.Session
import com.lokahe.backend.supabase.model.AuthResponse
import com.lokahe.backend.supabase.model.Follower
import com.lokahe.backend.supabase.model.Following
import com.lokahe.backend.supabase.model.Post
import com.lokahe.backend.supabase.model.PostRequest
import com.lokahe.backend.supabase.model.Profile
import com.lokahe.backend.supabase.model.User
import com.lokahe.domain.model.AuthResult
import com.lokahe.domain.model.Follower as DomainFollower
import com.lokahe.domain.model.Followers as DomainFollowers
import com.lokahe.domain.model.Following as DomainFollowing
import com.lokahe.domain.model.Like as DomainLike
import com.lokahe.domain.model.LikeStatus as DomainLikeStatus
import com.lokahe.domain.model.Post as DomainPost
import com.lokahe.domain.model.PostRequest as DomainPostRequest
import com.lokahe.domain.model.Profile as DomainProfile
import com.lokahe.domain.model.User as DomainUser

/**
 * DTO → domain mapping. Lives in the backend module so :domain stays
 * wire-format-free; ViewModels/UI never see these DTOs.
 */

fun Post.toDomain(): DomainPost = DomainPost(
    id = id,
    authorId = authorId,
    createdAt = createdAt,
    content = content,
    imageUrls = imageUrls,
    videoUrls = videoUrls,
    hashtag = hashtag,
    replyPostId = replyPostId,
    profiles = profiles.toDomain(),
    likes = likes.map { DomainLike(it.count) },
    liked = liked.map { DomainLikeStatus(it.userId) }
)

fun Profile.toDomain(): DomainProfile = DomainProfile(
    id = id,
    name = name,
    avatar = avatar,
    email = email,
    phone = phone,
    address = address,
    birth = birth,
    description = description,
    gender = gender,
    createdAt = createdAt,
    updatedAt = updatedAt,
    lastSignInAt = lastSignInAt,
    emailConfirmedAt = emailConfirmedAt,
    followers = followers?.map { DomainFollowers(it.count) },
    followings = followings?.map { DomainFollowers(it.count) },
    followingList = followingList?.map { DomainFollowing(it.targetId) }
)

fun Following.toDomain(): DomainFollowing = DomainFollowing(targetId)

fun Follower.toDomain(): DomainFollower = DomainFollower(followerId)

fun User.toDomain(): DomainUser = DomainUser(
    id = id,
    email = email,
    phone = phone,
    name = userMetadata?.name,
    fullName = userMetadata?.fullName,
    avatarUrl = userMetadata?.avatarUrl,
    profile = profile?.toDomain()
)

fun AuthResponse.toAuthResult(): AuthResult = AuthResult(
    session = Session(
        userId = user?.id,
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresAt = expiresAt
    ),
    user = user?.toDomain()
)

fun DomainPostRequest.toDto(): PostRequest = PostRequest(
    authorId = authorId,
    content = content,
    imageUrls = imageUrls,
    videoUrls = videoUrls,
    hashtag = hashtag,
    replyPostId = replyPostId
)