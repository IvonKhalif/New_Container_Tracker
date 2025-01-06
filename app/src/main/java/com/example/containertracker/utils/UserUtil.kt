package com.example.containertracker.utils

import com.example.containertracker.data.user.models.User

object UserUtil {
    fun get() = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
    fun set(user: User) = PreferenceUtils.put(user, PreferenceUtils.USER_PREFERENCE)

    fun getDepartmentId() = get()?.departmentId.orEmpty()
}