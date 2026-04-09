package com.splanes.uoc.wishlify.data.feature.shared.mapper

import com.splanes.uoc.wishlify.data.common.utils.expirationDateInMillis
import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistEntity
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistItemEntity
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.ShareWishlistRequest
import java.sql.Date

class SharedWishlistsDataMapper {

  fun sharedWishlistFromRequest(request: ShareWishlistRequest): SharedWishlistEntity =
    SharedWishlistEntity(
      id = newUuid(),
      wishlist = request.wishlistId,
      owner = request.owner,
      editors = request.editors,
      group = request.group,
      participants = emptyList(),
      editorsCanSeeUpdates = request.editorsCanSeeUpdates,
      inviteLink = request.shareLink.token,
      deadline = request.deadline,
      sharedAt = nowInMillis(),
    )

  fun sharedItemEntityFromRequest(
    uid: String,
    request: SharedWishlistItemUpdateStateRequest
  ): SharedWishlistItemEntity =
    when (val newStateRequest = request.newStateRequest) {
      SharedWishlistItemStateRequest.Unlock,
      SharedWishlistItemStateRequest.CancelShareRequest ->
        SharedWishlistItemEntity(
          id = request.item.id,
          item = request.item.linkedItem.id,
          state = SharedWishlistItemEntity.State.Available,
          reservation = null,
          shareRequest = null,
          purchased = null,
        )

      SharedWishlistItemStateRequest.JoinToShareRequest -> {

        val currentState = request.item.state
        val newState = (currentState as? SharedWishlistItem.ShareRequest)?.let { current ->
          val joined = current.participantsJoined.count()
          val requested = current.numOfParticipantsRequested
          when {
            joined + 1 < requested -> SharedWishlistItemEntity.State.ShareRequest
            joined + 1 == requested -> SharedWishlistItemEntity.State.Reserved
            else -> error("Joined participants has reached the amount requested but the item state is not reserved...")
          }
        } ?: error("Trying to join to a share request but there's no one available")

        val reservation = if (newState == SharedWishlistItemEntity.State.Reserved) {
          SharedWishlistItemEntity.Lock(
            reservedBy = currentState.requestedBy.uid,
            reservedByGroup = buildList {
              add(currentState.requestedBy.uid)
              addAll(currentState.participantsJoined.map { it.uid })
              add(uid)
            }.distinct(),
            reservedAt = nowInMillis(),
            expiresAt = expirationDateInMillis()
          )
        } else {
          null
        }

        val shareRequest = if (newState == SharedWishlistItemEntity.State.ShareRequest) {
          SharedWishlistItemEntity.ShareRequest(
            requestedBy = currentState.requestedBy.uid,
            participantsRequested = currentState.numOfParticipantsRequested,
            participantsJoined = (currentState.participantsJoined.map { it.uid } + uid).distinct(),
            requestedAt = currentState.requestedAt.time,
            expiresAt = currentState.expiresAt.time
          )
        } else {
          null
        }

        SharedWishlistItemEntity(
          id = request.item.id,
          item = request.item.linkedItem.id,
          state = newState,
          reservation = reservation,
          shareRequest = shareRequest,
          purchased = null,
        )
      }

      SharedWishlistItemStateRequest.Lock ->
        SharedWishlistItemEntity(
          id = request.item.id,
          item = request.item.linkedItem.id,
          state = SharedWishlistItemEntity.State.Reserved,
          reservation = SharedWishlistItemEntity.Lock(
            reservedBy = uid,
            reservedByGroup = listOf(uid),
            reservedAt = nowInMillis(),
            expiresAt = expirationDateInMillis()
          ),
          shareRequest = null,
          purchased = null,
        )

      SharedWishlistItemStateRequest.Purchase ->
        SharedWishlistItemEntity(
          id = request.item.id,
          item = request.item.linkedItem.id,
          state = SharedWishlistItemEntity.State.Purchased,
          reservation = null,
          shareRequest = null,
          purchased = SharedWishlistItemEntity.Purchased(
            purchasedAt = nowInMillis(),
            purchasedBy = uid,
            purchasedByGroup = listOf(uid),
          ),
        )

      is SharedWishlistItemStateRequest.ShareRequest -> SharedWishlistItemEntity(
        id = request.item.id,
        item = request.item.linkedItem.id,
        state = SharedWishlistItemEntity.State.ShareRequest,
        reservation = null,
        shareRequest = SharedWishlistItemEntity.ShareRequest(
          requestedBy = uid,
          participantsRequested = newStateRequest.numOfParticipants,
          requestedAt = nowInMillis(),
          expiresAt = expirationDateInMillis()
        ),
        purchased = null,
      )
    }

  fun mapWishlist(
    uid: String,
    entity: SharedWishlistEntity,
    groups: Map<String, Group.Basic>,
    wishlists: Map<String, SharedWishlist.LinkedWishlist>,
    users: Map<String, User.Basic>,
    numOfItemsMap: Map<String, Int>,
    pendingNotificationsMap: Map<String, Int>
  ): SharedWishlist =
    if (uid in entity.editors && !entity.editorsCanSeeUpdates) {
      SharedWishlist.Own(
        id = entity.id,
        linkedWishlist = wishlists[entity.wishlist]
          ?: error("No linked wishlist found for shared: `${entity.id}`"),
        owner = users[entity.owner] ?: error("No owner found for shared: `${entity.id}`"),
        group = groups[entity.group],
        participants = entity.participants.mapNotNull { participant -> users[participant] },
        editors = entity.editors.mapNotNull { editor -> users[editor] },
        inviteLink = InviteLink(
          origin = InviteLink.WishlistShare,
          token = entity.inviteLink,
        ),
        deadline = Date(entity.deadline),
        sharedAt = Date(entity.sharedAt),
        numOfItems = numOfItemsMap[entity.id] ?: 0,
      )
    } else {
      SharedWishlist.ThirdParty(
        id = entity.id,
        linkedWishlist = wishlists[entity.wishlist]
          ?: error("No linked wishlist found for shared: `${entity.id}`"),
        owner = users[entity.owner] ?: error("No owner found for shared: `${entity.id}`"),
        group = groups[entity.group],
        participants = entity.participants.mapNotNull { participant -> users[participant] },
        editors = entity.editors.mapNotNull { editor -> users[editor] },
        editorsCanSeeUpdates = entity.editorsCanSeeUpdates,
        inviteLink = InviteLink(
          origin = InviteLink.WishlistShare,
          token = entity.inviteLink,
        ),
        deadline = Date(entity.deadline),
        sharedAt = Date(entity.sharedAt),
        numOfItems = numOfItemsMap[entity.id] ?: 0,
        target = wishlists[entity.wishlist]?.target ?: users[entity.owner]?.username ?: error(""),
        pendingNotificationsCount = pendingNotificationsMap[entity.id] ?: 0
      )
    }

  fun mapItem(
    uid: String,
    linkedItem: SharedWishlistItem.LinkedItem,
    sharedItem: SharedWishlistItemEntity?,
    users: Map<String, User.Basic>,
  ): SharedWishlistItem =
    SharedWishlistItem(
      id = sharedItem?.id ?: newUuid(),
      linkedItem = linkedItem,
      state = if (sharedItem != null) {
        mapItemState(uid, sharedItem, users)
      } else {
        SharedWishlistItem.Available
      }
    )

  private fun mapItemState(
    uid: String,
    sharedItem: SharedWishlistItemEntity,
    users: Map<String, User.Basic>,
  ) =
    when (sharedItem.state) {
      SharedWishlistItemEntity.State.Available ->
        SharedWishlistItem.Available

      SharedWishlistItemEntity.State.Reserved ->
        sharedItem.reservation?.let { reservation ->
          SharedWishlistItem.Lock(
            isCurrentUserParticipant = uid in reservation.reservedBy,
            isLockedByCurrentUser = reservation.reservedBy == uid,
            reservedBy = users[reservation.reservedBy]
              ?: error("No reservedBy user found for item `${sharedItem.id}`"),
            reservedByGroup = reservation.reservedByGroup.mapNotNull { users[it] },
            reservedAt = Date(reservation.reservedAt),
            expiresAt = Date(reservation.expiresAt),
          )
        } ?: error("Item `${sharedItem.id}` with state=Reserved but no `reservation` data.")

      SharedWishlistItemEntity.State.Purchased ->
        sharedItem.purchased?.let { purchase ->
          SharedWishlistItem.Purchased(
            isCurrentUserParticipant = uid in purchase.purchasedBy,
            isPurchasedByCurrentUser = uid == purchase.purchasedBy,
            purchasedBy = users[purchase.purchasedBy]
              ?: error("No purchasedBy user found for item `${sharedItem.id}`"),
            purchasedByGroup = purchase.purchasedByGroup.mapNotNull { users[it] },
            purchasedAt = Date(purchase.purchasedAt),
          )
        } ?: error("Item `${sharedItem.id}` with state=Purchased but no `purchased` data.")

      SharedWishlistItemEntity.State.ShareRequest ->
        sharedItem.shareRequest?.let { shareRequest ->
          SharedWishlistItem.ShareRequest(
            isCurrentUserParticipant =
              shareRequest.requestedBy == uid || uid in shareRequest.participantsJoined,
            isRequestedByCurrentUser = shareRequest.requestedBy == uid,
            requestedBy = users[shareRequest.requestedBy]
              ?: error("No share request user found for item `${sharedItem.id}`"),
            requestedAt = Date(shareRequest.requestedAt),
            expiresAt = Date(shareRequest.expiresAt),
            numOfParticipantsRequested = shareRequest.participantsRequested,
            participantsJoined = shareRequest.participantsJoined.mapNotNull { users[it] },
          )
        } ?: error("Item `${sharedItem.id}` with state=ShareRequest but no `shareRequest` data.")
    }
}