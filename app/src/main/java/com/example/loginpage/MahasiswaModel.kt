package com.example.loginpage

import android.os.Parcel
import android.os.Parcelable

data class MahasiswaModel(
    var dbId: String? = null,
    var name: String? = null,
    var nim: String? = null,
    var jurusan: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dbId)
        parcel.writeString(name)
        parcel.writeString(nim)
        parcel.writeString(jurusan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MahasiswaModel> {
        override fun createFromParcel(parcel: Parcel): MahasiswaModel {
            return MahasiswaModel(parcel)
        }

        override fun newArray(size: Int): Array<MahasiswaModel?> {
            return arrayOfNulls(size)
        }
    }
}
