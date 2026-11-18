package fengcalen.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class ContentProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "fengcalen.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/data")
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = "vnd.android.cursor.dir/vnd.fengcalen.item"
}
