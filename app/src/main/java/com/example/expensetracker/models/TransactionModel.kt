package com.example.expensetracker.models

import android.os.Parcel
import android.os.Parcelable

data class TransactionModel(
    var transactionId: String? = null,
    var title: String? = null,
    var description: String? = null,
    var amount: Float = 0f,
    var type: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transactionId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeFloat(amount)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransactionModel> {
        override fun createFromParcel(parcel: Parcel): TransactionModel {
            return TransactionModel(parcel)
        }

        override fun newArray(size: Int): Array<TransactionModel?> {
            return arrayOfNulls(size)
        }
    }
}
