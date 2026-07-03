package com.troves.data.mapper

import com.troves.data.source.local.database.AddressEntity
import com.troves.domain.entity.Address
import com.troves.domain.entity.AddressIcon

fun AddressEntity.toDomain(): Address {
    return Address(
        id = this.id,
        label = this.label,
        icon = try {
            AddressIcon.valueOf(this.icon)
        } catch (e: Exception) {
            AddressIcon.HOME
        },
        phone = this.phone,
        lines = this.lines.split("|").filter { it.isNotBlank() },
        isDefault = this.isDefault,
        note = this.note
    )
}

fun Address.toEntity(): AddressEntity {
    return AddressEntity(
        id = this.id,
        label = this.label,
        icon = this.icon.name,
        phone = this.phone,
        lines = this.lines.joinToString("|"),
        isDefault = this.isDefault,
        note = this.note
    )
}
