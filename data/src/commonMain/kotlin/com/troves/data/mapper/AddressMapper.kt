package com.troves.data.mapper

import com.troves.data.source.local.database.AddressDecorationEntity
import com.troves.domain.entity.Address
import com.troves.domain.entity.AddressIcon

fun Address.applyDecoration(decoration: AddressDecorationEntity?): Address = copy(
    label = decoration?.label,
    icon = decoration?.icon
        ?.let { runCatching { AddressIcon.valueOf(it) }.getOrNull() }
        ?: AddressIcon.HOME,
    note = decoration?.note,
)

fun Address.toDecorationEntity(): AddressDecorationEntity = AddressDecorationEntity(
    addressId = id,
    label = label,
    icon = icon.name,
    note = note,
)
