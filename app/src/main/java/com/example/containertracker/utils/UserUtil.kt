package com.example.containertracker.utils

import com.example.containertracker.data.user.models.User
import com.example.containertracker.utils.enums.RoleAccessEnum

object UserUtil {
    fun get() = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
    fun set(user: User) = PreferenceUtils.put(user, PreferenceUtils.USER_PREFERENCE)

    fun getDepartmentId() = get()?.departmentId.orEmpty()

    fun isLocalSalesUser() = getDepartmentId() == RoleAccessEnum.LOCALSALES.value
    fun isTallyUser() = getDepartmentId() == RoleAccessEnum.TALLY.value
}