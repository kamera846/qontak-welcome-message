package com.topmortar.topmortarsales.model

data class ContactModel(
    var id_contact: String = "",
    var nama: String = "",
    var nomorhp: String = "",
    var tgl_lahir: String = "0000-00-00",
    var store_owner: String = "",
    var id_city: String = "0",
    var maps_url: String = "",
    var address: String = "",
    var store_status: String = "",
    var tagihan_mingguan: String? = null,
    var ktp_owner: String = "",
    var payment_method: String = "",
    var termin_payment: String = "",
    var id_promo: String = "",
    var reputation: String = "",
    var is_birthday: String = "",
    var created_at: String = "",
    var deliveryStatus: String = "",
)