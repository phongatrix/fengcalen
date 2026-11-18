package fengcalen.service

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeService @Inject constructor() {
    fun isNativeLoaded(): Boolean = false
}
