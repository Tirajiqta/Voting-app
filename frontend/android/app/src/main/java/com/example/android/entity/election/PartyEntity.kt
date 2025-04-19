package com.example.android.entity.election

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "parties")
data class PartyEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "abbreviation")
    val abbreviation: String? = null,

    @Column(name = "logo_url")
    val logoUrl: String? = null,

    @Column(name = "leader_name")
    val leaderName: String? = null
)
