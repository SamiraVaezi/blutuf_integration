package net.grandcentrix.blutufintegration.data.model

sealed class ErrorCondition {

    object PermissionNotGuaranteedError : ErrorCondition()

    object DisabledBluetoothError : ErrorCondition()

    object GpsDisabledError : ErrorCondition()

    object UnknownError : ErrorCondition()
}
