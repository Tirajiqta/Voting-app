package com.example.android.entity.referendum

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "referendum_options")
data class ReferendumOptionEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "option_text")
    val optionText: String,

    @Column(name = "vote_count")
    val voteCount: Int = 0,

    @Column(name = "referendum_id")
    val referendumId: Long
)